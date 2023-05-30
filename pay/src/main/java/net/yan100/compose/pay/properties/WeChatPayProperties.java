package net.yan100.compose.pay.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "compose.pay.wechat")
public class WeChatPayProperties {
  private static final String WECHAT_KEY_DIR = "security/wechat/pay/";
  /**
   * 开启 单配置支付
   */
  private Boolean enableSingle = false;

  private String merchantId = null;

  private String merchantSerialNumber = null;

  private String certPath = WECHAT_KEY_DIR + "/apiclient_cert.pem";
  private String privateKeyPath = WECHAT_KEY_DIR + "/apiclient_key.pem";

  /**
   * appId 小程序 Id
   */
  private String mpAppId = null;

  /**
   * 异步通知 Id
   */
  private String asyncNotifyUrl = null;

  /**
   * api 密钥
   */
  private String apiSecret = null;
  private String apiV3Key = null;
}
