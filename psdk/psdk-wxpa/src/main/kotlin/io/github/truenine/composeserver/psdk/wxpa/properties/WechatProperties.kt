package io.github.truenine.composeserver.psdk.wxpa.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.PSDK_WXPA)
data class WechatProperties(@NestedConfigurationProperty var wxpa: WxpaProperties = WxpaProperties())
