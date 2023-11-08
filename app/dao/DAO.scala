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

  def updateById(merchantId: String, form: DeliveryForm): Delivery = {
    db.withConnection { implicit connection =>
      BaseQuery.updateQuery(merchantId, form).as(parser().single)
    }
  }

  def deleteById(merchantId: String): Delivery = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.deleteQuery(merchantId)).as(parser().single)
    }
  }

  private object BaseQuery {

    def selectQuery(merchantId: String, id: String): String = {
      s"SELECT * FROM deliveries WHERE id = '$id' AND merchant_id = '$merchantId';"
    }

    def insertQuery(delivery: DeliveryForm): SimpleSql[Row] = {

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
        .on("order_number" -> delivery.orderNumber)
        .on("merchant_id" -> "merchant-x")
        .on("estimated_delivery_date" -> "2023-10-31 15:00:00+05:30")
        .on("origin" -> Json.toJson(delivery.origin).toString())
        .on("destination" -> Json.toJson(delivery.destination).toString())
        .on("contact_info" -> Json.toJson(delivery.contactInfo).toString())
    }

    def updateQuery(merchantId: String, form: DeliveryForm): SimpleSql[Row] = {

      SQL(
        s"""
           |UPDATE deliveries SET order_number = {order_number},
           |estimated_delivery_date = {estimated_delivery_date}::date,
           |origin =  {origin}::jsonb,
           |destination = {destination}::jsonb,
           |contact_info = {contact_info}::jsonb
           |WHERE merchant_id = {merchant_id} RETURNING *
           |""".stripMargin)
        .on("order_number" -> form.orderNumber)
        .on("estimated_delivery_date" -> "2023-10-31 15:00:00+05:30")
        .on("origin" -> Json.toJson(form.origin).toString())
        .on("destination" -> Json.toJson(form.destination).toString())
        .on("contact_info" -> Json.toJson(form.contactInfo).toString())
        .on("merchant_id" -> merchantId)
    }

    def deleteQuery(merchantId: String): String = {
      s"""
         |DELETE FROM deliveries
         |WHERE merchant_id = '$merchantId'
         |RETURNING *;
         |""".stripMargin
    }
  }
}
