package io.github.truenine.composeserver.psdk.wxpa.event

import io.github.truenine.composeserver.psdk.wxpa.model.WxpaTicket
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaToken
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WxpaTokenEventTest {

  @Nested
  inner class `TokenExpiredEvent 测试` {

    @Test
    fun `应该正确创建 TokenExpiredEvent`() {
      // Given
      val source = "test-source"
      val appId = "test-app-id"
      val tokenType = TokenType.ACCESS_TOKEN
      val token = WxpaToken("test-token", 7200L)
      val reason = "Token expired"

      // When
      val event = TokenExpiredEvent(source = source, appId = appId, tokenType = tokenType, currentToken = token, reason = reason)

      // Then
      assertEquals(source, event.source)
      assertEquals(appId, event.appId)
      assertEquals(tokenType, event.tokenType)
      assertEquals(token, event.currentToken)
      assertEquals(reason, event.reason)
      assertNotNull(event.eventTime)
      assertTrue(event.eventTime.isBefore(LocalDateTime.now().plusSeconds(1)))
    }

    @Test
    fun `应该支持创建没有当前 Token 的过期事件`() {
      // When
      val event = TokenExpiredEvent(source = this, appId = "test-app-id", tokenType = TokenType.JSAPI_TICKET, reason = "Token missing")

      // Then
      assertEquals(TokenType.JSAPI_TICKET, event.tokenType)
      assertEquals(null, event.currentToken)
      assertEquals(null, event.currentTicket)
      assertEquals("Token missing", event.reason)
    }
  }

  @Nested
  inner class `TokenRefreshedEvent 测试` {

    @Test
    fun `应该正确创建 TokenRefreshedEvent`() {
      // Given
      val source = "test-source"
      val appId = "test-app-id"
      val tokenType = TokenType.ACCESS_TOKEN
      val newToken = WxpaToken("new-token", 7200L)
      val duration = 1500L

      // When
      val event = TokenRefreshedEvent(source = source, appId = appId, tokenType = tokenType, newToken = newToken, refreshDurationMs = duration)

      // Then
      assertEquals(source, event.source)
      assertEquals(appId, event.appId)
      assertEquals(tokenType, event.tokenType)
      assertEquals(newToken, event.newToken)
      assertEquals(duration, event.refreshDurationMs)
      assertNotNull(event.eventTime)
    }

    @Test
    fun `应该支持创建包含两种 Token 的刷新事件`() {
      // Given
      val newToken = WxpaToken("new-token", 7200L)
      val newTicket = WxpaTicket("new-ticket", 7200L)

      // When
      val event =
        TokenRefreshedEvent(
          source = this,
          appId = "test-app-id",
          tokenType = TokenType.BOTH,
          newToken = newToken,
          newTicket = newTicket,
          refreshDurationMs = 2000L,
        )

      // Then
      assertEquals(TokenType.BOTH, event.tokenType)
      assertEquals(newToken, event.newToken)
      assertEquals(newTicket, event.newTicket)
      assertEquals(2000L, event.refreshDurationMs)
    }
  }

  @Nested
  inner class `TokenRefreshFailedEvent 测试` {

    @Test
    fun `应该正确创建 TokenRefreshFailedEvent`() {
      // Given
      val source = "test-source"
      val appId = "test-app-id"
      val tokenType = TokenType.ACCESS_TOKEN
      val reason = "Network error"
      val exception = RuntimeException("Connection timeout")
      val retryCount = 3

      // When
      val event =
        TokenRefreshFailedEvent(source = source, appId = appId, tokenType = tokenType, failureReason = reason, exception = exception, retryCount = retryCount)

      // Then
      assertEquals(source, event.source)
      assertEquals(appId, event.appId)
      assertEquals(tokenType, event.tokenType)
      assertEquals(reason, event.failureReason)
      assertEquals(exception, event.exception)
      assertEquals(retryCount, event.retryCount)
      assertNotNull(event.eventTime)
    }
  }

  @Nested
  inner class `TokenHealthCheckEvent 测试` {

    @Test
    fun `应该正确创建 TokenHealthCheckEvent`() {
      // Given
      val source = "test-source"
      val appId = "test-app-id"
      val tokenStatus = mapOf("hasAccessToken" to true, "accessTokenExpired" to false, "hasJsapiTicket" to true, "jsapiTicketExpired" to false)
      val healthStatus = HealthStatus.HEALTHY

      // When
      val event = TokenHealthCheckEvent(source = source, appId = appId, tokenStatus = tokenStatus, healthStatus = healthStatus)

      // Then
      assertEquals(source, event.source)
      assertEquals(appId, event.appId)
      assertEquals(tokenStatus, event.tokenStatus)
      assertEquals(healthStatus, event.healthStatus)
      assertNotNull(event.eventTime)
    }
  }

  @Nested
  inner class `TokenUsedEvent 测试` {

    @Test
    fun `应该正确创建 TokenUsedEvent`() {
      // Given
      val source = "test-source"
      val appId = "test-app-id"
      val tokenType = TokenType.ACCESS_TOKEN
      val usageContext = "API call"

      // When
      val event = TokenUsedEvent(source = source, appId = appId, tokenType = tokenType, usageContext = usageContext)

      // Then
      assertEquals(source, event.source)
      assertEquals(appId, event.appId)
      assertEquals(tokenType, event.tokenType)
      assertEquals(usageContext, event.usageContext)
      assertNotNull(event.eventTime)
    }

    @Test
    fun `应该支持默认使用上下文`() {
      // When
      val event = TokenUsedEvent(source = this, appId = "test-app-id", tokenType = TokenType.JSAPI_TICKET)

      // Then
      assertEquals("unknown", event.usageContext)
    }
  }

  @Nested
  inner class `枚举类型测试` {

    @Test
    fun `TokenType 应该包含所有预期值`() {
      val types = TokenType.entries
      assertEquals(3, types.size)
      assertTrue(types.contains(TokenType.ACCESS_TOKEN))
      assertTrue(types.contains(TokenType.JSAPI_TICKET))
      assertTrue(types.contains(TokenType.BOTH))
    }

    @Test
    fun `HealthStatus 应该包含所有预期值`() {
      val statuses = HealthStatus.entries
      assertEquals(3, statuses.size)
      assertTrue(statuses.contains(HealthStatus.HEALTHY))
      assertTrue(statuses.contains(HealthStatus.WARNING))
      assertTrue(statuses.contains(HealthStatus.UNHEALTHY))
    }
  }
}
