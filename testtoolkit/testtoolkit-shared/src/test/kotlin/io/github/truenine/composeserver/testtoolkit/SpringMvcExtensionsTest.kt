package io.github.truenine.composeserver.testtoolkit

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import org.springframework.mock.web.MockMultipartFile

/**
 * Spring MVC extension function tests.
 *
 * Verifies the extension functions defined in SpringMvcExtensions.kt.
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class SpringMvcExtensionsTest {

  @Test
  fun mockMultipartFileCopyShouldPreserveOriginalProperties() {
    val originalContent = "Hello, World!".toByteArray()
    val original = MockMultipartFile("testFile", "test.txt", "text/plain", originalContent)
    val copied = original.copy()

    assertEquals(original.name, copied.name)
    assertEquals(original.originalFilename, copied.originalFilename)
    assertEquals(original.contentType, copied.contentType)
    assertContentEquals(original.bytes, copied.bytes)
  }

  @Test
  fun mockMultipartFileCopyShouldAllowCustomProperties() {
    val original = MockMultipartFile("originalFile", "original.txt", "text/plain", "Original".toByteArray())
    val newContent = "New content".toByteArray()
    val copied = original.copy(name = "newFile", originalFilename = "new.txt", contentType = "text/html", content = newContent)

    assertEquals("newFile", copied.name)
    assertEquals("new.txt", copied.originalFilename)
    assertEquals("text/html", copied.contentType)
    assertContentEquals(newContent, copied.bytes)
  }

  @Test
  fun mockMultipartFileCopyShouldHandlePartialOverrides() {
    val original = MockMultipartFile("testFile", "test.txt", "text/plain", "content".toByteArray())

    // override only name
    val copied1 = original.copy(name = "newName")
    assertEquals("newName", copied1.name)
    assertEquals(original.originalFilename, copied1.originalFilename)

    // override only content type
    val copied2 = original.copy(contentType = "application/json")
    assertEquals("application/json", copied2.contentType)
    assertEquals(original.name, copied2.name)
  }

  @Test
  fun mockMultipartFileCopyShouldHandleEdgeCases() {
    // empty content
    val emptyFile = MockMultipartFile("empty", "empty.txt", "text/plain", ByteArray(0))
    val copiedEmpty = emptyFile.copy()
    assertEquals(0, copiedEmpty.size)

    // null content type
    val nullContentType = MockMultipartFile("file", "file.txt", null, "content".toByteArray())
    val copiedNull = nullContentType.copy()
    assertEquals(null, copiedNull.contentType)
  }
}
