package io.github.truenine.composeserver.security.oauth2.autoconfig

import io.github.truenine.composeserver.security.oauth2.properties.WechatProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan(
  value =
    [
      "io.github.truenine.composeserver.security.oauth2.api",
      "io.github.truenine.composeserver.security.oauth2.autoconfig",
      "io.github.truenine.composeserver.security.oauth2.schedule",
      "io.github.truenine.composeserver.security.oauth2.property",
    ]
)
@EnableConfigurationProperties(value = [WechatProperties::class])
@Import(ApiExchangeAutoConfiguration::class, WxpaAutoConfiguration::class)
class AutoConfigEntrance
