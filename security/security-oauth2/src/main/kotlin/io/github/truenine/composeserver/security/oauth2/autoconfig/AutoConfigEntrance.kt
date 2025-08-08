package io.github.truenine.composeserver.security.oauth2.autoconfig

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan(value = ["io.github.truenine.composeserver.security.oauth2.api", "io.github.truenine.composeserver.security.oauth2.autoconfig"])
@Import(ApiExchangeAutoConfiguration::class)
class AutoConfigEntrance
