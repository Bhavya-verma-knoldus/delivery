package actors

import akka.actor.ActorSystem
import play.api.db.Database

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.{Success, Try}

@Singleton
class DeliveryJournalActor @Inject()(system: ActorSystem, override val db: Database)
  extends DBPollActor(table = "deliveries") {

  println("actor initialized")

  override def preStart(): Unit = {
    // super.preStart()
    log.info("[DeliveryJournalActor] Inside preStart")
    //    self ! "Insert"
  }

  def schedule() = {
    system.scheduler.scheduleWithFixedDelay(FiniteDuration(5, SECONDS), delay, self, "Insert")(system.dispatcher)
  }

  override def process(record: ProcessQueueDelivery): Try[Unit] = {
    record.operation match {
      case "INSERT" | "UPDATE" => // TODO: Publish using kinesis
        Try {
          log.info("Inside DeliveryJournalActor")
        }
      case "DELETE" => Success(())
    }
  }
}