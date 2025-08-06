package io.github.truenine.composeserver.security.crypto

import io.github.truenine.composeserver.consts.IRegexes
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class ByteArrayExtensionsTest {
  private val log = LoggerFactory.getLogger(ByteArrayExtensionsTest::class.java)

  private val testText = "Hello, World! 测试文本 🌟"
  private val testBytes = testText.toByteArray(Charsets.UTF_8)
  private val expectedBase64 = "SGVsbG8sIFdvcmxkISDmtYvor5XmlofmnKwg8J+Mnw=="

  // === Basic Functionality Tests ===

  @Test
  fun `encodeBase64 property returns correct base64 byte array`() {
    val result = testBytes.encodeBase64
    val resultString = String(result, Charsets.UTF_8)

    assertNotEquals(testBytes, result)
    assertEquals(expectedBase64, resultString)
    assertTrue(IRegexes.BASE_64.toRegex().matches(resultString))
  }

  @Test
  fun `encodeBase64String property returns correct base64 string`() {
    val result = testBytes.encodeBase64String

    assertNotEquals(testText, result)
    assertEquals(expectedBase64, result)
    assertTrue(IRegexes.BASE_64.toRegex().matches(result))
  }

  @Test
  fun `decodeBase64 function returns original byte array`() {
    val base64Bytes = expectedBase64.toByteArray(Charsets.UTF_8)
    val result = base64Bytes.decodeBase64()
    val resultString = String(result, Charsets.UTF_8)

    assertNotEquals(base64Bytes, result)
    assertContentEquals(testBytes, result)
    assertEquals(testText, resultString)
  }

  @Test
  fun `decodeBase64String function with default charset returns original string`() {
    val base64Bytes = expectedBase64.toByteArray(Charsets.UTF_8)
    val result = base64Bytes.decodeBase64String()

    assertNotEquals(expectedBase64, result)
    assertEquals(testText, result)
  }

  @Test
  fun `decodeBase64String function with custom charset works correctly`() {
    val originalText = "UTF-16 test string"
    val utf16Bytes = originalText.toByteArray(Charsets.UTF_16)
    val encoded = utf16Bytes.encodeBase64String
    val encodedBytes = encoded.toByteArray(Charsets.UTF_8)

    val decodedUtf16 = encodedBytes.decodeBase64String(Charsets.UTF_16)
    val decodedUtf8 = encodedBytes.decodeBase64String(Charsets.UTF_8)

    assertEquals(originalText, decodedUtf16)
    assertNotEquals(originalText, decodedUtf8)
  }

  // === Round-trip Tests ===

  @Test
  fun `round trip encoding and decoding preserves original data`() {
    val originalData = "Round trip test data with special chars: !@#$%^&*()".toByteArray()

    // Test byte array round trip
    val encodedBytes = originalData.encodeBase64
    val decodedBytes = encodedBytes.decodeBase64()
    assertContentEquals(originalData, decodedBytes)

    // Test string round trip
    val encodedString = originalData.encodeBase64String
    val encodedStringBytes = encodedString.toByteArray(Charsets.UTF_8)
    val decodedFromString = encodedStringBytes.decodeBase64()
    assertContentEquals(originalData, decodedFromString)
  }

  @Test
  fun `round trip with different charsets preserves data integrity`() {
    val charsets = listOf(Charsets.UTF_8, Charsets.UTF_16, Charsets.ISO_8859_1)

    charsets.forEach { charset ->
      val originalText = "Charset test: $charset"
      val originalBytes = originalText.toByteArray(charset)

      val encoded = originalBytes.encodeBase64String
      val encodedBytes = encoded.toByteArray(Charsets.UTF_8)
      val decoded = encodedBytes.decodeBase64String(charset)

      assertEquals(originalText, decoded, "Round trip failed for charset: $charset")
    }
  }

  // === Edge Cases and Boundary Tests ===

  @Test
  fun `handle empty byte array encoding`() {
    val emptyArray = byteArrayOf()

    val encodedBytes = emptyArray.encodeBase64
    val encodedString = emptyArray.encodeBase64String

    assertContentEquals(byteArrayOf(), encodedBytes)
    assertEquals("", encodedString)
  }

  @Test
  fun `handle empty byte array decoding`() {
    val emptyBase64 = "".toByteArray(Charsets.UTF_8)

    // decodeBase64() should work with empty string (empty byte array)
    val decodedBytes = emptyBase64.decodeBase64()
    assertContentEquals(byteArrayOf(), decodedBytes)

    // decodeBase64String() should return empty string for empty byte array
    val decodedString = emptyBase64.decodeBase64String()
    assertEquals("", decodedString)
  }

  @Test
  fun `handle single byte input`() {
    val singleByte = byteArrayOf(65) // 'A'

    val encodedBytes = singleByte.encodeBase64
    val encodedString = singleByte.encodeBase64String

    assertTrue(IRegexes.BASE_64.toRegex().matches(String(encodedBytes, Charsets.UTF_8)))
    assertTrue(IRegexes.BASE_64.toRegex().matches(encodedString))

    // Test round trip
    val decodedFromBytes = encodedBytes.decodeBase64()
    val decodedFromString = encodedString.toByteArray().decodeBase64()

    assertContentEquals(singleByte, decodedFromBytes)
    assertContentEquals(singleByte, decodedFromString)
  }

  @Test
  fun `handle large data sets efficiently`() {
    val largeData = ByteArray(10000) { (it % 256).toByte() }

    val encodedBytes = largeData.encodeBase64
    val encodedString = largeData.encodeBase64String

    assertTrue(IRegexes.BASE_64.toRegex().matches(String(encodedBytes, Charsets.UTF_8)))
    assertTrue(IRegexes.BASE_64.toRegex().matches(encodedString))

    // Test round trip for large data
    val decodedFromBytes = encodedBytes.decodeBase64()
    val decodedFromString = encodedString.toByteArray().decodeBase64()

    assertContentEquals(largeData, decodedFromBytes)
    assertContentEquals(largeData, decodedFromString)
  }

  @Test
  fun `handle binary data with all byte values`() {
    val binaryData = ByteArray(256) { it.toByte() }

    val encodedBytes = binaryData.encodeBase64
    val encodedString = binaryData.encodeBase64String

    assertTrue(IRegexes.BASE_64.toRegex().matches(String(encodedBytes, Charsets.UTF_8)))
    assertTrue(IRegexes.BASE_64.toRegex().matches(encodedString))

    // Test round trip for binary data
    val decodedFromBytes = encodedBytes.decodeBase64()
    val decodedFromString = encodedString.toByteArray().decodeBase64()

    assertContentEquals(binaryData, decodedFromBytes)
    assertContentEquals(binaryData, decodedFromString)
  }

  // === Error Handling Tests ===

  @Test
  fun `decodeBase64 throws exception for invalid base64 data`() {
    val invalidBase64Strings =
      listOf(
        "Invalid!Base64@String", // Contains invalid characters
        "SGVsbG8gV29ybGQ!", // Contains invalid character !
        "SGVsbG8gV29ybGQ===", // Too much padding
        "SGVsbG8gV29ybGQ====", // Way too much padding
        "SGVsbG8gV29ybGQ#", // Contains invalid character #
      )

    invalidBase64Strings.forEach { invalidString ->
      val invalidBytes = invalidString.toByteArray(Charsets.UTF_8)
      assertFailsWith<IllegalArgumentException> { invalidBytes.decodeBase64() }
    }
  }

  @Test
  fun `decodeBase64String throws exception for invalid base64 data`() {
    val invalidBase64 = "Invalid!Base64@String".toByteArray(Charsets.UTF_8)

    assertFailsWith<IllegalArgumentException> { invalidBase64.decodeBase64String() }

    assertFailsWith<IllegalArgumentException> { invalidBase64.decodeBase64String(Charsets.UTF_16) }
  }

  @Test
  fun `decodeBase64 handles blank string correctly`() {
    val blankBase64 = "   ".toByteArray(Charsets.UTF_8)

    assertFailsWith<IllegalArgumentException> { blankBase64.decodeBase64() }
  }

  // === Performance and Consistency Tests ===

  @Test
  fun `encoding operations produce consistent results`() {
    val testData = "Consistency test data".toByteArray()

    // Multiple calls should produce identical results
    val encoded1 = testData.encodeBase64
    val encoded2 = testData.encodeBase64
    val encodedString1 = testData.encodeBase64String
    val encodedString2 = testData.encodeBase64String

    assertContentEquals(encoded1, encoded2)
    assertEquals(encodedString1, encodedString2)
    assertEquals(String(encoded1, Charsets.UTF_8), encodedString1)
  }

  @Test
  fun `decoding operations produce consistent results`() {
    val base64Data = "Q29uc2lzdGVuY3kgdGVzdA==".toByteArray(Charsets.UTF_8)

    // Multiple calls should produce identical results
    val decoded1 = base64Data.decodeBase64()
    val decoded2 = base64Data.decodeBase64()
    val decodedString1 = base64Data.decodeBase64String()
    val decodedString2 = base64Data.decodeBase64String()

    assertContentEquals(decoded1, decoded2)
    assertEquals(decodedString1, decodedString2)
    assertEquals(String(decoded1, Charsets.UTF_8), decodedString1)
  }

  @Test
  fun `extensions work with IBase64 interface consistently`() {
    val testData = "Interface consistency test".toByteArray()

    // Compare extension results with direct IBase64 calls
    val extensionEncodedBytes = testData.encodeBase64
    val directEncodedBytes = IBase64.encodeToByte(testData)
    assertContentEquals(extensionEncodedBytes, directEncodedBytes)

    val extensionEncodedString = testData.encodeBase64String
    val directEncodedString = IBase64.encode(testData)
    assertEquals(extensionEncodedString, directEncodedString)

    // Test decoding consistency
    val base64String = "SW50ZXJmYWNlIGNvbnNpc3RlbmN5IHRlc3Q="
    val base64Bytes = base64String.toByteArray(Charsets.UTF_8)

    val extensionDecoded = base64Bytes.decodeBase64()
    val directDecoded = IBase64.decodeToByte(base64String)
    assertContentEquals(extensionDecoded, directDecoded)

    val extensionDecodedString = base64Bytes.decodeBase64String()
    val directDecodedString = IBase64.decode(base64Bytes)
    assertEquals(extensionDecodedString, directDecodedString)
  }
}
