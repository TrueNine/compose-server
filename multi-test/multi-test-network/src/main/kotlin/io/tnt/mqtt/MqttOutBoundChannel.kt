package io.tnt.mqtt

import io.tnt.properties.MqttProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.mqtt.core.MqttPahoClientFactory
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler
import org.springframework.messaging.MessageHandler

@Configuration
class MqttOutBoundChannel(
  private val client: MqttPahoClientFactory
) {
  @Autowired
  lateinit var p: MqttProperties


  @Bean
  fun outBoundChannel2(): DirectChannel {
    return DirectChannel()
  }

  @Bean(name = ["outBoundChannel"])
  fun outBoundChannel(): DirectChannel {
    val channel = DirectChannel()

    channel.interceptors

    return channel
  }

  @Bean(name = ["outboundMessageHandler"])
  @ServiceActivator(inputChannel = "outBoundChannel")
  fun mqttOutBound(): MessageHandler {
    val handler = MqttPahoMessageHandler(p.clientId, client)

    handler.setDefaultTopic("23333")
    //handler.setTopicExpressionString("headers['23333/#']")

    handler.setAsync(true)
    return handler
  }


}
