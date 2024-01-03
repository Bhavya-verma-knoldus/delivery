package dao

import anorm.{Row, RowParser, SQL, SimpleSql, SqlParser, ~}
import com.nashtech.delivery.v1.anorm.conversions.Standard.columnToJsValue
import com.nashtech.delivery.v1.anorm.parsers.Delivery.parser
import com.nashtech.delivery.v1.models.json.{jsonWritesDeliveryAddress, jsonWritesDeliveryContact}
import com.nashtech.delivery.v1.models.{Address, Contact, Delivery, DeliveryForm}
import org.joda.time.DateTime
import play.api.db._
import play.api.libs.json._

import javax.inject.Inject
import scala.util.Random

class DAO @Inject()(db: Database) {



  def createDelivery(delivery: DeliveryForm, merchantId: String): Delivery = {
    db.withConnection { implicit connection =>
      BaseQuery.insertQuery(delivery, merchantId).as(parser().single)
    }
  }

  def getById(merchantId: String, id: String): Delivery = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.selectQuery(merchantId, id)).as(deliveryParser().single)
    }
  }

  def updateByOrderNumber(merchantId: String, form: DeliveryForm, orderNumber: String): Delivery = {
    db.withConnection { implicit connection =>
      BaseQuery.updateQuery(merchantId, form, orderNumber).as(parser().single)
    }
  }

  def deleteByOrderNumber(merchantId: String, orderNumber: String): Delivery = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.deleteQuery(merchantId, orderNumber)).as(parser().single)
    }
  }

  private def generateOrderNumber(): String = {
    java.util.UUID.randomUUID().toString
  }

  private object BaseQuery {

    def selectQuery(merchantId: String, id: String): String = {
      s"SELECT * FROM deliveries WHERE id = '$id' AND merchant_id = '$merchantId';"
    }

    def insertQuery(delivery: DeliveryForm, merchantId: String): SimpleSql[Row] = {

      val orderNumber = generateOrderNumber()

      SQL(
        s"""
           |INSERT INTO deliveries(id, order_number, merchant_id, estimated_delivery_date, origin, destination, contact_info)
           |VALUES
           |(
           |{id},
           |{order_number},
           |{merchant_id},
           |{estimated_delivery_date}::timestamp,
           |{origin}::jsonb,
           |{destination}::jsonb,
           |{contact_info}::jsonb
           |)
           |returning *
           |""".stripMargin)
        .on("id" -> Random.nextLong(10000))
        .on("order_number" -> orderNumber)
        .on("merchant_id" -> merchantId)
        .on("estimated_delivery_date" -> "2023-10-31 15:00:00+05:30")
        .on("origin" -> Json.toJson(delivery.origin).toString())
        .on("destination" -> Json.toJson(delivery.destination).toString())
        .on("contact_info" -> Json.toJson(delivery.contactInfo).toString())
    }

    def updateQuery(merchantId: String, form: DeliveryForm, orderNumber: String): SimpleSql[Row] = {

      SQL(
        s"""
           |UPDATE deliveries SET estimated_delivery_date = {estimated_delivery_date}::timestamp,
           |origin =  {origin}::jsonb,
           |destination = {destination}::jsonb,
           |contact_info = {contact_info}::jsonb
           |WHERE merchant_id = {merchant_id} AND order_number = {order_number}
           |RETURNING *;
           |""".stripMargin)
        .on("estimated_delivery_date" -> "2023-10-31 15:01:00+05:30")
        .on("origin" -> Json.toJson(form.origin).toString())
        .on("destination" -> Json.toJson(form.destination).toString())
        .on("contact_info" -> Json.toJson(form.contactInfo).toString())
        .on("merchant_id" -> merchantId)
        .on("order_number" -> orderNumber)
    }

    def deleteQuery(merchantId: String, orderNumber: String): String = {
      s"""
         |DELETE FROM deliveries
         |WHERE merchant_id = '$merchantId' AND order_number = '$orderNumber'
         |RETURNING *;
         |""".stripMargin
    }
  }
    private def deliveryParser(): RowParser[Delivery]= {
      SqlParser.str("id") ~
        SqlParser.str("order_number") ~
        SqlParser.str("merchant_id") ~
        SqlParser.get[DateTime]("estimated_delivery_date") ~
        SqlParser.get[JsValue]("origin") ~
        SqlParser.get[JsValue]("destination") ~
        SqlParser.get[JsValue]("contact_info") map{
        case id ~ orderNumber ~ merchantId ~ estimatedDeliveryDate ~ originAddressJson ~ destinationAddressJson ~ contactInfoJson => {
          val originAddress: JsResult[Address] = Parsers.parseAddress(originAddressJson)
          val destinationAddress: JsResult[Address] = Parsers.parseAddress(destinationAddressJson)
          val contactInfo: JsResult[Contact] = Parsers.parseContact(contactInfoJson)

          println(s"${contactInfoJson.toString()}")
          println(s"\n\n ${originAddress.get}\n${destinationAddress.get}\n${contactInfo.get}")

          com.nashtech.delivery.v1.models.Delivery(
            id = id,
            orderNumber = orderNumber,
            merchantId = merchantId,
            estimatedDeliveryDate = estimatedDeliveryDate,
            origin = originAddress.get,
            destination = destinationAddress.get,
            contactInfo = contactInfo.get
          )
        }

      }
    }

  private object Parsers {

    implicit val addressFormat: OFormat[Address] = Json.format[Address]
    implicit val contactFormat: OFormat[Contact] = Json.format[Contact]

    def parseAddress(json: JsValue): JsResult[Address] = Json.fromJson[Address](json)
    def parseContact(json: JsValue): JsResult[Contact] = Json.fromJson[Contact](json)
  }
}
