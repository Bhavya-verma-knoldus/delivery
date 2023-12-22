package actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable}
import akka.dispatch.MessageDispatcher
import play.api.i18n.Lang.logger

import javax.inject.Singleton
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.Try

sealed trait PollActorMessage

object PollActorMessage {
  final case object Poll extends PollActorMessage
}

@Singleton
trait PollActor extends Actor with ActorLogging {

  import PollActorMessage._

  val system: ActorSystem = context.system
  private val pollContext: MessageDispatcher = system.dispatchers.lookup("poll-context")

  private def initialDelay: FiniteDuration = FiniteDuration(10L, SECONDS)

  def delay: FiniteDuration = FiniteDuration(10, SECONDS)

  def processRecord(): Unit

  def startPolling(): Cancellable = {
    system.scheduler.scheduleWithFixedDelay(initialDelay, delay, self, Poll)(pollContext)
  }

  override def receive: Receive = {
    case Poll =>
      logger.info("Inside receive method")
      safeProcessMessage()
  }

  private def safeProcessMessage(): Unit = {
    Try {
      logger.info("Inside safeProcessMessage method")
      processRecord()
    }.recover {
      case ex =>
        logger.info("Discontinuing with safeProcessMessage method")
        println("Error:-" + ex.getMessage)
    }
  }
}