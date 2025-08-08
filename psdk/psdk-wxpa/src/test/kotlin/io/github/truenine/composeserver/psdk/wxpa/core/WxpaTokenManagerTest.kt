package io.github.truenine.composeserver.psdk.wxpa.core

import io.github.truenine.composeserver.psdk.wxpa.api.IWxpaWebClient
import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaApiException
import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaTokenException
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class WxpaTokenManagerTest {

  private lateinit var apiClient: IWxpaWebClient
  private lateinit var properties: WxpaProperties
  private lateinit var tokenManager: WxpaTokenManager

  @BeforeEach
  fun setup() {
    apiClient = mockk()
    properties = WxpaProperties(appId = "test_app_id", appSecret = "test_app_secret", verifyToken = "test_verify_token")
    tokenManager = WxpaTokenManager(apiClient, properties)
  }

  @Nested
  inner class `Access Token 管理` {

    @Test
    fun `应该成功获取 access token`() {
      // Given
      val mockResponse =
        mockk<IWxpaWebClient.WxpaGetAccessTokenResp> {
          every { isError } returns false
          every { accessToken } returns "mock_access_token"
          every { expireInSecond } returns 7200L
        }
      every { apiClient.getAccessToken("test_app_id", "test_app_secret") } returns mockResponse

      // When
      val token = tokenManager.getValidAccessToken()

      // Then
      assertEquals("mock_access_token", token)
      verify { apiClient.getAccessToken("test_app_id", "test_app_secret") }
    }

    @Test
    fun `当 API 返回错误时应该抛出异常`() {
      // Given
      val mockResponse =
        mockk<IWxpaWebClient.WxpaGetAccessTokenResp> {
          every { isError } returns true
          every { errorCode } returns 40001
          every { errorMessage } returns "invalid credential"
        }
      every { apiClient.getAccessToken("test_app_id", "test_app_secret") } returns mockResponse

      // When & Then
      assertThrows<WxpaApiException> { tokenManager.getValidAccessToken() }
    }

    @Test
    fun `当 API 返回 null 时应该抛出异常`() {
      // Given
      every { apiClient.getAccessToken("test_app_id", "test_app_secret") } returns null

      // When & Then
      assertThrows<WxpaTokenException> { tokenManager.getValidAccessToken() }
    }

    @Test
    fun `当 access token 为空时应该抛出异常`() {
      // Given
      val mockResponse =
        mockk<IWxpaWebClient.WxpaGetAccessTokenResp> {
          every { isError } returns false
          every { accessToken } returns null
          every { expireInSecond } returns 7200L
        }
      every { apiClient.getAccessToken("test_app_id", "test_app_secret") } returns mockResponse

      // When & Then
      assertThrows<WxpaTokenException> { tokenManager.getValidAccessToken() }
    }
  }

  @Nested
  inner class `JSAPI Ticket 管理` {

    @Test
    fun `应该成功获取 jsapi ticket`() {
      // Given - 先设置 access token
      val mockTokenResponse =
        mockk<IWxpaWebClient.WxpaGetAccessTokenResp> {
          every { isError } returns false
          every { accessToken } returns "mock_access_token"
          every { expireInSecond } returns 7200L
        }
      every { apiClient.getAccessToken("test_app_id", "test_app_secret") } returns mockTokenResponse

      val mockTicketResponse =
        mockk<IWxpaWebClient.WxpaGetTicketResp> {
          every { isError } returns false
          every { ticket } returns "mock_jsapi_ticket"
          every { expireInSecond } returns 7200L
        }
      every { apiClient.getTicket("mock_access_token") } returns mockTicketResponse

      // When
      val ticket = tokenManager.getValidJsapiTicket()

      // Then
      assertEquals("mock_jsapi_ticket", ticket)
    }

    @Test
    fun `当 ticket API 返回错误时应该抛出异常`() {
      // Given - 先设置 access token
      val mockTokenResponse =
        mockk<IWxpaWebClient.WxpaGetAccessTokenResp> {
          every { isError } returns false
          every { accessToken } returns "mock_access_token"
          every { expireInSecond } returns 7200L
        }
      every { apiClient.getAccessToken("test_app_id", "test_app_secret") } returns mockTokenResponse

      val mockTicketResponse =
        mockk<IWxpaWebClient.WxpaGetTicketResp> {
          every { isError } returns true
          every { errorCode } returns 40001
          every { errorMessage } returns "invalid access token"
        }
      every { apiClient.getTicket("mock_access_token") } returns mockTicketResponse

      // When & Then
      assertThrows<WxpaApiException> { tokenManager.getValidJsapiTicket() }
    }
  }

  @Nested
  inner class `Token 状态管理` {

    @Test
    fun `应该正确返回 token 状态`() {
      // When
      val status = tokenManager.getTokenStatus()

      // Then
      assertNotNull(status)
      assertTrue(status.containsKey("hasAccessToken"))
      assertTrue(status.containsKey("accessTokenExpired"))
      assertTrue(status.containsKey("hasJsapiTicket"))
      assertTrue(status.containsKey("jsapiTicketExpired"))
    }

    @Test
    fun `强制刷新应该清除所有缓存的 token`() {
      // Given
      val mockTokenResponse =
        mockk<IWxpaWebClient.WxpaGetAccessTokenResp> {
          every { isError } returns false
          every { accessToken } returns "mock_access_token"
          every { expireInSecond } returns 7200L
        }
      every { apiClient.getAccessToken("test_app_id", "test_app_secret") } returns mockTokenResponse

      val mockTicketResponse =
        mockk<IWxpaWebClient.WxpaGetTicketResp> {
          every { isError } returns false
          every { ticket } returns "mock_jsapi_ticket"
          every { expireInSecond } returns 7200L
        }
      every { apiClient.getTicket("mock_access_token") } returns mockTicketResponse

      // When
      tokenManager.forceRefreshAll()

      // Then
      verify { apiClient.getAccessToken("test_app_id", "test_app_secret") }
      verify { apiClient.getTicket("mock_access_token") }
    }
  }

  @Nested
  inner class `异常情况处理` {

    @Test
    fun `当配置缺失时应该抛出配置异常`() {
      // Given
      val invalidProperties = WxpaProperties(appId = "", appSecret = "test_app_secret")
      val invalidTokenManager = WxpaTokenManager(apiClient, invalidProperties)

      // When & Then
      assertThrows<WxpaTokenException> { invalidTokenManager.getValidAccessToken() }
    }

    @Test
    fun `当网络异常时应该抛出 token 异常`() {
      // Given
      every { apiClient.getAccessToken("test_app_id", "test_app_secret") } throws RuntimeException("Network error")

      // When & Then
      assertThrows<WxpaTokenException> { tokenManager.getValidAccessToken() }
    }
  }
}
