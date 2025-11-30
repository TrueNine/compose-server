package io.github.truenine.composeserver.psdk.wxpa.event

import io.github.truenine.composeserver.psdk.wxpa.model.WxpaTicket
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaToken
import org.springframework.context.ApplicationEvent
import java.time.LocalDateTime

/**
 * Base event type for WeChat Official Account token lifecycle events.
 *
 * @author TrueNine
 * @since 2025-08-08
 */
abstract class WxpaTokenEvent(
  source: Any,
  /** Time when the event occurred. */
  val eventTime: LocalDateTime = LocalDateTime.now(),
  /** Application ID. */
  val appId: String,
) : ApplicationEvent(source)

/**
 * Token expiration event.
 *
 * Published when a token is about to expire or has already expired.
 */
class TokenExpiredEvent(
  source: Any,
  appId: String,
  /** Type of token that expired. */
  val tokenType: TokenType,
  /** Current token, if present. */
  val currentToken: WxpaToken? = null,
  /** Current ticket, if present. */
  val currentTicket: WxpaTicket? = null,
  /** Reason for expiration. */
  val reason: String = "Token expired or missing",
) : WxpaTokenEvent(source, LocalDateTime.now(), appId)

/**
 * Token refreshed event.
 *
 * Published when token refresh completes successfully.
 */
class TokenRefreshedEvent(
  source: Any,
  appId: String,
  /** Type of token that was refreshed. */
  val tokenType: TokenType,
  /** New token, when the access token was refreshed. */
  val newToken: WxpaToken? = null,
  /** New ticket, when the JSAPI ticket was refreshed. */
  val newTicket: WxpaTicket? = null,
  /** Refresh duration in milliseconds. */
  val refreshDurationMs: Long = 0,
) : WxpaTokenEvent(source, LocalDateTime.now(), appId)

/**
 * Token refresh failed event.
 *
 * Published when token refresh fails.
 */
class TokenRefreshFailedEvent(
  source: Any,
  appId: String,
  /** Type of token that failed to refresh. */
  val tokenType: TokenType,
  /** Reason for failure. */
  val failureReason: String,
  /** Exception that caused the failure. */
  val exception: Throwable? = null,
  /** Number of retry attempts. */
  val retryCount: Int = 0,
) : WxpaTokenEvent(source, LocalDateTime.now(), appId)

/**
 * Token health check event.
 *
 * Periodically published to describe token status.
 */
class TokenHealthCheckEvent(
  source: Any,
  appId: String,
  /** Token status information. */
  val tokenStatus: Map<String, Any>,
  /** Resulting overall health status. */
  val healthStatus: HealthStatus,
) : WxpaTokenEvent(source, LocalDateTime.now(), appId)

/**
 * Token usage event.
 *
 * Published whenever a token is used for API calls or operations.
 */
class TokenUsedEvent(
  source: Any,
  appId: String,
  /** Type of token that was used. */
  val tokenType: TokenType,
  /** Usage context or scenario. */
  val usageContext: String = "unknown",
) : WxpaTokenEvent(source, LocalDateTime.now(), appId)

/** Token type enum. */
enum class TokenType {
  /** Access token. */
  ACCESS_TOKEN,

  /** JSAPI ticket. */
  JSAPI_TICKET,

  /** Both access token and JSAPI ticket. */
  BOTH,
}

/** Health status enum. */
enum class HealthStatus {
  /** Healthy. */
  HEALTHY,

  /** Warning (about to expire). */
  WARNING,

  /** Unhealthy (expired or missing). */
  UNHEALTHY,
}
