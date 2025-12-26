package io.github.truenine.composeserver.pay.wechat.autoconfig

import com.wechat.pay.java.core.RSAAutoCertificateConfig
import com.wechat.pay.java.service.payments.jsapi.JsapiService
import com.wechat.pay.java.service.refund.RefundService
import io.github.truenine.composeserver.*
import io.github.truenine.composeserver.pay.wechat.properties.WeChatPayProperties
import io.github.truenine.composeserver.pay.wechat.properties.WeChatPaySingleConfigProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*
import org.springframework.core.io.ClassPathResource

private val log = slf4j(WeChatPaySingleAutoConfiguration::class)

@Configuration
class WeChatPaySingleAutoConfiguration {
  companion object {
    const val CREATE_CONFIG_NAME = "rsaAutoCertificateConfig"
  }

  @Bean
  @ConditionalOnProperty("compose.pay.wechat.enable-single", havingValue = "true")
  fun rsaAutoCertificateConfig(p: WeChatPayProperties): RSAAutoCertificateConfig {
    if (p.asyncSuccessNotifyUrl?.startsWith("https://") == false)
      log.warn("Warning: configured asynchronous payment notification URL is not https [{}]", p.asyncSuccessNotifyUrl)

    if (p.asyncSuccessRefundNotifyUrl?.startsWith("https://") == false)
      log.warn("Warning: configured asynchronous refund notification URL is not https [{}]", p.asyncSuccessRefundNotifyUrl)

    log.info("Register WeChat single payment properties p = {}", p)
    log.info("privateKeyPath = {}", p.privateKeyPath)
    log.info("certKeyPath = {}", p.certPath)

    val privateKey = ClassPathResource(p.privateKeyPath).contentAsByteArray.utf8String

    // TODO Important warning: this class must not be created twice
    return RSAAutoCertificateConfig.Builder()
      .merchantId(p.merchantId)
      .privateKey(privateKey)
      .merchantSerialNumber(p.merchantSerialNumber)
      .apiV3Key(p.apiV3Key)
      .build()
  }

  @Bean
  @DependsOn(CREATE_CONFIG_NAME)
  @ConditionalOnBean(RSAAutoCertificateConfig::class)
  fun jsapiService(config: RSAAutoCertificateConfig?): JsapiService {
    return JsapiService.Builder().config(config).build()
  }

  @Bean
  @DependsOn(CREATE_CONFIG_NAME)
  @ConditionalOnBean(RSAAutoCertificateConfig::class)
  fun refundService(config: RSAAutoCertificateConfig?): RefundService {
    return RefundService.Builder().config(config).build()
  }

  @Bean
  @DependsOn(CREATE_CONFIG_NAME)
  @ConditionalOnBean(RSAAutoCertificateConfig::class)
  fun WeChatPaySingleConfigProperty(p: WeChatPayProperties): WeChatPaySingleConfigProperty {
    val privateKeyFile = p.privateKeyPath.resourceAsStream(this::class).use { it?.readAllBytes()?.utf8String }
    p.certPath.resourceAsStream(this::class).use { it?.readAllBytes()?.utf8String }

    return WeChatPaySingleConfigProperty().apply {
      enable = p.enableSingle
      privateKey = privateKeyFile!!
      mpAppId = p.mpAppId!!
      apiSecret = p.apiSecret!!
      merchantId = p.merchantId!!
      asyncSuccessNotifyUrl = p.asyncSuccessNotifyUrl!!
      asyncSuccessRefundNotifyUrl = p.asyncSuccessRefundNotifyUrl!!
    }
  }
}
