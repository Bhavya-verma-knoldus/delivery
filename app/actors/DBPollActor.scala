package actors

import anorm._
import com.nashtech.delivery.v1.anorm.conversions.Standard.columnToJsValue
import org.joda.time.DateTime
import play.api.db.Database
import play.api.libs.json.{JsValue, Json}

import scala.util.{Failure, Success, Try}

abstract class DBPollActor(schema: String = "public", table: String) extends PollActor {

  def db: Database

  private val processingTable = {
    val tempName = s"${table}_processing_queue"
    if (schema.equalsIgnoreCase("public")) tempName
    else s"${schema}.${tempName}"
  }
  private val journalTable = {
    val tempName = s"${table}_journal"
    if (schema.equalsIgnoreCase("public")) tempName
    else s"${schema}.${tempName}"
  }

  startPolling()

  override def preStart(): Unit = {
    log.info("[DBPollActor] Pre-Start")
    println("[DBPollActor] Pre-Start")
  }

  def process(record: ProcessQueueDelivery): Try[Unit]

  override def processRecord(): Unit = {
    log.info("Inside processRecord method")
    val record = getEarliestRecord(processingTable)
    safeProcessRecord(record)

  }

  private def safeProcessRecord(record: ProcessQueueDelivery) = {
    Try {
      log.info("Inside safeProcessRecord method")
      println("Inside safeProcessRecord method")
      process(record)
    } match {
      case Success(_) =>
        log.info("Continuing with safeProcessRecord method")
        println("Continuing with safeProcessRecord method")
        deleteProcessingQueueRecord(record.processingQueueId)
        insertJournalRecord(record)
      case Failure(ex) =>
        log.info("Discontinuing with safeProcessRecord method")
        println("Discontinuing with safeProcessRecord method")
        setErrors(record.processingQueueId, ex)
    }
  }

  private def deleteProcessingQueueRecord(id: Int) = {
    db.withConnection { implicit connection =>
      SQL(deleteQuery(id)).executeUpdate()
    }
  }

  private def insertJournalRecord(record: ProcessQueueDelivery) = {
    db.withConnection { implicit connection =>
      insertQuery(record).executeInsert()
    }
  }

  private def setErrors(processingQueueId: Int, throwable: Throwable) = {
    db.withConnection { implicit connection =>
      SQL(setErrorsQuery(processingQueueId, throwable)).executeUpdate()
    }
  }

  private def getEarliestRecord(processingTable: String): ProcessQueueDelivery = {
    db.withConnection { implicit connection =>
      SQL(baseQuery).as(processingQueueDeliveryParser().single)
    }
  }

  private def baseQuery =
    s"""
       |select processing_queue_id, id, order_number, merchant_id, estimated_delivery_date, origin, destination, contact_info, created_at,
       |updated_at,operation
       |from ${processingTable}
       |order by created_at asc limit 1
       |""".stripMargin

  private def deleteQuery(id: Int) =
    s"""
       |delete from ${processingTable} where processing_queue_id = $id
       |""".stripMargin

  private def insertQuery(record: ProcessQueueDelivery) = {
    SQL(
      s"""
         |INSERT INTO $journalTable (processing_queue_id, id, order_number, merchant_id, estimated_delivery_date,
         | origin, destination, contact_info, created_at,
         |updated_at, journal_timestamp, journal_operation
         |)
         |VALUES
         |(
         |{processing_queue_id},
         |{id},
         |{order_number},
         |{merchant_id},
         |{estimated_delivery_date}::date,
         |{origin}::jsonb,
         |{destination}::jsonb,
         |{contact_info}::jsonb,
         |{created_at}::date,
         |{updated_at}::date,
         |{journal_timestamp}::date,
         |{journal_operation}
         |)
         |returning *
         |""".stripMargin)
      .on("processing_queue_id" -> record.processingQueueId)
      .on("id" -> record.id)
      .on("order_number" -> record.orderNumber)
      .on("merchant_id" -> record.merchantId)
      .on("estimated_delivery_date" -> record.estimateDeliveryDate.toString)
      .on("origin" -> Json.toJson(record.origin).toString())
      .on("destination" -> Json.toJson(record.destination).toString())
      .on("contact_info" -> Json.toJson(record.contactInfo).toString())
      .on("created_at" -> record.createdAt.toString)
      .on("updated_at" -> record.updatedAt.toString)
      .on("journal_timestamp" -> DateTime.now().toString)
      .on("journal_operation" -> record.operation)
  }

  private def setErrorsQuery(id: Int, ex: Throwable) = {
    s"""
       |update $processingTable set error_message = '${ex.getMessage}'
       | where processing_queue_id = $id
       |""".stripMargin
  }

  private def processingQueueDeliveryParser(): RowParser[ProcessQueueDelivery] = {
    SqlParser.int("processing_queue_id") ~
      SqlParser.str("id") ~
      SqlParser.str("order_number") ~
      SqlParser.str("merchant_id") ~
      SqlParser.get[DateTime]("estimated_delivery_date") ~
      SqlParser.get[JsValue]("origin") ~
      SqlParser.get[JsValue]("destination") ~
      SqlParser.get[JsValue]("contact_info") ~
      SqlParser.get[DateTime]("created_at") ~
      SqlParser.get[DateTime]("updated_at") ~
      SqlParser.str("operation") map {

      case processingQueueId ~ id ~ orderNumber ~ merchantId ~ estimateDeliveryDate ~ origin ~ destination ~ contactInfo ~ createdAt ~ submittedAt ~ operation =>
        ProcessQueueDelivery(
          processingQueueId, id, orderNumber, merchantId, estimateDeliveryDate, origin, destination, contactInfo, createdAt, submittedAt, operation
        )
    }
  }
}

case class ProcessQueueDelivery(
  processingQueueId: Int,
  id: String,
  orderNumber: String,
  merchantId: String,
  estimateDeliveryDate: DateTime,
  origin: JsValue,
  destination: JsValue,
  contactInfo: JsValue,
  createdAt: DateTime,
  updatedAt: DateTime,
  operation: String
)