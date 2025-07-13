package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.domain.IReadableAttachment
import java.nio.charset.StandardCharsets
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.springframework.mock.web.MockMultipartFile

class IReadableAttachmentFnsTest {

  @Test
  fun `toReadableAttachment 应正确转换普通文件`() {
    val content = "test file content".toByteArray(StandardCharsets.UTF_8)
    val multipartFile = MockMultipartFile("testFile", "test.txt", "text/plain", content)

    val attachment = multipartFile.toReadableAttachment()

    assertEquals("test.txt", attachment.name)
    assertEquals("text/plain", attachment.mimeType)
    assertEquals(content.size.toLong(), attachment.size)
    assertEquals(content.contentToString(), attachment.bytes?.invoke()?.contentToString())
    assertFalse(attachment.empty)
  }

  @Test
  fun `toReadableAttachment 应处理空文件`() {
    val multipartFile = MockMultipartFile("emptyFile", "empty.txt", "text/plain", ByteArray(0))

    val attachment = multipartFile.toReadableAttachment()

    assertEquals("empty.txt", attachment.name)
    assertEquals("text/plain", attachment.mimeType)
    assertEquals(0L, attachment.size)
    assertEquals(0, attachment.bytes?.invoke()?.size ?: 0)
    assertTrue(attachment.empty)
  }

  @Test
  fun `toReadableAttachment 应处理无原始文件名的情况`() {
    val content = "no original filename".toByteArray()
    val multipartFile =
      MockMultipartFile(
        "fieldName",
        null, // 无原始文件名
        "application/octet-stream",
        content,
      )

    val attachment = multipartFile.toReadableAttachment()

    // 修复后的转换逻辑：this.originalFilename?.takeIf { it.isNotBlank() } ?: this.name
    // 当 originalFilename 为空字符串时，应该 fallback 到 name
    assertEquals("fieldName", attachment.name) // 现在应该使用 name
    assertEquals("application/octet-stream", attachment.mimeType)
    assertEquals(content.size.toLong(), attachment.size)
    assertFalse(attachment.empty)
  }

  @Test
  fun `toReadableAttachment 应处理 null 内容类型`() {
    val content = "null content type".toByteArray()
    val multipartFile =
      MockMultipartFile(
        "testFile",
        "test.bin",
        null, // null 内容类型
        content,
      )

    val attachment = multipartFile.toReadableAttachment()

    assertEquals("test.bin", attachment.name)
    assertEquals(null, attachment.mimeType)
    assertEquals(content.size.toLong(), attachment.size)
    assertFalse(attachment.empty)
  }

  @Test
  fun `toReadableAttachment 应提供可用的 InputStream`() {
    val content = "stream test content".toByteArray(StandardCharsets.UTF_8)
    val multipartFile = MockMultipartFile("streamFile", "stream.txt", "text/plain", content)

    val attachment = multipartFile.toReadableAttachment()

    attachment.inputStream?.use { inputStream ->
      val readContent = inputStream.readAllBytes()
      assertEquals(content.contentToString(), readContent.contentToString())
    }
  }

  @Test
  fun `toReadableAttachment 字节函数应返回相同内容`() {
    val content = "bytes test content".toByteArray(StandardCharsets.UTF_8)
    val multipartFile = MockMultipartFile("bytesFile", "bytes.txt", "text/plain", content)

    val attachment = multipartFile.toReadableAttachment()

    val bytes1 = attachment.bytes?.invoke()
    val bytes2 = attachment.bytes?.invoke()

    assertEquals(content.contentToString(), bytes1?.contentToString())
    assertEquals(content.contentToString(), bytes2?.contentToString())
    assertEquals(bytes1?.contentToString(), bytes2?.contentToString())
  }

  @Test
  fun `toReadableAttachment 应正确处理二进制文件`() {
    val binaryContent = ByteArray(256) { it.toByte() }
    val multipartFile = MockMultipartFile("binaryFile", "binary.bin", "application/octet-stream", binaryContent)

    val attachment = multipartFile.toReadableAttachment()

    assertEquals("binary.bin", attachment.name)
    assertEquals("application/octet-stream", attachment.mimeType)
    assertEquals(256L, attachment.size)
    assertEquals(binaryContent.contentToString(), attachment.bytes?.invoke()?.contentToString())
    assertFalse(attachment.empty)
  }

  @Test
  fun `toReadableAttachment 应正确处理大文件`() {
    val largeContent = ByteArray(10240) { (it % 256).toByte() }
    val multipartFile = MockMultipartFile("largeFile", "large.bin", "application/octet-stream", largeContent)

    val attachment = multipartFile.toReadableAttachment()

    assertEquals("large.bin", attachment.name)
    assertEquals("application/octet-stream", attachment.mimeType)
    assertEquals(10240L, attachment.size)
    assertEquals(largeContent.size, attachment.bytes?.invoke()?.size ?: 0)
    assertFalse(attachment.empty)
  }

  @Test
  fun `toReadableAttachment 应创建 DefaultReadableAttachment 类型`() {
    val multipartFile = MockMultipartFile("testFile", "test.txt", "text/plain", "test".toByteArray())

    val attachment = multipartFile.toReadableAttachment()

    assertTrue(attachment is IReadableAttachment.DefaultReadableAttachment)
  }

  @Test
  fun `toReadableAttachment 多次调用应产生独立实例`() {
    val content = "independence test".toByteArray()
    val multipartFile = MockMultipartFile("testFile", "test.txt", "text/plain", content)

    val attachment1 = multipartFile.toReadableAttachment()
    val attachment2 = multipartFile.toReadableAttachment()

    // 应该是不同的实例但内容相同
    assertEquals(attachment1.name, attachment2.name)
    assertEquals(attachment1.mimeType, attachment2.mimeType)
    assertEquals(attachment1.size, attachment2.size)
    assertEquals(attachment1.bytes?.invoke()?.contentToString(), attachment2.bytes?.invoke()?.contentToString())
  }
}
