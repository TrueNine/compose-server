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
package net.yan100.compose.depend.mqtt.properties

import lombok.Data
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*

@Data
@ConfigurationProperties(prefix = "compose.mqtt-client")
class SingleMqttProperties {
  /** schema = tcp://  */
  var url: String? = null

  var port = 1883
  var clientId = UUID.randomUUID().toString()
  var topics: List<String> = ArrayList()
  var username = ""
  var password = ""
  var connectTimeoutSecond = 10
  var completionTimeout = 1000L * 5L
  var qos = 0
  var keepAliveSecond = 10

  val fullUrl: String get() = "$url:$port"
}
