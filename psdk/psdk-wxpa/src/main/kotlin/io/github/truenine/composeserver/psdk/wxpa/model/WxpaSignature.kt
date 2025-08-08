package io.github.truenine.composeserver.psdk.wxpa.model

/**
 * # 微信公众号JSAPI签名信息
 *
 * @author TrueNine
 * @since 2025-08-08
 */
data class WxpaSignature(
  /** 应用ID */
  val appId: String,
  /** 随机字符串 */
  val nonceStr: String,
  /** 时间戳 */
  val timestamp: Long,
  /** 签名URL */
  val url: String,
  /** 签名 */
  val signature: String,
)
