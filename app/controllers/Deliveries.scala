package controllers

import com.nashtech.delivery.v1.controllers.DeliveriesController
import play.api.mvc.{AnyContent, ControllerComponents, Request}
import service.DeliveriesService
import akka.actor._

import javax.inject.{Inject, Named}
import scala.concurrent.Future

class Deliveries @Inject() (
  deliveriesService: DeliveriesService,
  val controllerComponents: ControllerComponents,
  @Named("delivery-journal-actor") actor: ActorRef
) extends DeliveriesController {
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
}
