package service

import com.google.inject.ImplementedBy
import com.nashtech.delivery.v1.models.{Delivery, DeliveryForm}
import dao.DAO

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}


@ImplementedBy(classOf[DeliveriesServiceImpl])
trait DeliveriesService {
  def createDelivery(delivery: DeliveryForm, merchantId: String): Either[Seq[String], Delivery]

  def getById(merchantId: String, id: String): Either[Seq[String], Delivery]

  def updateByOrderNumber(id: String, form: DeliveryForm, orderNumber: String): Either[Seq[String], Delivery]

  def deleteByOrderNumber(merchantId: String, orderNumber: String): Either[Seq[String], Delivery]

}

@Singleton
class DeliveriesServiceImpl @Inject()(db: DAO) extends DeliveriesService {

  override def getById(merchantId: String, id: String): Either[Seq[String], Delivery] = {
    Try(db.getById(merchantId, id)) match {
      case Success(value) => Right(value)
      case Failure(e) => Left(Seq(e.getMessage))
    }
  }

  def createDelivery(delivery: DeliveryForm, merchantId: String): Either[Seq[String], Delivery] = {
    Try(db.createDelivery(delivery, merchantId)) match {
      case Success(value) => Right(value)
      case Failure(e) => Left(Seq(e.getMessage))
    }
  }

  def updateByOrderNumber(id: String, form: DeliveryForm, orderNumber: String): Either[Seq[String], Delivery] = {
    Try(db.updateByOrderNumber(id, form, orderNumber)) match {
      case Success(value) => Right(value)
      case Failure(e) => Left(Seq(e.getMessage))
    }
  }

  def deleteByOrderNumber(merchantId: String, orderNumber: String): Either[Seq[String], Delivery] = {
    Try(db.deleteByOrderNumber(merchantId, orderNumber)) match {
      case Success(value) => Right(value)
      case Failure(e) => Left(Seq(e.getMessage))
    }
  }
}