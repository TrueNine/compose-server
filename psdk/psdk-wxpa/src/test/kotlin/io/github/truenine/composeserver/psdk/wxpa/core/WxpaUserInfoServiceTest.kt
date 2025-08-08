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
  inner class `通过授权码获取用户信息` {

    @Test
    fun `应该成功获取用户信息`() {
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
          every { nickName } returns "测试用户"
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
      assertEquals("测试用户", userInfo.nickname)
      assertEquals(listOf("privilege1", "privilege2"), userInfo.privilege)
      assertEquals("test_union_id", userInfo.unionId)
    }

    @Test
    fun `当 AppId 未配置时应该返回 null`() {
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
    fun `当 AppSecret 未配置时应该返回 null`() {
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
    fun `当获取 access token 失败时应该返回 null`() {
      // Given
      val authCode = "test_auth_code"
      every { apiClient.getWebsiteAccessToken("test_app_id", "test_app_secret", authCode) } returns null

      // When
      val userInfo = userInfoService.getUserInfoByAuthCode(authCode)

      // Then
      assertNull(userInfo)
    }

    @Test
    fun `当 access token 响应有错误时应该返回 null`() {
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
    fun `当 access token 为空时应该返回 null`() {
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
    fun `当 openId 为空时应该返回 null`() {
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
    fun `当获取用户信息失败时应该返回 null`() {
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
    fun `当用户信息中 openId 为空时应该返回 null`() {
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
  inner class `检查用户授权状态` {

    @Test
    fun `应该正确检查有效的用户授权`() {
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
    fun `应该正确检查无效的用户授权`() {
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
    fun `当用户信息中 openId 为空时应该返回 false`() {
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
    fun `当发生异常时应该返回 false`() {
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
