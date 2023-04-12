package com.truenine.component.pay.autoconfig;

import com.truenine.component.pay.properties.WeChatProperties;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class WeChatPayAutoConfiguration {

  @Bean
  public Config createConfig(WeChatProperties weChatProperties) {
    return new RSAConfig.Builder()
      .merchantId(weChatProperties.getMerchantId())
      // 使用 com.wechat.pay.java.core.util 中的函数从本地文件中加载商户私钥，商户私钥会用来生成请求的签名
      .privateKeyFromPath(weChatProperties.getPrivateKeyPath())
      .merchantSerialNumber(weChatProperties.getMerchantSerialNumber())
      .wechatPayCertificatesFromPath(weChatProperties.getCertPaths())
      .build();
  }

  @Bean
  @DependsOn(value = "createConfig")
  public JsapiService createNativePayService(Config config) {
    return new JsapiService.Builder().config(config).build();
  }

}
