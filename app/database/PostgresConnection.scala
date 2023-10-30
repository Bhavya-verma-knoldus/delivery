package database
import anorm.SqlParser.str
import anorm.{RowParser, SQL, SqlParser, ~}
import com.nashtech.delivery.v1.anorm.conversions.Standard.columnToJsObject
import com.nashtech.delivery.v1.models.json.{jsonReadsDeliveryAddress, jsonReadsDeliveryContact}
import com.nashtech.delivery.v1.models.{Address, Contact, Delivery}
import play.api.db._
import play.api.libs.json._

import javax.inject.Inject

class PostgresConnection @Inject()(db: Database) {
  def getAll(sqlForD: String): Seq[Delivery] = {
    db.withConnection { implicit connection =>
      SQL(sqlForD).as(com.nashtech.delivery.v1.anorm.parsers.Delivery.parser().*)
    }
  }

  val deliveryParser: RowParser[Delivery] = {
    str("id") ~
      str("order_number") ~
      str("merchant_id") ~
      SqlParser.get[org.joda.time.DateTime]("estimated_delivery_date") ~
      SqlParser.get[play.api.libs.json.JsObject]("origin") ~
      SqlParser.get[play.api.libs.json.JsObject]("destination") ~
      SqlParser.get[play.api.libs.json.JsObject]("contact_info") map {
      case id ~ orderNumber ~ merchantId ~ estimatedDeliveryDate ~ origin ~ destination ~ contactInfo =>

        val originResult: JsResult[Address] = origin.validate[Address]
        val destinationResult : JsResult[Address] = destination.validate[Address]
        val contactResult: JsResult[Contact] = contactInfo.validate[Contact]

        (originResult,destinationResult,contactResult) match {
          case (JsSuccess(originAddress, _), JsSuccess(destinationAddress, _), JsSuccess(contact, _)) =>
            Delivery(id, orderNumber, merchantId, estimatedDeliveryDate, originAddress, destinationAddress, contact)
        }
    }
  }
}
