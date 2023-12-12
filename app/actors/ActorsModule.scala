package actors

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class ActorsModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    // bindActor[PollActor]("poll-actor", ActorsModule.withIoDispatcher)
    // bindActor[DBPollActor]("db-poll-actor", ActorsModule.withIoDispatcher)
    bindActor[DeliveryJournalActor]("delivery-journal-actor")
  }
}