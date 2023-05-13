package net.yan100.compose.core.autoconfig

import net.yan100.compose.core.properties.KeysProperties
import net.yan100.compose.core.properties.ServletWebApplicationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@EnableConfigurationProperties(
  ServletWebApplicationProperties::class,
  KeysProperties::class
)
@ComponentScan(
  "net.yan100.compose.core.autoconfig"
)
class AutoConfigEntrance
