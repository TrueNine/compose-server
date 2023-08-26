package net.yan100.compose.core.autoconfig

import net.yan100.compose.core.properties.KeysProperties
import net.yan100.compose.core.properties.SnowflakeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@EnableConfigurationProperties(
  KeysProperties::class,
  SnowflakeProperties::class
)
@ComponentScan(
  "net.yan100.compose.core.autoconfig"
)
class AutoConfigEntrance
