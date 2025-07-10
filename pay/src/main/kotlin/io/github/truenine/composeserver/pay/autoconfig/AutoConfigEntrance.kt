package io.github.truenine.composeserver.pay.autoconfig

import io.github.truenine.composeserver.pay.properties.WeChatPayProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@Import(value = [WeChatPaySingleAutoConfiguration::class, ApiExchangeAutoConfiguration::class])
@EnableConfigurationProperties(WeChatPayProperties::class)
@ComponentScan(
  value = ["io.github.truenine.composeserver.pay.autoconfig", "io.github.truenine.composeserver.pay.service", "io.github.truenine.composeserver.pay.api"]
)
class AutoConfigEntrance
