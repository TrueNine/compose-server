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
package net.yan100.compose.depend.paho.properties


import net.yan100.compose.core.i32
import net.yan100.compose.core.i64
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*

private const val PREFIX = "compose.depend.paho"


@ConfigurationProperties(prefix = "$PREFIX.client")
data class SingleMqttProperties(
  /** schema = tcp://  */
  var url: String? = null,
  var port: i32 = 1883,
  var clientId: String = UUID.randomUUID().toString(),
  var topics: MutableList<String> = ArrayList(),
  var username: String = "",
  var password: String = "",
  var connectTimeoutSecond: i32 = 10,
  var completionTimeout: i64 = 1000L * 5L,
  var qos: i32 = 0,
  var keepAliveSecond: i32 = 10
) {
  val fullUrl: String get() = "$url:$port"
}
