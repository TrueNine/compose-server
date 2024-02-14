package net.yan100.compose.depend.mqtt

import net.yan100.compose.depend.mqtt.paho.MqttPahoClientWrapper
import net.yan100.compose.depend.mqtt.paho.subscribe
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.beans.factory.annotation.Autowired

//@SpringBootTest(classes = [ApplicationRunner::class])
class PahoTest {

    @Autowired
    lateinit var mqttClient: MqttClient

    @Autowired
    lateinit var options: MqttConnectOptions

    @Autowired
    lateinit var wrapper: MqttPahoClientWrapper

    //@Test
    fun testConnectClient() {
        wrapper.subscribe<Any>("order/a") { _, a ->
            println(a)
        }
    }
}
