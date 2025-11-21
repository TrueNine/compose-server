package io.github.truenine.composeserver.psdk.wxpa.model

import java.time.LocalDateTime

/**
 * WeChat Official Account token information.
 *
 * @author TrueNine
 * @since 2025-08-08
 */
data class WxpaToken(
  /** Access token. */
  val accessToken: String,
  /** Expiration time in seconds. */
  val expiresIn: Long,
  /** Time when the token was obtained. */
  val obtainedAt: LocalDateTime = LocalDateTime.now(),
) {
  /** Whether the token is expired. */
  val isExpired: Boolean
    // Consider the token expired 5 minutes before the actual expiration time.
    get() = LocalDateTime.now().isAfter(obtainedAt.plusSeconds(expiresIn - 300))
}

/** WeChat Official Account ticket information. */
data class WxpaTicket(
  /** JSAPI ticket. */
  val ticket: String,
  /** Expiration time in seconds. */
  val expiresIn: Long,
  /** Time when the ticket was obtained. */
  val obtainedAt: LocalDateTime = LocalDateTime.now(),
) {
  /** Whether the ticket is expired. */
  val isExpired: Boolean
    // Consider the ticket expired 5 minutes before the actual expiration time.
    get() = LocalDateTime.now().isAfter(obtainedAt.plusSeconds(expiresIn - 300))
}
