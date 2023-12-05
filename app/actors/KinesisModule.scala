package actors

import com.google.inject.AbstractModule

import service.{DeliveryEventConsumer, DeliveryEventProcessor, DeliveryEventProcessorFactory}

class KinesisModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[DeliveryEventProcessorFactory]).asEagerSingleton()
    bind(classOf[DeliveryEventProcessor]).asEagerSingleton()
    bind(classOf[DeliveryEventConsumer]).asEagerSingleton()
  }
}
