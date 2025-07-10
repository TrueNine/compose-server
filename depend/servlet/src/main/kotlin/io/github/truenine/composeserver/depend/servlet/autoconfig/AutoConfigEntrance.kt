package io.github.truenine.composeserver.depend.servlet.autoconfig

import io.github.truenine.composeserver.depend.servlet.properties.ServletWebApplicationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@ComponentScan("io.github.truenine.composeserver.depend.servlet.autoconfig")
@EnableConfigurationProperties(ServletWebApplicationProperties::class)
class AutoConfigEntrance
