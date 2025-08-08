package io.github.truenine.composeserver.psdk.wxpa.model

/**
 * # 微信公众号用户信息
 *
 * @author TrueNine
 * @since 2025-08-08
 */
data class WxpaUserInfo(
  /** 用户OpenID */
  val openId: String,
  /** 用户昵称 */
  val nickname: String?,
  /** 用户特权信息 */
  val privilege: List<String> = emptyList(),
  /** 用户全局唯一标识 */
  val unionId: String?,
)
