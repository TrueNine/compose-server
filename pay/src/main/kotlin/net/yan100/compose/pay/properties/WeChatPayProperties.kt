package net.yan100.compose.pay.properties

import org.springframework.boot.context.properties.ConfigurationProperties

const val wechatKeyDir = "security/wechat/pay/"

@ConfigurationProperties(prefix = "compose.pay.wechat")
open class WeChatProperties {
  open var merchantId: String? = null
  open var merchantSerialNumber: String? = null

  open var certPath: String? = "$wechatKeyDir/apiclient_cert.pem"
  open var privateKeyPath: String? = "$wechatKeyDir/apiclient_key.pem"

  /**
   * appId 小程序 Id
   */
  open var mpAppId: String? = null

  /**
   * 异步通知 Id
   */
  open var notifyUrl: String? = null

  /**
   * api 密钥
   */
  open var apiSecret: String? = null
  open var apiV3Key: String? = null
}
