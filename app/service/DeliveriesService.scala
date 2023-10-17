package service

import com.google.inject.ImplementedBy
import com.nashtech.delivery.v1.models.{Address, Contact, Delivery}
import db.Connection
import doa.DAO
import org.joda.time.DateTime

import javax.inject.Singleton
import scala.concurrent.Future

//import play.api.db.Database

@ImplementedBy(classOf[DeliveriesServiceImpl])
trait DeliveriesService {
  def getByid(merchantId: String, id: String): Either[Seq[String], Delivery]
}

@Singleton
class DeliveriesServiceImpl extends DAO {
//  private val db = Connection.connection()

  private val db: Map[String, Delivery] = Map(
    "1" -> Delivery(
      id = "1",
      orderNumber = "1",
      merchantId = "X",
      estimatedDeliveryDate = DateTime.now(),
      origin = Address(country = Some("IND")),
      destination = Address(Some("USA")),
      contactInfo = Contact(
        firstName = Some("Bhavya")
      )
    )
  )
  override def getByid(merchantId: String, id: String): Either[Seq[String], Delivery] = {
    db.get(id) match {
      case Some(delivery) => Right(delivery)
      case None => Left(Seq("Delivery Not Found"))
    }
  }

  def addUser(delivery: Delivery): Future[String] = ???

  def getAll: Either[Seq[String], Delivery] = ???

  def updateById(merchantId: String, id: String): Either[Seq[String], Delivery] = ???

  def deleteById(merchantId: String): Future[String] = ???

  def deleteAll(): Future[String] = ???
}