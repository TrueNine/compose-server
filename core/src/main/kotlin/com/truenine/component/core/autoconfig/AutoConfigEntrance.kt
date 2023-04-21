package com.truenine.component.core.autoconfig

import com.fasterxml.jackson.annotation.JsonProperty
import com.truenine.component.core.properties.ServletWebApplicationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@EnableConfigurationProperties(ServletWebApplicationProperties::class)
@ComponentScan(
  "com.truenine.component.core.autoconfig"
)
class AutoConfigEntrance
