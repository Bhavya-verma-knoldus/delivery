package dao

import anorm.{Row, SQL, SimpleSql}
import com.nashtech.delivery.v1.anorm.parsers.Delivery.parser
import com.nashtech.delivery.v1.models.json.{jsonWritesDeliveryAddress, jsonWritesDeliveryContact}
import com.nashtech.delivery.v1.models.{Delivery, DeliveryForm}
import play.api.db._
import play.api.libs.json._

import javax.inject.Inject
import scala.util.Random

class DAO @Inject()(db: Database) {

  def createDelivery(delivery: DeliveryForm): Delivery = {
    db.withConnection { implicit connection =>
      BaseQuery.insertQuery(delivery).as(parser().single)
    }
  }

  def getById(merchantId: String, id: String): Delivery = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.selectQuery(merchantId, id)).as(parser().single)
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

    def insertQuery(delivery: DeliveryForm): SimpleSql[Row] = {

      val orderNumber = generateOrderNumber()

      SQL(
        s"""
           |INSERT INTO deliveries(id, order_number, merchant_id, estimated_delivery_date, origin, destination, contact_info)
           |VALUES
           |(
           |{id},
           |{order_number},
           |{merchant_id},
           |{estimated_delivery_date}::date,
           |{origin}::jsonb,
           |{destination}::jsonb,
           |{contact_info}::jsonb
           |)
           |returning *
           |""".stripMargin)
        .on("id" -> Random.nextLong(10000))
        .on("order_number" -> orderNumber)
        .on("merchant_id" -> "merchant-x")
        .on("estimated_delivery_date" -> "2023-10-31 15:00:00+05:30")
        .on("origin" -> Json.toJson(delivery.origin).toString())
        .on("destination" -> Json.toJson(delivery.destination).toString())
        .on("contact_info" -> Json.toJson(delivery.contactInfo).toString())
    }

    def updateQuery(merchantId: String, form: DeliveryForm, orderNumber: String): SimpleSql[Row] = {

      SQL(
        s"""
           |UPDATE deliveries SET estimated_delivery_date = {estimated_delivery_date}::date,
           |origin =  {origin}::jsonb,
           |destination = {destination}::jsonb,
           |contact_info = {contact_info}::jsonb
           |WHERE merchant_id = {merchant_id} AND order_number = {order_number}
           |RETURNING *;
           |""".stripMargin)
        .on("estimated_delivery_date" -> "2023-10-31 15:00:00+05:30")
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
}
