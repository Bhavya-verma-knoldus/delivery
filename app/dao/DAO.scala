package dao

import anorm.SQL
import com.nashtech.delivery.v1.anorm.parsers.Delivery.parser
import com.nashtech.delivery.v1.models.{Delivery, DeliveryForm}
import play.api.db._
import play.api.libs.json._
import com.nashtech.delivery.v1.models.json.{jsonWritesDeliveryAddress,jsonWritesDeliveryContact}


import javax.inject.Inject

class DAO @Inject()(db: Database) {

  def createDelivery(delivery: DeliveryForm): Delivery = {
    db.withConnection { implicit connection =>
      println("********************"+BaseQuery.insertQuery(delivery))

      SQL(BaseQuery.insertQuery(delivery)).as(parser().single)
    }
  }

  def getById(merchantId: String, id: String): Delivery = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.selectQuery(merchantId, id)).as(parser().single)
    }
  }

  def updateById(merchantId: String, form: DeliveryForm):  Delivery = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.updateQuery( form,merchantId)).as(parser().single)
    }
  }

  def deleteById(merchantId: String): Delivery = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.deleteQuery(merchantId)).as(parser().single)
    }
  }

  object BaseQuery {

    def selectQuery(merchantId: String, id: String): String = {
      s"SELECT * FROM deliveries WHERE id = '$id' AND merchant_id = '$merchantId';"
    }

    def insertQuery(deliveryForm: DeliveryForm): String = {

      val originAddress = Json.toJson(deliveryForm.origin)(jsonWritesDeliveryAddress)
      val destinationAddress = Json.toJson(deliveryForm.destination)(jsonWritesDeliveryAddress)
      val contact = Json.toJson(deliveryForm.contactInfo)(jsonWritesDeliveryContact)

      println("++++++++++++++++++"+ originAddress)

      val date = "2023-10-31 15:00:00"
      val query =
        s"""
           |INSERT INTO deliveries(order_number, merchant_id, estimated_delivery_date, origin, destination, contact_info)
           |VALUES
           |(
           |'${deliveryForm.orderNumber}',
           |'${deliveryForm.merchantId}',
           |'$date',
           |'$originAddress',
           |'$destinationAddress',
           |'$contact');
           |""".stripMargin

      query


    }

    def updateQuery(deliveryForm: DeliveryForm, merchantId: String): String = {

      val originAddress = Json.toJson(deliveryForm.origin)(jsonWritesDeliveryAddress)
      val destinationAddress = Json.toJson(deliveryForm.destination)(jsonWritesDeliveryAddress)
      val contact = Json.toJson(deliveryForm.contactInfo)(jsonWritesDeliveryContact)

      val date = "2023-10-31 15:00:00"


      val query =
        s"""
           |UPDATE deliveries
           | SET order_number = '${deliveryForm.orderNumber}',
           |    merchant_id = '${deliveryForm.merchantId}',
           |    estimated_delivery_date = '$date',
           |    origin =  '$originAddress'
           |    destination = '{"city": "CityB", "country": "456 Elm Ave"}'::json,
           |    contact_info = '{"email": "john@example.com", "phone": "+1 (123) 456-7890"}'::json,
           |    updated_at = 'now()'
           |WHERE merchant_id = '$merchantId';
           |""".stripMargin

      query
    }

    def deleteQuery(merchantId: String): String = {
      val query =
        s"""
          |DELETE FROM deliveries
          |WHERE merchant_id = '$merchantId'
          |RETURNING *;
          |""".stripMargin

      query
    }

    def generateOrderId(): String = {
      java.util.UUID.randomUUID().toString
    }
  }
}
