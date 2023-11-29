package service

import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model._

import java.util.Optional
import java.util.concurrent.CompletableFuture
import scala.annotation.tailrec
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

  //    println(kinesisClient.listStreams().get())
  //    Thread.sleep(5000)
  println(s"List of steams ------> ${kinesisClient.listStreams().get().streamNames()}")

  def consume(): Either[Throwable, GetRecordsResponse] = {

    println("****************** line 27")

    val stream = describeStream("lambda-stream-4")

    println("****************** line 41")

    val shardId = stream.streamDescription().shards().get(0).shardId()
    val streamName = stream.streamDescription().streamName()
    System.setProperty("aws.cborEnabled", "false")

    println("****************** line 48")

    val getShardIteratorRequest = {
      GetShardIteratorRequest.builder()
        .streamName(streamName)
        .shardId(shardId)
        .shardIteratorType(ShardIteratorType.LATEST)
        .build()
    }

    println("****************** line 58")
    //    Thread.sleep(5000)
    //    def shardIterators(): Either[Throwable, Optional[String]] = {
    //      Try(kinesisClient.getShardIterator(getShardIteratorRequest).get().getValueForField("ShardIterator", classOf[String])) match {
    //        case Failure(exception) => Left(exception)
    //        case Success(value) => Right(value)
    //      }
    //    }

    def getShardIterator: Either[Throwable, Optional[String]] = {
      println("************* Line 68")
      Try(kinesisClient.getShardIterator(getShardIteratorRequest).get().getValueForField("ShardIterator", classOf[String])) match {
        case Failure(exception) => Left(exception)
        case Success(value) => Right(value)
      }
    }

    println("****************** line 74")

    def getRecordRequest: Either[Throwable, GetRecordsRequest] = {
      getShardIterator match {
        case Left(ex) => Left(ex)
        case Right(value) => Right(GetRecordsRequest.builder()
          .shardIterator(value.get())
          .build())
      }
    }

    def getRecordsFromKinesis: Either[Throwable, GetRecordsResponse] = {
      getRecordRequest match {
        case Left(value) => Left(value)
        case Right(getRecordRequest) => Try(kinesisClient.getRecords(getRecordRequest)) match {
          case Failure(exception) =>
            println(s"Error while getting records: ${exception.getMessage}")
            Left(exception)
          case Success(result) =>
            println("Received data: ______________________________________________" + result.get().records())
            println("Response content: " + result.toString)
            Right(result.get())
        }
      }
    }
    getRecordsFromKinesis
  }

  private def createStream(streamName: String): CompletableFuture[CreateStreamResponse] = {
    println("****************** line 91")
    kinesisClient.createStream(
      CreateStreamRequest.builder()
        .streamName(streamName)
        .shardCount(1)
        .build())
  }
  @tailrec
  private def describeStream(streamName: String): DescribeStreamResponse = {
    println("****************** line 101")
    val result = Try(kinesisClient.describeStream(DescribeStreamRequest.builder().streamName(streamName).build()).get())

    println("****************** line 104")
    println(result)

    result match {
      case Failure(_) =>
        println("****************** line 110")
        createStream(streamName)
        describeStream(streamName)
      case Success(value) =>
        println("****************** line 114")
        value
    }
  }
}
