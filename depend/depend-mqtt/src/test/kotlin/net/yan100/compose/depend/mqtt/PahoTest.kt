package net.yan100.compose.depend.mqtt

import net.yan100.compose.depend.mqtt.paho.MqttPahoClientWrapper
import net.yan100.compose.depend.mqtt.paho.subscribe
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

@SpringBootTest(classes = [ApplicationRunner::class])
class PahoTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var mqttClient: MqttClient

  @Autowired
  lateinit var options: MqttConnectOptions

  @Autowired
  lateinit var wrapper: MqttPahoClientWrapper

  @Test
  fun testConnectClient() {
    val a = wrapper.subscribe<Any>("order/a") { _, a ->
      println(a)
    }
  }
}
