import com.nashtech.delivery.v1.controllers.DeliveriesController
import com.nashtech.delivery.v1.models.{Address, Contact, Delivery}
import org.joda.time.DateTime
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc._

import scala.concurrent.Future

class DeliveriesControllerSpec extends AsyncWordSpec with Matchers with ScalaFutures with MockitoSugar {

  val address: Address = Address(
    text = Some("123 Main St"),
    streets = Some(Seq("Main St", "Broadway")),
    streetNumber = Some("123"),
    city = Some("New York"),
    province = Some("NY"),
    postal = Some("10001"),
    country = Some("USA"),
    latitude = Some("40.7128"),
    longitude = Some("-74.0060")
  )

  val contact: Contact = Contact(
    emailId = Some("john.doe@example.com"),
    lastName = Some("Doe"),
    firstName = Some("John"),
    mobileNumber = Some("1234567890")
  )
  val delivery: Delivery = Delivery(
    id = "9074",
    orderNumber = "9c658627-5733-4f0b-8f1f-36e5ff456131",
    merchantId = "MERC-2",
    estimatedDeliveryDate = DateTime.now(),
    origin = address,
    destination = address,
    contactInfo = contact
  )

  val mockDeliveriesController: DeliveriesController = mock[DeliveriesController]

  "DeliveriesController getById" should {
    "return HTTP 200 with body if GetById.HTTP200" in {
      val merchantId = "someMerchantId"
      val id = "someId"
      val expectedBody = delivery

      val mockRequest = mock[Request[AnyContent]]
      when(mockDeliveriesController.getById(any[Request[AnyContent]], merchantId, id))
        .thenReturn(Future.successful(mockDeliveriesController.GetById.HTTP200(expectedBody)))

      val result: Future[mockDeliveriesController.GetById] = mockDeliveriesController.getById(mockRequest, merchantId, id)

      result.map { getByIdResult =>
        getByIdResult shouldBe mockDeliveriesController.GetById.HTTP200(expectedBody)
      }
    }
  }
}
