package io.tnt.mqtt

import io.tnt.properties.MqttProperties
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.core.MqttPahoClientFactory

@Configuration
class MqttClientsConfig(
  private val p: MqttProperties
) {
  companion object {
    const val CLIENT_FACTORY_BEAN_NAME = "composeMqttClientFactoryChannel"
  }

  @Bean(name = [CLIENT_FACTORY_BEAN_NAME])
  fun mqttOutClient(): MqttPahoClientFactory {
    val factory = DefaultMqttPahoClientFactory()
    val connectOptions = MqttConnectOptions()

    factory.connectionOptions = connectOptions.also {
      it.serverURIs = arrayOf(p.url)
      it.userName = p.username
      it.password = p.password?.toCharArray()
      it.keepAliveInterval = p.keepAliveSecond.toInt()
      it.isCleanSession = false
      it.connectionTimeout = p.connectTimeoutSecond
    }

    return factory
  }

}
