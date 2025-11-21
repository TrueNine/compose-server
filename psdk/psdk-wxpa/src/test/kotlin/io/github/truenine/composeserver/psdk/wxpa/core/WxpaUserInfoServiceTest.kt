package io.github.truenine.composeserver.psdk.wxpa.core

import io.github.truenine.composeserver.psdk.wxpa.api.IWxpaWebClient
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WxpaUserInfoServiceTest {

  private lateinit var apiClient: IWxpaWebClient
  private lateinit var properties: WxpaProperties
  private lateinit var userInfoService: WxpaUserInfoService

  @BeforeEach
  fun setup() {
    apiClient = mockk()
    properties = WxpaProperties(appId = "test_app_id", appSecret = "test_app_secret", verifyToken = "test_verify_token")
    userInfoService = WxpaUserInfoService(apiClient, properties)
  }

  @Nested
  inner class `Get user info by auth code` {

    @Test
    fun `should get user info successfully`() {
      // Given
      val authCode = "test_auth_code"
      val mockTokenResponse =
        mockk<IWxpaWebClient.WxpaWebsiteAuthGetAccessTokenResp> {
          every { isError } returns false
          every { accessToken } returns "test_access_token"
          every { openId } returns "test_open_id"
        }
      val mockUserInfoResponse =
        mockk<IWxpaWebClient.WxpaWebsiteUserInfoResp> {
          every { openId } returns "test_open_id"
          every { nickName } returns "Test user"
          every { privilege } returns listOf("privilege1", "privilege2")
          every { unionId } returns "test_union_id"
        }

      every { apiClient.getWebsiteAccessToken("test_app_id", "test_app_secret", authCode) } returns mockTokenResponse

      every { apiClient.getUserInfoByAccessToken("test_access_token", "test_open_id") } returns mockUserInfoResponse

      // When
      val userInfo = userInfoService.getUserInfoByAuthCode(authCode)

      // Then
      assertNotNull(userInfo)
      assertEquals("test_open_id", userInfo.openId)
      assertEquals("Test user", userInfo.nickname)
      assertEquals(listOf("privilege1", "privilege2"), userInfo.privilege)
      assertEquals("test_union_id", userInfo.unionId)
    }

    @Test
    fun `should return null when AppId is not configured`() {
      // Given
      val invalidProperties = WxpaProperties(appId = "", appSecret = "test_app_secret")
      val invalidService = WxpaUserInfoService(apiClient, invalidProperties)
      val authCode = "test_auth_code"

      // When
      val userInfo = invalidService.getUserInfoByAuthCode(authCode)

      // Then
      assertNull(userInfo)
    }

    @Test
    fun `should return null when AppSecret is not configured`() {
      // Given
      val invalidProperties = WxpaProperties(appId = "test_app_id", appSecret = "")
      val invalidService = WxpaUserInfoService(apiClient, invalidProperties)
      val authCode = "test_auth_code"

      // When
      val userInfo = invalidService.getUserInfoByAuthCode(authCode)

      // Then
      assertNull(userInfo)
    }

    @Test
    fun `should return null when access token retrieval fails`() {
      // Given
      val authCode = "test_auth_code"
      every { apiClient.getWebsiteAccessToken("test_app_id", "test_app_secret", authCode) } returns null

      // When
      val userInfo = userInfoService.getUserInfoByAuthCode(authCode)

      // Then
      assertNull(userInfo)
    }

    @Test
    fun `should return null when access token response has error`() {
      // Given
      val authCode = "test_auth_code"
      val mockTokenResponse =
        mockk<IWxpaWebClient.WxpaWebsiteAuthGetAccessTokenResp> {
          every { isError } returns true
          every { errorCode } returns 40001
          every { errorMessage } returns "invalid credential"
        }

      every { apiClient.getWebsiteAccessToken("test_app_id", "test_app_secret", authCode) } returns mockTokenResponse

      // When
      val userInfo = userInfoService.getUserInfoByAuthCode(authCode)

      // Then
      assertNull(userInfo)
    }

    @Test
    fun `should return null when access token is null`() {
      // Given
      val authCode = "test_auth_code"
      val mockTokenResponse =
        mockk<IWxpaWebClient.WxpaWebsiteAuthGetAccessTokenResp> {
          every { isError } returns false
          every { accessToken } returns null
          every { openId } returns "test_open_id"
        }

      every { apiClient.getWebsiteAccessToken("test_app_id", "test_app_secret", authCode) } returns mockTokenResponse

      // When
      val userInfo = userInfoService.getUserInfoByAuthCode(authCode)

      // Then
      assertNull(userInfo)
    }

    @Test
    fun `should return null when openId is null`() {
      // Given
      val authCode = "test_auth_code"
      val mockTokenResponse =
        mockk<IWxpaWebClient.WxpaWebsiteAuthGetAccessTokenResp> {
          every { isError } returns false
          every { accessToken } returns "test_access_token"
          every { openId } returns null
        }

      every { apiClient.getWebsiteAccessToken("test_app_id", "test_app_secret", authCode) } returns mockTokenResponse

      // When
      val userInfo = userInfoService.getUserInfoByAuthCode(authCode)

      // Then
      assertNull(userInfo)
    }

    @Test
    fun `should return null when user info retrieval fails`() {
      // Given
      val authCode = "test_auth_code"
      val mockTokenResponse =
        mockk<IWxpaWebClient.WxpaWebsiteAuthGetAccessTokenResp> {
          every { isError } returns false
          every { accessToken } returns "test_access_token"
          every { openId } returns "test_open_id"
        }

      every { apiClient.getWebsiteAccessToken("test_app_id", "test_app_secret", authCode) } returns mockTokenResponse

      every { apiClient.getUserInfoByAccessToken("test_access_token", "test_open_id") } returns null

      // When
      val userInfo = userInfoService.getUserInfoByAuthCode(authCode)

      // Then
      assertNull(userInfo)
    }

    @Test
    fun `should return null when openId in user info is null`() {
      // Given
      val authCode = "test_auth_code"
      val mockTokenResponse =
        mockk<IWxpaWebClient.WxpaWebsiteAuthGetAccessTokenResp> {
          every { isError } returns false
          every { accessToken } returns "test_access_token"
          every { openId } returns "test_open_id"
        }
      val mockUserInfoResponse = mockk<IWxpaWebClient.WxpaWebsiteUserInfoResp> { every { openId } returns null }

      every { apiClient.getWebsiteAccessToken("test_app_id", "test_app_secret", authCode) } returns mockTokenResponse

      every { apiClient.getUserInfoByAccessToken("test_access_token", "test_open_id") } returns mockUserInfoResponse

      // When
      val userInfo = userInfoService.getUserInfoByAuthCode(authCode)

      // Then
      assertNull(userInfo)
    }
  }

  @Nested
  inner class `Check user authorization status` {

    @Test
    fun `should validate valid user authorization correctly`() {
      // Given
      val accessToken = "test_access_token"
      val userOpenId = "test_open_id"
      val mockUserInfoResponse = mockk<IWxpaWebClient.WxpaWebsiteUserInfoResp> { every { openId } returns "test_open_id" }

      every { apiClient.getUserInfoByAccessToken(accessToken, userOpenId) } returns mockUserInfoResponse

      // When
      val isValid = userInfoService.checkUserAuthStatus(accessToken, userOpenId)

      // Then
      assertTrue(isValid)
    }

    @Test
    fun `should validate invalid user authorization correctly`() {
      // Given
      val accessToken = "invalid_access_token"
      val openId = "test_open_id"

      every { apiClient.getUserInfoByAccessToken(accessToken, openId) } returns null

      // When
      val isValid = userInfoService.checkUserAuthStatus(accessToken, openId)

      // Then
      assertFalse(isValid)
    }

    @Test
    fun `should return false when openId in user info is empty`() {
      // Given
      val accessToken = "test_access_token"
      val userOpenId = "test_open_id"
      val mockUserInfoResponse = mockk<IWxpaWebClient.WxpaWebsiteUserInfoResp> { every { openId } returns "" }

      every { apiClient.getUserInfoByAccessToken(accessToken, userOpenId) } returns mockUserInfoResponse

      // When
      val isValid = userInfoService.checkUserAuthStatus(accessToken, userOpenId)

      // Then
      assertFalse(isValid)
    }

    @Test
    fun `should return false when exception occurs`() {
      // Given
      val accessToken = "test_access_token"
      val openId = "test_open_id"

      every { apiClient.getUserInfoByAccessToken(accessToken, openId) } throws RuntimeException("Network error")

      // When
      val isValid = userInfoService.checkUserAuthStatus(accessToken, openId)

      // Then
      assertFalse(isValid)
    }
  }
}
