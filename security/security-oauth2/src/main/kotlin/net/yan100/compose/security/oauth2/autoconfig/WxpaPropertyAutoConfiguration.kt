package net.yan100.compose.security.oauth2.autoconfig

import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.security.oauth2.properties.WechatProperties
import net.yan100.compose.security.oauth2.property.WxpaProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WxpaPropertyAutoConfiguration {
    companion object {
        private val log = slf4j(WxpaPropertyAutoConfiguration::class)
    }

    @Bean
    fun wxpaProperty(properties: WechatProperties): WxpaProperty {
        log.trace("注册 wechat 相关属性配置 = {}", properties)
        val p = WxpaProperty()

        val pa = properties.wxpa

        p.fixedExpiredSecond = pa.fixedExpiredSecond

        p.preValidToken = pa.verifyToken
        p.appId = pa.appId
        p.appSecret = pa.appSecret

        return p
    }
}
