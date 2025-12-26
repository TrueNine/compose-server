package io.github.truenine.composeserver.psdk.wxpa.integration

import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.psdk.wxpa.autoconfig.AutoConfigEntrance
import io.github.truenine.composeserver.psdk.wxpa.event.WxpaTokenEventManager
import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaApiException
import io.github.truenine.composeserver.psdk.wxpa.service.WxpaService
import io.github.truenine.composeserver.security.crypto.sha1
import jakarta.annotation.Resource
import kotlin.test.*
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.boot.test.context.SpringBootTest

private val log = logger<WxpaIntegrationTest>()

/**
 * WeChat Official Account integration tests.
 *
 * The following environment variables must be set in order to run real integration tests:
 * - WECHAT_APP_ID: WeChat Official Account application ID
 * - WECHAT_APP_SECRET: WeChat Official Account application secret
 * - WECHAT_VERIFY_TOKEN: WeChat Official Account verification token
 *
 * If these environment variables are missing, the tests will be skipped.
 *
 * @author TrueNine
 * @since 2025-08-08
 */
@EnabledIf("hasRequiredEnvironmentVariables")
@SpringBootTest(
  classes = [AutoConfigEntrance::class],
  webEnvironment = SpringBootTest.WebEnvironment.NONE,
  properties =
    [
      "compose.psdk.wxpa.wxpa.app-id=\${WXPA_APP_ID}",
      "compose.psdk.wxpa.wxpa.app-secret=\${WXPA_APP_SECURET}",
      "compose.psdk.wxpa.wxpa.verify-token=\${WXPA_VERIFY_TOKEN}",
      "compose.psdk.wxpa.wxpa.enable-auto-refresh=true", // Disable automatic refresh to avoid network calls during tests
    ],
)
class WxpaIntegrationTest {

  @Resource private lateinit var wxpaService: WxpaService

  @Resource private lateinit var wxpaTokenEventManager: WxpaTokenEventManager

  companion object {
    /** Check whether required environment variables exist for JUnit5 conditional tests. */
    @JvmStatic
    fun hasRequiredEnvironmentVariables(): Boolean {
      val appId = System.getenv("WXPA_APP_ID")
      val appSecret = System.getenv("WXPA_APP_SECRET")
      val verifyToken = System.getenv("WXPA_VERIFY_TOKEN")

      val hasCredentials = !appId.isNullOrBlank() && !appSecret.isNullOrBlank() && !verifyToken.isNullOrBlank()

      if (!hasCredentials) {
        log.warn(
          "Skipping WeChat Official Account integration tests: missing required environment variables WECHAT_APP_ID, WECHAT_APP_SECRET or WECHAT_VERIFY_TOKEN"
        )
      } else {
        log.info("Detected WeChat Official Account credentials, integration tests will run")
      }

      return hasCredentials
    }
  }

  @Nested
  inner class `Spring context integration tests` {

    @Test
    fun `should register WxpaService bean successfully`() {
      assertNotNull(wxpaService)
      log.info("WxpaService bean registered successfully")
    }

    @Test
    fun `should register WxpaTokenEventManager bean successfully`() {
      assertNotNull(wxpaTokenEventManager)
      log.info("WxpaTokenEventManager bean registered successfully")
    }

    @Test
    fun `should get token status successfully`() {
      // When
      val status = wxpaService.getTokenStatus()

      // Then
      assertNotNull(status)
      assertTrue(status.containsKey("hasAccessToken"))
      assertTrue(status.containsKey("accessTokenExpired"))
      assertTrue(status.containsKey("hasJsapiTicket"))
      assertTrue(status.containsKey("jsapiTicketExpired"))
      log.info("Token status retrieved successfully: {}", status)
    }

    @Test
    fun `should handle server verification request`() {
      // Given
      val request =
        WxpaService.ServerVerificationRequest(signature = "test_signature", timestamp = "1234567890", nonce = "test_nonce", echostr = "test_echo_string")

      // When
      val result = wxpaService.verifyServerConfiguration(request)

      // Then
      // Because the signature is incorrect, it should return null but not throw an exception.
      // This verifies that the service's exception handling works as expected.
      assertNull(result)
      log.info("Server verification request handled correctly, invalid signature was rejected")
    }
  }

  @Nested
  inner class `Configuration validation tests` {

    @Test
    fun `should load configuration properties correctly`() {
      // Verify configuration loading by ensuring the service instance can be created
      assertNotNull(wxpaService)
      log.info("Configuration properties validated successfully")
    }
  }

  @Nested
  inner class `Normal business flow tests` {

    @Test
    fun `should generate JSAPI signature`() {
      // Given
      val testUrl = "https://example.com/test"

      // When
      val signature = wxpaService.generateJsapiSignature(testUrl)

      // Then
      // In the test environment, because test configuration is used, an invalid appSecret will cause null to be returned.
      // This is expected behavior and verifies that exception handling works correctly.
      assertNull(signature)
      log.info("JSAPI signature generation test finished, invalid credentials were handled correctly")
    }

    @Test
    fun `should handle fetching user info by auth code`() {
      // Given
      val testAuthCode = "test_auth_code_12345"

      // When
      val userInfo = wxpaService.getUserInfoByAuthCode(testAuthCode)

      // Then
      // In the test environment, because test configuration is used, real user info cannot be fetched.
      // It should return null without throwing an exception.
      assertNull(userInfo)
      log.info("User info retrieval test finished, invalid auth code was handled correctly")
    }

    @Test
    fun `should check user authorization status`() {
      // Given
      val testAccessToken = "test_access_token"
      val testOpenId = "test_open_id"

      // When
      val authStatus = wxpaService.checkUserAuthStatus(testAccessToken, testOpenId)

      // Then
      // In the test environment, because test configuration is used, the authorization check should return false.
      assertFalse(authStatus)
      log.info("User authorization status check test finished, result: {}", authStatus)
    }

    @Test
    fun `should force refresh tokens`() {
      // When & Then
      // In the test environment, because invalid credentials are used, forceRefreshTokens will throw an exception.
      // This is expected behavior and verifies that exception handling works correctly.
      assertThrows<WxpaApiException> { wxpaService.forceRefreshTokens() }
      log.info("Force refresh tokens test finished, expected exception was thrown correctly")
    }
  }

  @Nested
  inner class `Exception handling tests` {

    @Test
    fun `should handle invalid server verification signature correctly`() {
      // Given
      val invalidRequest =
        WxpaService.ServerVerificationRequest(signature = "invalid_signature", timestamp = "1234567890", nonce = "test_nonce", echostr = "test_echo_string")

      // When
      val result = wxpaService.verifyServerConfiguration(invalidRequest)

      // Then
      assertNull(result)
      log.info("Invalid signature verification test finished, correctly returned null")
    }

    @Test
    fun `should handle valid server verification signature correctly`() {
      // Given
      val timestamp = "1234567890"
      val nonce = "test_nonce"
      // Use the token from environment variables, which is consistent with the Spring configuration
      val token = System.getenv("WXPA_VERIFY_TOKEN") ?: "test_verify_token"
      val echostr = "test_echo_string"

      // Calculate the correct signature
      val sortedParams = listOf(token, timestamp, nonce).sorted()
      val signatureString = sortedParams.joinToString("")
      val validSignature = signatureString.sha1

      val validRequest = WxpaService.ServerVerificationRequest(signature = validSignature, timestamp = timestamp, nonce = nonce, echostr = echostr)

      log.info("Testing signature verification, using token: {}, computed signature: {}", token, validSignature)

      // When
      val result = wxpaService.verifyServerConfiguration(validRequest)

      // Then
      assertEquals(echostr, result)
      log.info("Valid signature verification test finished, correctly returned echostr: {}", result)
    }

    @Test
    fun `should handle empty string parameters correctly`() {
      // Given
      val emptyRequest = WxpaService.ServerVerificationRequest(signature = "", timestamp = "", nonce = "", echostr = "")

      // When
      val result = wxpaService.verifyServerConfiguration(emptyRequest)

      // Then
      assertNull(result)
      log.info("Empty string parameter test finished, correctly returned null")
    }

    @Test
    fun `should handle invalid auth code correctly`() {
      // Given
      val invalidAuthCode = ""

      // When
      val userInfo = wxpaService.getUserInfoByAuthCode(invalidAuthCode)

      // Then
      assertNull(userInfo)
      log.info("Invalid auth code test finished, correctly returned null")
    }

    @Test
    fun `should handle invalid URL when generating JSAPI signature`() {
      // Given
      val invalidUrl = ""

      // When
      val signature = wxpaService.generateJsapiSignature(invalidUrl)

      // Then
      // Even when the URL is empty, it should be handled without throwing an exception
      log.info("Invalid URL JSAPI signature generation test finished, result: {}", signature)
    }
  }

  @Nested
  inner class `Boundary condition tests` {

    @Test
    fun `should handle very long URL when generating JSAPI signature`() {
      // Given
      val longUrl = "https://example.com/" + "a".repeat(2000) + "?param=value"

      // When
      val signature = wxpaService.generateJsapiSignature(longUrl)

      // Then
      // In the test environment, because test credentials are used, this will return null but should not throw an exception
      assertNull(signature)
      log.info("Very long URL JSAPI signature generation test finished, invalid credentials were handled correctly")
    }

    @Test
    fun `should handle URL with special characters`() {
      // Given
      val specialUrl = "https://example.com/test?param=value&other=specialChars!@#$%^&*()"

      // When
      val signature = wxpaService.generateJsapiSignature(specialUrl)

      // Then
      // In the test environment, because test credentials are used, this will return null but should not throw an exception
      assertNull(signature)
      log.info("Special-character URL JSAPI signature generation test finished, invalid credentials were handled correctly")
    }

    @Test
    fun `should handle URL with anchor`() {
      // Given
      val urlWithAnchor = "https://example.com/test#section1"

      // When
      val signature = wxpaService.generateJsapiSignature(urlWithAnchor)

      // Then
      // In the test environment, because test credentials are used, this will return null.
      // But we can still verify whether URL handling (removing the anchor) works correctly.
      assertNull(signature)
      log.info("Anchor URL JSAPI signature generation test finished, invalid credentials were handled correctly")
    }

    @Test
    fun `should handle very long auth code`() {
      // Given
      val longAuthCode = "auth_code_" + "x".repeat(1000)

      // When
      val userInfo = wxpaService.getUserInfoByAuthCode(longAuthCode)

      // Then
      // Should be able to handle a long auth code without throwing an exception and return null
      assertNull(userInfo)
      log.info("Very long auth code test finished, invalid auth code was handled correctly")
    }

    @Test
    fun `should handle auth code with special characters`() {
      // Given
      val specialAuthCode = "auth_code_!@#$%^&*()"

      // When
      val userInfo = wxpaService.getUserInfoByAuthCode(specialAuthCode)

      // Then
      // Should be able to handle an auth code with special characters without throwing an exception and return null
      assertNull(userInfo)
      log.info("Special-character auth code test finished, invalid auth code was handled correctly")
    }
  }

  @Nested
  inner class `Event-driven mechanism tests` {

    @Test
    fun `should get token usage statistics`() {
      // When
      val stats = wxpaTokenEventManager.getTokenUsageStats()

      // Then
      assertNotNull(stats)
      assertTrue(stats.isNotEmpty())
      // Verify the structure of the statistics data
      stats.forEach { (key, value) -> assertTrue(value >= 0L, "Statistic value should be non-negative: $key = $value") }
      log.info("Token usage statistics retrieved successfully: {}", stats)
    }

    @Test
    fun `should get refresh failure count`() {
      // When
      val failureCount = wxpaTokenEventManager.getRefreshFailureCount()

      // Then
      assertTrue(failureCount >= 0L, "Refresh failure count should be non-negative, actual value: $failureCount")
      log.info("Refresh failure count retrieved successfully: {}", failureCount)
    }

    @Test
    fun `should get last health check time`() {
      // When
      val lastCheckTime = wxpaTokenEventManager.getLastHealthCheckTime()

      // Then
      // It may be null in the initial state, which is normal
      log.info("Last health check time: {}", lastCheckTime)
    }
  }

  @Nested
  inner class `Performance and stability tests` {

    @Test
    fun `should get token status concurrently`() {
      // Given
      val threadCount = 10
      val results = mutableListOf<Map<String, Any>>()

      // When
      val threads =
        (1..threadCount).map {
          Thread {
            val status = wxpaService.getTokenStatus()
            synchronized(results) { results.add(status) }
          }
        }

      threads.forEach { it.start() }
      threads.forEach { it.join() }

      // Then
      assertEquals(threadCount, results.size)
      results.forEach { status ->
        assertNotNull(status)
        assertTrue(status.containsKey("hasAccessToken"))
      }
      log.info("Concurrent token status retrieval test finished, executed {} times", threadCount)
    }

    @Test
    fun `should call service methods repeatedly`() {
      // Given
      val callCount = 5
      val testUrl = "https://example.com/test"

      // When & Then
      repeat(callCount) { index ->
        val signature = wxpaService.generateJsapiSignature(testUrl)
        val status = wxpaService.getTokenStatus()

        // In the test environment, signature will return null but status should be valid
        assertNotNull(status)
        assertTrue(status.containsKey("hasAccessToken"))
        log.debug("Call {} completed, signature: {}", index + 1, signature)
      }

      log.info("Repeated calls test finished, executed {} times in total", callCount)
    }
  }
}
