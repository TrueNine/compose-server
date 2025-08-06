package io.github.truenine.composeserver.depend.paho

import io.github.truenine.composeserver.depend.paho.properties.SingleMqttProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@ComponentScan(basePackages = ["io.github.truenine.composeserver.depend.paho.autoconfig", "io.github.truenine.composeserver.depend.paho.integration"])
@EnableConfigurationProperties(SingleMqttProperties::class)
@SpringBootApplication
class TestEntrance
