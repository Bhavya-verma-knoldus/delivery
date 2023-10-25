package controllers

import com.nashtech.delivery.v1.controllers.DeliveriesController
import database.PostgresConnection
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import service.DeliveriesService

import javax.inject.Inject
import scala.concurrent.Future

class Deliveries @Inject() (
  deliveriesService: DeliveriesService,
  controllerComponents: ControllerComponents,
  postgresDAO: PostgresConnection
) extends   AbstractController(controllerComponents)  with DeliveriesController {
  override def getById(request: Request[AnyContent], merchantId: String, id: String): Future[GetById] = {
    Future.successful(
      deliveriesService.getByid(merchantId, id) match {
        case Left(_) => GetById.HTTP404
        case Right(delivery) =>
          index
          GetById.HTTP200(delivery)

      }
    )
  }

  def index = {
//    val  deliveries_journal_query= s"SELECT * FROM deliveries_journal;"
    val deliveries_query = s"SELECT * FROM deliveries;"
    postgresDAO.getAll(deliveries_query)


  }

  override def post(
                     request: play.api.mvc.Request[com.nashtech.delivery.v1.models.DeliveryForm],
                     merchantId: String,
                     body: com.nashtech.delivery.v1.models.DeliveryForm
                   ): scala.concurrent.Future[Post] = ???
}
