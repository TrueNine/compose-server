/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
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
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.depend.mqtt.integration

import net.yan100.compose.core.extensionfunctions.replaceFirstX
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
    return unsafeSendToTopic(topic.replaceFirstX("/", ""), data)
  }
}
