package com.truenine.component.pay.autoconfig;

import cn.hutool.core.io.FileUtil;
import com.truenine.component.core.encrypt.Keys;
import com.truenine.component.core.lang.ResourcesLocator;
import com.truenine.component.pay.properties.WeChatProperties;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.RSAConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Configuration
public class WeChatPayAutoConfiguration {

  @Bean
  public Config createConfig(WeChatProperties weChatProperties) throws URISyntaxException {
    // TODO hutool
    String privateKey = FileUtil.readString(weChatProperties.getPrivateKeyPath(), StandardCharsets.UTF_8);
    String cert = FileUtil.readString(weChatProperties.getCertPaths(), StandardCharsets.UTF_8);
//    return new RSAConfig.Builder()
//      .merchantId(weChatProperties.getMerchantId())
//      // 使用 com.wechat.pay.java.core.util 中的函数从本地文件中加载商户私钥，商户私钥会用来生成请求的签名
////      .privateKeyFromPath(ResourcesLocator.classpathUrl(weChatProperties.getPrivateKeyPath()).toString())
//      .privateKey(privateKey)
//      .merchantSerialNumber(weChatProperties.getMerchantSerialNumber())
//      .wechatPayCertificates(cert)
//
////      .wechatPayCertificatesFromPath(weChatProperties.getCertPaths())
//      .build();
    return new RSAAutoCertificateConfig.Builder()
      .merchantId(weChatProperties.getMerchantId())
      .privateKey(privateKey)
      .merchantSerialNumber(weChatProperties.getMerchantSerialNumber())
      .apiV3Key(weChatProperties.getApiKey())
      .build();
  }

  @Bean
  @DependsOn(value = "createConfig")
  public JsapiService createNativePayService(Config config) {
    return new JsapiService.Builder().config(config).build();
  }

}
