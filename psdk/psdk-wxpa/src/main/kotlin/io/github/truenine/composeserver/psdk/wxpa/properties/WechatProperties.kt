package io.github.truenine.composeserver.psdk.wxpa.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

private const val PREFIX = "compose.psdk.wxpa"

@ConfigurationProperties(prefix = PREFIX) data class WechatProperties(@NestedConfigurationProperty var wxpa: WxpaProperties = WxpaProperties())
