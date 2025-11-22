package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * # Byte array extension tests
 *
 * Tests for the extension functions defined in ByteArrayExtensions.kt
 */
class ByteArrayExtensionsTest {

  @Test
  fun utf8StringConvertsByteArrayToUtf8String() {
    val testString = "Hello, world!"
    val byteArray = testString.toByteArray(Charsets.UTF_8)

    val result = byteArray.utf8String

    log.info("Original string: {}", testString)
    log.info("Byte array length: {}", byteArray.size)
    log.info("Converted string: {}", result)

    assertEquals(testString, result, "Byte array should convert to UTF-8 string")
  }

  @Test
  fun utf8StringConvertsEmptyByteArrayToEmptyString() {
    val emptyByteArray = ByteArray(0)

    val result = emptyByteArray.utf8String

    log.info("Empty byte array conversion result: '{}'", result)

    assertEquals("", result, "Empty byte array should convert to empty string")
  }

  @Test
  fun utf8StringHandlesNonLatinCharacters() {
    val nonLatinText = "Привет, мир! This is a test."
    val byteArray = nonLatinText.toByteArray(Charsets.UTF_8)

    val result = byteArray.utf8String

    log.info("Original non-Latin text: {}", nonLatinText)
    log.info("UTF-8 byte array length: {}", byteArray.size)
    log.info("Converted text: {}", result)

    assertEquals(nonLatinText, result, "Non-Latin characters should convert correctly")
  }

  @Test
  fun utf8StringHandlesSpecialCharactersAndEmoji() {
    val specialText = "Special chars: @#$%^&*()_+-=[]{}|;':\",./<>?"
    val byteArray = specialText.toByteArray(Charsets.UTF_8)

    val result = byteArray.utf8String

    log.info("Original special text: {}", specialText)
    log.info("UTF-8 byte array length: {}", byteArray.size)
    log.info("Converted text: {}", result)

    assertEquals(specialText, result, "Special characters and emoji should convert correctly")
  }

  @Test
  fun utf8StringHandlesMultilineText() {
    val multilineText =
      """
      First line of text
      Second line of text
      Third line with special characters: !@#$%
      Fourth line with special text
      """
        .trimIndent()

    val byteArray = multilineText.toByteArray(Charsets.UTF_8)

    val result = byteArray.utf8String

    log.info("Original multiline text:\n{}", multilineText)
    log.info("UTF-8 byte array length: {}", byteArray.size)
    log.info("Converted text:\n{}", result)

    assertEquals(multilineText, result, "Multiline text should convert correctly")
  }

  @Test
  fun utf8StringHandlesMixedContent() {
    val mixedText = "ABC123abcTest456MixedContent789"
    val byteArray = mixedText.toByteArray(Charsets.UTF_8)

    val result = byteArray.utf8String

    log.info("Original mixed text: {}", mixedText)
    log.info("UTF-8 byte array length: {}", byteArray.size)
    log.info("Converted text: {}", result)

    assertEquals(mixedText, result, "Mixed alphanumeric text should convert correctly")
  }

  @Test
  fun utf8StringComparedWithOtherEncodings() {
    val testText = "Encoding test"

    val utf8Bytes = testText.toByteArray(Charsets.UTF_8)
    val iso8859Bytes = testText.toByteArray(Charsets.ISO_8859_1)

    val utf8Result = utf8Bytes.utf8String
    val iso8859AsUtf8Result = iso8859Bytes.utf8String

    log.info("Original text: {}", testText)
    log.info("UTF-8 encoded then decoded: {}", utf8Result)
    log.info("ISO-8859-1 encoded then decoded with UTF-8: {}", iso8859AsUtf8Result)
    log.info("UTF-8 byte array length: {}", utf8Bytes.size)
    log.info("ISO-8859-1 byte array length: {}", iso8859Bytes.size)

    assertEquals(testText, utf8Result, "UTF-8 encode/decode should be consistent")
  }

  @Test
  fun utf8StringHandlesLongTextPerformance() {
    val longText = "This is a long piece of text used for performance testing.".repeat(1000)
    val byteArray = longText.toByteArray(Charsets.UTF_8)

    val startTime = System.currentTimeMillis()
    val result = byteArray.utf8String
    val endTime = System.currentTimeMillis()

    log.info("Long text length: {} characters", longText.length)
    log.info("Byte array length: {} bytes", byteArray.size)
    log.info("Conversion time: {} ms", endTime - startTime)
    log.info("Result length: {} characters", result.length)

    assertEquals(longText, result, "Long text should convert correctly")
    assertEquals(longText.length, result.length, "Result length should match input length")
  }
}
