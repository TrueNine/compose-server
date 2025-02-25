package net.yan100.compose.depend.paho.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.slf4j
import net.yan100.compose.depend.paho.paho.MqttPahoClientWrapper
import net.yan100.compose.depend.paho.properties.SingleMqttProperties
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
class SingleMqttConnectionAutoConfiguration(
  private val p: SingleMqttProperties,
  private val objectMapper: ObjectMapper,
) {

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
      log.trace("等待创建 mqtt 连接 client = {}", client)
      client.connect(options)
      log.trace("创建完成 mqtt 连接 client = {}", client)
    } else {
      log.trace("客户端已连接")
      client.disconnect()
      client.connect(options)
    }

    return client
  }

  @Bean
  fun mqttConnectionOptions(
    factory: MqttPahoClientFactory
  ): MqttConnectOptions? {
    return factory.connectionOptions
  }

  @Bean
  @ConditionalOnBean(name = [CLIENT_FACTORY_BEAN_NAME])
  fun mqttPahoClientWrapper(
    factory: MqttPahoClientFactory
  ): MqttPahoClientWrapper {
    val client = factory.getClientInstance(p.fullUrl, p.clientId)
    return MqttPahoClientWrapper(
      client,
      factory.connectionOptions,
      objectMapper,
    )
  }

  companion object {
    const val CLIENT_FACTORY_BEAN_NAME = "composeMqttClientFactoryChannel"
    const val MQTT_PAHO_CLIENT_BEAN_NAME = "mqttPahoClientBeanName"
  }
}
