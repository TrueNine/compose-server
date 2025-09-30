package io.github.truenine.composeserver

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class AliasExtensionsTest {

  // Long.isId() tests
  @Test
  fun `Long isId() should return true for valid positive ids`() {
    assertTrue(0L.isId(), "Zero should be a valid ID")
    assertTrue(1L.isId(), "Positive integer should be a valid ID")
    assertTrue(123L.isId(), "Any positive long should be a valid ID")
    assertTrue(Long.MAX_VALUE.isId(), "Maximum long value should be a valid ID")
  }

  @Test
  fun `Long isId() should return false for negative numbers`() {
    assertFalse((-1L).isId(), "Negative one should not be a valid ID")
    assertFalse((-123L).isId(), "Any negative number should not be a valid ID")
    assertFalse(Long.MIN_VALUE.isId(), "Minimum long value should not be a valid ID")
  }

  // String.isId() tests
  @Test
  fun `String isId() should return true for valid alphanumeric strings`() {
    assertTrue("a".isId(), "Single letter should be a valid ID")
    assertTrue("A".isId(), "Single uppercase letter should be a valid ID")
    assertTrue("0".isId(), "Single digit should be a valid ID")
    assertTrue("abc123".isId(), "Mixed alphanumeric should be a valid ID")
    assertTrue("ABC123".isId(), "Uppercase alphanumeric should be a valid ID")
    assertTrue("123abc".isId(), "Digits followed by letters should be a valid ID")
    assertTrue("a1b2c3".isId(), "Alternating letters and digits should be a valid ID")
    assertTrue("000123".isId(), "Leading zeros should be allowed")
    assertTrue("veryLongIdNameWithNumbers12345".isId(), "Long alphanumeric string should be valid")
  }

  @Test
  fun `String isId() should return false for empty strings`() {
    assertFalse("".isId(), "Empty string should not be a valid ID")
  }

  @Test
  fun `String isId() should return false for strings with non-ASCII characters`() {
    assertFalse("一二三".isId(), "Chinese characters should not be valid")
    assertFalse("café".isId(), "Accented characters should not be valid")
    assertFalse("naïve".isId(), "Special accented characters should not be valid")
    assertFalse("Москва".isId(), "Cyrillic characters should not be valid")
  }

  @Test
  fun `String isId() should return false for strings with spaces`() {
    assertFalse("abc 123".isId(), "String with space should not be valid")
    assertFalse(" abc123".isId(), "String with leading space should not be valid")
    assertFalse("abc123 ".isId(), "String with trailing space should not be valid")
    assertFalse("abc   123".isId(), "String with multiple spaces should not be valid")
  }

  @Test
  fun `String isId() should return false for strings with special characters`() {
    assertFalse("abc@123".isId(), "String with @ symbol should not be valid")
    assertFalse("abc-123".isId(), "String with hyphen should not be valid")
    assertFalse("abc_123".isId(), "String with underscore should not be valid")
    assertFalse("abc.123".isId(), "String with dot should not be valid")
    assertFalse("abc/123".isId(), "String with slash should not be valid")
    assertFalse("abc!123".isId(), "String with exclamation mark should not be valid")
    assertFalse("abc#123".isId(), "String with hash symbol should not be valid")
    assertFalse("abc$123".isId(), "String with dollar sign should not be valid")
    assertFalse("abc%123".isId(), "String with percent sign should not be valid")
    assertFalse("abc^123".isId(), "String with caret should not be valid")
    assertFalse("abc&123".isId(), "String with ampersand should not be valid")
    assertFalse("abc*123".isId(), "String with asterisk should not be valid")
    assertFalse("abc(123)".isId(), "String with parentheses should not be valid")
    assertFalse("abc[123]".isId(), "String with brackets should not be valid")
    assertFalse("abc{123}".isId(), "String with braces should not be valid")
  }

  // getDefaultNullableId() tests - SKIPPED due to ERROR deprecation level
  // This function is marked with DeprecationLevel.ERROR and should not be called by users

  // Number.toId() tests
  @Test
  fun `Number toId() should convert valid numbers to ID`() {
    assertEquals(1L, 1.toId(), "Int should convert to Long ID")
    assertEquals(123L, 123.toId(), "Positive Int should convert to Long ID")
    assertEquals(0L, 0.toId(), "Zero Int should convert to Long ID")
    assertEquals(1L, 1L.toId(), "Long should remain as Long ID")
    assertEquals(123L, 123L.toId(), "Positive Long should remain as Long ID")
    assertEquals(0L, 0L.toId(), "Zero Long should remain as Long ID")
    assertEquals(1L, 1.0.toId(), "Double 1.0 should convert to Long ID")
    assertEquals(123L, 123.0.toId(), "Double 123.0 should convert to Long ID")
    assertEquals(1L, 1.5.toId(), "Double 1.5 should truncate to 1L")
    assertEquals(1L, (1.999999).toId(), "Double 1.999999 should truncate to 1L")
    assertEquals(123L, 123.99.toId(), "Double 123.99 should truncate to 123L")
    assertEquals(1L, 1.0f.toId(), "Float 1.0 should convert to Long ID")
    assertEquals(123L, 123.0f.toId(), "Float 123.0 should convert to Long ID")
    assertEquals(1L, 1.5f.toId(), "Float 1.5 should truncate to 1L")
    assertEquals(1L, (1.999999f).toId(), "Float 1.999999 should truncate to 1L")
  }

  @Test
  fun `Number toId() should return null for Long MIN_VALUE`() {
    assertNull(Long.MIN_VALUE.toId(), "Long.MIN_VALUE should return null")
  }

  // Number.toIdOrThrow() tests
  @Test
  fun `Number toIdOrThrow() should convert valid numbers to ID`() {
    assertEquals(1L, 1.toIdOrThrow(), "Int should convert to Long ID")
    assertEquals(123L, 123.toIdOrThrow(), "Positive Int should convert to Long ID")
    assertEquals(0L, 0.toIdOrThrow(), "Zero Int should convert to Long ID")
    assertEquals(1L, 1L.toIdOrThrow(), "Long should remain as Long ID")
    assertEquals(123L, 123L.toIdOrThrow(), "Positive Long should remain as Long ID")
    assertEquals(0L, 0L.toIdOrThrow(), "Zero Long should remain as Long ID")
    assertEquals(1L, 1.0.toIdOrThrow(), "Double 1.0 should convert to Long ID")
    assertEquals(123L, 123.0.toIdOrThrow(), "Double 123.0 should convert to Long ID")
    assertEquals(1L, 1.5.toIdOrThrow(), "Double 1.5 should truncate to 1L")
    assertEquals(1L, (1.999999).toIdOrThrow(), "Double 1.999999 should truncate to 1L")
    assertEquals(123L, 123.99.toIdOrThrow(), "Double 123.99 should truncate to 123L")
    assertEquals(1L, 1.0f.toIdOrThrow(), "Float 1.0 should convert to Long ID")
    assertEquals(123L, 123.0f.toIdOrThrow(), "Float 123.0 should convert to Long ID")
    assertEquals(1L, 1.5f.toIdOrThrow(), "Float 1.5 should truncate to 1L")
    assertEquals(1L, (1.999999f).toIdOrThrow(), "Float 1.999999 should truncate to 1L")
  }

  @Test
  fun `Number toIdOrThrow() should throw exception for Long MIN_VALUE`() {
    try {
      Long.MIN_VALUE.toIdOrThrow()
      fail("Expected IllegalArgumentException to be thrown")
    } catch (e: IllegalArgumentException) {
      assertEquals("Invalid Id: ${Long.MIN_VALUE}", e.message, "Exception message should contain the invalid ID")
    }
  }

  // String.toId() tests
  @Test
  fun `String toId() should convert valid numeric strings to ID`() {
    assertEquals(1L, "1".toId(), "String '1' should convert to Long 1")
    assertEquals(123L, "123".toId(), "String '123' should convert to Long 123")
    assertEquals(0L, "0".toId(), "String '0' should convert to Long 0")
    assertEquals(-1L, "-1".toId(), "String '-1' should convert to Long -1")
    assertEquals(-123L, "-123".toId(), "String '-123' should convert to Long -123")
    assertEquals(Long.MAX_VALUE, Long.MAX_VALUE.toString().toId(), "Max long string should convert to max long")
    assertEquals(Long.MIN_VALUE + 1, (Long.MIN_VALUE + 1).toString().toId(), "Min long + 1 string should convert")
  }

  @Test
  fun `String toId() should return null for invalid numeric strings`() {
    assertNull("".toId(), "Empty string should return null")
    assertNull("abc".toId(), "Non-numeric string should return null")
    assertNull("123abc".toId(), "Mixed alphanumeric string should return null")
    assertNull("abc123".toId(), "Letters followed by numbers should return null")
    assertNull("12.34".toId(), "Decimal string should return null")
    assertNull("12,34".toId(), "String with comma should return null")
    assertNull(" 123".toId(), "String with leading space should return null")
    assertNull("123 ".toId(), "String with trailing space should return null")
    assertNull("12 34".toId(), "String with internal space should return null")
    assertNull("++123".toId(), "String with double plus should return null")
    assertNull("--123".toId(), "String with double minus should return null")
    assertNull("1a2b3c".toId(), "Mixed alphanumeric should return null")
    assertNull(Long.MIN_VALUE.toString().toId(), "Long.MIN_VALUE string should return null")
  }

  @Test
  fun `String toId() should correctly handle strings with plus sign`() {
    assertEquals(123L, "+123".toId(), "String with plus sign should be parsed correctly")
  }

  // String.toIdOrThrow() tests
  @Test
  fun `String toIdOrThrow() should convert valid numeric strings to ID`() {
    assertEquals(1L, "1".toIdOrThrow(), "String '1' should convert to Long 1")
    assertEquals(123L, "123".toIdOrThrow(), "String '123' should convert to Long 123")
    assertEquals(0L, "0".toIdOrThrow(), "String '0' should convert to Long 0")
    assertEquals(-1L, "-1".toIdOrThrow(), "String '-1' should convert to Long -1")
    assertEquals(-123L, "-123".toIdOrThrow(), "String '-123' should convert to Long -123")
    assertEquals(Long.MAX_VALUE, Long.MAX_VALUE.toString().toIdOrThrow(), "Max long string should convert to max long")
    assertEquals(Long.MIN_VALUE + 1, (Long.MIN_VALUE + 1).toString().toIdOrThrow(), "Min long + 1 string should convert")
  }

  @Test
  fun `String toIdOrThrow() should throw exception for invalid numeric strings`() {
    val invalidInputs = listOf("", "abc", "123abc", "abc123", "12.34", "12,34", " 123", "123 ", "12 34", "++123", "--123", "1a2b3c", Long.MIN_VALUE.toString())

    invalidInputs.forEach { input ->
      try {
        input.toIdOrThrow()
        fail("Expected IllegalArgumentException to be thrown for input: $input")
      } catch (e: IllegalArgumentException) {
        assertEquals("Invalid Id: $input", e.message, "Exception message should contain the invalid ID")
      }
    }
  }

  @Test
  fun `String toIdOrThrow() should correctly handle strings with plus sign`() {
    assertEquals(123L, "+123".toIdOrThrow(), "String with plus sign should be parsed correctly")
  }

  // Number.toId with receiver function tests
  @Test
  fun `Number toId() with receiver should apply function when valid ID`() {
    val result1 = 123.toId { it * 2 }
    assertEquals(246L, result1, "Should apply function to valid ID")

    val result2 = 0.toId { it.toString() }
    assertEquals("0", result2, "Should apply function to zero ID")

    val result3 = 1.5.toId { it + 100 }
    assertEquals(101L, result3, "Should truncate and apply function")
  }

  @Test
  fun `Number toId() with receiver should return null when invalid ID`() {
    val result1 = Long.MIN_VALUE.toId { it * 2 }
    assertNull(result1, "Should return null for invalid ID")
  }

  // String.toId with receiver function tests
  @Test
  fun `String toId() with receiver should apply function when valid ID`() {
    val result1 = "123".toId { it * 2 }
    assertEquals(246L, result1, "Should apply function to valid ID")

    val result2 = "0".toId { it.toString() }
    assertEquals("0", result2, "Should apply function to zero ID")

    val result3 = "-456".toId { it + 100 }
    assertEquals(-356L, result3, "Should apply function to negative ID")
  }

  @Test
  fun `String toId() with receiver should return null when invalid ID`() {
    val result1 = "".toId { it * 2 }
    assertNull(result1, "Should return null for empty string")

    val result2 = "abc".toId { it.toString() }
    assertNull(result2, "Should return null for non-numeric string")

    val result3 = Long.MIN_VALUE.toString().toId { it + 100 }
    assertNull(result3, "Should return null for Long.MIN_VALUE string")
  }
}
