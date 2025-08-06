package io.github.truenine.composeserver.depend.paho.properties

import java.util.*
import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.depend.paho"

@ConfigurationProperties(prefix = "$PREFIX.client")
data class SingleMqttProperties(
  /** schema = tcp:// */
  var url: String? = null,
  var port: Int = 1883,
  var clientId: String = UUID.randomUUID().toString(),
  var topics: MutableList<String> = ArrayList(),
  var username: String = "",
  var password: String = "",
  var connectTimeoutSecond: Int = 10,
  var completionTimeout: Long = 1000L * 5L,
  var qos: Int = 0,
  var keepAliveSecond: Int = 10,
) {
  val fullUrl: String
    get() = "$url:$port"
}
