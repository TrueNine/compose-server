package io.github.truenine.composeserver.security.crypto

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

/** Test cases for String extension functions */
class StringFnsTest {

  @Test
  fun `test uuid generation`() {
    val id1 = uuid()
    val id2 = uuid()

    assertNotNull(id1)
    assertNotNull(id2)
    assertTrue(id1.isNotEmpty())
    assertTrue(id2.isNotEmpty())
    assertTrue(id1 != id2, "Generated UUIDs should be unique")

    // UUID format validation (36 characters with hyphens)
    assertEquals(36, id1.length)
    assertEquals(4, id1.count { it == '-' })
  }

  @Test
  fun `test base64 encoding and decoding`() {
    val originalText = "Hello, World! 你好世界"

    val encoded = originalText.base64()
    assertNotNull(encoded)
    assertTrue(encoded.isNotEmpty())
    assertTrue(encoded != originalText)

    val decoded = encoded.base64Decode()
    assertEquals(originalText, decoded)
  }

  @Test
  fun `test base64 with different charsets`() {
    val originalText = "测试文本"

    val encodedUtf8 = originalText.base64(Charsets.UTF_8)
    val encodedUtf16 = originalText.base64(Charsets.UTF_16)

    assertTrue(encodedUtf8 != encodedUtf16)

    val decodedUtf8 = encodedUtf8.base64Decode(Charsets.UTF_8)
    val decodedUtf16 = encodedUtf16.base64Decode(Charsets.UTF_16)

    assertEquals(originalText, decodedUtf8)
    assertEquals(originalText, decodedUtf16)
  }

  @Test
  fun `test base64DecodeByteArray`() {
    val originalText = "Test data"
    val encoded = originalText.base64()

    val decodedBytes = encoded.base64DecodeByteArray
    val decodedText = String(decodedBytes, Charsets.UTF_8)

    assertEquals(originalText, decodedText)
  }

  @Test
  fun `test sha1 hash`() {
    val text = "Hello World"
    val hash = text.sha1

    assertNotNull(hash)
    assertTrue(hash.isNotEmpty())
    assertTrue(hash != text)
    assertTrue(hash.length >= 32) // SHA1 produces at least 32 hex characters

    // Same input should produce same hash
    assertEquals(hash, text.sha1)
  }

  @Test
  fun `test sha256 hash`() {
    val text = "Hello World"
    val hash = text.sha256

    assertNotNull(hash)
    assertTrue(hash.isNotEmpty())
    assertTrue(hash != text)
    assertTrue(hash.length >= 32) // SHA256 produces at least 32 hex characters

    // Same input should produce same hash
    assertEquals(hash, text.sha256)

    // SHA256 should be different from SHA1
    assertTrue(hash != text.sha1)
  }

  @Test
  fun `test empty string operations`() {
    val emptyString = ""

    val uuid = uuid()
    assertTrue(uuid.isNotEmpty())

    val encoded = emptyString.base64()
    val decoded = encoded.base64Decode()
    assertEquals(emptyString, decoded)

    val sha1 = emptyString.sha1
    val sha256 = emptyString.sha256
    assertNotNull(sha1)
    assertNotNull(sha256)
    assertTrue(sha1 != sha256)
  }
}
