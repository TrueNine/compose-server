package io.tnt.mqtt

import net.yan100.compose.core.lang.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.MessageHandler

@Configuration
class MessageProducer {
  @Bean
  @ServiceActivator(inputChannel = MqttInBoundChannel.INPUT_CHANNEL_BEAN_NAME)
  fun messageHandler(): MessageHandler {
    return MessageHandler { msg -> log.info("mqtt msg = {}", msg) }
  }

  companion object {
    private val log = slf4j(MessageProducer::class)
  }
}
