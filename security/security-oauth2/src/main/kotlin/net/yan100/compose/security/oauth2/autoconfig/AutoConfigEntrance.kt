package net.yan100.compose.security.oauth2.autoconfig

import net.yan100.compose.security.oauth2.properties.WechatProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan(
  value = [
    "net.yan100.compose.security.oauth2.api",
    "net.yan100.compose.security.oauth2.autoconfig",
    "net.yan100.compose.security.oauth2.schedule",
    "net.yan100.compose.security.oauth2.property"
  ]
)
@EnableConfigurationProperties(
  value = [
    WechatProperties::class
  ]
)
@Import(
  ApiExchangeAutoConfiguration::class,
  WxpaPropertyAutoConfiguration::class
)
class AutoConfigEntrance
