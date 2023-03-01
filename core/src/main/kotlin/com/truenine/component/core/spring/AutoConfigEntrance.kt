package com.truenine.component.core.spring

import com.truenine.component.core.spring.properties.ServletWebApplicationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@EnableConfigurationProperties(ServletWebApplicationProperties::class)
@ComponentScan(
  "com.truenine.component.spring"
)
class AutoConfigEntrance {
}
