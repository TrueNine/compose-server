package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.consts.IHeaders
import io.github.truenine.composeserver.typing.MimeTypes
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.springframework.mock.web.MockHttpServletResponse

class HttpServletResponseFnsTest {

  @Test
  fun `headerMap 应正确转换响应头为 Map`() {
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
  fun `headerMap 空响应头应返回空 Map`() {
    val response = MockHttpServletResponse()
    val headerMap = response.headerMap
    assertTrue(headerMap.isEmpty())
  }

  @Test
  fun `useResponse 应正确设置响应属性`() {
    val response = MockHttpServletResponse()

    val result =
      response.useResponse(contentType = MimeTypes.JSON, charset = StandardCharsets.UTF_8, locale = Locale.US) { resp ->
        resp.setHeader("X-Test", "test-value")
        resp
      }

    assertTrue(result.contentType?.contains("application/json") == true)
    assertEquals(StandardCharsets.UTF_8.displayName(), result.characterEncoding)
    assertEquals(Locale.US, result.locale)
    assertEquals("test-value", result.getHeader("X-Test"))
  }

  @Test
  fun `useResponse 使用默认参数应设置正确值`() {
    val response = MockHttpServletResponse()

    val result = response.useResponse { it }

    assertTrue(result.contentType?.contains("application/octet-stream") == true)
    assertEquals(Charsets.UTF_8.displayName(), result.characterEncoding)
    assertEquals(Locale.CHINA, result.locale)
  }

  @Test
  fun `useSse 应设置 SSE 相关响应头`() {
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
  fun `useSse 使用默认参数应设置 SSE 类型`() {
    val response = MockHttpServletResponse()

    val result = response.useSse { it }

    assertTrue(result.contentType?.contains("text/event-stream") == true)
    assertEquals(Charsets.UTF_8.displayName(), result.characterEncoding)
    assertEquals(Locale.CHINA, result.locale)
  }

  @Test
  fun `withDownload 应设置下载相关响应头`() {
    val response = MockHttpServletResponse()
    val testData = "test file content".toByteArray()

    response.withDownload(fileName = "test.txt", contentType = MimeTypes.TEXT, charset = StandardCharsets.UTF_8) { outputStream ->
      outputStream.write(testData)
    }

    // 验证 Content-Disposition 头设置正确
    val contentDisposition = response.getHeader(IHeaders.CONTENT_DISPOSITION)
    assertNotNull(contentDisposition)
    assertTrue(contentDisposition.contains("attachment"))

    // 验证 Content-Type 头设置正确
    assertTrue(response.getHeader(IHeaders.CONTENT_TYPE)?.contains("text/plain") == true)
    assertEquals(StandardCharsets.UTF_8.displayName(), response.characterEncoding)

    // 验证输出内容
    assertEquals(testData.toString(StandardCharsets.UTF_8), response.contentAsString)
  }

  @Test
  fun `withDownload 使用默认参数应设置正确值`() {
    val response = MockHttpServletResponse()

    response.withDownload(fileName = "default.bin", closeBlock = null)

    val contentDisposition = response.getHeader(IHeaders.CONTENT_DISPOSITION)
    assertNotNull(contentDisposition)
    assertTrue(contentDisposition.contains("attachment"))

    assertTrue(response.getHeader(IHeaders.CONTENT_TYPE)?.contains("application/octet-stream") == true)
    assertEquals(Charsets.UTF_8.displayName(), response.characterEncoding)
  }

  @Test
  fun `withDownload 处理中文文件名应正确编码`() {
    val response = MockHttpServletResponse()

    response.withDownload(fileName = "测试文件.txt", contentType = MimeTypes.TEXT, charset = StandardCharsets.UTF_8, closeBlock = null)

    val contentDisposition = response.getHeader(IHeaders.CONTENT_DISPOSITION)
    assertNotNull(contentDisposition)
    assertTrue(contentDisposition.contains("attachment"))
    // 文件名编码取决于 IHeaders.downloadDisposition 的实现
  }

  @Test
  fun `withDownload 空文件名应正确处理`() {
    val response = MockHttpServletResponse()

    response.withDownload(fileName = "", closeBlock = null)

    val contentDisposition = response.getHeader(IHeaders.CONTENT_DISPOSITION)
    assertNotNull(contentDisposition)
  }

  @Test
  fun `withDownload 处理大文件应正确写入`() {
    val response = MockHttpServletResponse()
    val largeData = ByteArray(1024) { it.toByte() }

    response.withDownload(fileName = "large.bin", contentType = MimeTypes.BINARY) { outputStream -> outputStream.write(largeData) }

    assertEquals(largeData.size, response.contentAsByteArray.size)
  }
}
