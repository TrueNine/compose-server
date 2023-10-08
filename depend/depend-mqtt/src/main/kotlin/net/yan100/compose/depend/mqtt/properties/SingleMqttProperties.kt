package net.yan100.compose.depend.mqtt.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "compose.mqtt-client")
class SingleMqttProperties {
  /**
   * schema = tcp://
   */
  var url: String? = null
  var clientId: String? = null
  var topics: MutableList<String> = mutableListOf()
  var username: String? = null
  var password: String? = null
  var connectTimeoutSecond: Int = 3000
  var completionTimeout: Long = 1000 * 5
  var qos: Int = 0
  var keepAliveSecond: Long = 10
}
