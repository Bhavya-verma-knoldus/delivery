package controllers

import com.nashtech.delivery.v1.controllers.DeliveriesController
import com.nashtech.delivery.v1.models.Error
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import service.DeliveriesService

import javax.inject.Inject
import scala.concurrent.Future

class Deliveries @Inject() (
  deliveriesService: DeliveriesService,
  controllerComponents: ControllerComponents
//  postgresDAO: PostgresConnection
) extends   AbstractController(controllerComponents)  with DeliveriesController {
  override def getById(request: Request[AnyContent], merchantId: String, id: String): Future[GetById] = {
    Future.successful(
      deliveriesService.getByid(merchantId, id) match {
        case Left(_) => GetById.HTTP404
        case Right(delivery) => GetById.HTTP200(delivery)

      }
    )
  }

//  def index = {
////    val  deliveries_journal_query= s"SELECT * FROM deliveries_journal;"
//    val deliveries_query = s"SELECT * FROM deliveries;"
//    postgresDAO.getAll(deliveries_query)
//  }

  override def post(
     request: play.api.mvc.Request[com.nashtech.delivery.v1.models.DeliveryForm],
     merchantId: String,
     body: com.nashtech.delivery.v1.models.DeliveryForm
   ): scala.concurrent.Future[Post] = Future.successful {
    if (merchantId.isEmpty) Post.HTTP404
    else deliveriesService.createDelivery(body) match {
      case Left(_) => Post.HTTP404
      case Right(delivery) => Post.HTTP200(delivery)
    }
  }

  override def put(
     request: play.api.mvc.Request[com.nashtech.delivery.v1.models.DeliveryForm],
     merchantId: String,
     body: com.nashtech.delivery.v1.models.DeliveryForm
   ): scala.concurrent.Future[Put] = Future.successful {
    if (merchantId.isEmpty) Put.HTTP404
    else deliveriesService.updateById(merchantId, body) match {
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
