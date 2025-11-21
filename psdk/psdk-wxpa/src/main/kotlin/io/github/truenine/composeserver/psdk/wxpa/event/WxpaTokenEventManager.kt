package io.github.truenine.composeserver.psdk.wxpa.event

import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.psdk.wxpa.core.WxpaTokenManager
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

private val log = logger<WxpaTokenEventManager>()

/**
 * WeChat Official Account token event manager.
 *
 * Handles token-related events and provides event-driven token management.
 *
 * @author TrueNine
 * @since 2025-08-08
 */
@Component
class WxpaTokenEventManager(private val tokenManager: WxpaTokenManager, private val properties: WxpaProperties) {

  /** Token usage statistics. */
  private val tokenUsageStats = ConcurrentHashMap<TokenType, AtomicLong>()

  /** Time of the last health check. */
  @Volatile private var lastHealthCheckTime: LocalDateTime? = null

  /** Counter for token refresh failures. */
  private val refreshFailureCount = AtomicLong(0)

  init {
    log.info("WxpaTokenEventManager initialized for appId: {}", properties.appId)
    // Initialize usage counters
    TokenType.entries.forEach { type -> tokenUsageStats[type] = AtomicLong(0) }
  }

  /**
   * Handle application startup completion event.
   *
   * Performs an initial token health check after application startup.
   */
  @EventListener
  fun handleApplicationReady(event: ApplicationReadyEvent) {
    if (!properties.enableAutoRefresh) {
      log.debug("Auto refresh is disabled, skipping initial token check")
      return
    }

    log.info("Application ready, performing initial token health check")

    try {
      val status = tokenManager.getTokenStatus()
      val healthStatus = determineHealthStatus(status)

      // Publish health-check event
      publishHealthCheckEvent(status, healthStatus)

      // If tokens are unhealthy, trigger refresh
      if (healthStatus == HealthStatus.UNHEALTHY) {
        log.info("Initial token check found unhealthy tokens, triggering refresh")
        handleTokenExpired(
          TokenExpiredEvent(
            source = this,
            appId = properties.appId ?: "unknown",
            tokenType = TokenType.BOTH,
            reason = "Initial health check found expired tokens",
          )
        )
      }
    } catch (e: Exception) {
      log.error("Error during initial token health check", e)
    }
  }

  /**
   * Handle token expiration events.
   *
   * Performs token refresh asynchronously.
   */
  @Async
  @EventListener
  fun handleTokenExpired(event: TokenExpiredEvent) {
    log.info("Handling token expired event for appId: {}, tokenType: {}, reason: {}", event.appId, event.tokenType, event.reason)

    val startTime = System.currentTimeMillis()

    try {
      when (event.tokenType) {
        TokenType.ACCESS_TOKEN -> {
          val newToken = tokenManager.refreshAccessToken()
          val duration = System.currentTimeMillis() - startTime

          publishTokenRefreshedEvent(TokenType.ACCESS_TOKEN, newToken = newToken, duration = duration)
          log.info("Access token refreshed successfully in {}ms", duration)
        }

        TokenType.JSAPI_TICKET -> {
          val newTicket = tokenManager.refreshJsapiTicket()
          val duration = System.currentTimeMillis() - startTime

          publishTokenRefreshedEvent(TokenType.JSAPI_TICKET, newTicket = newTicket, duration = duration)
          log.info("JSAPI ticket refreshed successfully in {}ms", duration)
        }

        TokenType.BOTH -> {
          val (newToken, newTicket) = tokenManager.refreshBoth()
          val duration = System.currentTimeMillis() - startTime

          publishTokenRefreshedEvent(TokenType.BOTH, newToken = newToken, newTicket = newTicket, duration = duration)
          log.info("Both tokens refreshed successfully in {}ms", duration)
        }
      }

      // Reset failure counter after a successful refresh
      refreshFailureCount.set(0)
    } catch (e: Exception) {
      val duration = System.currentTimeMillis() - startTime
      val failureCount = refreshFailureCount.incrementAndGet()

      log.error("Failed to refresh token after {}ms, failure count: {}", duration, failureCount, e)

      publishTokenRefreshFailedEvent(event.tokenType, e.message ?: "Unknown error", e, failureCount.toInt())
    }
  }

  /**
   * Handle token usage events.
   *
   * Records token usage statistics.
   */
  @EventListener
  fun handleTokenUsed(event: TokenUsedEvent) {
    tokenUsageStats[event.tokenType]?.incrementAndGet()
    log.debug("Token used: type={}, context={}, total usage={}", event.tokenType, event.usageContext, tokenUsageStats[event.tokenType]?.get())
  }

  /**
   * Handle token refreshed events.
   *
   * Records successful refresh statistics.
   */
  @EventListener
  fun handleTokenRefreshed(event: TokenRefreshedEvent) {
    log.info("Token refreshed successfully: type={}, duration={}ms", event.tokenType, event.refreshDurationMs)
  }

  /**
   * Handle token refresh failed events.
   *
   * Records failure information and can trigger retries or alerts.
   */
  @EventListener
  fun handleTokenRefreshFailed(event: TokenRefreshFailedEvent) {
    log.error("Token refresh failed: type={}, reason={}, retryCount={}", event.tokenType, event.failureReason, event.retryCount)

    // If failures happen too frequently, alert logic can be implemented here
    if (event.retryCount >= properties.apiRetryCount) {
      log.error("Token refresh failed too many times, consider manual intervention")
    }
  }

  /** Publish token refreshed event. */
  private fun publishTokenRefreshedEvent(
    tokenType: TokenType,
    newToken: io.github.truenine.composeserver.psdk.wxpa.model.WxpaToken? = null,
    newTicket: io.github.truenine.composeserver.psdk.wxpa.model.WxpaTicket? = null,
    duration: Long,
  ) {
    val event =
      TokenRefreshedEvent(
        source = this,
        appId = properties.appId ?: "unknown",
        tokenType = tokenType,
        newToken = newToken,
        newTicket = newTicket,
        refreshDurationMs = duration,
      )

    // Publish via EventPublisherHolder
    io.github.truenine.composeserver.holders.EventPublisherHolder.get()?.publishEvent(event)
  }

  /** Publish token refresh failed event. */
  private fun publishTokenRefreshFailedEvent(tokenType: TokenType, reason: String, exception: Throwable?, retryCount: Int) {
    val event =
      TokenRefreshFailedEvent(
        source = this,
        appId = properties.appId ?: "unknown",
        tokenType = tokenType,
        failureReason = reason,
        exception = exception,
        retryCount = retryCount,
      )

    io.github.truenine.composeserver.holders.EventPublisherHolder.get()?.publishEvent(event)
  }

  /** Publish health check event. */
  private fun publishHealthCheckEvent(status: Map<String, Any>, healthStatus: HealthStatus) {
    lastHealthCheckTime = LocalDateTime.now()

    val event = TokenHealthCheckEvent(source = this, appId = properties.appId ?: "unknown", tokenStatus = status, healthStatus = healthStatus)

    io.github.truenine.composeserver.holders.EventPublisherHolder.get()?.publishEvent(event)
  }

  /** Determine health status based on the current token state. */
  private fun determineHealthStatus(status: Map<String, Any>): HealthStatus {
    val hasAccessToken = status["hasAccessToken"] as? Boolean ?: false
    val hasJsapiTicket = status["hasJsapiTicket"] as? Boolean ?: false
    val accessTokenExpired = status["accessTokenExpired"] as? Boolean ?: true
    val jsapiTicketExpired = status["jsapiTicketExpired"] as? Boolean ?: true

    return when {
      (!hasAccessToken || accessTokenExpired) && (!hasJsapiTicket || jsapiTicketExpired) -> HealthStatus.UNHEALTHY
      (!hasAccessToken || accessTokenExpired) || (!hasJsapiTicket || jsapiTicketExpired) -> HealthStatus.WARNING
      else -> HealthStatus.HEALTHY
    }
  }

  /** Get token usage statistics. */
  fun getTokenUsageStats(): Map<TokenType, Long> {
    return tokenUsageStats.mapValues { it.value.get() }
  }

  /** Get time of the last health check. */
  fun getLastHealthCheckTime(): LocalDateTime? = lastHealthCheckTime

  /** Get token refresh failure count. */
  fun getRefreshFailureCount(): Long = refreshFailureCount.get()
}
