package net.yan100.compose.pay.properties

/**
 * # 微信支付 单配置属性
 *
 * @author TrueNine
 * @since 2023-05-28
 */
open class WeChatPaySingleConfigProperty {
  /**
   * ## 此属性代表是否开启了 单配置支付
   * 如果未开启，则其他属性都是未初始化的属性
   */
  open var enable: Boolean = false
  open lateinit var privateKey: String
  open lateinit var apiSecret: String
  open lateinit var merchantId: String
  open lateinit var mpAppId: String
  open lateinit var asyncSuccessNotifyUrl: String
  open lateinit var asyncSuccessRefundNotifyUrl: String
}
