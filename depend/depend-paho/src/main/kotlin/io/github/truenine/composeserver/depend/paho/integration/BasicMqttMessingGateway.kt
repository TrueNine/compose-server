package io.github.truenine.composeserver.depend.paho.integration

import io.github.truenine.composeserver.replaceFirstIfPrefix
import org.springframework.integration.mqtt.support.MqttHeaders
import org.springframework.messaging.handler.annotation.Header

/**
 * Basic MQTT message sender.
 *
 * @author TrueNine
 * @since 2023-10-15
 */
interface BasicMqttMessingGateway {
  /**
   * Sends a message to the default topic, if it exists.
   *
   * @param data The data to be sent.
   */
  fun <T> send(data: T)

  /**
   * Sends a message to the specified topic.
   *
   * @param topic The target topic.
   * @param data The data to be sent.
   */
  fun <T> unsafeSendToTopic(@Header(MqttHeaders.TOPIC) topic: String, data: T)

  /**
   * Sends a message to the specified topic.
   *
   * @param topic The target topic.
   * @param data The data to be sent.
   */
  fun <T> sendToTopic(@Header(MqttHeaders.TOPIC) topic: String, data: T) {
    return unsafeSendToTopic(topic.replaceFirstIfPrefix("/", ""), data)
  }
}
