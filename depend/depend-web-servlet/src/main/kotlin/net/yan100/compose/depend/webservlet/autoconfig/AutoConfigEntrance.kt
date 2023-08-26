package net.yan100.compose.depend.webservlet.autoconfig

import net.yan100.compose.webservlet.properties.ServletWebApplicationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

@EnableConfigurationProperties(
  ServletWebApplicationProperties::class
)
class AutoConfigEntrance
