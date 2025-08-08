package io.github.truenine.composeserver.psdk.wxpa.model

import java.time.LocalDateTime

/**
 * # 微信公众号Token信息
 *
 * @author TrueNine
 * @since 2025-08-08
 */
data class WxpaToken(
  /** Access Token */
  val accessToken: String,
  /** 过期时间（秒） */
  val expiresIn: Long,
  /** 获取时间 */
  val obtainedAt: LocalDateTime = LocalDateTime.now(),
) {
  /** 是否已过期 */
  val isExpired: Boolean
    get() = LocalDateTime.now().isAfter(obtainedAt.plusSeconds(expiresIn - 300)) // 提前5分钟过期
}

/** # 微信公众号Ticket信息 */
data class WxpaTicket(
  /** JSAPI Ticket */
  val ticket: String,
  /** 过期时间（秒） */
  val expiresIn: Long,
  /** 获取时间 */
  val obtainedAt: LocalDateTime = LocalDateTime.now(),
) {
  /** 是否已过期 */
  val isExpired: Boolean
    get() = LocalDateTime.now().isAfter(obtainedAt.plusSeconds(expiresIn - 300)) // 提前5分钟过期
}
