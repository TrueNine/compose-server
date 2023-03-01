package io.tn.core.spring

import io.tn.core.spring.properties.ServletWebApplicationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@EnableConfigurationProperties(ServletWebApplicationProperties::class)
@ComponentScan(
  "io.tn.core.spring"
)
class AutoConfigEntrance {
}
