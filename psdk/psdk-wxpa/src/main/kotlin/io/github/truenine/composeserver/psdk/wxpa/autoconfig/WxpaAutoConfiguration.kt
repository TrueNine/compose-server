package io.github.truenine.composeserver.psdk.wxpa.autoconfig

import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.psdk.wxpa.api.IWxpaWebClient
import io.github.truenine.composeserver.psdk.wxpa.core.WxpaSignatureGenerator
import io.github.truenine.composeserver.psdk.wxpa.core.WxpaTokenManager
import io.github.truenine.composeserver.psdk.wxpa.core.WxpaUserInfoService
import io.github.truenine.composeserver.psdk.wxpa.event.WxpaTokenEventManager
import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaConfigurationException
import io.github.truenine.composeserver.psdk.wxpa.properties.WechatProperties
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.github.truenine.composeserver.psdk.wxpa.service.WxpaService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val log = logger<WxpaAutoConfiguration>()

@Configuration
class WxpaAutoConfiguration {

  @Bean
  fun wxpaProperties(wechatProperties: WechatProperties): WxpaProperties {
    log.info("Configuring Wxpa properties")

    val wxpaProps = wechatProperties.wxpa

    // 验证必需的配置
    if (wxpaProps.appId.isNullOrBlank()) {
      throw WxpaConfigurationException("AppId is required but not configured")
    }
    if (wxpaProps.appSecret.isNullOrBlank()) {
      throw WxpaConfigurationException("AppSecret is required but not configured")
    }

    log.info("Wxpa properties configured successfully for appId: {}", wxpaProps.appId)
    return wxpaProps
  }

  @Bean
  fun wxpaTokenManager(apiClient: IWxpaWebClient, properties: WxpaProperties): WxpaTokenManager {
    log.info("Creating WxpaTokenManager")
    return WxpaTokenManager(apiClient, properties)
  }

  @Bean
  fun wxpaSignatureGenerator(tokenManager: WxpaTokenManager, properties: WxpaProperties): WxpaSignatureGenerator {
    log.info("Creating WxpaSignatureGenerator")
    return WxpaSignatureGenerator(tokenManager, properties)
  }

  @Bean
  fun wxpaUserInfoService(apiClient: IWxpaWebClient, properties: WxpaProperties): WxpaUserInfoService {
    log.info("Creating WxpaUserInfoService")
    return WxpaUserInfoService(apiClient, properties)
  }

  @Bean
  fun wxpaService(tokenManager: WxpaTokenManager, signatureGenerator: WxpaSignatureGenerator, userInfoService: WxpaUserInfoService): WxpaService {
    log.info("Creating WxpaService")
    return WxpaService(tokenManager, signatureGenerator, userInfoService)
  }

  @Bean
  @ConditionalOnProperty(prefix = "compose.psdk.wxpa.wxpa", name = ["enable-auto-refresh"], havingValue = "true", matchIfMissing = true)
  fun wxpaTokenEventManager(tokenManager: WxpaTokenManager, properties: WxpaProperties): WxpaTokenEventManager {
    log.info("Creating WxpaTokenEventManager")
    return WxpaTokenEventManager(tokenManager, properties)
  }
}
