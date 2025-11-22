package io.github.truenine.composeserver.psdk.wxpa.model

/**
 * WeChat Official Account JSAPI signature information.
 *
 * @author TrueNine
 * @since 2025-08-08
 */
data class WxpaSignature(
  /** Application ID. */
  val appId: String,
  /** Random nonce string. */
  val nonceStr: String,
  /** Timestamp. */
  val timestamp: Long,
  /** URL used for signing. */
  val url: String,
  /** Signature value. */
  val signature: String,
)
