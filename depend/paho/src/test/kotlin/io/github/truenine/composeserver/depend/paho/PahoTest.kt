package io.github.truenine.composeserver.depend.paho

import io.github.truenine.composeserver.depend.paho.paho.MqttPahoClientWrapper
import io.github.truenine.composeserver.depend.paho.paho.subscribe
import jakarta.annotation.Resource
import kotlin.test.Ignore
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PahoTest {
  lateinit var mqttClient: MqttClient
    @Resource set

  lateinit var options: MqttConnectOptions
    @Resource set

  lateinit var wrapper: MqttPahoClientWrapper
    @Resource set

  @Ignore
  fun `test connect client`() {
    wrapper.subscribe<Any>("order/a") { _, a -> println(a) }
  }
}
