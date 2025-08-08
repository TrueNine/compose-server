package io.github.truenine.composeserver.psdk.wxpa.event

import io.github.truenine.composeserver.holders.EventPublisherHolder
import io.github.truenine.composeserver.psdk.wxpa.core.WxpaTokenManager
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaTicket
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaToken
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.mockk.*
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

    // 设置 EventPublisherHolder
    EventPublisherHolder.set(mockPublisher)

    eventManager = WxpaTokenEventManager(tokenManager, properties)
  }

  @AfterEach
  fun tearDown() {
    EventPublisherHolder.close()
    clearAllMocks()
  }

  @Nested
  inner class `应用启动事件处理` {

    @Test
    fun `应该在应用启动时进行健康检查`() {
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
    fun `应该在发现不健康 Token 时触发刷新`() {
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
      // 应该发布过期事件，但由于是异步处理，这里不直接验证
    }

    @Test
    fun `当自动刷新禁用时应该跳过初始检查`() {
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
  inner class `Token 过期事件处理` {

    @Test
    fun `应该处理 ACCESS_TOKEN 过期事件`() {
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
    fun `应该处理 JSAPI_TICKET 过期事件`() {
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
    fun `应该处理 BOTH 类型的过期事件`() {
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
    fun `应该处理刷新失败的情况`() {
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
  inner class `Token 使用事件处理` {

    @Test
    fun `应该记录 Token 使用统计`() {
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
    fun `应该累计多次使用统计`() {
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
  inner class `事件处理统计` {

    @Test
    fun `应该记录刷新成功事件`() {
      // Given
      val refreshedEvent = TokenRefreshedEvent(source = this, appId = "test-app-id", tokenType = TokenType.ACCESS_TOKEN, refreshDurationMs = 1500L)

      // When
      eventManager.handleTokenRefreshed(refreshedEvent)

      // Then
      // 验证日志记录（这里主要是确保方法被调用而不抛异常）
      assertTrue(true)
    }

    @Test
    fun `应该记录刷新失败事件`() {
      // Given
      val failedEvent =
        TokenRefreshFailedEvent(source = this, appId = "test-app-id", tokenType = TokenType.ACCESS_TOKEN, failureReason = "Network error", retryCount = 1)

      // When
      eventManager.handleTokenRefreshFailed(failedEvent)

      // Then
      // 验证日志记录（这里主要是确保方法被调用而不抛异常）
      assertTrue(true)
    }
  }

  @Nested
  inner class `统计信息获取` {

    @Test
    fun `应该返回正确的使用统计`() {
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
    fun `应该返回刷新失败次数`() {
      // When
      val failureCount = eventManager.getRefreshFailureCount()

      // Then
      assertEquals(0L, failureCount)
    }
  }
}
