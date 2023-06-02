package net.yan100.compose.security.oauth2.autoconfig

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan(value = [
  "net.yan100.compose.security.oauth2.api"
])
@Import(ApiExchangeAutoConfiguration::class)
class AutoConfigEntrance
