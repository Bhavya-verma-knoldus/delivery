package service

import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisClient
import software.amazon.awssdk.services.kinesis.model.{CreateStreamRequest, DescribeStreamRequest, GetRecordsRequest, GetShardIteratorRequest, ShardIteratorType}

import scala.util.{Failure, Success, Try}
import software.amazon.awssdk.http.AbortableInputStream
class Consumer {
  val kinesisClient = KinesisClient.builder()
    .region(Region.US_EAST_1)
    .endpointOverride(new java.net.URI("http://localhost:4566"))
    .httpClient(UrlConnectionHttpClient.builder().build())
    .build()
  def consume() = {
    //    val data = SdkBytes.fromString("Order()", Charset.defaultCharset())
    println("******************")
    val shardId = "shardId-000000000000"
//
//    kinesisClient.createStream(
//      CreateStreamRequest.builder()
//        .streamName("lambda-stream-3")
//        .shardCount(1)
//        .build())

//    kinesisClient.describeStreamSummary(DescribeStreamSummaryRequest)
    val stream = kinesisClient.describeStream(DescribeStreamRequest.builder().streamName("lambda-stream-3").build())
//    println("\n***************************"+stream+"\n")

     val streamName = stream.streamDescription().streamName()
    System.setProperty("aws.cborEnabled", "false")

    val getShardIteratorRequest = GetShardIteratorRequest.builder()
      .streamName(streamName)
      .shardId(shardId)
      .shardIteratorType(ShardIteratorType.LATEST)
      .build()

    val shardIterator = kinesisClient.getShardIterator(getShardIteratorRequest).getValueForField("ShardIterator", classOf[String])
    println(s"\n\n$shardIterator\n")

    val getRecordRequest: GetRecordsRequest = GetRecordsRequest.builder()
      .shardIterator(shardIterator.get())
      .build()

    Try(kinesisClient.getRecords(getRecordRequest)) match {
      case Failure(exception) =>
        println(s"Error while getting records: ${exception.getMessage}")
      case Success(value) =>
        println("Received data: " + value.toString)
        println("Response content: " + value.toString)
    }

  }
}
