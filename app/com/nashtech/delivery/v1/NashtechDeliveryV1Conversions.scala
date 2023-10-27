/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 1.0.1
 * apibuilder app.apibuilder.io/nashtech/delivery/latest/anorm_2_6_parsers
 */
package com.nashtech.delivery.v1.anorm.conversions {

  import anorm.{Column, MetaDataItem, TypeDoesNotMatch}

  import scala.util.{Failure, Success, Try}
//  import play.api.libs.json.JodaReads._

  /**
    * Conversions to collections of objects using JSON.
    */
  object Util {

    def parser[T](
      f: play.api.libs.json.JsValue => T
    ) = anorm.Column.nonNull { (value, meta) =>
      val MetaDataItem(columnName, nullable, clazz) = meta
      value match {
        case json: org.postgresql.util.PGobject => parseJson(f, columnName.qualified, json.getValue)
        case json: java.lang.String => parseJson(f, columnName.qualified, json)
        case _=> {
          Left(
            TypeDoesNotMatch(
              s"Column[${columnName.qualified}] error converting $value to Json. Expected instance of type[org.postgresql.util.PGobject] and not[${value.asInstanceOf[AnyRef].getClass}]"
            )
          )
        }


      }
    }

    private[this] def parseJson[T](f: play.api.libs.json.JsValue => T, columnName: String, value: String) = {
      Try {
        f(
          play.api.libs.json.Json.parse(value)
        )
      } match {
        case Success(result) => Right(result)
        case Failure(ex) => Left(
          TypeDoesNotMatch(
            s"Column[$columnName] error parsing json $value: $ex"
          )
        )
      }
    }

  }

//  import play.api.libs.json._
//
//     object JodaDateTimeReads extends Reads[org.joda.time.DateTime] {
//      def reads(json: JsValue): JsResult[org.joda.time.DateTime] = {
//        json.validate[Long].map { millis =>
//          new org.joda.time.DateTime(millis)
//        }
//      }
//    }
//
//     object JodaDateTimeWrites extends Writes[org.joda.time.DateTime] {
//      def writes(dateTime: org.joda.time.DateTime): JsValue = {
//        Json.toJson(dateTime.getMillis)
//      }
//    }
//
//    implicit val mapDateTimeReads: Reads[Map[String, org.joda.time.DateTime]] = Reads.map(JodaDateTimeReads)
//    implicit val mapDateTimeWrites: Writes[Map[String, org.joda.time.DateTime]] = Writes.map(JodaDateTimeWrites)


  object Types {
    import com.nashtech.delivery.v1.models.json._
    implicit val columnToSeqDeliveryAddress: Column[Seq[_root_.com.nashtech.delivery.v1.models.Address]] = Util.parser { _.as[Seq[_root_.com.nashtech.delivery.v1.models.Address]] }
    implicit val columnToMapDeliveryAddress: Column[Map[String, _root_.com.nashtech.delivery.v1.models.Address]] = Util.parser { _.as[Map[String, _root_.com.nashtech.delivery.v1.models.Address]] }
    implicit val columnToSeqDeliveryContact: Column[Seq[_root_.com.nashtech.delivery.v1.models.Contact]] = Util.parser { _.as[Seq[_root_.com.nashtech.delivery.v1.models.Contact]] }
    implicit val columnToMapDeliveryContact: Column[Map[String, _root_.com.nashtech.delivery.v1.models.Contact]] = Util.parser { _.as[Map[String, _root_.com.nashtech.delivery.v1.models.Contact]] }
    implicit val columnToSeqDeliveryDelivery: Column[Seq[_root_.com.nashtech.delivery.v1.models.Delivery]] = Util.parser { _.as[Seq[_root_.com.nashtech.delivery.v1.models.Delivery]] }
    implicit val columnToMapDeliveryDelivery: Column[Map[String, _root_.com.nashtech.delivery.v1.models.Delivery]] = Util.parser { _.as[Map[String, _root_.com.nashtech.delivery.v1.models.Delivery]] }
    implicit val columnToSeqDeliveryDeliveryForm: Column[Seq[_root_.com.nashtech.delivery.v1.models.DeliveryForm]] = Util.parser { _.as[Seq[_root_.com.nashtech.delivery.v1.models.DeliveryForm]] }
    implicit val columnToMapDeliveryDeliveryForm: Column[Map[String, _root_.com.nashtech.delivery.v1.models.DeliveryForm]] = Util.parser { _.as[Map[String, _root_.com.nashtech.delivery.v1.models.DeliveryForm]] }
    implicit val columnToSeqDeliveryError: Column[Seq[_root_.com.nashtech.delivery.v1.models.Error]] = Util.parser { _.as[Seq[_root_.com.nashtech.delivery.v1.models.Error]] }
    implicit val columnToMapDeliveryError: Column[Map[String, _root_.com.nashtech.delivery.v1.models.Error]] = Util.parser { _.as[Map[String, _root_.com.nashtech.delivery.v1.models.Error]] }
  }

  object Standard {
    implicit val columnToJsObject: Column[play.api.libs.json.JsObject] = Util.parser { _.as[play.api.libs.json.JsObject] }
    implicit val columnToJsValue: Column[play.api.libs.json.JsValue] = Util.parser { _.as[play.api.libs.json.JsValue] }
    implicit val columnToSeqBoolean: Column[Seq[Boolean]] = Util.parser { _.as[Seq[Boolean]] }
    implicit val columnToMapBoolean: Column[Map[String, Boolean]] = Util.parser { _.as[Map[String, Boolean]] }
    implicit val columnToSeqDouble: Column[Seq[Double]] = Util.parser { _.as[Seq[Double]] }
    implicit val columnToMapDouble: Column[Map[String, Double]] = Util.parser { _.as[Map[String, Double]] }
    implicit val columnToSeqInt: Column[Seq[Int]] = Util.parser { _.as[Seq[Int]] }
    implicit val columnToMapInt: Column[Map[String, Int]] = Util.parser { _.as[Map[String, Int]] }
    implicit val columnToSeqLong: Column[Seq[Long]] = Util.parser { _.as[Seq[Long]] }
    implicit val columnToMapLong: Column[Map[String, Long]] = Util.parser { _.as[Map[String, Long]] }
//    implicit val columnToSeqLocalDate: Column[Seq[_root_.org.joda.time.LocalDate]] = Util.parser { _.as[Seq[_root_.org.joda.time.LocalDate]] }
//    implicit val columnToMapLocalDate: Column[Map[String, _root_.org.joda.time.LocalDate]] = Util.parser { _.as[Map[String, _root_.org.joda.time.LocalDate]] }
//    implicit val columnToSeqDateTime: Column[Seq[_root_.org.joda.time.DateTime]] = Util.parser { _.as[Seq[_root_.org.joda.time.DateTime]] }
//    implicit val columnToMapDateTime: Column[Map[String, _root_.org.joda.time.DateTime]] = Util.parser { _.as[Map[String, _root_.org.joda.time.DateTime]] }
    implicit val columnToSeqBigDecimal: Column[Seq[BigDecimal]] = Util.parser { _.as[Seq[BigDecimal]] }
    implicit val columnToMapBigDecimal: Column[Map[String, BigDecimal]] = Util.parser { _.as[Map[String, BigDecimal]] }
    implicit val columnToSeqJsObject: Column[Seq[_root_.play.api.libs.json.JsObject]] = Util.parser { _.as[Seq[_root_.play.api.libs.json.JsObject]] }
    implicit val columnToMapJsObject: Column[Map[String, _root_.play.api.libs.json.JsObject]] = Util.parser { _.as[Map[String, _root_.play.api.libs.json.JsObject]] }
    implicit val columnToSeqJsValue: Column[Seq[_root_.play.api.libs.json.JsValue]] = Util.parser { _.as[Seq[_root_.play.api.libs.json.JsValue]] }
    implicit val columnToMapJsValue: Column[Map[String, _root_.play.api.libs.json.JsValue]] = Util.parser { _.as[Map[String, _root_.play.api.libs.json.JsValue]] }
    implicit val columnToSeqString: Column[Seq[String]] = Util.parser { _.as[Seq[String]] }
    implicit val columnToMapString: Column[Map[String, String]] = Util.parser { _.as[Map[String, String]] }
    implicit val columnToSeqUUID: Column[Seq[_root_.java.util.UUID]] = Util.parser { _.as[Seq[_root_.java.util.UUID]] }
    implicit val columnToMapUUID: Column[Map[String, _root_.java.util.UUID]] = Util.parser { _.as[Map[String, _root_.java.util.UUID]] }
  }

}