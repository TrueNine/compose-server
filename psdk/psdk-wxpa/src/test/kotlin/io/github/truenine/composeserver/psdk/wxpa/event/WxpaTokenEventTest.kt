package io.github.truenine.composeserver.psdk.wxpa.event

import io.github.truenine.composeserver.psdk.wxpa.model.WxpaTicket
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaToken
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.*

class WxpaTokenEventTest {

  @Nested
  inner class `TokenExpiredEvent tests` {

    @Test
    fun `should create TokenExpiredEvent correctly`() {
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
    fun `should create expiration event without current token`() {
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
  inner class `TokenRefreshedEvent tests` {

    @Test
    fun `should create TokenRefreshedEvent correctly`() {
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
    fun `should create refresh event with both token types`() {
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
  inner class `TokenRefreshFailedEvent tests` {

    @Test
    fun `should create TokenRefreshFailedEvent correctly`() {
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
  inner class `TokenHealthCheckEvent tests` {

    @Test
    fun `should create TokenHealthCheckEvent correctly`() {
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
  inner class `TokenUsedEvent tests` {

    @Test
    fun `should create TokenUsedEvent correctly`() {
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
    fun `should support default usage context`() {
      // When
      val event = TokenUsedEvent(source = this, appId = "test-app-id", tokenType = TokenType.JSAPI_TICKET)

      // Then
      assertEquals("unknown", event.usageContext)
    }
  }

  @Nested
  inner class `Enum tests` {

    @Test
    fun `TokenType should contain all expected values`() {
      val types = TokenType.entries
      assertEquals(3, types.size)
      assertTrue(types.contains(TokenType.ACCESS_TOKEN))
      assertTrue(types.contains(TokenType.JSAPI_TICKET))
      assertTrue(types.contains(TokenType.BOTH))
    }

    @Test
    fun `HealthStatus should contain all expected values`() {
      val statuses = HealthStatus.entries
      assertEquals(3, statuses.size)
      assertTrue(statuses.contains(HealthStatus.HEALTHY))
      assertTrue(statuses.contains(HealthStatus.WARNING))
      assertTrue(statuses.contains(HealthStatus.UNHEALTHY))
    }
  }
}
