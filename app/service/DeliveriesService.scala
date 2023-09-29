package service

import com.google.inject.ImplementedBy
import com.nashtech.delivery.v1.models.{Address, Contact, Delivery}
import org.joda.time.DateTime

import javax.inject.Singleton

@ImplementedBy(classOf[DeliveriesServiceImpl])
trait DeliveriesService {
  def getByid(merchantId: String, id: String): Either[Seq[String], Delivery]
}

@Singleton
class DeliveriesServiceImpl extends DeliveriesService {
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
}