package net.yan100.compose.autoconfig

import net.yan100.compose.holders.EventPublisherHolder
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Configuration

@Configuration
class EventPublisherHolderAutoConfiguration(
  publisher: ApplicationEventPublisher
) {
  init {
    EventPublisherHolder.set(publisher)
  }
}
