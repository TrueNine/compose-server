package net.yan100.compose.security.oauth2.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

private const val PREFIX = "compose.security.oauth2.wechat"

@ConfigurationProperties(prefix = PREFIX) data class WechatProperties(@NestedConfigurationProperty var wxpa: WxpaProperties = WxpaProperties())
