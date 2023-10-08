package net.yan100.compose.depend.mqtt.autoconfig

import net.yan100.compose.depend.mqtt.properties.SingleMqttProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@ComponentScan(
  basePackages = [
    "net.yan100.compose.depend.mqtt.autoconfig"
  ]
)
@EnableConfigurationProperties(SingleMqttProperties::class)
class MqttEntrance {
}
