package actors

import akka.actor.ActorSystem

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.{Success, Try}

@Singleton
class DeliveryJournalActor @Inject()(system: ActorSystem)
  extends DBPollActor(table = "deliveries") {

  println("actor initialized")

  override def preStart(): Unit = {
    // super.preStart()
    println("[DeliveryJournalActor] Inside preStart")
    log.info("[DeliveryJournalActor] Inside preStart")
//    self ! "Insert"
  }

  def schedule() = {
    system.scheduler.scheduleWithFixedDelay(FiniteDuration(5, SECONDS), delay, self, "Insert")(system.dispatcher)
  }

  override def process(record: ProcessQueueOrder): Try[Unit] = {
    record.operation match {
      case "Insert" | "Update" => // TODO: Publish using kinesis
        Try {
          log.info("Inside DeliveryJournalActor")
        }
      case "Delete" => Success(())
    }
  }
}