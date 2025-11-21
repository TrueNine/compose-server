package io.github.truenine.composeserver.testtoolkit

import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Kotlin test extension function tests.
 *
 * Verifies the testing utility functions defined in KotlinTestExtensions.kt.
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class KotlinTestExtensionsTest {

  @Test
  fun assertNotEmptyShouldPassForNonEmptyCollections() {
    assertNotEmpty { listOf(1, 2, 3) }
    assertNotEmpty("custom message") { listOf("a", "b") }
  }

  @Test
  fun assertNotEmptyShouldFailForEmptyCollections() {
    assertFailsWith<AssertionError> { assertNotEmpty { emptyList<String>() } }
  }

  @Test
  fun assertEmptyShouldPassForEmptyCollections() {
    assertEmpty { emptyList<Int>() }
    assertEmpty("custom message") { emptyList<String>() }
  }

  @Test
  fun assertEmptyShouldFailForNonEmptyCollections() {
    assertFailsWith<AssertionError> { assertEmpty { listOf(1, 2) } }
  }

  @Test
  fun assertNotBlankShouldPassForNonBlankStrings() {
    assertNotBlank("Hello")
    assertNotBlank("  test  ", "custom message")
  }

  @Test
  fun assertNotBlankShouldFailForBlankStrings() {
    assertFailsWith<AssertionError> { assertNotBlank("") }
    assertFailsWith<AssertionError> { assertNotBlank("   ") }
    assertFailsWith<AssertionError> { assertNotBlank("\t\n\r") }
  }

  @Test
  fun assertBlankShouldPassForBlankStrings() {
    assertBlank("")
    assertBlank("   ", "custom message")
    assertBlank("\t\n\r")
  }

  @Test
  fun assertBlankShouldFailForNonBlankStrings() {
    assertFailsWith<AssertionError> { assertBlank("Hello") }
    assertFailsWith<AssertionError> { assertBlank("  test  ") }
  }
}
