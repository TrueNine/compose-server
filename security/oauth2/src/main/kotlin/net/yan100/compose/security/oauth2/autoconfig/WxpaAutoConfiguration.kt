package net.yan100.compose.security.oauth2.autoconfig

import net.yan100.compose.security.oauth2.api.IWxpaWebClient
import net.yan100.compose.security.oauth2.properties.WechatProperties
import net.yan100.compose.security.oauth2.property.WxpaProperty
import net.yan100.compose.security.oauth2.service.WxpaService
import net.yan100.compose.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WxpaAutoConfiguration {
  companion object {
    private val log = slf4j(WxpaAutoConfiguration::class)
  }

  @Bean
  fun wxpaService(client: IWxpaWebClient, property: WxpaProperty): WxpaService {
    return WxpaService(client = client, property = property)
  }

  @Bean
  fun wxpaProperty(properties: WechatProperties): WxpaProperty {
    log.trace("注册 wechat 相关属性配置 = {}", properties)
    val p = WxpaProperty()

    val pa = properties.wxpa
    p.appId = pa.appId!!
    p.fixedExpiredSecond = pa.fixedExpiredSecond
    p.preValidToken = pa.verifyToken!!
    p.appSecret = pa.appSecret!!

    return p
  }
}
