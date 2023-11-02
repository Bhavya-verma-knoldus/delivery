package service

import com.google.inject.ImplementedBy
import com.nashtech.delivery.v1.models.{Delivery, DeliveryForm}
import dao.DAO

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}


@ImplementedBy(classOf[DeliveriesServiceImpl])
trait DeliveriesService {
  def createDelivery(delivery: DeliveryForm): Either[Seq[String],Delivery]

  def getById(merchantId: String, id: String): Either[Seq[String], Delivery]

  def updateById(id: String, form: DeliveryForm): Either[Seq[String], Delivery]

  def deleteById(merchantId: String):  Either[Seq[String], Delivery]

}

@Singleton
class DeliveriesServiceImpl @Inject()(db: DAO) extends DeliveriesService {
//  private val db = Connection.connection()
//
//  private var database: Map[String, Delivery] = Map(
//    "1" -> Delivery(
//      id = "1",
//      orderNumber = "1",
//      merchantId = "X",
//      estimatedDeliveryDate = DateTime.now(),
//      origin = Address(country = Some("IND")),
//      destination = Address(Some("USA")),
//      contactInfo = Contact(
//        firstName = Some("Bhavya")
//      )
//    )
//  )
  override def getById(merchantId: String, id: String): Either[Seq[String], Delivery] = {
    Try(db.getById(merchantId, id)) match {
      case Success(value) => Right(value)
      case Failure(e) => Left(Seq(e.getMessage))
    }
  }

  def createDelivery(delivery: DeliveryForm): Either[Seq[String],Delivery] = {
    Try(db.createDelivery(delivery)) match {
      case Success(value) => Right(value)
      case Failure(e) => Left(Seq(e.getMessage))
    }

  }

  def updateById(id: String, form: DeliveryForm): Either[Seq[String], Delivery] = {
    Try(db.updateById(id, form)) match {
      case Success(value) => Right(value)
      case Failure(e) => Left(Seq(e.getMessage))
    }
  }

  def deleteById(merchantId: String):Either[Seq[String], Delivery] = {
    Try(db.deleteById(merchantId)) match {
      case Success(value) => Right(value)
      case Failure(e) => Left(Seq(e.getMessage))
    }
  }
}