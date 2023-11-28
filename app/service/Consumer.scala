package service

import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model._

import java.util.concurrent.CompletableFuture
import scala.util.{Failure, Success, Try}

class Consumer {

  val credentials: AwsBasicCredentials = AwsBasicCredentials.create("test", "test")

  val credentialsProvider: StaticCredentialsProvider = StaticCredentialsProvider.create(credentials)

  val kinesisClient: KinesisAsyncClient = KinesisAsyncClient.builder()
    .region(Region.US_EAST_1)
    .credentialsProvider(credentialsProvider)
    .endpointOverride(new java.net.URI("http://localhost:4566"))
    .httpClient(NettyNioAsyncHttpClient.builder().build())
    .build()

  def consume(): Unit = {

    println("******************")

    //    kinesisClient.describeStreamSummary(DescribeStreamSummaryRequest)

    val stream = describeStream("lambda-stream-0") match {
      case Left(_) => createStream("lambda-stream-0").get().asInstanceOf[DescribeStreamResponse]
      case Right(stream) => stream.get()
    }

    //    println("\n***************************"+stream+"\n")

    val shardId = stream.streamDescription().shards().get(0).shardId()
    val streamName = stream.streamDescription().streamName()
    System.setProperty("aws.cborEnabled", "false")

    val getShardIteratorRequest = GetShardIteratorRequest.builder()
      .streamName(streamName)
      .shardId(shardId)
      .shardIteratorType(ShardIteratorType.LATEST)
      .build()

    val shardIterator = kinesisClient.getShardIterator(getShardIteratorRequest).get().getValueForField("ShardIterator", classOf[String])
    println(s"\n\n$shardIterator\n")

    val getRecordRequest: GetRecordsRequest = GetRecordsRequest.builder()
      .shardIterator(shardIterator.get())
      .build()

    Try(kinesisClient.getRecords(getRecordRequest)) match {
      case Failure(exception) =>
        println(s"Error while getting records: ${exception.getMessage}")
      case Success(value) =>
        println("Received data: ______________________________________________" + value.get().records())
        println("Response content: " + value.toString)
    }
  }

  private def createStream(streamName: String): CompletableFuture[CreateStreamResponse] = {
    kinesisClient.createStream(
      CreateStreamRequest.builder()
        .streamName(streamName)
        .shardCount(1)
        .build())
  }

  private def describeStream(streamName: String): Either[software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException, CompletableFuture[DescribeStreamResponse]] = {
    Try(kinesisClient.describeStream(DescribeStreamRequest.builder().streamName(streamName).build())) match {
      case Failure(exception: software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException) => Left(exception)
      case Success(value) => Right(value)
    }
  }
}