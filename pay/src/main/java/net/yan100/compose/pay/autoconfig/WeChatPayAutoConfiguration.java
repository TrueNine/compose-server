package net.yan100.compose.pay.autoconfig;

import cn.hutool.core.io.FileUtil;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.refund.RefundService;
import net.yan100.compose.pay.properties.WeChatProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.nio.charset.StandardCharsets;

@Configuration
public class WeChatPayAutoConfiguration {

  @Bean
  public RSAAutoCertificateConfig createConfig(WeChatProperties weChatProperties) {
    String privateKey = FileUtil.readString(weChatProperties.getPrivateKeyPath(), StandardCharsets.UTF_8);
    String cert = FileUtil.readString(weChatProperties.getCertPaths(), StandardCharsets.UTF_8);
//    return new RSAConfig.Builder()
//      .merchantId(weChatProperties.getMerchantId())
//      // 使用 com.wechat.pay.java.core.util 中的函数从本地文件中加载商户私钥，商户私钥会用来生成请求的签名
////      .privateKeyFromPath(ResourcesLocator.classpathUrl(weChatProperties.getPrivateKeyPath()).toString())
//      .privateKey(privateKey)
//      .merchantSerialNumber(weChatProperties.getMerchantSerialNumber())
//      .wechatPayCertificates(cert)
////      .wechatPayCertificatesFromPath(weChatProperties.getCertPaths())
//      .build();
    return new RSAAutoCertificateConfig.Builder()
      .merchantId(weChatProperties.getMerchantId())
      .privateKey(privateKey)
      .merchantSerialNumber(weChatProperties.getMerchantSerialNumber())
      .apiV3Key(weChatProperties.getAppKey())
      .build();
  }

  @Bean
  @DependsOn(value = "createConfig")
  public JsapiService createJsapiServicePayService(RSAAutoCertificateConfig config) {
    return new JsapiService.Builder().config(config).build();
  }

  @Bean
  @DependsOn(value = "createConfig")
  public RefundService createJsapiServicePayRefundService(RSAAutoCertificateConfig config) {
    return new RefundService.Builder().config(config).build();
  }

}
