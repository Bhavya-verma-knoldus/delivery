package controllers

import akka.actor._
import com.nashtech.delivery.v1.controllers.DeliveriesController
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import service.DeliveriesService

import javax.inject.{Inject, Named}
import scala.concurrent.Future

class Deliveries @Inject() (
  deliveriesService: DeliveriesService,
  override val controllerComponents: ControllerComponents,
  @Named("delivery-journal-actor") actor: ActorRef
) extends   AbstractController(controllerComponents)  with DeliveriesController {
  override def getById(request: Request[AnyContent], merchantId: String, id: String): Future[GetById] = {
    Future.successful(
      deliveriesService.getByid(merchantId, id) match {
        case Left(_) =>
          actor ! "Insert"
          GetById.HTTP404
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
