package net.yan100.compose.depend.servlet.autoconfig

import net.yan100.compose.depend.servlet.properties.ServletWebApplicationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@ComponentScan("net.yan100.compose.depend.servlet.autoconfig")
@EnableConfigurationProperties(ServletWebApplicationProperties::class)
class AutoConfigEntrance
