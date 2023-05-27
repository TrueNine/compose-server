package net.yan100.compose.pay.autoconfig

import cn.hutool.core.io.FileUtil
import com.wechat.pay.java.core.RSAAutoCertificateConfig
import com.wechat.pay.java.service.payments.jsapi.JsapiService
import com.wechat.pay.java.service.refund.RefundService
import net.yan100.compose.pay.properties.WeChatProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import java.nio.charset.StandardCharsets

@Configuration
class WeChatPayAutoConfiguration {

  @Bean
  fun createConfig(weChatProperties: WeChatProperties): RSAAutoCertificateConfig {
    val privateKey = FileUtil.readString(weChatProperties.privateKeyPath, StandardCharsets.UTF_8)
    val cert = FileUtil.readString(weChatProperties.certPath, StandardCharsets.UTF_8)
    return RSAAutoCertificateConfig.Builder()
      .merchantId(weChatProperties.merchantId)
      .privateKey(privateKey)
      .merchantSerialNumber(weChatProperties.merchantSerialNumber)
      .apiV3Key(weChatProperties.apiV3Key)
      .build()
  }

  @Bean
  @DependsOn(value = ["createConfig"])
  fun createJsapiServicePayService(config: RSAAutoCertificateConfig?): JsapiService {
    return JsapiService.Builder().config(config).build()
  }

  @Bean
  @DependsOn(value = ["createConfig"])
  fun createJsapiServicePayRefundService(config: RSAAutoCertificateConfig?): RefundService {
    return RefundService.Builder().config(config).build()
  }
}
