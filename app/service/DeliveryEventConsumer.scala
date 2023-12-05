package service

import com.nashtech.delivery.v1.models.Delivery
import com.nashtech.delivery.v1.models.json.jsonReadsDeliveryDelivery
import com.typesafe.scalalogging.LazyLogging
import dao.DAO
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
import scala.util.{Failure, Success, Try}

class DeliveryEventProcessorFactory @Inject()(dao: DAO) extends ShardRecordProcessorFactory {
  override def shardRecordProcessor(): ShardRecordProcessor = new DeliveryEventProcessor(dao)
}

class DeliveryEventConsumer @Inject()(
                                       applicationName: String,
                                       deliveryDao: DAO
                                     ) extends LazyLogging {

  val credentials = AwsBasicCredentials.create("test", "test")

  val credentialsProvider = StaticCredentialsProvider.create(credentials)


  val kinesisClient: KinesisAsyncClient = KinesisAsyncClient.builder()
    .region(Region.US_EAST_1)
    .credentialsProvider(credentialsProvider)
    .endpointOverride(new java.net.URI("http://localhost:4566"))
    .httpClient(NettyNioAsyncHttpClient.builder().build())
    .build()
    //    //    val data = SdkBytes.fromString("Order()", Charset.defaultCharset())
    //    println("******************")
    ////    val shardId = "shardId-000000000000"
    ////
    ////    kinesisClient.createStream(
    ////      CreateStreamRequest.builder()
    ////        .streamName("lambda-stream-3")
    ////        .shardCount(1)
    ////        .build())
    //
    ////    kinesisClient.describeStreamSummary(DescribeStreamSummaryRequest)
    //    val stream = kinesisClient.describeStream(DescribeStreamRequest.builder().streamName("lambda-stream-3").build())
    ////    println("\n***************************"+stream+"\n")
    //
    //     val streamName = stream.streamDescription().streamName()
    //    System.setProperty("aws.cborEnabled", "false")
    //
    //    val getShardIteratorRequest = GetShardIteratorRequest.builder()
    //      .streamName(streamName)
    //      .shardId(shardId)
    //      .shardIteratorType(ShardIteratorType.LATEST)
    //      .build()
    //
    //    val shardIterator = kinesisClient.getShardIterator(getShardIteratorRequest).getValueForField("ShardIterator", classOf[String])
    //    println(s"\n\n$shardIterator\n")
    //
    //    val getRecordRequest: GetRecordsRequest = GetRecordsRequest.builder()
    //      .shardIterator(shardIterator.get())
    //      .build()

    def run(): Unit = {
      val dynamoClient = DynamoDbAsyncClient.builder().region(Region.US_EAST_1).build()
      val cloudWatchClient = CloudWatchAsyncClient.builder().region(Region.US_EAST_1).build()
      val configsBuilder = new ConfigsBuilder(
        "order-stream",
        applicationName,
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

class DeliveryEventProcessor @Inject()(dao: DAO) extends ShardRecordProcessor with LazyLogging {

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

  private def processRecord(record: KinesisClientRecord): Unit = {

    val eventString = StandardCharsets.UTF_8.decode(record.data).toString

    logger.info(
      s"Processing record pk: ${record.partitionKey()} -- Data: $eventString"
    )
    val eventJson = Json.parse(eventString)
    val event = Try(eventJson.as[Delivery])

    event match {
      case Success(order) => logger.info(s"Consumed Order. $order")
      case Failure(e) => logger.info(s"Failed to consume. $e")
    }
  }

    override def leaseLost(leaseLostInput: LeaseLostInput): Unit =
      println("Lost lease, so terminating.")

    override def shardEnded(shardEndedInput: ShardEndedInput): Unit =
      try {
        // Important to checkpoint after reaching end of shard, so to start processing data from child shards.
        println("Reached shard end checkpointing.")
        shardEndedInput.checkpointer.checkpoint()
      } catch {
        case e: Throwable =>
          println("Exception while checkpointing at shard end. Giving up.", e)
      }

    override def shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput): Unit =
      try {
        println("Scheduler is shutting down, checkpointing.")
        shutdownRequestedInput.checkpointer().checkpoint()
      } catch {
        case e: Throwable =>
          println("Exception while checkpointing at requested shutdown. Giving up.", e)
      }
}


//Try(kinesisClient.getRecords(getRecordRequest)) match {
//  case Failure(exception) =>
//    println(s"Error while getting records: ${exception.getMessage}")
//  case Success(value) =>
//    println("Received data: " + value.toString)
//    println("Response content: " + value.toString)
//}
