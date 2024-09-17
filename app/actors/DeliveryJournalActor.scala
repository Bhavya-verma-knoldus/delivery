package actors

import akka.actor.{ActorSystem, Cancellable}
import play.api.db.Database
import play.api.i18n.Lang.logger
import service.DeliveryEventConsumer
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.{Success, Try}

@Singleton
class DeliveryJournalActor @Inject()(system: ActorSystem,
                                     deliveryEventConsumer: DeliveryEventConsumer,
                                     override val db: Database)
  extends DBPollActor(table = "deliveries") {

  println("actor initialized")

  override def preStart(): Unit = {
    logger.info("[DeliveryJournalActor] Inside preStart")
    startPolling()
  }

  def schedule(): Cancellable = {
    system.scheduler.scheduleWithFixedDelay(FiniteDuration(5, SECONDS), delay, self, "INSERT")(system.dispatcher)
  }

  override def process[T](record: T): Unit = {
    record match {
      case processQueueDelivery: ProcessQueueDelivery =>
        processQueueDelivery.operation match {
          case "INSERT" | "UPDATE" =>
            deliveryEventConsumer.initialize()
          //throw new ArithmeticException("Error occur")
          case "DELETE" => Success(())
        }
    }
  }
}