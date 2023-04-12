package com.truenine.component.pay.properties;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "component.pay.wechat")
public class WeChatProperties {

  private String merchantId;
  private String privateKeyPath;
  private String merchantSerialNumber;
  private String certPaths;

  /**
   * appId 小程序 Id
   */
  private String appId;
  /**
   * 异步通知 Id
   */
  private String notifyUrl;

}
