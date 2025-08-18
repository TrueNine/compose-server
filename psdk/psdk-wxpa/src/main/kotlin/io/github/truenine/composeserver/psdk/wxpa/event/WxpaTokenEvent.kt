package io.github.truenine.composeserver.psdk.wxpa.event

import io.github.truenine.composeserver.psdk.wxpa.model.WxpaTicket
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaToken
import java.time.LocalDateTime
import org.springframework.context.ApplicationEvent

/**
 * # 微信公众号Token事件基类
 *
 * @author TrueNine
 * @since 2025-08-08
 */
abstract class WxpaTokenEvent(
  source: Any,
  /** 事件发生时间 */
  val eventTime: LocalDateTime = LocalDateTime.now(),
  /** 应用ID */
  val appId: String,
) : ApplicationEvent(source)

/**
 * # Token过期事件
 *
 * 当检测到Token即将过期或已过期时发布此事件
 */
class TokenExpiredEvent(
  source: Any,
  appId: String,
  /** 过期的Token类型 */
  val tokenType: TokenType,
  /** 当前Token（如果存在） */
  val currentToken: WxpaToken? = null,
  /** 当前Ticket（如果存在） */
  val currentTicket: WxpaTicket? = null,
  /** 过期原因 */
  val reason: String = "Token expired or missing",
) : WxpaTokenEvent(source, LocalDateTime.now(), appId)

/**
 * # Token刷新完成事件
 *
 * 当Token刷新成功完成时发布此事件
 */
class TokenRefreshedEvent(
  source: Any,
  appId: String,
  /** 刷新的Token类型 */
  val tokenType: TokenType,
  /** 新的Token（如果刷新了AccessToken） */
  val newToken: WxpaToken? = null,
  /** 新的Ticket（如果刷新了JsapiTicket） */
  val newTicket: WxpaTicket? = null,
  /** 刷新耗时（毫秒） */
  val refreshDurationMs: Long = 0,
) : WxpaTokenEvent(source, LocalDateTime.now(), appId)

/**
 * # Token刷新失败事件
 *
 * 当Token刷新失败时发布此事件
 */
class TokenRefreshFailedEvent(
  source: Any,
  appId: String,
  /** 失败的Token类型 */
  val tokenType: TokenType,
  /** 失败原因 */
  val failureReason: String,
  /** 异常信息 */
  val exception: Throwable? = null,
  /** 重试次数 */
  val retryCount: Int = 0,
) : WxpaTokenEvent(source, LocalDateTime.now(), appId)

/**
 * # Token健康检查事件
 *
 * 定期发布的Token状态检查事件
 */
class TokenHealthCheckEvent(
  source: Any,
  appId: String,
  /** Token状态信息 */
  val tokenStatus: Map<String, Any>,
  /** 检查结果 */
  val healthStatus: HealthStatus,
) : WxpaTokenEvent(source, LocalDateTime.now(), appId)

/**
 * # Token使用事件
 *
 * 当Token被使用时发布此事件，用于统计和监控
 */
class TokenUsedEvent(
  source: Any,
  appId: String,
  /** 使用的Token类型 */
  val tokenType: TokenType,
  /** 使用场景 */
  val usageContext: String = "unknown",
) : WxpaTokenEvent(source, LocalDateTime.now(), appId)

/** # Token类型枚举 */
enum class TokenType {
  /** Access Token */
  ACCESS_TOKEN,

  /** JSAPI Ticket */
  JSAPI_TICKET,

  /** 两者都包含 */
  BOTH,
}

/** # 健康状态枚举 */
enum class HealthStatus {
  /** 健康 */
  HEALTHY,

  /** 警告（即将过期） */
  WARNING,

  /** 不健康（已过期或缺失） */
  UNHEALTHY,
}
