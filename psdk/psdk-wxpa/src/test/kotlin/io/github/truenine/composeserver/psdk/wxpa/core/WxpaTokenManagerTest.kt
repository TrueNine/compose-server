package io.github.truenine.composeserver.psdk.wxpa.core

import io.github.truenine.composeserver.psdk.wxpa.api.IWxpaWebClient
import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaApiException
import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaTokenException
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
  inner class `Access token management` {

    @Test
    fun `should get access token successfully`() {
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
    fun `should throw exception when API returns error`() {
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
    fun `should throw exception when API returns null`() {
      // Given
      every { apiClient.getAccessToken("test_app_id", "test_app_secret") } returns null

      // When & Then
      assertThrows<WxpaTokenException> { tokenManager.getValidAccessToken() }
    }

    @Test
    fun `should throw exception when access token is null`() {
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
  inner class `JSAPI ticket management` {

    @Test
    fun `should get jsapi ticket successfully`() {
      // Given - first set up access token
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
    fun `should throw exception when ticket API returns error`() {
      // Given - first set up access token
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
  inner class `Token status management` {

    @Test
    fun `should return token status correctly`() {
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
    fun `forceRefresh should clear all cached tokens`() {
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
  inner class `Exception handling` {

    @Test
    fun `should throw token exception when configuration is missing`() {
      // Given
      val invalidProperties = WxpaProperties(appId = "", appSecret = "test_app_secret")
      val invalidTokenManager = WxpaTokenManager(apiClient, invalidProperties)

      // When & Then
      assertThrows<WxpaTokenException> { invalidTokenManager.getValidAccessToken() }
    }

    @Test
    fun `should throw token exception when network error occurs`() {
      // Given
      every { apiClient.getAccessToken("test_app_id", "test_app_secret") } throws RuntimeException("Network error")

      // When & Then
      assertThrows<WxpaTokenException> { tokenManager.getValidAccessToken() }
    }
  }
}
