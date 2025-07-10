package net.yan100.compose.pay.autoconfig

import com.wechat.pay.java.core.RSAAutoCertificateConfig
import com.wechat.pay.java.service.payments.jsapi.JsapiService
import com.wechat.pay.java.service.refund.RefundService
import net.yan100.compose.pay.properties.WeChatPayProperties
import net.yan100.compose.pay.properties.WeChatPaySingleConfigProperty
import net.yan100.compose.resourceAsStream
import net.yan100.compose.slf4j
import net.yan100.compose.utf8String
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
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
    if (p.asyncSuccessNotifyUrl?.startsWith("https://") == false) log.warn("警告：配置的异步支付通知地址不是 https 地址 [{}]", p.asyncSuccessNotifyUrl)

    if (p.asyncSuccessRefundNotifyUrl?.startsWith("https://") == false) log.warn("警告：配置的异步退款通知地址不是 https 地址 [{}]", p.asyncSuccessRefundNotifyUrl)

    log.info("注册 微信 单支付属性 p = {}", p)
    log.info("privateKeyPath = {}", p.privateKeyPath)
    log.info("certKeyPath = {}", p.certPath)

    val privateKey = ClassPathResource(p.privateKeyPath).contentAsByteArray.utf8String

    // TODO 郑重警告，此类不能被创建两次
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
