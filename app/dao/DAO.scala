package dao

import anorm.SQL
import com.nashtech.delivery.v1.anorm.parsers.Delivery.parser
import com.nashtech.delivery.v1.models.{Delivery, DeliveryForm}
import play.api.db._

import javax.inject.Inject

class DAO @Inject()(db: Database) {

  def createDelivery(delivery: DeliveryForm): Delivery = {
    db.withConnection { implicit connection =>
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

  def deleteById(id: String): Delivery = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.deleteQuery(id)).as(parser().single)
    }
  }

  object BaseQuery {

    def selectQuery(merchantId: String, id: String): String = {
      s"SELECT * FROM deliveries WHERE id = '$id' AND merchant_id = '$merchantId';"
    }

    def insertQuery(deliveryForm: DeliveryForm): String = {
      val id = generateOrderId()
      val query =
        s"""
           |INSERT INTO deliveries
           |(
           |id,
           |order_number,
           |merchant_id,
           |estimated_delivery_date,
           |origin,
           |destination,
           |contact_info
           |)
           |VALUES
           |(
           |$id,
           |${deliveryForm.orderNumber},
           |${deliveryForm.merchantId},
           |now(),
           |${deliveryForm.origin},
           |${deliveryForm.destination},
           |${deliveryForm.contactInfo};
           |""".stripMargin

      query
    }

    def updateQuery(deliveryForm: DeliveryForm, id: String): String = {
      val query =
        s"""
          |UPDATE deliveries
          |    SET order_number = ${deliveryForm.orderNumber},
          |        merchant_id = ${deliveryForm.merchantId},
          |        estimated_delivery_date = now(),
          |        origin = ${deliveryForm.origin},
          |        destination = ${deliveryForm.destination},
          |        contact_info = ${deliveryForm.contactInfo},
          |        updated_at = now()
          |     WHERE id = $id;
          |""".stripMargin

      query
    }

    def deleteQuery(id: String): String = {
      val query =
        s"""
          |DELETE FROM deliveries
          |WHERE id = $id
          |RETURNING *;
          |""".stripMargin

      query
    }

    def generateOrderId(): String = {
      java.util.UUID.randomUUID().toString
    }
  }

}
