package service

import actors.{OrderActor, ProcessRecord}
import akka.actor.{ActorSystem, Props}
import com.nashtech.delivery.v1.models.{Address, Contact, Delivery}
import com.nashtech.order.v1.models.Order
import com.nashtech.order.v1.models.json.jsonReadsOrderOrder
import com.typesafe.scalalogging.LazyLogging
import dao.DAO
import org.joda.time.DateTime
import play.api.libs.json.Json
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.ConfigsBuilder
import software.amazon.kinesis.coordinator.Scheduler
import software.amazon.kinesis.lifecycle.events._
import software.amazon.kinesis.processor.{ShardRecordProcessor, ShardRecordProcessorFactory}
import software.amazon.kinesis.retrieval.KinesisClientRecord

import java.io.{BufferedReader, IOException, InputStreamReader}
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.{ExecutionException, TimeUnit, TimeoutException}
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class DeliveryEventProcessorFactory @Inject()(dao: DAO) extends ShardRecordProcessorFactory {
  override def shardRecordProcessor(): ShardRecordProcessor = new DeliveryEventProcessor(dao)
}

class DeliveryEventConsumer @Inject()(
  deliveryDao: DAO
) extends LazyLogging {

  def initialize(): Future[Unit] = {

    val credentials = AwsBasicCredentials.create("test", "test")

    val credentialsProvider = StaticCredentialsProvider.create(credentials)

    val kinesisClient: KinesisAsyncClient = KinesisAsyncClient.builder()
      .region(Region.US_EAST_1)
      .credentialsProvider(credentialsProvider)
      .endpointOverride(new java.net.URI("http://localhost:4566"))
      .httpClient(NettyNioAsyncHttpClient.builder().build())
      .build()

    Future(run(kinesisClient))
  }

  private def run(kinesisClient: KinesisAsyncClient): Unit = {
    val credentials = AwsBasicCredentials.create("test", "test")
    val credentialsProvider = StaticCredentialsProvider.create(credentials)

    val dynamoClient = DynamoDbAsyncClient
      .builder()
      .region(Region.US_EAST_1)
      .credentialsProvider(credentialsProvider)
      .endpointOverride(new java.net.URI("http://localhost:4566"))
      .httpClient(NettyNioAsyncHttpClient.builder().build())
      .build()

    val cloudWatchClient = CloudWatchAsyncClient
      .builder()
      .credentialsProvider(credentialsProvider)
      .endpointOverride(new java.net.URI("http://localhost:4566"))
      .httpClient(NettyNioAsyncHttpClient.builder().build())
      .region(Region.US_EAST_1)
      .build()

    val configsBuilder = new ConfigsBuilder(
      "order-stream",
      "delivery-application",
      kinesisClient,
      dynamoClient,
      cloudWatchClient,
      UUID.randomUUID().toString,
      new DeliveryEventProcessorFactory(deliveryDao)
    )

    val scheduler = new Scheduler(
      configsBuilder.checkpointConfig,
      configsBuilder.coordinatorConfig,
      configsBuilder.leaseManagementConfig,
      configsBuilder.lifecycleConfig,
      configsBuilder.metricsConfig,
      configsBuilder.processorConfig,
      configsBuilder.retrievalConfig
    )

    val schedulerThread = new Thread(scheduler)
    schedulerThread.setDaemon(true)
    schedulerThread.start()

    val reader = new BufferedReader(new InputStreamReader(System.in))

    try reader.readLine
    catch {
      case ioException: IOException =>
        logger.error("Caught exception while waiting for confirm. Shutting down.", ioException)
    }

    val gracefulShutdownFuture = scheduler.startGracefulShutdown
    logger.info("Waiting up to 20 seconds for shutdown to complete.")
    try gracefulShutdownFuture.get(20, TimeUnit.SECONDS)
    catch {
      case _: InterruptedException =>
        logger.info("Interrupted while waiting for graceful shutdown. Continuing.")
      case e: ExecutionException =>
        logger.error("Exception while executing graceful shutdown.", e)
      case _: TimeoutException =>
        logger.error("Timeout while waiting for shutdown. Scheduler may not have exited.")
    }
    logger.info("Completed, shutting down now.")

  }
}

class DeliveryEventProcessor @Inject()(dao: DAO, system: ActorSystem) extends ShardRecordProcessor with LazyLogging {

  override def initialize(initializationInput: InitializationInput): Unit = {
    logger.info(s"Initializing record processor for shard: ${initializationInput.shardId}")
    logger.info(s"Initializing @ Sequence: ${initializationInput.extendedSequenceNumber.toString}")
  }

  override def processRecords(processRecordsInput: ProcessRecordsInput): Unit =
    try {
      logger.info("Processing " + processRecordsInput.records.size + " record(s)")
      processRecordsInput.records.forEach((r: KinesisClientRecord) => processRecord(r))
    } catch {
      case _: Throwable =>
        logger.error("Caught throwable while processing records. Aborting.")
        Runtime.getRuntime.halt(1)
    }

  private def processRecord(record: KinesisClientRecord): Either[String, Unit] = {

    val eventString = StandardCharsets.UTF_8.decode(record.data).toString

    println(
      s"Processing record pk: ${record.partitionKey()} -- Data: $eventString"
    )

    val eventJson = Json.parse(eventString)
    //database work to be done
    val event = Try(eventJson.as[Order])

    event match {
      case Success(order) =>
        Right(system.actorOf(Props[OrderActor]) ! ProcessRecord(order))
//        Right(createDeliveryObjectUsingOrder(order))
      case Failure(e) => Left(e.getMessage)
    }
  }

  override def leaseLost(leaseLostInput: LeaseLostInput): Unit =
    println("Lost lease, so terminating.")

  override def shardEnded(shardEndedInput: ShardEndedInput): Unit =
    try {
      // Important to checkpoint after reaching end of shard, so to start processing data from child shards.
      shardEndedInput.checkpointer.checkpoint()
    } catch {
      case e: Throwable =>
        println("Exception while checkpointing at shard end. Giving up.", e)
    }

  override def shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput): Unit =
    try {
      shutdownRequestedInput.checkpointer().checkpoint()
    } catch {
      case e: Throwable =>
        println("Exception while checkpointing at requested shutdown. Giving up.", e)
    }

}