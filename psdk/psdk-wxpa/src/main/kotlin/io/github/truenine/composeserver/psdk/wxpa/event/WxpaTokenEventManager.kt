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
 * # 微信公众号Token事件管理器
 *
 * 负责处理Token相关的事件，实现事件驱动的Token管理
 *
 * @author TrueNine
 * @since 2025-08-08
 */
@Component
class WxpaTokenEventManager(private val tokenManager: WxpaTokenManager, private val properties: WxpaProperties) {

  /** Token使用统计 */
  private val tokenUsageStats = ConcurrentHashMap<TokenType, AtomicLong>()

  /** 最后一次健康检查时间 */
  @Volatile private var lastHealthCheckTime: LocalDateTime? = null

  /** 刷新失败计数器 */
  private val refreshFailureCount = AtomicLong(0)

  init {
    log.info("WxpaTokenEventManager initialized for appId: {}", properties.appId)
    // 初始化统计计数器
    TokenType.entries.forEach { type -> tokenUsageStats[type] = AtomicLong(0) }
  }

  /**
   * ## 处理应用启动完成事件
   *
   * 应用启动后进行初始Token检查
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

      // 发布健康检查事件
      publishHealthCheckEvent(status, healthStatus)

      // 如果Token不健康，触发刷新
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
   * ## 处理Token过期事件
   *
   * 异步处理Token刷新
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

      // 重置失败计数器
      refreshFailureCount.set(0)
    } catch (e: Exception) {
      val duration = System.currentTimeMillis() - startTime
      val failureCount = refreshFailureCount.incrementAndGet()

      log.error("Failed to refresh token after {}ms, failure count: {}", duration, failureCount, e)

      publishTokenRefreshFailedEvent(event.tokenType, e.message ?: "Unknown error", e, failureCount.toInt())
    }
  }

  /**
   * ## 处理Token使用事件
   *
   * 记录Token使用统计
   */
  @EventListener
  fun handleTokenUsed(event: TokenUsedEvent) {
    tokenUsageStats[event.tokenType]?.incrementAndGet()
    log.debug("Token used: type={}, context={}, total usage={}", event.tokenType, event.usageContext, tokenUsageStats[event.tokenType]?.get())
  }

  /**
   * ## 处理Token刷新完成事件
   *
   * 记录刷新成功的统计信息
   */
  @EventListener
  fun handleTokenRefreshed(event: TokenRefreshedEvent) {
    log.info("Token refreshed successfully: type={}, duration={}ms", event.tokenType, event.refreshDurationMs)
  }

  /**
   * ## 处理Token刷新失败事件
   *
   * 记录失败信息并可能触发重试或告警
   */
  @EventListener
  fun handleTokenRefreshFailed(event: TokenRefreshFailedEvent) {
    log.error("Token refresh failed: type={}, reason={}, retryCount={}", event.tokenType, event.failureReason, event.retryCount)

    // 如果失败次数过多，可以在这里实现告警逻辑
    if (event.retryCount >= properties.apiRetryCount) {
      log.error("Token refresh failed too many times, consider manual intervention")
    }
  }

  /** ## 发布Token刷新完成事件 */
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

    // 通过 EventPublisherHolder 发布事件
    io.github.truenine.composeserver.holders.EventPublisherHolder.get()?.publishEvent(event)
  }

  /** ## 发布Token刷新失败事件 */
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

  /** ## 发布健康检查事件 */
  private fun publishHealthCheckEvent(status: Map<String, Any>, healthStatus: HealthStatus) {
    lastHealthCheckTime = LocalDateTime.now()

    val event = TokenHealthCheckEvent(source = this, appId = properties.appId ?: "unknown", tokenStatus = status, healthStatus = healthStatus)

    io.github.truenine.composeserver.holders.EventPublisherHolder.get()?.publishEvent(event)
  }

  /** ## 根据Token状态确定健康状态 */
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

  /** ## 获取Token使用统计 */
  fun getTokenUsageStats(): Map<TokenType, Long> {
    return tokenUsageStats.mapValues { it.value.get() }
  }

  /** ## 获取最后健康检查时间 */
  fun getLastHealthCheckTime(): LocalDateTime? = lastHealthCheckTime

  /** ## 获取刷新失败次数 */
  fun getRefreshFailureCount(): Long = refreshFailureCount.get()
}
