package net.yan100.compose.depend.mqtt.autoconfig

import net.yan100.compose.depend.mqtt.properties.SingleMqttProperties
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.core.MqttPahoClientFactory

@Configuration
class SingleMqttConnectionAutoConfiguration(
  private val p: SingleMqttProperties
) {

  @ConditionalOnMissingBean
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

  companion object {
    const val CLIENT_FACTORY_BEAN_NAME = "composeMqttClientFactoryChannel"
  }
}
