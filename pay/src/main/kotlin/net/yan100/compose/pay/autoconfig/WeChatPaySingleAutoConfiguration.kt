/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
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
import org.springframework.core.io.ClassPathResource

@Configuration
class WeChatPaySingleAutoConfiguration {
  companion object {
    const val CREATE_CONFIG_NAME = "rsaAutoCertificateConfig"

    @JvmStatic private val log = slf4j(WeChatPaySingleAutoConfiguration::class)
  }

  @Bean
  @ConditionalOnProperty("compose.pay.wechat.enable-single", havingValue = "true")
  fun rsaAutoCertificateConfig(p: WeChatPayProperties): RSAAutoCertificateConfig {
    if (!p.asyncSuccessNotifyUrl.startsWith("https://")) {
      log.warn("警告：配置的异步支付通知地址不是 https 地址 [{}]", p.asyncSuccessNotifyUrl)
    }
    if (!p.asyncSuccessRefundNotifyUrl.startsWith("https://")) {
      log.warn("警告：配置的异步退款通知地址不是 https 地址 [{}]", p.asyncSuccessRefundNotifyUrl)
    }

    log.info("注册 微信 单支付属性 p = {}", p)
    log.info("privateKeyPath = {}", p.privateKeyPath)
    log.info("certKeyPath = {}", p.certPath)

    val privateKey = ClassPathResource(p.privateKeyPath).contentAsByteArray.utf8String
    val cert = ClassPathResource(p.certPath).contentAsByteArray.utf8String

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
    val privateKeyFile =
      p.privateKeyPath?.resourceAsStream(this::class).use { it?.readAllBytes()?.utf8String }
    val certKeyFile =
      p.certPath?.resourceAsStream(this::class).use { it?.readAllBytes()?.utf8String }

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
