package io.github.truenine.composeserver.security.crypto

import io.github.truenine.composeserver.consts.IRegexes
import io.github.truenine.composeserver.slf4j
import java.util.Base64
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

private val log = slf4j<IBase64Test>()

class IBase64Test {
  private val testText = "Hello, World! sample text"
  private val testBytes = testText.toByteArray(Charsets.UTF_8)
  private val expectedBase64 = Base64.getEncoder().encodeToString(testBytes)

  // === Basic Functionality Tests ===

  @Test
  fun `encode byte array to base64 string`() {
    val result = IBase64.encode(testBytes)

    assertNotEquals(testText, result)
    assertEquals(expectedBase64, result)
    assertTrue(IRegexes.BASE_64.toRegex().matches(result))
  }

  @Test
  fun `encode byte array to base64 byte array`() {
    val result = IBase64.encodeToByte(testBytes)
    val resultString = String(result, Charsets.UTF_8)

    assertNotEquals(testBytes, result)
    assertEquals(expectedBase64, resultString)
    assertTrue(IRegexes.BASE_64.toRegex().matches(resultString))
  }

  @Test
  fun `decode base64 string to string with default charset`() {
    val result = IBase64.decode(expectedBase64)

    assertNotEquals(expectedBase64, result)
    assertEquals(testText, result)
  }

  @Test
  fun `decode base64 string to byte array`() {
    val result = IBase64.decodeToByte(expectedBase64)
    val resultString = String(result, Charsets.UTF_8)

    assertNotEquals(expectedBase64.toByteArray(), result)
    assertEquals(testText, resultString)
  }

  @Test
  fun `decode base64 byte array to string`() {
    val base64Bytes = expectedBase64.toByteArray(Charsets.UTF_8)
    val result = IBase64.decode(base64Bytes)

    assertEquals(testText, result)
  }

  @Test
  fun `round trip encoding and decoding preserves data`() {
    val originalData = "Complex data with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?"
    val originalBytes = originalData.toByteArray(Charsets.UTF_8)

    val encoded = IBase64.encode(originalBytes)
    val decoded = IBase64.decode(encoded)

    assertEquals(originalData, decoded)
  }

  // === URL-Safe Base64 Tests ===

  @Test
  fun `encode url safe base64`() {
    val dataWithSpecialChars = "data>with?special&chars="
    val bytes = dataWithSpecialChars.toByteArray()

    val urlSafeEncoded = IBase64.encodeUrlSafe(bytes)
    val standardEncoded = IBase64.encode(bytes)

    // URL-safe should not contain +, /, or = characters
    assertFalse(urlSafeEncoded.contains('+'))
    assertFalse(urlSafeEncoded.contains('/'))
    assertFalse(urlSafeEncoded.contains('='))

    // Should be different from standard encoding if it contains special chars
    if (standardEncoded.contains('+') || standardEncoded.contains('/') || standardEncoded.contains('=')) {
      assertNotEquals(standardEncoded, urlSafeEncoded)
    }
  }

  @Test
  fun `decode url safe base64`() {
    val originalData = "URL-safe test data with special characters"
    val bytes = originalData.toByteArray()

    val urlSafeEncoded = IBase64.encodeUrlSafe(bytes)
    val decoded = IBase64.decodeUrlSafe(urlSafeEncoded)
    val decodedString = String(decoded, Charsets.UTF_8)

    assertEquals(originalData, decodedString)
  }

  // === Charset Handling Tests ===

  @Test
  fun `encode and decode with different charsets`() {
    val originalText = "Multi-byte characters: العربية русский Ελληνικά"
    val charsets = listOf(Charsets.UTF_8, Charsets.UTF_16, Charsets.ISO_8859_1)

    charsets.forEach { charset ->
      val bytes = originalText.toByteArray(charset)
      val encoded = IBase64.encode(bytes)
      val decoded = IBase64.decode(encoded, charset)

      if (charset == Charsets.ISO_8859_1) {
        // ISO_8859_1 cannot represent all Unicode characters
        assertNotEquals(originalText, decoded)
      } else {
        assertEquals(originalText, decoded, "Failed for charset: $charset")
      }
    }
  }

  @Test
  fun `decode with explicit charset specification`() {
    val originalText = "UTF-16 test string"
    val utf16Bytes = originalText.toByteArray(Charsets.UTF_16)
    val encoded = IBase64.encode(utf16Bytes)

    val decodedUtf16 = IBase64.decode(encoded, Charsets.UTF_16)
    val decodedUtf8 = IBase64.decode(encoded, Charsets.UTF_8)

    assertEquals(originalText, decodedUtf16)
    assertNotEquals(originalText, decodedUtf8)
  }

  // === Edge Cases and Boundary Tests ===

  @Test
  fun `handle single byte input`() {
    val singleByte = byteArrayOf(65) // 'A'
    val encoded = IBase64.encode(singleByte)
    val decoded = IBase64.decodeToByte(encoded)

    assertTrue(IRegexes.BASE_64.toRegex().matches(encoded))
    assertContentEquals(singleByte, decoded)
  }

  @Test
  fun `handle large data sets`() {
    val largeData = ByteArray(10000) { (it % 256).toByte() }

    val encoded = IBase64.encode(largeData)
    val decoded = IBase64.decodeToByte(encoded)

    assertTrue(IRegexes.BASE_64.toRegex().matches(encoded))
    assertContentEquals(largeData, decoded)
  }

  @Test
  fun `handle binary data with all byte values`() {
    val binaryData = ByteArray(256) { it.toByte() }

    val encoded = IBase64.encode(binaryData)
    val decoded = IBase64.decodeToByte(encoded)

    assertTrue(IRegexes.BASE_64.toRegex().matches(encoded))
    assertContentEquals(binaryData, decoded)
  }

  // === Error Handling Tests ===

  @Test
  fun `encode handles empty byte array`() {
    val emptyArray = byteArrayOf()
    val result = IBase64.encode(emptyArray)
    assertEquals("", result)
  }

  @Test
  fun `encodeToByte handles empty byte array`() {
    val emptyArray = byteArrayOf()
    val result = IBase64.encodeToByte(emptyArray)
    assertContentEquals(byteArrayOf(), result)
  }

  @Test
  fun `encodeUrlSafe handles empty byte array`() {
    val emptyArray = byteArrayOf()
    val result = IBase64.encodeUrlSafe(emptyArray)
    assertEquals("", result)
  }

  @Test
  fun `decodeToByte throws exception for blank string`() {
    // Empty string should be allowed (it's a valid Base64 encoding of empty byte array)
    val emptyResult = IBase64.decodeToByte("")
    assertEquals(0, emptyResult.size)

    // Blank strings with whitespace should throw exception
    assertFailsWith<IllegalArgumentException> { IBase64.decodeToByte("   ") }
  }

  @Test
  fun `decode throws exception for invalid base64 string`() {
    val invalidBase64Strings =
      listOf(
        "Invalid!Base64@String", // Contains invalid characters
        "SGVsbG8gV29ybGQ!", // Contains invalid character !
        "SGVsbG8gV29ybGQ===", // Too much padding
        "SGVsbG8gV29ybGQ====", // Way too much padding
        "SGVsbG8gV29ybGQ#", // Contains invalid character #
      )

    invalidBase64Strings.forEach { invalidString -> assertFailsWith<IllegalArgumentException> { IBase64.decodeToByte(invalidString) } }
  }

  @Test
  fun `decode byte array handles empty array`() {
    val emptyArray = byteArrayOf()

    val result = IBase64.decode(emptyArray)
    assertEquals("", result)
  }

  @Test
  fun `decodeUrlSafe handles empty and blank strings`() {
    // Empty string should return empty byte array
    val emptyResult = IBase64.decodeUrlSafe("")
    assertContentEquals(ByteArray(0), emptyResult)

    // Blank strings with whitespace should still throw exception
    assertFailsWith<IllegalArgumentException> { IBase64.decodeUrlSafe("   ") }
  }

  // === Validation Tests ===

  @Test
  fun `isValidBase64 correctly identifies valid base64 strings`() {
    val validBase64Strings =
      listOf(
        "SGVsbG8gV29ybGQ=", // Valid with single padding
        "SGVsbG8gV29ybGQh", // Valid without padding
        "QQ==", // Simple valid Base64
        expectedBase64,
      )

    validBase64Strings.forEach { validString ->
      val isValid = IBase64.isValidBase64(validString)
      log.info("Testing validation for: '{}' - Result: {}", validString, isValid)
      assertTrue(isValid, "Should be valid: $validString")
    }
  }

  @Test
  fun `isValidBase64 correctly identifies invalid base64 strings`() {
    val invalidBase64Strings =
      listOf(
        null,
        "",
        "   ",
        "Invalid!Base64@String", // Contains invalid characters
        "SGVsbG8gV29ybGQ!", // Contains invalid character !
        "SGVsbG8gV29ybGQ===", // Too much padding
        "SGVsbG8gV29ybGQ====", // Way too much padding
        "SGVsbG8gV29ybGQ#", // Contains invalid character #
      )

    invalidBase64Strings.forEach { invalidString -> assertFalse(IBase64.isValidBase64(invalidString), "Should be invalid: $invalidString") }
  }

  // === Performance Benchmark Tests ===

  @Test
  fun `performance benchmark for string encoding operations`() {
    val testData = "Performance test data with various characters: sample-data".repeat(100)
    val testBytes = testData.toByteArray(Charsets.UTF_8)
    val iterations = 1000

    val encodingTime = measureTimeMillis { repeat(iterations) { IBase64.encode(testBytes) } }

    log.info("String encoding performance: {} iterations in {} ms", iterations, encodingTime)
    assertTrue(encodingTime < 5000, "String encoding should complete $iterations iterations in < 5s, took ${encodingTime}ms")
  }

  @Test
  fun `performance benchmark for byte array encoding operations`() {
    val testData = ByteArray(1024) { (it % 256).toByte() }
    val iterations = 1000

    val encodingTime = measureTimeMillis { repeat(iterations) { IBase64.encodeToByte(testData) } }

    log.info("Byte array encoding performance: {} iterations in {} ms", iterations, encodingTime)
    assertTrue(encodingTime < 3000, "Byte encoding should complete $iterations iterations in < 3s, took ${encodingTime}ms")
  }

  @Test
  fun `performance benchmark for decoding operations`() {
    val testData = "Performance test for decoding operations with mixed content".repeat(50)
    val encoded = IBase64.encode(testData.toByteArray())
    val iterations = 1000

    val decodingTime = measureTimeMillis { repeat(iterations) { IBase64.decode(encoded) } }

    log.info("String decoding performance: {} iterations in {} ms", iterations, decodingTime)
    assertTrue(decodingTime < 3000, "String decoding should complete $iterations iterations in < 3s, took ${decodingTime}ms")
  }

  @Test
  fun `performance benchmark for url safe operations`() {
    val testData = "URL-safe performance test data with special chars: +/=".repeat(100)
    val testBytes = testData.toByteArray()
    val iterations = 1000

    val urlSafeTime = measureTimeMillis {
      repeat(iterations) {
        val encoded = IBase64.encodeUrlSafe(testBytes)
        IBase64.decodeUrlSafe(encoded)
      }
    }

    log.info("URL-safe operations performance: {} iterations in {} ms", iterations, urlSafeTime)
    assertTrue(urlSafeTime < 5000, "URL-safe operations should complete $iterations iterations in < 5s, took ${urlSafeTime}ms")
  }

  @Test
  fun `performance benchmark for large data processing`() {
    val largeData = ByteArray(100000) { (it % 256).toByte() } // 100KB
    val iterations = 10

    val processingTime = measureTimeMillis {
      repeat(iterations) {
        val encoded = IBase64.encode(largeData)
        IBase64.decodeToByte(encoded)
      }
    }

    log.info("Large data processing performance: {} iterations of 100KB in {} ms", iterations, processingTime)
    assertTrue(processingTime < 10000, "Large data processing should complete $iterations iterations in < 10s, took ${processingTime}ms")
  }

  @Test
  fun `performance comparison with direct java util Base64`() {
    val testData = "Comparison test data for performance analysis".repeat(100)
    val testBytes = testData.toByteArray()
    val iterations = 1000

    // Test IBase64 performance
    val iBase64Time = measureTimeMillis {
      repeat(iterations) {
        val encoded = IBase64.encode(testBytes)
        IBase64.decodeToByte(encoded)
      }
    }

    // Test direct java.util.Base64 performance
    val directBase64Time = measureTimeMillis {
      repeat(iterations) {
        val encoded = java.util.Base64.getEncoder().encodeToString(testBytes)
        java.util.Base64.getDecoder().decode(encoded)
      }
    }

    log.info("IBase64 performance: {} ms, Direct Base64 performance: {} ms", iBase64Time, directBase64Time)

    // IBase64 should not be significantly slower than direct usage (allow reasonable overhead for validation and error handling)
    val performanceRatio = iBase64Time.toDouble() / directBase64Time.toDouble()
    assertTrue(performanceRatio <= 4.0, "IBase64 performance ratio should be <= 4.0x, actual: ${performanceRatio}x")
  }

  @Test
  fun `memory efficiency test for repeated operations`() {
    val testData = "Memory efficiency test data".toByteArray()
    val iterations = 10000

    // Force garbage collection before test
    System.gc()
    val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

    val processingTime = measureTimeMillis {
      repeat(iterations) {
        val encoded = IBase64.encode(testData)
        IBase64.decodeToByte(encoded)
      }
    }

    // Force garbage collection after test
    System.gc()
    val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    val memoryUsed = finalMemory - initialMemory

    log.info("Memory efficiency: {} iterations in {} ms, memory used: {} bytes", iterations, processingTime, memoryUsed)

    // Memory usage should be reasonable (less than 10MB for this test)
    assertTrue(memoryUsed < 10 * 1024 * 1024, "Memory usage should be < 10MB, actual: ${memoryUsed / 1024 / 1024}MB")
  }

  @Test
  fun `validation performance benchmark`() {
    val validBase64 = IBase64.encode("Test data for validation".toByteArray())
    val invalidBase64 = "Invalid!Base64@String"
    val iterations = 10000

    val validationTime = measureTimeMillis {
      repeat(iterations) {
        IBase64.isValidBase64(validBase64)
        IBase64.isValidBase64(invalidBase64)
      }
    }

    log.info("Validation performance: {} iterations in {} ms", iterations * 2, validationTime)
    assertTrue(validationTime < 2000, "Validation should complete ${iterations * 2} operations in < 2s, took ${validationTime}ms")
  }
}
