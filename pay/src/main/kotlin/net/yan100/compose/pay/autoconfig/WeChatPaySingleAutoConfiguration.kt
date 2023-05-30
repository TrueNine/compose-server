package net.yan100.compose.pay.autoconfig

import com.wechat.pay.java.core.RSAAutoCertificateConfig
import com.wechat.pay.java.service.payments.jsapi.JsapiService
import com.wechat.pay.java.service.refund.RefundService
import net.yan100.compose.core.lang.resourceAsStream
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.core.lang.utf8String
import net.yan100.compose.pay.properties.WeChatPayProperties
import net.yan100.compose.pay.properties.WeChatPaySingleConfigProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
class WeChatPaySingleAutoConfiguration {
  companion object {
    const val CREATE_CONFIG_NAME = "rsaAutoCertificateConfig"

    @JvmStatic
    private val log = slf4j(WeChatPaySingleAutoConfiguration::class)
  }

  @Bean
  @ConditionalOnProperty("compose.pay.wechat.enable-single", havingValue = "true")
  fun rsaAutoCertificateConfig(p: WeChatPayProperties): RSAAutoCertificateConfig {
    val privateKey = p.privateKeyPath?.resourceAsStream(this::class).use { it?.readAllBytes()?.utf8String }
    val cert = p.certPath?.resourceAsStream(this::class).use { it?.readAllBytes()?.utf8String }
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
    log.trace("注册 微信 单支付属性 p = {}", p)
    val privateKeyFile = p.privateKeyPath?.resourceAsStream(this::class).use { it?.readAllBytes()?.utf8String }
    val certKeyFile = p.certPath?.resourceAsStream(this::class).use { it?.readAllBytes()?.utf8String }
    return WeChatPaySingleConfigProperty().apply {
      enable = p.enableSingle
      privateKey = privateKeyFile!!
      mpAppId = p.mpAppId!!
      apiSecret = p.apiSecret!!
      merchantId = p.merchantId!!
      asyncNotifyUrl = p.asyncNotifyUrl!!
    }
  }
}
