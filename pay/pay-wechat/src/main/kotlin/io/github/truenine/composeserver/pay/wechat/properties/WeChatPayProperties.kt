package io.github.truenine.composeserver.pay.wechat.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * # WeChat single payment JS API configuration <br></br>
 * [WeChat Pay documentation](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_1.shtml)
 *
 * @author shanghua
 * @since 2023-05-05
 */
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.PAY_WECHAT, ignoreUnknownFields = true)
data class WeChatPayProperties(
  /** Enable single-configuration payment */
  var enableSingle: Boolean = false,

  /** Merchant ID */
  var merchantId: String? = null,

  /** Merchant serial number */
  var merchantSerialNumber: String? = null,

  /** Certificate file path */
  var certPath: String = WECHAT_KEY_DIR + "apiclient_cert.pem",

  /** Private key file path */
  var privateKeyPath: String = WECHAT_KEY_DIR + "apiclient_key.pem",

  /** appId of the mini program */
  var mpAppId: String? = null,

  /**
   * Asynchronous payment success notification URL <br></br>
   * [WeChat Pay notification documentation](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_5.shtml)
   */
  var asyncSuccessNotifyUrl: String? = null,

  /** Asynchronous refund success notification URL <br></br> [WeChat refund documentation](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_11.shtml) */
  var asyncSuccessRefundNotifyUrl: String? = null,

  /** API key */
  var apiSecret: String? = null,

  /** WeChat Pay jsAPI v3 private key */
  var apiV3Key: String? = null,
) {
  companion object {
    const val WECHAT_KEY_DIR = "security/wechat/pay/"
  }
}
