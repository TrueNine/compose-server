package io.github.truenine.composeserver.pay.wechat.autoconfig

import io.github.truenine.composeserver.pay.wechat.properties.WeChatPayProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@Import(value = [WeChatPaySingleAutoConfiguration::class, ApiExchangeAutoConfiguration::class])
@EnableConfigurationProperties(WeChatPayProperties::class)
@ComponentScan(
  value =
    [
      "io.github.truenine.composeserver.pay.wechat.autoconfig",
      "io.github.truenine.composeserver.pay.wechat.service",
      "io.github.truenine.composeserver.pay.wechat.api",
    ]
)
class AutoConfigEntrance
