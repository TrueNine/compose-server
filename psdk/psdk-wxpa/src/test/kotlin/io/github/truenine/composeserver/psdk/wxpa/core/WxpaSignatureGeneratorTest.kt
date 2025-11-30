package io.github.truenine.composeserver.psdk.wxpa.core

import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaSignatureException
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.github.truenine.composeserver.security.crypto.sha1
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.*
import kotlin.test.assertNotNull

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
  inner class `JSAPI signature generation` {

    @Test
    fun `should generate JSAPI signature successfully`() {
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
    fun `should handle URL with anchor correctly`() {
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
    fun `should use provided nonce string and timestamp`() {
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
    fun `should throw exception when AppId is not configured`() {
      // Given
      val invalidProperties = WxpaProperties(appId = "", appSecret = "test_app_secret")
      val invalidGenerator = WxpaSignatureGenerator(tokenManager, invalidProperties)
      val testUrl = "https://example.com/test"

      // When & Then
      assertThrows<WxpaSignatureException> { invalidGenerator.generateJsapiSignature(testUrl) }
    }

    @Test
    fun `should throw exception when ticket retrieval fails`() {
      // Given
      val testUrl = "https://example.com/test"
      every { tokenManager.getValidJsapiTicket() } throws RuntimeException("Failed to get ticket")

      // When & Then
      assertThrows<WxpaSignatureException> { signatureGenerator.generateJsapiSignature(testUrl) }
    }
  }

  @Nested
  inner class `Server signature verification` {

    @Test
    fun `should validate correct server signature successfully`() {
      // Given
      val timestamp = "1234567890"
      val nonce = "test_nonce"
      val token = "test_verify_token"

      // Calculate expected signature
      val sortedParams = listOf(token, timestamp, nonce).sorted()
      val signatureString = sortedParams.joinToString("")
      val expectedSignature = signatureString.sha1

      // When
      val isValid = signatureGenerator.verifyServerSignature(expectedSignature, timestamp, nonce)

      // Then
      assertTrue(isValid)
    }

    @Test
    fun `should reject invalid server signature`() {
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
    fun `should return false when verify token is not configured`() {
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
    fun `should generate server verification response correctly`() {
      // Given
      val timestamp = "1234567890"
      val nonce = "test_nonce"
      val echostr = "test_echo_string"
      val token = "test_verify_token"

      // Calculate correct signature
      val sortedParams = listOf(token, timestamp, nonce).sorted()
      val signatureString = sortedParams.joinToString("")
      val correctSignature = signatureString.sha1

      // When
      val response = signatureGenerator.generateServerVerificationResponse(correctSignature, timestamp, nonce, echostr)

      // Then
      assertEquals(echostr, response)
    }

    @Test
    fun `should return null when server signature verification fails`() {
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
  inner class `Edge case tests` {

    @Test
    fun `should handle empty URL`() {
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
    fun `should handle URL with special characters`() {
      // Given
      val specialUrl = "https://example.com/test?param=value&other=special"
      val testTicket = "test_jsapi_ticket"
      every { tokenManager.getValidJsapiTicket() } returns testTicket

      // When
      val signature = signatureGenerator.generateJsapiSignature(specialUrl)

      // Then
      assertEquals(specialUrl, signature.url)
    }
  }
}
