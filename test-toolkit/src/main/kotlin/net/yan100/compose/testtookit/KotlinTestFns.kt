package net.yan100.compose.testtookit

import kotlin.test.assertTrue

fun <T> assertNotEmpty(message: String? = "collection is empty", actual: () -> List<T>) {
  assertTrue(message) { actual().isNotEmpty() }
}

fun <T> assertEmpty(message: String? = "collection is not empty", actual: () -> List<T>) {
  assertTrue(message) { actual().isEmpty() }
}
