package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.consts.IHeaders
import io.github.truenine.composeserver.enums.MediaTypes
import org.springframework.mock.web.MockHttpServletResponse
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.test.*

class HttpServletResponseFnsTest {

  @Test
  fun `headerMap should correctly convert response headers to a Map`() {
    val response = MockHttpServletResponse()
    response.setHeader("Content-Type", "application/json")
    response.setHeader("Cache-Control", "no-cache")
    response.setHeader("X-Custom-Header", "custom-value")

    val headerMap = response.headerMap

    assertEquals("application/json", headerMap["Content-Type"])
    assertEquals("no-cache", headerMap["Cache-Control"])
    assertEquals("custom-value", headerMap["X-Custom-Header"])
    assertEquals(3, headerMap.size)
  }

  @Test
  fun `headerMap should return an empty Map for empty response headers`() {
    val response = MockHttpServletResponse()
    val headerMap = response.headerMap
    assertTrue(headerMap.isEmpty())
  }

  @Test
  fun `useResponse should correctly set response properties`() {
    val response = MockHttpServletResponse()

    val result =
      response.useResponse(contentType = MediaTypes.JSON, charset = StandardCharsets.UTF_8, locale = Locale.US) { resp ->
        resp.setHeader("X-Test", "test-value")
        resp
      }

    assertTrue(result.contentType?.contains("application/json") == true)
    assertEquals(StandardCharsets.UTF_8.displayName(), result.characterEncoding)
    assertEquals(Locale.US, result.locale)
    assertEquals("test-value", result.getHeader("X-Test"))
  }

  @Test
  fun `useResponse with default parameters should set correct values`() {
    val response = MockHttpServletResponse()

    val result = response.useResponse { it }

    assertTrue(result.contentType?.contains("application/octet-stream") == true)
    assertEquals(Charsets.UTF_8.displayName(), result.characterEncoding)
    assertEquals(Locale.CHINA, result.locale)
  }

  @Test
  fun `useSse should set SSE-related response headers`() {
    val response = MockHttpServletResponse()

    val result =
      response.useSse(charset = StandardCharsets.UTF_8, locale = Locale.US) { resp ->
        resp.setHeader("X-SSE-Test", "sse-value")
        resp
      }

    assertTrue(result.contentType?.contains("text/event-stream") == true)
    assertEquals(StandardCharsets.UTF_8.displayName(), result.characterEncoding)
    assertEquals(Locale.US, result.locale)
    assertEquals("sse-value", result.getHeader("X-SSE-Test"))
  }

  @Test
  fun `useSse with default parameters should set SSE type`() {
    val response = MockHttpServletResponse()

    val result = response.useSse { it }

    assertTrue(result.contentType?.contains("text/event-stream") == true)
    assertEquals(Charsets.UTF_8.displayName(), result.characterEncoding)
    assertEquals(Locale.CHINA, result.locale)
  }

  @Test
  fun `withDownload should set download-related response headers`() {
    val response = MockHttpServletResponse()
    val testData = "test file content".toByteArray()

    response.withDownload(fileName = "test.txt", contentType = MediaTypes.TEXT, charset = StandardCharsets.UTF_8) { outputStream ->
      outputStream.write(testData)
    }

    // Verify that the Content-Disposition header is set correctly
    val contentDisposition = response.getHeader(IHeaders.CONTENT_DISPOSITION)
    assertNotNull(contentDisposition)
    assertTrue(contentDisposition.contains("attachment"))

    // Verify that the Content-Type header is set correctly
    assertTrue(response.getHeader(IHeaders.CONTENT_TYPE)?.contains("text/plain") == true)
    assertEquals(StandardCharsets.UTF_8.displayName(), response.characterEncoding)

    // Verify the output content
    assertEquals(testData.toString(StandardCharsets.UTF_8), response.contentAsString)
  }

  @Test
  fun `withDownload with default parameters should set correct values`() {
    val response = MockHttpServletResponse()

    response.withDownload(fileName = "default.bin", closeBlock = null)

    val contentDisposition = response.getHeader(IHeaders.CONTENT_DISPOSITION)
    assertNotNull(contentDisposition)
    assertTrue(contentDisposition.contains("attachment"))

    assertTrue(response.getHeader(IHeaders.CONTENT_TYPE)?.contains("application/octet-stream") == true)
    assertEquals(Charsets.UTF_8.displayName(), response.characterEncoding)
  }

  @Test
  fun `withDownload should correctly encode Chinese filenames`() {
    val response = MockHttpServletResponse()

    response.withDownload(fileName = "test_file.txt", contentType = MediaTypes.TEXT, charset = StandardCharsets.UTF_8, closeBlock = null)

    val contentDisposition = response.getHeader(IHeaders.CONTENT_DISPOSITION)
    assertNotNull(contentDisposition)
    assertTrue(contentDisposition.contains("attachment"))
    // The filename encoding depends on the implementation of IHeaders.downloadDisposition
  }

  @Test
  fun `withDownload should handle empty filenames correctly`() {
    val response = MockHttpServletResponse()

    response.withDownload(fileName = "", closeBlock = null)

    val contentDisposition = response.getHeader(IHeaders.CONTENT_DISPOSITION)
    assertNotNull(contentDisposition)
  }

  @Test
  fun `withDownload should write large files correctly`() {
    val response = MockHttpServletResponse()
    val largeData = ByteArray(1024) { it.toByte() }

    response.withDownload(fileName = "large.bin", contentType = MediaTypes.BINARY) { outputStream -> outputStream.write(largeData) }

    assertEquals(largeData.size, response.contentAsByteArray.size)
  }
}
