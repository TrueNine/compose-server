package net.yan100.compose.core.autoconfig

import net.yan100.compose.core.properties.DataLoadProperties
import net.yan100.compose.core.properties.SnowflakeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@EnableConfigurationProperties(
  SnowflakeProperties::class,
  DataLoadProperties::class,
)
@ComponentScan("net.yan100.compose.core.autoconfig")
class AutoConfigEntrance
