package io.github.truenine.composeserver.depend.paho.paho

import org.eclipse.paho.client.mqttv3.IMqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import tools.jackson.databind.ObjectMapper
import java.io.Closeable
import kotlin.reflect.KClass

class MqttPahoClientWrapper(private val client: IMqttClient, private val options: MqttConnectOptions, private val objectMapper: ObjectMapper) : Closeable {
  val isConnected: Boolean
    get() = client.isConnected

  init {
    connect()
  }

  fun connect() = connect(options)

  fun connect(connectOptions: MqttConnectOptions) {
    if (!isConnected) client.connect(connectOptions)
  }

  private val mapper: ObjectMapper
    get() = objectMapper

  fun unsubscribe(vararg topic: String) {
    client.unsubscribe(topic)
  }

  fun unsubscribe(topic: String) {
    if (isConnected) client.unsubscribe(topic)
  }

  fun <T : Any> subscribe(topic: String, qos: Int = 0, type: KClass<T>, callback: (callbackTopic: String, message: T) -> Unit) {
    connect()
    return client.subscribe(topic, qos) { _, message ->
      val payload = mapper.readValue(message.payload, type.java)
      callback(topic, payload)
    }
  }

  fun reConnect() {
    if (isConnected) disconnect()
    connect()
  }

  fun disconnect() {
    client.disconnect()
  }

  override fun close() {
    if (isConnected) client.close()
  }
}

inline fun <reified T : Any> MqttPahoClientWrapper.subscribe(topic: String, qos: Int = 0, noinline callback: (callbackTopic: String, message: T) -> Unit) {
  return subscribe(topic, qos, T::class, callback)
}
