package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.domain.IReadableAttachment
import org.springframework.mock.web.MockMultipartFile
import java.nio.charset.StandardCharsets
import kotlin.test.*

class IReadableAttachmentFnsTest {

  @Test
  fun `toReadableAttachment should correctly convert a regular file`() {
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
  fun `toReadableAttachment should handle an empty file`() {
    val multipartFile = MockMultipartFile("emptyFile", "empty.txt", "text/plain", ByteArray(0))

    val attachment = multipartFile.toReadableAttachment()

    assertEquals("empty.txt", attachment.name)
    assertEquals("text/plain", attachment.mimeType)
    assertEquals(0L, attachment.size)
    assertEquals(0, attachment.bytes?.invoke()?.size ?: 0)
    assertTrue(attachment.empty)
  }

  @Test
  fun `toReadableAttachment should handle the case of no original filename`() {
    val content = "no original filename".toByteArray()
    val multipartFile =
      MockMultipartFile(
        "fieldName",
        null, // No original filename
        "application/octet-stream",
        content,
      )

    val attachment = multipartFile.toReadableAttachment()

    // Corrected conversion logic: this.originalFilename?.takeIf { it.isNotBlank() } ?: this.name
    // When originalFilename is an empty string, it should fall back to name
    assertEquals("fieldName", attachment.name) // Should now use the name
    assertEquals("application/octet-stream", attachment.mimeType)
    assertEquals(content.size.toLong(), attachment.size)
    assertFalse(attachment.empty)
  }

  @Test
  fun `toReadableAttachment should handle a null content type`() {
    val content = "null content type".toByteArray()
    val multipartFile =
      MockMultipartFile(
        "testFile",
        "test.bin",
        null, // null content type
        content,
      )

    val attachment = multipartFile.toReadableAttachment()

    assertEquals("test.bin", attachment.name)
    assertEquals(null, attachment.mimeType)
    assertEquals(content.size.toLong(), attachment.size)
    assertFalse(attachment.empty)
  }

  @Test
  fun `toReadableAttachment should provide a usable InputStream`() {
    val content = "stream test content".toByteArray(StandardCharsets.UTF_8)
    val multipartFile = MockMultipartFile("streamFile", "stream.txt", "text/plain", content)

    val attachment = multipartFile.toReadableAttachment()

    attachment.inputStream?.use { inputStream ->
      val readContent = inputStream.readAllBytes()
      assertEquals(content.contentToString(), readContent.contentToString())
    }
  }

  @Test
  fun `toReadableAttachment bytes function should return the same content`() {
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
  fun `toReadableAttachment should correctly handle a binary file`() {
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
  fun `toReadableAttachment should correctly handle a large file`() {
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
  fun `toReadableAttachment should create a DefaultReadableAttachment type`() {
    val multipartFile = MockMultipartFile("testFile", "test.txt", "text/plain", "test".toByteArray())

    val attachment = multipartFile.toReadableAttachment()

    assertTrue(attachment is IReadableAttachment.DefaultReadableAttachment)
  }

  @Test
  fun `toReadableAttachment multiple calls should produce independent instances`() {
    val content = "independence test".toByteArray()
    val multipartFile = MockMultipartFile("testFile", "test.txt", "text/plain", content)

    val attachment1 = multipartFile.toReadableAttachment()
    val attachment2 = multipartFile.toReadableAttachment()

    // Should be different instances but with the same content
    assertEquals(attachment1.name, attachment2.name)
    assertEquals(attachment1.mimeType, attachment2.mimeType)
    assertEquals(attachment1.size, attachment2.size)
    assertEquals(attachment1.bytes?.invoke()?.contentToString(), attachment2.bytes?.invoke()?.contentToString())
  }
}
