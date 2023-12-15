package actors

import anorm._
import com.nashtech.delivery.v1.anorm.conversions.Standard.columnToJsValue
import org.joda.time.DateTime
import play.api.db.Database
import play.api.libs.json.{JsValue, Json}

import scala.util.{Failure, Success, Try}

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

abstract class DBPollActor(schema: String = "public", table: String) extends PollActor {

  import DBPollActor._

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
  }

  def process(record: ProcessQueueDelivery): Try[Unit]

  override def processRecord(): Unit = {
    println("Inside ProcessRecord Method")
    log.info("Inside processRecord method")
    val record = getEarliestRecord(processingTable)
    safeProcessRecord(record)
  }

  private def safeProcessRecord(record: ProcessQueueDelivery): Unit = {
    Try {
      log.info("Inside safeProcessRecord method")
      process(record)
    } match {
      case Success(_) =>
        log.info("Continuing with safeProcessRecord method")
        deleteProcessingQueueRecord(record.processingQueueId)
        insertJournalRecord(record)
      case Failure(ex) =>
        log.info("Discontinuing with safeProcessRecord method")
        setErrors(record.processingQueueId, ex)
    }
  }

  private def deleteProcessingQueueRecord(id: Int): Int = {
    db.withConnection { implicit connection =>
      SQL(deleteQuery(id, processingTable)).executeUpdate()
    }
  }

  private def insertJournalRecord(record: ProcessQueueDelivery): Unit = {
    db.withConnection { implicit connection =>
      insertQuery(record, journalTable).executeInsert()
    }
    ()
  }

  private def setErrors(processingQueueId: Int, throwable: Throwable): Int = {
    db.withConnection { implicit connection =>
      SQL(setErrorsQuery(processingQueueId, throwable, processingTable)).executeUpdate()
    }
  }

  private def getEarliestRecord(processingTable: String): ProcessQueueDelivery = {
    db.withConnection { implicit connection =>
      SQL(baseQuery(processingTable)).as(processingQueueDeliveryParser().single)
    }
  }
}

object DBPollActor {
  private def baseQuery(processingTable: String): String =
    s"""
       |select processing_queue_id, id, order_number, merchant_id, estimated_delivery_date, origin, destination, contact_info, created_at,
       |updated_at,operation
       |from ${processingTable}
       |order by created_at asc limit 1
       |""".stripMargin

  private def deleteQuery(id: Int, processingTable: String): String =
    s"""
       |delete from ${processingTable} where processing_queue_id = $id
       |""".stripMargin

  private def insertQuery(record: ProcessQueueDelivery, journalTable: String): SimpleSql[Row] = {
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

  private def setErrorsQuery(id: Int, ex: Throwable, processingTable: String): String = {
    s"""
       |update $processingTable set error_message = '${ex.getMessage}', error = '${ex.getClass.getSimpleName}'
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
