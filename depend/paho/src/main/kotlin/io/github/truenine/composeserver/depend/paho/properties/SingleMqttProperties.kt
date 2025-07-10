package io.github.truenine.composeserver.depend.paho.properties

import io.github.truenine.composeserver.i32
import io.github.truenine.composeserver.i64
import java.util.*
import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.depend.paho"

@ConfigurationProperties(prefix = "$PREFIX.client")
data class SingleMqttProperties(
  /** schema = tcp:// */
  var url: String? = null,
  var port: i32 = 1883,
  var clientId: String = UUID.randomUUID().toString(),
  var topics: MutableList<String> = ArrayList(),
  var username: String = "",
  var password: String = "",
  var connectTimeoutSecond: i32 = 10,
  var completionTimeout: i64 = 1000L * 5L,
  var qos: i32 = 0,
  var keepAliveSecond: i32 = 10,
) {
  val fullUrl: String
    get() = "$url:$port"
}
