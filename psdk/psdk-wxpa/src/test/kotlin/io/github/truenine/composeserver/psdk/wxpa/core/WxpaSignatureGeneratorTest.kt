package io.github.truenine.composeserver.psdk.wxpa.core

import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaSignatureException
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.github.truenine.composeserver.security.crypto.sha1
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class WxpaSignatureGeneratorTest {

  private lateinit var tokenManager: WxpaTokenManager
  private lateinit var properties: WxpaProperties
  private lateinit var signatureGenerator: WxpaSignatureGenerator

  @BeforeEach
  fun setup() {
    tokenManager = mockk()
    properties = WxpaProperties(appId = "test_app_id", appSecret = "test_app_secret", verifyToken = "test_verify_token")
    signatureGenerator = WxpaSignatureGenerator(tokenManager, properties)
  }

  @Nested
  inner class `JSAPI 签名生成` {

    @Test
    fun `应该成功生成 JSAPI 签名`() {
      // Given
      val testUrl = "https://example.com/test"
      val testTicket = "test_jsapi_ticket"
      every { tokenManager.getValidJsapiTicket() } returns testTicket

      // When
      val signature = signatureGenerator.generateJsapiSignature(testUrl)

      // Then
      assertNotNull(signature)
      assertEquals("test_app_id", signature.appId)
      assertEquals(testUrl, signature.url)
      assertNotNull(signature.nonceStr)
      assertNotNull(signature.timestamp)
      assertNotNull(signature.signature)
    }

    @Test
    fun `应该正确处理带锚点的 URL`() {
      // Given
      val testUrl = "https://example.com/test#anchor"
      val expectedUrl = "https://example.com/test"
      val testTicket = "test_jsapi_ticket"
      every { tokenManager.getValidJsapiTicket() } returns testTicket

      // When
      val signature = signatureGenerator.generateJsapiSignature(testUrl)

      // Then
      assertEquals(expectedUrl, signature.url)
    }

    @Test
    fun `应该使用提供的随机字符串和时间戳`() {
      // Given
      val testUrl = "https://example.com/test"
      val testNonceStr = "test_nonce_str"
      val testTimestamp = 1234567890L
      val testTicket = "test_jsapi_ticket"
      every { tokenManager.getValidJsapiTicket() } returns testTicket

      // When
      val signature = signatureGenerator.generateJsapiSignature(testUrl, testNonceStr, testTimestamp)

      // Then
      assertEquals(testNonceStr, signature.nonceStr)
      assertEquals(testTimestamp, signature.timestamp)
    }

    @Test
    fun `当 AppId 未配置时应该抛出异常`() {
      // Given
      val invalidProperties = WxpaProperties(appId = "", appSecret = "test_app_secret")
      val invalidGenerator = WxpaSignatureGenerator(tokenManager, invalidProperties)
      val testUrl = "https://example.com/test"

      // When & Then
      assertThrows<WxpaSignatureException> { invalidGenerator.generateJsapiSignature(testUrl) }
    }

    @Test
    fun `当获取 ticket 失败时应该抛出异常`() {
      // Given
      val testUrl = "https://example.com/test"
      every { tokenManager.getValidJsapiTicket() } throws RuntimeException("Failed to get ticket")

      // When & Then
      assertThrows<WxpaSignatureException> { signatureGenerator.generateJsapiSignature(testUrl) }
    }
  }

  @Nested
  inner class `服务器签名验证` {

    @Test
    fun `应该成功验证正确的服务器签名`() {
      // Given
      val timestamp = "1234567890"
      val nonce = "test_nonce"
      val token = "test_verify_token"

      // 计算预期的签名
      val sortedParams = listOf(token, timestamp, nonce).sorted()
      val signatureString = sortedParams.joinToString("")
      val expectedSignature = signatureString.sha1

      // When
      val isValid = signatureGenerator.verifyServerSignature(expectedSignature, timestamp, nonce)

      // Then
      assertTrue(isValid)
    }

    @Test
    fun `应该拒绝错误的服务器签名`() {
      // Given
      val timestamp = "1234567890"
      val nonce = "test_nonce"
      val wrongSignature = "wrong_signature"

      // When
      val isValid = signatureGenerator.verifyServerSignature(wrongSignature, timestamp, nonce)

      // Then
      assertFalse(isValid)
    }

    @Test
    fun `当验证 token 未配置时应该返回 false`() {
      // Given
      val invalidProperties = WxpaProperties(appId = "test_app_id", appSecret = "test_app_secret", verifyToken = "")
      val invalidGenerator = WxpaSignatureGenerator(tokenManager, invalidProperties)
      val timestamp = "1234567890"
      val nonce = "test_nonce"
      val signature = "test_signature"

      // When
      val isValid = invalidGenerator.verifyServerSignature(signature, timestamp, nonce)

      // Then
      assertFalse(isValid)
    }

    @Test
    fun `应该正确生成服务器验证响应`() {
      // Given
      val timestamp = "1234567890"
      val nonce = "test_nonce"
      val echostr = "test_echo_string"
      val token = "test_verify_token"

      // 计算正确的签名
      val sortedParams = listOf(token, timestamp, nonce).sorted()
      val signatureString = sortedParams.joinToString("")
      val correctSignature = signatureString.sha1

      // When
      val response = signatureGenerator.generateServerVerificationResponse(correctSignature, timestamp, nonce, echostr)

      // Then
      assertEquals(echostr, response)
    }

    @Test
    fun `当签名验证失败时应该返回 null`() {
      // Given
      val timestamp = "1234567890"
      val nonce = "test_nonce"
      val echostr = "test_echo_string"
      val wrongSignature = "wrong_signature"

      // When
      val response = signatureGenerator.generateServerVerificationResponse(wrongSignature, timestamp, nonce, echostr)

      // Then
      assertEquals(null, response)
    }
  }

  @Nested
  inner class `边界条件测试` {

    @Test
    fun `应该处理空字符串 URL`() {
      // Given
      val emptyUrl = ""
      val testTicket = "test_jsapi_ticket"
      every { tokenManager.getValidJsapiTicket() } returns testTicket

      // When
      val signature = signatureGenerator.generateJsapiSignature(emptyUrl)

      // Then
      assertEquals(emptyUrl, signature.url)
    }

    @Test
    fun `应该处理特殊字符的 URL`() {
      // Given
      val specialUrl = "https://example.com/test?param=value&other=测试"
      val testTicket = "test_jsapi_ticket"
      every { tokenManager.getValidJsapiTicket() } returns testTicket

      // When
      val signature = signatureGenerator.generateJsapiSignature(specialUrl)

      // Then
      assertEquals(specialUrl, signature.url)
    }
  }
}
