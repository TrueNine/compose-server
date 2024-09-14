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
package net.yan100.compose.depend.paho

import jakarta.annotation.Resource
import net.yan100.compose.depend.paho.paho.MqttPahoClientWrapper
import net.yan100.compose.depend.paho.paho.subscribe
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions


class PahoTest {

  @Resource
  lateinit var mqttClient: MqttClient

  @Resource lateinit var options: MqttConnectOptions

  @Resource lateinit var wrapper: MqttPahoClientWrapper

  // @Test
  fun testConnectClient() {
    wrapper.subscribe<Any>("order/a") { _, a -> println(a) }
  }
}
