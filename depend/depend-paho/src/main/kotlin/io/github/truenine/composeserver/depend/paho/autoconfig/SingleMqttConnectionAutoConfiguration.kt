package io.github.truenine.composeserver.depend.paho.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.paho.paho.MqttPahoClientWrapper
import io.github.truenine.composeserver.depend.paho.properties.SingleMqttProperties
import io.github.truenine.composeserver.slf4j
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.core.MqttPahoClientFactory

private val log = slf4j(SingleMqttConnectionAutoConfiguration::class)

@Configuration
class SingleMqttConnectionAutoConfiguration(private val p: SingleMqttProperties, private val objectMapper: ObjectMapper) {

  @ConditionalOnMissingBean
  @Bean(name = [CLIENT_FACTORY_BEAN_NAME])
  fun mqttOutClient(): MqttPahoClientFactory {
    val factory = DefaultMqttPahoClientFactory()
    val connectOptions = MqttConnectOptions()

    factory.connectionOptions =
      connectOptions.also {
        it.serverURIs = arrayOf(p.fullUrl)
        it.userName = p.username
        it.password = p.password.toCharArray()
        it.keepAliveInterval = p.keepAliveSecond.toInt()
        it.isCleanSession = false
        it.connectionTimeout = p.connectTimeoutSecond
      }

    return factory
  }

  @ConditionalOnMissingBean
  @Bean(name = [MQTT_PAHO_CLIENT_BEAN_NAME])
  fun mqttClient(): MqttClient {
    val client = MqttClient(p.fullUrl, p.clientId, MemoryPersistence())

    val options = MqttConnectOptions()
    options.userName = p.username
    options.serverURIs = arrayOf(p.fullUrl)
    options.password = p.password.toCharArray()
    options.connectionTimeout = p.connectTimeoutSecond
    options.keepAliveInterval = p.keepAliveSecond
    options.mqttVersion = 3
    if (!client.isConnected) {
      log.trace("Waiting to create MQTT connection for client = {}", client)
      client.connect(options)
      log.trace("MQTT connection created for client = {}", client)
    } else {
      log.trace("Client is already connected")
      client.disconnect()
      client.connect(options)
    }

    return client
  }

  @Bean
  fun mqttConnectionOptions(factory: MqttPahoClientFactory): MqttConnectOptions? {
    return factory.connectionOptions
  }

  @Bean
  @ConditionalOnBean(name = [CLIENT_FACTORY_BEAN_NAME])
  fun mqttPahoClientWrapper(factory: MqttPahoClientFactory): MqttPahoClientWrapper {
    val client = factory.getClientInstance(p.fullUrl, p.clientId)
    return MqttPahoClientWrapper(client, factory.connectionOptions, objectMapper)
  }

  companion object {
    const val CLIENT_FACTORY_BEAN_NAME = "composeMqttClientFactoryChannel"
    const val MQTT_PAHO_CLIENT_BEAN_NAME = "mqttPahoClientBeanName"
  }
}
