package actors

import com.nashtech.delivery.v1.models.{Address, Contact, Delivery}
import com.nashtech.order.v1.models.Order
import org.joda.time.DateTime
import play.api.db.Database
import play.api.i18n.Lang.logger

import javax.inject.{Inject, Singleton}

case class ProcessRecord(order: Order)

@Singleton
class OrderActor @Inject() (override val db: Database) extends DBPollActor(table = "ec_orders") {

  private val originAddress = Address(city = Some("Noida"), province = Some("Uttar Pradesh"), postal = Some("201301"), country = Some("India"))

  private val destinationAddress = Address(city = Some("Kanpur"), province = Some("Uttar Pradesh"), postal = Some("201206"), country = Some("India"))

  private val contactInfo = Contact(firstName = Some("John"), lastName = Some("Singh"), mobileNumber = Some("8090XXX110"))

  private val currentDate = DateTime.now()

  println("actor initialized")

  override def preStart(): Unit = {
    logger.info("[Order Actor] Inside preStart")
    startPolling()
  }


  override def receive: Receive = {
    case ProcessRecord(order) => convertOrderToDelivery(order)
  }

  private def convertOrderToDelivery(order: Order) : Delivery = {
    Delivery(id = order.id,
      orderNumber = order.number,
      merchantId = order.merchantId,
      estimatedDeliveryDate = currentDate.plusDays(3),
      origin = originAddress,
      destination = destinationAddress,
      contactInfo = contactInfo
    )

  }

  override def process(record: ProcessQueueDelivery): Unit = ???
}
