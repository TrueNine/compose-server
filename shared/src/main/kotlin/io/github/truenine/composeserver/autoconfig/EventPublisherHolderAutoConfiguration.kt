package io.github.truenine.composeserver.autoconfig

import io.github.truenine.composeserver.holders.EventPublisherHolder
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Configuration

@Configuration
class EventPublisherHolderAutoConfiguration(publisher: ApplicationEventPublisher) {
  init {
    EventPublisherHolder.set(publisher)
  }
}
