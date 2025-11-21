package io.github.truenine.composeserver.pay.wechat.properties

/**
 * # WeChat Pay single-configuration properties
 *
 * @author TrueNine
 * @since 2023-05-28
 */
open class WeChatPaySingleConfigProperty {
  /**
   * ## Indicates whether single-configuration payment is enabled.
   * If disabled, other properties are considered uninitialized.
   */
  open var enable: Boolean = false
  open lateinit var privateKey: String
  open lateinit var apiSecret: String
  open lateinit var merchantId: String
  open lateinit var mpAppId: String
  open lateinit var asyncSuccessNotifyUrl: String
  open lateinit var asyncSuccessRefundNotifyUrl: String
}
