package controllers

import com.nashtech.delivery.v1.controllers.DeliveriesController
import play.api.mvc.{AnyContent, ControllerComponents, Request}
import service.DeliveriesService

import javax.inject.Inject
import scala.concurrent.Future

class Deliveries @Inject() (
  deliveriesService: DeliveriesService,
  val controllerComponents: ControllerComponents,
) extends DeliveriesController {
  override def getById(request: Request[AnyContent], merchantId: String, id: String): Future[GetById] = {
    Future.successful(
      deliveriesService.getByid(merchantId, id) match {
        case Left(_) => GetById.HTTP404
        case Right(delivery) => GetById.HTTP200(delivery)
      }
    )
  }

  override def post(
                     request: play.api.mvc.Request[com.nashtech.delivery.v1.models.DeliveryForm],
                     merchantId: String,
                     body: com.nashtech.delivery.v1.models.DeliveryForm
                   ): scala.concurrent.Future[Post] = ???
}
