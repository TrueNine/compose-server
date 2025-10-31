package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import java.nio.charset.StandardCharsets
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Validates HTTP header constants and helper methods defined in {@link IHeaders}.
 */
class IHeadersTest {

  @Test
  fun verifiesStandardHttpHeaders() {
    log.info("Verifying standard HTTP header constants")

    assertEquals("Server", IHeaders.SERVER)
    assertEquals("Accept", IHeaders.ACCEPT)
    assertEquals("Accept-Encoding", IHeaders.ACCEPT_ENCODING)
    assertEquals("Accept-Language", IHeaders.ACCEPT_LANGUAGE)
    assertEquals("Cookie", IHeaders.COOKIE)
    assertEquals("Host", IHeaders.HOST)
    assertEquals("Referer", IHeaders.REFERER)
    assertEquals("User-Agent", IHeaders.USER_AGENT)

    log.info("Standard HTTP header constants verified")
  }

  @Test
  fun verifiesProxyHeaders() {
    log.info("Verifying proxy-related header constants")

    assertEquals("X-Forwarded-For", IHeaders.X_FORWARDED_FOR)
    assertEquals("X-Forwarded-Proto", IHeaders.X_FORWARDED_PROTO)
    assertEquals("Proxy-Client-IP", IHeaders.PROXY_CLIENT_IP)
    assertEquals("X-Real-IP", IHeaders.X_REAL_IP)

    log.info("Proxy-related header constants verified")
  }

  @Test
  fun verifiesCustomHeaders() {
    log.info("Verifying custom header constants")

    assertEquals("X-Device-Id", IHeaders.X_DEVICE_ID)
    assertEquals("X-Refresh", IHeaders.X_REFRESH)
    assertEquals("X-Require-Clean-Authentication", IHeaders.X_REQUIRE_CLEN_AUTHENTICATION)
    assertEquals("X-Wechat-Authorization-Id", IHeaders.X_WECHAT_AUTHORIZATION_ID)

    log.info("Custom header constants verified")
  }

  @Test
  fun createsDownloadDispositionWithUtf8() {
    log.info("Verifying downloadDisposition with UTF-8 encoded filename")

    val fileName = "résumé.txt"
    val result = IHeaders.downloadDisposition(fileName, StandardCharsets.UTF_8)

    log.info("Original filename: {}", fileName)
    log.info("Generated Content-Disposition: {}", result)

    assertTrue(result.startsWith("attachment; filename="), "Header should start with attachment directive")
    assertTrue(result.contains("%"), "Header should contain URL-encoded characters")
  }

  @Test
  fun createsDownloadDispositionForAsciiName() {
    log.info("Verifying downloadDisposition with ASCII filename")

    val fileName = "test-file.txt"
    val result = IHeaders.downloadDisposition(fileName, StandardCharsets.UTF_8)

    log.info("Original filename: {}", fileName)
    log.info("Generated Content-Disposition: {}", result)

    assertEquals("attachment; filename=test-file.txt", result)
  }

  @Test
  fun createsDownloadDispositionForSpecialCharacters() {
    log.info("Verifying downloadDisposition with special characters in filename")

    val fileName = "file with spaces & symbols!.pdf"
    val result = IHeaders.downloadDisposition(fileName, StandardCharsets.UTF_8)

    log.info("Original filename: {}", fileName)
    log.info("Generated Content-Disposition: {}", result)

    assertTrue(result.startsWith("attachment; filename="), "Header should start with attachment directive")
    assertTrue(result.contains("%"), "Special characters should be URL-encoded")
  }

  // Note: getDeviceId requires an HttpServletRequest and should be validated via integration tests.

  @Test
  fun enforcesHeaderNamingConventions() {
    log.info("Verifying header naming conventions")

    // Custom headers should use the X- prefix
    assertTrue(IHeaders.X_FORWARDED_FOR.startsWith("X-"), "X_FORWARDED_FOR should start with X-")
    assertTrue(IHeaders.X_FORWARDED_PROTO.startsWith("X-"), "X_FORWARDED_PROTO should start with X-")
    assertTrue(IHeaders.X_REAL_IP.startsWith("X-"), "X_REAL_IP should start with X-")
    assertTrue(IHeaders.X_DEVICE_ID.startsWith("X-"), "X_DEVICE_ID should start with X-")
    assertTrue(IHeaders.X_REFRESH.startsWith("X-"), "X_REFRESH should start with X-")
    assertTrue(IHeaders.X_REQUIRE_CLEN_AUTHENTICATION.startsWith("X-"), "X_REQUIRE_CLEN_AUTHENTICATION should start with X-")
    assertTrue(IHeaders.X_WECHAT_AUTHORIZATION_ID.startsWith("X-"), "X_WECHAT_AUTHORIZATION_ID should start with X-")

    // CORS headers should use the Access-Control- prefix
    assertTrue(IHeaders.CORS_ALLOW_ORIGIN.startsWith("Access-Control-"), "CORS_ALLOW_ORIGIN should start with Access-Control-")
    assertTrue(IHeaders.CORS_ALLOW_METHODS.startsWith("Access-Control-"), "CORS_ALLOW_METHODS should start with Access-Control-")
    assertTrue(IHeaders.CORS_ALLOW_HEADERS.startsWith("Access-Control-"), "CORS_ALLOW_HEADERS should start with Access-Control-")
    assertTrue(IHeaders.CORS_ALLOW_CREDENTIALS.startsWith("Access-Control-"), "CORS_ALLOW_CREDENTIALS should start with Access-Control-")

    log.info("Header naming conventions validated")
  }
}
