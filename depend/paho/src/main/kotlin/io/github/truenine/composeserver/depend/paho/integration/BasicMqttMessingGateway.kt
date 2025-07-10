package io.github.truenine.composeserver.depend.paho.integration

import io.github.truenine.composeserver.replaceFirstIfPrefix
import org.springframework.integration.mqtt.support.MqttHeaders
import org.springframework.messaging.handler.annotation.Header

/**
 * mqtt 基础消息发送器
 *
 * @author TrueNine
 * @since 2023-10-15
 */
interface BasicMqttMessingGateway {
  /**
   * 将消息发送到默认主题，如果存在
   *
   * @param data 发送数据
   */
  fun <T> send(data: T)

  /**
   * 将消息发送到指定主题
   *
   * @param topic 主题
   * @param data 发送数据
   */
  fun <T> unsafeSendToTopic(@Header(MqttHeaders.TOPIC) topic: String, data: T)

  /**
   * 将消息发送到指定主题
   *
   * @param topic 主题
   * @param data 发送数据
   */
  fun <T> sendToTopic(@Header(MqttHeaders.TOPIC) topic: String, data: T) {
    return unsafeSendToTopic(topic.replaceFirstIfPrefix("/", ""), data)
  }
}
