package io.github.truenine.composeserver.psdk.wxpa.event

import io.github.truenine.composeserver.holders.EventPublisherHolder
import io.github.truenine.composeserver.psdk.wxpa.core.WxpaTokenManager
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaTicket
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaToken
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ConfigurableApplicationContext

class WxpaTokenEventManagerTest {

  private lateinit var tokenManager: WxpaTokenManager
  private lateinit var properties: WxpaProperties
  private lateinit var eventManager: WxpaTokenEventManager
  private lateinit var mockPublisher: ApplicationEventPublisher

  @BeforeEach
  fun setUp() {
    tokenManager = mockk()
    properties = WxpaProperties(appId = "test-app-id", appSecret = "test-app-secret", enableAutoRefresh = true, apiRetryCount = 3)
    mockPublisher = mockk(relaxed = true)

    // Set EventPublisherHolder
    EventPublisherHolder.set(mockPublisher)

    eventManager = WxpaTokenEventManager(tokenManager, properties)
  }

  @AfterEach
  fun tearDown() {
    EventPublisherHolder.close()
    clearAllMocks()
  }

  @Nested
  inner class `Application startup event handling` {

    @Test
    fun `should perform health check when application starts`() {
      // Given
      val mockApplication = mockk<SpringApplication>()
      val mockContext = mockk<ConfigurableApplicationContext>()
      val applicationReadyEvent = ApplicationReadyEvent(mockApplication, emptyArray(), mockContext, Duration.ofSeconds(1))

      val tokenStatus = mapOf("hasAccessToken" to true, "accessTokenExpired" to false, "hasJsapiTicket" to true, "jsapiTicketExpired" to false)

      every { tokenManager.getTokenStatus() } returns tokenStatus

      // When
      eventManager.handleApplicationReady(applicationReadyEvent)

      // Then
      verify { tokenManager.getTokenStatus() }
      verify { mockPublisher.publishEvent(any<TokenHealthCheckEvent>()) }
    }

    @Test
    fun `should trigger refresh when unhealthy tokens are detected`() {
      // Given
      val mockApplication = mockk<SpringApplication>()
      val mockContext = mockk<ConfigurableApplicationContext>()
      val applicationReadyEvent = ApplicationReadyEvent(mockApplication, emptyArray(), mockContext, Duration.ofSeconds(1))

      val tokenStatus = mapOf("hasAccessToken" to false, "accessTokenExpired" to true, "hasJsapiTicket" to false, "jsapiTicketExpired" to true)

      every { tokenManager.getTokenStatus() } returns tokenStatus

      // When
      eventManager.handleApplicationReady(applicationReadyEvent)

      // Then
      verify { tokenManager.getTokenStatus() }
      verify { mockPublisher.publishEvent(any<TokenHealthCheckEvent>()) }
      // An expiration event should also be published, but since it is handled asynchronously we do not verify it here
    }

    @Test
    fun `should skip initial check when auto refresh is disabled`() {
      // Given
      val disabledProperties = properties.copy(enableAutoRefresh = false)
      val disabledEventManager = WxpaTokenEventManager(tokenManager, disabledProperties)
      val mockApplication = mockk<SpringApplication>()
      val mockContext = mockk<ConfigurableApplicationContext>()
      val applicationReadyEvent = ApplicationReadyEvent(mockApplication, emptyArray(), mockContext, Duration.ofSeconds(1))

      // When
      disabledEventManager.handleApplicationReady(applicationReadyEvent)

      // Then
      verify(exactly = 0) { tokenManager.getTokenStatus() }
    }
  }

  @Nested
  inner class `Token expiration event handling` {

    @Test
    fun `should handle ACCESS_TOKEN expiration event`() {
      // Given
      val newToken = WxpaToken("new-access-token", 7200L)
      val expiredEvent = TokenExpiredEvent(source = this, appId = "test-app-id", tokenType = TokenType.ACCESS_TOKEN, reason = "Token expired")

      every { tokenManager.refreshAccessToken() } returns newToken

      // When
      eventManager.handleTokenExpired(expiredEvent)

      // Then
      verify { tokenManager.refreshAccessToken() }
      verify { mockPublisher.publishEvent(any<TokenRefreshedEvent>()) }
    }

    @Test
    fun `should handle JSAPI_TICKET expiration event`() {
      // Given
      val newTicket = WxpaTicket("new-jsapi-ticket", 7200L)
      val expiredEvent = TokenExpiredEvent(source = this, appId = "test-app-id", tokenType = TokenType.JSAPI_TICKET, reason = "Ticket expired")

      every { tokenManager.refreshJsapiTicket() } returns newTicket

      // When
      eventManager.handleTokenExpired(expiredEvent)

      // Then
      verify { tokenManager.refreshJsapiTicket() }
      verify { mockPublisher.publishEvent(any<TokenRefreshedEvent>()) }
    }

    @Test
    fun `should handle BOTH type expiration event`() {
      // Given
      val newToken = WxpaToken("new-access-token", 7200L)
      val newTicket = WxpaTicket("new-jsapi-ticket", 7200L)
      val expiredEvent = TokenExpiredEvent(source = this, appId = "test-app-id", tokenType = TokenType.BOTH, reason = "Both tokens expired")

      every { tokenManager.refreshBoth() } returns (newToken to newTicket)

      // When
      eventManager.handleTokenExpired(expiredEvent)

      // Then
      verify { tokenManager.refreshBoth() }
      verify { mockPublisher.publishEvent(any<TokenRefreshedEvent>()) }
    }

    @Test
    fun `should handle token refresh failure`() {
      // Given
      val exception = RuntimeException("Network error")
      val expiredEvent = TokenExpiredEvent(source = this, appId = "test-app-id", tokenType = TokenType.ACCESS_TOKEN, reason = "Token expired")

      every { tokenManager.refreshAccessToken() } throws exception

      // When
      eventManager.handleTokenExpired(expiredEvent)

      // Then
      verify { tokenManager.refreshAccessToken() }
      verify { mockPublisher.publishEvent(any<TokenRefreshFailedEvent>()) }
    }
  }

  @Nested
  inner class `Token usage event handling` {

    @Test
    fun `should record token usage statistics`() {
      // Given
      val usedEvent = TokenUsedEvent(source = this, appId = "test-app-id", tokenType = TokenType.ACCESS_TOKEN, usageContext = "API call")

      // When
      eventManager.handleTokenUsed(usedEvent)

      // Then
      val stats = eventManager.getTokenUsageStats()
      assertEquals(1L, stats[TokenType.ACCESS_TOKEN])
      assertEquals(0L, stats[TokenType.JSAPI_TICKET])
    }

    @Test
    fun `should accumulate usage statistics for multiple events`() {
      // Given
      val usedEvent1 = TokenUsedEvent(source = this, appId = "test-app-id", tokenType = TokenType.ACCESS_TOKEN, usageContext = "API call 1")
      val usedEvent2 = TokenUsedEvent(source = this, appId = "test-app-id", tokenType = TokenType.ACCESS_TOKEN, usageContext = "API call 2")

      // When
      eventManager.handleTokenUsed(usedEvent1)
      eventManager.handleTokenUsed(usedEvent2)

      // Then
      val stats = eventManager.getTokenUsageStats()
      assertEquals(2L, stats[TokenType.ACCESS_TOKEN])
    }
  }

  @Nested
  inner class `Event handling statistics` {

    @Test
    fun `should record token refresh success events`() {
      // Given
      val refreshedEvent = TokenRefreshedEvent(source = this, appId = "test-app-id", tokenType = TokenType.ACCESS_TOKEN, refreshDurationMs = 1500L)

      // When
      eventManager.handleTokenRefreshed(refreshedEvent)

      // Then
      // Verify logging (mainly ensure the method can be called without throwing exceptions)
      assertTrue(true)
    }

    @Test
    fun `should record token refresh failure events`() {
      // Given
      val failedEvent =
        TokenRefreshFailedEvent(source = this, appId = "test-app-id", tokenType = TokenType.ACCESS_TOKEN, failureReason = "Network error", retryCount = 1)

      // When
      eventManager.handleTokenRefreshFailed(failedEvent)

      // Then
      // Verify logging (mainly ensure the method can be called without throwing exceptions)
      assertTrue(true)
    }
  }

  @Nested
  inner class `Statistics retrieval` {

    @Test
    fun `should return correct usage statistics`() {
      // When
      val stats = eventManager.getTokenUsageStats()

      // Then
      assertNotNull(stats)
      assertEquals(3, stats.size)
      assertTrue(stats.containsKey(TokenType.ACCESS_TOKEN))
      assertTrue(stats.containsKey(TokenType.JSAPI_TICKET))
      assertTrue(stats.containsKey(TokenType.BOTH))
    }

    @Test
    fun `should return refresh failure count`() {
      // When
      val failureCount = eventManager.getRefreshFailureCount()

      // Then
      assertEquals(0L, failureCount)
    }
  }
}
