package controllers

import akka.actor._
import com.nashtech.delivery.v1.controllers.DeliveriesController
import com.nashtech.delivery.v1.models.Error
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import service.{DeliveriesService, DeliveryEventConsumer}

import javax.inject.{Inject, Named}
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class Deliveries @Inject()(
    deliveriesService: DeliveriesService,
    override val controllerComponents: ControllerComponents,
    @Named("delivery-journal-actor") actor: ActorRef
  ) extends AbstractController(controllerComponents) with DeliveriesController {
  override def getById(request: Request[AnyContent], merchantId: String, id: String): Future[GetById] = {
    Future.successful(
      deliveriesService.getById(merchantId, id) match {
        case Left(_) =>
          GetById.HTTP404
        case Right(delivery) =>
          GetById.HTTP200(delivery)
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

  override def putByOrderNumber(
    request: play.api.mvc.Request[com.nashtech.delivery.v1.models.DeliveryForm],
    merchantId: String,
    orderNumber: String,
    body: com.nashtech.delivery.v1.models.DeliveryForm
  ): scala.concurrent.Future[PutByOrderNumber] = Future.successful {
    deliveriesService.updateById(merchantId, body, orderNumber) match {
      case Left(_) => PutByOrderNumber.HTTP404
      case Right(delivery) => PutByOrderNumber.HTTP200(delivery)
    }
  }

  override def deleteByOrderNumber(
     request: play.api.mvc.Request[play.api.mvc.AnyContent],
     merchantId: String,
     orderNumber: String
   ): scala.concurrent.Future[DeleteByOrderNumber] = Future.successful {
    deliveriesService.deleteById(merchantId, orderNumber) match {
      case Left(_) => DeleteByOrderNumber.HTTP422(Error(DeleteByOrderNumber.HTTP404.toString, Seq("Record could not delete!")))
      case Right(_) => DeleteByOrderNumber.HTTP200
    }
  }
}
