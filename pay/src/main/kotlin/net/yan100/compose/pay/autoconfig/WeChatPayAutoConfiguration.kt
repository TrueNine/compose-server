package net.yan100.compose.pay.autoconfig

import com.wechat.pay.java.core.RSAAutoCertificateConfig
import com.wechat.pay.java.service.payments.jsapi.JsapiService
import com.wechat.pay.java.service.refund.RefundService
import net.yan100.compose.core.lang.resourceAsStream
import net.yan100.compose.core.lang.utf8String
import net.yan100.compose.pay.properties.WeChatProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
class WeChatPayAutoConfiguration {
  companion object {
    const val CREATE_CONFIG_NAME = "rsaAutoCertificateConfig"
  }

  @Bean
  fun rsaAutoCertificateConfig(p: WeChatProperties): RSAAutoCertificateConfig {
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
  fun jsapiService(config: RSAAutoCertificateConfig?): JsapiService {
    return JsapiService.Builder().config(config).build()
  }


  @Bean
  @DependsOn(CREATE_CONFIG_NAME)
  fun refundService(config: RSAAutoCertificateConfig?): RefundService {
    return RefundService.Builder().config(config).build()
  }
}
