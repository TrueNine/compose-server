package net.yan100.compose.depend.paho

import net.yan100.compose.depend.paho.properties.SingleMqttProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@ComponentScan(basePackages = ["net.yan100.compose.depend.paho.autoconfig", "net.yan100.compose.depend.paho.integration"])
@EnableConfigurationProperties(SingleMqttProperties::class)
@SpringBootApplication
class TestEntrance
