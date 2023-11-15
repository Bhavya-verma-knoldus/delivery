package service

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisClient
import software.amazon.awssdk.services.kinesis.model.{GetRecordsRequest, GetShardIteratorRequest, ShardIteratorType}

import scala.util.{Failure, Success, Try}

class Consumer {
  val kinesisClient = KinesisClient.builder()
    .region(Region.US_EAST_1)
    .endpointOverride(new java.net.URI("http://localhost:4566"))
    .build()
  private def consume() = {
    //    val data = SdkBytes.fromString("Order()", Charset.defaultCharset())
    val streamName = " lambda-stream"
    val shardId = "shardId-000000000000"

    val getShardIteratorRequest = GetShardIteratorRequest.builder()
      .streamName(streamName)
      .shardId(shardId)
      .shardIteratorType(ShardIteratorType.LATEST)
      .build()

    val shardIterator = kinesisClient.getShardIterator(getShardIteratorRequest).shardIterator()

    val getRecordRequest: GetRecordsRequest = GetRecordsRequest.builder()
      .shardIterator(shardIterator)
      .limit(10) // Set the desired limit for the number of records to retrieve
      .build()

    Try(kinesisClient.getRecords(getRecordRequest)) match {
      case Failure(exception) => sys.error(exception.getMessage)
      case Success(value) => println("Recieved data: " + value.toString)
    }
  }
}
