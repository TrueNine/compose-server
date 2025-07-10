package net.yan100.compose.pay.autoconfig

import net.yan100.compose.pay.properties.WeChatPayProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@Import(value = [WeChatPaySingleAutoConfiguration::class, ApiExchangeAutoConfiguration::class])
@EnableConfigurationProperties(WeChatPayProperties::class)
@ComponentScan(value = ["net.yan100.compose.pay.autoconfig", "net.yan100.compose.pay.service", "net.yan100.compose.pay.api"])
class AutoConfigEntrance
