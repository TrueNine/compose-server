package io.github.truenine.composeserver.ide.ideamcp.common

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** Error handling tests for common error details. */
class ErrorHandlingTest : BasePlatformTestCase() {

  fun testErrorDetailsCreation() {
    // Verify error details creation
    val errorDetails = ErrorDetails(type = "TestError", message = "Test error message", suggestions = listOf("Suggestion1", "Suggestion2"))

    assertNotNull(errorDetails)
    assertEquals("TestError", errorDetails.type)
    assertEquals("Test error message", errorDetails.message)
    assertEquals(2, errorDetails.suggestions.size)
    assertTrue(errorDetails.suggestions.contains("Suggestion1"))
    assertTrue(errorDetails.suggestions.contains("Suggestion2"))
  }

  fun testErrorDetailsWithEmptySuggestions() {
    // Verify error details without suggestions
    val errorDetails = ErrorDetails(type = "SimpleError", message = "Simple error")

    assertNotNull(errorDetails)
    assertEquals("SimpleError", errorDetails.type)
    assertEquals("Simple error", errorDetails.message)
    assertTrue(errorDetails.suggestions.isEmpty())
  }

  fun testErrorDetailsWithLongMessage() {
    // Verify handling of long error messages
    val longMessage =
      "This is a very long error message used to verify that the ErrorDetails class can correctly handle long text content. " +
        "The message contains enough characters to validate correctness and stability of long-text handling."
    val errorDetails = ErrorDetails(type = "LongMessageError", message = longMessage)

    assertNotNull(errorDetails)
    assertEquals("LongMessageError", errorDetails.type)
    assertEquals(longMessage, errorDetails.message)
    assertTrue(errorDetails.message.length > 50)
  }

  fun testErrorDetailsWithMultipleSuggestions() {
    // Verify error details with multiple suggestions
    val suggestions = listOf("Check network connection", "Validate configuration file", "Restart service", "Contact administrator")
    val errorDetails = ErrorDetails(type = "NetworkError", message = "Network connection failed", suggestions = suggestions)

    assertNotNull(errorDetails)
    assertEquals("NetworkError", errorDetails.type)
    assertEquals("Network connection failed", errorDetails.message)
    assertEquals(4, errorDetails.suggestions.size)
    suggestions.forEach { suggestion -> assertTrue(errorDetails.suggestions.contains(suggestion)) }
  }

  fun testErrorDetailsEquality() {
    // Verify equality of error details
    val errorDetails1 = ErrorDetails(type = "TestError", message = "Test message", suggestions = listOf("Suggestion1"))

    val errorDetails2 = ErrorDetails(type = "TestError", message = "Test message", suggestions = listOf("Suggestion1"))

    assertEquals(errorDetails1, errorDetails2)
    assertEquals(errorDetails1.hashCode(), errorDetails2.hashCode())
  }

  fun testErrorDetailsInequality() {
    // Verify inequality of error details
    val errorDetails1 = ErrorDetails(type = "TestError", message = "Test message 1")

    val errorDetails2 = ErrorDetails(type = "TestError", message = "Test message 2")

    assertFalse(errorDetails1 == errorDetails2)
  }

  fun testErrorDetailsToString() {
    // Verify string representation of error details
    val errorDetails = ErrorDetails(type = "TestError", message = "Test message", suggestions = listOf("Suggestion1", "Suggestion2"))

    val stringRepresentation = errorDetails.toString()
    assertNotNull(stringRepresentation)
    assertTrue(stringRepresentation.contains("TestError"))
    assertTrue(stringRepresentation.contains("Test message"))
  }
}
