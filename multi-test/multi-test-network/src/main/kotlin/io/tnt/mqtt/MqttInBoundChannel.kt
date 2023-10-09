package io.tnt.mqtt

import io.tnt.properties.MqttProperties
import net.yan100.compose.core.lang.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.core.MessageProducer
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlowBuilder
import org.springframework.integration.mqtt.core.MqttPahoClientFactory
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter
import org.springframework.messaging.MessageHandler


@Configuration
class MqttInBoundChannel(
  private val p: MqttProperties,
  private val client: MqttPahoClientFactory
) {

  @Bean
  fun messageProducer(): MessageProducer {
    val topics = p.topics.toTypedArray()
    val adapter = MqttPahoMessageDrivenChannelAdapter(p.clientId, client, *topics)

    adapter.setQos(p.qos)
    adapter.setCompletionTimeout(p.completionTimeout)
    adapter.setConverter(DefaultPahoMessageConverter())
    adapter.outputChannel = mqttInputChannel()

    return adapter
  }

  @Bean(name = [INPUT_CHANNEL_BEAN_NAME])
  fun mqttInputChannel(): DirectChannel {
    return DirectChannel()
  }

  companion object {
    private val log = slf4j(MqttInBoundChannel::class)
    const val INPUT_CHANNEL_BEAN_NAME = "mqttInputChannel"
  }
}
