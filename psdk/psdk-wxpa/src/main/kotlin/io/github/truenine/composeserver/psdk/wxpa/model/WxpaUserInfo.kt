package io.github.truenine.composeserver.psdk.wxpa.model

/**
 * WeChat Official Account user information.
 *
 * @author TrueNine
 * @since 2025-08-08
 */
data class WxpaUserInfo(
  /** User OpenID. */
  val openId: String,
  /** User nickname. */
  val nickname: String?,
  /** User privilege information. */
  val privilege: List<String> = emptyList(),
  /** User global unique identifier (unionId). */
  val unionId: String?,
)
