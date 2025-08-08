package io.github.truenine.composeserver.psdk.wxpa.service

import io.github.truenine.composeserver.psdk.wxpa.core.WxpaSignatureGenerator
import io.github.truenine.composeserver.psdk.wxpa.core.WxpaTokenManager
import io.github.truenine.composeserver.psdk.wxpa.core.WxpaUserInfoService
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaSignature
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaUserInfo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WxpaServiceTest {

  private lateinit var tokenManager: WxpaTokenManager
  private lateinit var signatureGenerator: WxpaSignatureGenerator
  private lateinit var userInfoService: WxpaUserInfoService
  private lateinit var wxpaService: WxpaService

  @BeforeEach
  fun setup() {
    tokenManager = mockk()
    signatureGenerator = mockk()
    userInfoService = mockk()
    wxpaService = WxpaService(tokenManager, signatureGenerator, userInfoService)
  }

  @Nested
  inner class `服务器配置验证` {

    @Test
    fun `应该成功验证服务器配置`() {
      // Given
      val request =
        WxpaService.ServerVerificationRequest(signature = "test_signature", timestamp = "1234567890", nonce = "test_nonce", echostr = "test_echo_string")
      every { signatureGenerator.generateServerVerificationResponse("test_signature", "1234567890", "test_nonce", "test_echo_string") } returns
        "test_echo_string"

      // When
      val result = wxpaService.verifyServerConfiguration(request)

      // Then
      assertEquals("test_echo_string", result)
      verify { signatureGenerator.generateServerVerificationResponse("test_signature", "1234567890", "test_nonce", "test_echo_string") }
    }

    @Test
    fun `当验证失败时应该返回 null`() {
      // Given
      val request =
        WxpaService.ServerVerificationRequest(signature = "wrong_signature", timestamp = "1234567890", nonce = "test_nonce", echostr = "test_echo_string")
      every { signatureGenerator.generateServerVerificationResponse("wrong_signature", "1234567890", "test_nonce", "test_echo_string") } returns null

      // When
      val result = wxpaService.verifyServerConfiguration(request)

      // Then
      assertNull(result)
    }

    @Test
    fun `当发生异常时应该返回 null`() {
      // Given
      val request =
        WxpaService.ServerVerificationRequest(signature = "test_signature", timestamp = "1234567890", nonce = "test_nonce", echostr = "test_echo_string")
      every { signatureGenerator.generateServerVerificationResponse(any(), any(), any(), any()) } throws RuntimeException("Test exception")

      // When
      val result = wxpaService.verifyServerConfiguration(request)

      // Then
      assertNull(result)
    }
  }

  @Nested
  inner class `JSAPI 签名生成` {

    @Test
    fun `应该成功生成 JSAPI 签名`() {
      // Given
      val testUrl = "https://example.com/test"
      val mockSignature = WxpaSignature(appId = "test_app_id", nonceStr = "test_nonce", timestamp = 1234567890L, url = testUrl, signature = "test_signature")
      every { signatureGenerator.generateJsapiSignature(testUrl, null, null) } returns mockSignature

      // When
      val result = wxpaService.generateJsapiSignature(testUrl)

      // Then
      assertNotNull(result)
      assertEquals(mockSignature, result)
      verify { signatureGenerator.generateJsapiSignature(testUrl, null, null) }
    }

    @Test
    fun `当签名生成失败时应该返回 null`() {
      // Given
      val testUrl = "https://example.com/test"
      every { signatureGenerator.generateJsapiSignature(any(), any(), any()) } throws RuntimeException("Test exception")

      // When
      val result = wxpaService.generateJsapiSignature(testUrl)

      // Then
      assertNull(result)
    }
  }

  @Nested
  inner class `用户信息获取` {

    @Test
    fun `应该成功获取用户信息`() {
      // Given
      val authCode = "test_auth_code"
      val mockUserInfo = WxpaUserInfo(openId = "test_open_id", nickname = "测试用户", privilege = emptyList(), unionId = "test_union_id")
      every { userInfoService.getUserInfoByAuthCode(authCode) } returns mockUserInfo

      // When
      val result = wxpaService.getUserInfoByAuthCode(authCode)

      // Then
      assertNotNull(result)
      assertEquals(mockUserInfo, result)
      verify { userInfoService.getUserInfoByAuthCode(authCode) }
    }

    @Test
    fun `当获取用户信息失败时应该返回 null`() {
      // Given
      val authCode = "test_auth_code"
      every { userInfoService.getUserInfoByAuthCode(authCode) } throws RuntimeException("Test exception")

      // When
      val result = wxpaService.getUserInfoByAuthCode(authCode)

      // Then
      assertNull(result)
    }
  }

  @Nested
  inner class `Token 管理` {

    @Test
    fun `应该正确获取 token 状态`() {
      // Given
      val mockStatus = mapOf("hasAccessToken" to true, "accessTokenExpired" to false, "hasJsapiTicket" to true, "jsapiTicketExpired" to false)
      every { tokenManager.getTokenStatus() } returns mockStatus

      // When
      val result = wxpaService.getTokenStatus()

      // Then
      assertEquals(mockStatus, result)
      verify { tokenManager.getTokenStatus() }
    }

    @Test
    fun `应该正确强制刷新 tokens`() {
      // Given
      every { tokenManager.forceRefreshAll() } returns Unit

      // When
      wxpaService.forceRefreshTokens()

      // Then
      verify { tokenManager.forceRefreshAll() }
    }
  }
}
