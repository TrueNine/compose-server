package net.yan100.compose.testtoolkit

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

fun <T> assertNotEmpty(message: String? = "collection is empty", actual: () -> List<T>) {
  assertNotEquals(0, actual().size, message)
}

fun <T> assertEmpty(message: String? = "collection is not empty", actual: () -> List<T>) {
  assertEquals(0, actual().size, message)
}

fun assertNotBlank(string: String, message: String? = "string is blank") {
  assertTrue(string.isNotBlank(), message)
}

fun assertBlank(string: String, message: String? = "string is not blank") {
  assertTrue(string.isBlank(), message)
}
