/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.depend.mqtt.paho

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.Closeable
import kotlin.reflect.KClass
import org.eclipse.paho.client.mqttv3.IMqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions

class MqttPahoClientWrapper(
  private val client: IMqttClient,
  private val options: MqttConnectOptions,
  private val objectMapper: ObjectMapper
) : Closeable {
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

  fun <T : Any> subscribe(
    topic: String,
    qos: Int = 0,
    type: KClass<T>,
    callback: (callbackTopic: String, message: T) -> Unit
  ) {
    connect()
    return client.subscribe(topic, qos) { t, message ->
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

inline fun <reified T : Any> MqttPahoClientWrapper.subscribe(
  topic: String,
  qos: Int = 0,
  noinline callback: (callbackTopic: String, message: T) -> Unit
) {
  return subscribe(topic, qos, T::class, callback)
}
