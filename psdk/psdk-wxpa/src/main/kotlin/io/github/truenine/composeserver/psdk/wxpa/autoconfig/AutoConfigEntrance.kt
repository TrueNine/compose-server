package io.github.truenine.composeserver.psdk.wxpa.autoconfig

import io.github.truenine.composeserver.depend.jackson.autoconfig.AutoConfigEntrance as JacksonAutoConfigEntrance
import io.github.truenine.composeserver.psdk.wxpa.properties.WechatProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableAsync

@AutoConfiguration
@EnableConfigurationProperties(WechatProperties::class)
@Import(JacksonAutoConfigEntrance::class, WxpaAutoConfiguration::class, ApiExchangeAutoConfiguration::class)
@EnableAsync
class AutoConfigEntrance
