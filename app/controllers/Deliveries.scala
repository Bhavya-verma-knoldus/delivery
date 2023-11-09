package controllers

import akka.actor._
import com.nashtech.delivery.v1.controllers.DeliveriesController
import com.nashtech.delivery.v1.models.Error
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import service.DeliveriesService

import javax.inject.{Inject, Named}
import scala.concurrent.Future

class Deliveries @Inject()(
    deliveriesService: DeliveriesService,
    override val controllerComponents: ControllerComponents,
    @Named("delivery-journal-actor") actor: ActorRef
  ) extends AbstractController(controllerComponents) with DeliveriesController {
  override def getById(request: Request[AnyContent], merchantId: String, id: String): Future[GetById] = {
    Future.successful(
      deliveriesService.getById(merchantId, id) match {
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
   ): scala.concurrent.Future[Post] = Future.successful {
    deliveriesService.createDelivery(body) match {
      case Left(_) => Post.HTTP404
      case Right(delivery) => Post.HTTP200(delivery)
    }
  }

  override def put(
    request: play.api.mvc.Request[com.nashtech.delivery.v1.models.DeliveryForm],
    merchantId: String,
    body: com.nashtech.delivery.v1.models.DeliveryForm
  ): scala.concurrent.Future[Put] = Future.successful {
    deliveriesService.updateById(merchantId, body) match {
      case Left(_) => Put.HTTP404
      case Right(delivery) => Put.HTTP200(delivery)
    }
  }

  override def delete(
     request: play.api.mvc.Request[play.api.mvc.AnyContent],
     merchantId: String
   ): scala.concurrent.Future[Delete] = Future.successful {
    deliveriesService.deleteById(merchantId) match {
      case Left(_) => Delete.HTTP422(Error(Delete.HTTP404.toString, Seq("Record could not delete!")))
      case Right(_) => Delete.HTTP200
    }
  }
}
