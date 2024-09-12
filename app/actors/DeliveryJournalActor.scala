package actors

import actors.DBPollActor.{baseQuery, processingQueueDeliveryParser}
import akka.actor.{ActorSystem, Cancellable}
import anorm.SQL
import play.api.db.Database
import play.api.i18n.Lang.logger
import service.DeliveryEventConsumer

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.{Failure, Success, Try}
@Singleton
class DeliveryJournalActor @Inject()(system: ActorSystem,
                                     deliveryEventConsumer: DeliveryEventConsumer,
                                     override val db: Database)
  extends DBPollActor[ProcessQueueDelivery](table = "deliveries") {

  println("actor initialized")

  override def preStart(): Unit = {
    logger.info("[DeliveryJournalActor] Inside preStart")
    startPolling()
  }

  def schedule(): Cancellable = {
    system.scheduler.scheduleWithFixedDelay(FiniteDuration(5, SECONDS), delay, self, "INSERT")(system.dispatcher)
  }

   def safeProcessRecord(record: ProcessQueueDelivery): Unit = {
    Try {
      logger.info("Inside safeProcessRecord method")
      process(record)

    } match {
      case Success(_) =>
        logger.info("Continuing with safeProcessRecord method")
        deleteProcessingQueueRecord(record.processingQueueId)
        insertJournalRecord(record)
      case Failure(ex) =>
        logger.info("Discontinuing with safeProcessRecord method")
        setErrors(record.processingQueueId, ex)
    }
  }

  def getEarliestRecord(processingTable: String): ProcessQueueDelivery = {
    db.withConnection { implicit connection =>
      SQL(baseQuery(processingTable)).as(processingQueueDeliveryParser().single)
    }
  }

  override def process(record: ProcessQueueDelivery): Unit = {
    record.operation match {
      case "INSERT" | "UPDATE" =>
        deliveryEventConsumer.initialize()
      case "DELETE" => Success(())
      case _ => logger.error("Type did not match")
    }
  }
}