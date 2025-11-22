package io.github.truenine.composeserver.ide.ideamcp.tools

import io.github.truenine.composeserver.ide.ideamcp.common.ErrorDetails
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

/** CleanCodeTool unit tests. */
class CleanCodeToolTest {

  private val cleanCodeTool = CleanCodeTool()

  @Test
  fun `tool name should be correct`() {
    assertEquals("clean_code", cleanCodeTool.name)
  }

  @Test
  fun `tool description should be correct`() {
    assertEquals("Clean and format code using IDEA capabilities with comprehensive reporting", cleanCodeTool.description)
  }

  @Test
  fun `CleanCodeArgs serialization should be correct`() {
    // Given
    val args = CleanCodeArgs(path = "/src/main/kotlin", formatCode = false, optimizeImports = false, runInspections = false)

    // When
    val json = Json.encodeToString(CleanCodeArgs.serializer(), args)
    val decoded = Json.decodeFromString(CleanCodeArgs.serializer(), json)

    // Then
    assertEquals(args.path, decoded.path)
    assertEquals(args.formatCode, decoded.formatCode)
    assertEquals(args.optimizeImports, decoded.optimizeImports)
    assertEquals(args.runInspections, decoded.runInspections)
  }

  @Test
  fun `CleanCodeArgs default parameters should be set correctly`() {
    // Given
    val args = CleanCodeArgs(path = "/src")

    // Then
    assertEquals("/src", args.path)
    assertTrue(args.formatCode)
    assertTrue(args.optimizeImports)
    assertTrue(args.runInspections)
  }

  @Test
  fun `CleanCodeResult serialization should be correct`() {
    // Given
    val operation = CleanOperation(type = "FORMAT", description = "Code formatting", filesAffected = 5)

    val result =
      CleanCodeResult(
        success = true,
        path = "/src/main/kotlin",
        processedFiles = 10,
        modifiedFiles = 5,
        operations = listOf(operation),
        summary = "Processed 10 files, modified 5 files",
        executionTime = 1500L,
        timestamp = 1234567890L,
      )

    // When
    val json = Json.encodeToString(CleanCodeResult.serializer(), result)
    val decoded = Json.decodeFromString(CleanCodeResult.serializer(), json)

    // Then
    assertEquals(result.success, decoded.success)
    assertEquals(result.path, decoded.path)
    assertEquals(result.processedFiles, decoded.processedFiles)
    assertEquals(result.modifiedFiles, decoded.modifiedFiles)
    assertEquals(result.operations.size, decoded.operations.size)
    assertEquals(result.summary, decoded.summary)
    assertEquals(result.executionTime, decoded.executionTime)
    assertEquals(result.timestamp, decoded.timestamp)

    // Verify operation information
    val decodedOperation = decoded.operations[0]
    assertEquals(operation.type, decodedOperation.type)
    assertEquals(operation.description, decodedOperation.description)
    assertEquals(operation.filesAffected, decodedOperation.filesAffected)
  }

  @Test
  fun `CleanOperation serialization should be correct`() {
    // Given
    val operation = CleanOperation(type = "OPTIMIZE_IMPORTS", description = "Optimize imports", filesAffected = 3)

    // When
    val json = Json.encodeToString(CleanOperation.serializer(), operation)
    val decoded = Json.decodeFromString(CleanOperation.serializer(), json)

    // Then
    assertEquals(operation.type, decoded.type)
    assertEquals(operation.description, decoded.description)
    assertEquals(operation.filesAffected, decoded.filesAffected)
  }

  @Test
  fun `CleanCodeErrorResponse serialization should be correct`() {
    // Given
    val errorResponse =
      CleanCodeErrorResponse(
        success = false,
        error = ErrorDetails(type = "INVALID_ARGUMENT", message = "Path must not be empty", suggestions = listOf("Check path format", "Use a valid file path")),
        path = "",
        timestamp = 1234567890L,
      )

    // When
    val json = Json.encodeToString(CleanCodeErrorResponse.serializer(), errorResponse)
    val decoded = Json.decodeFromString(CleanCodeErrorResponse.serializer(), json)

    // Then
    assertFalse(decoded.success)
    assertEquals(errorResponse.error.type, decoded.error.type)
    assertEquals(errorResponse.error.message, decoded.error.message)
    assertEquals(errorResponse.error.suggestions, decoded.error.suggestions)
    assertEquals(errorResponse.path, decoded.path)
    assertEquals(errorResponse.timestamp, decoded.timestamp)
  }

  @Test
  fun `CleanCodeResult with multiple operations should serialize correctly`() {
    // Given
    val operations =
      listOf(
        CleanOperation("FORMAT", "Code formatting", 8),
        CleanOperation("OPTIMIZE_IMPORTS", "Optimize imports", 6),
        CleanOperation("RUN_INSPECTIONS", "Code inspections and fixes", 3),
      )

    val result =
      CleanCodeResult(
        success = true,
        path = "/src",
        processedFiles = 15,
        modifiedFiles = 12,
        operations = operations,
        summary = "Multiple clean-up operations were executed",
        executionTime = 3000L,
        timestamp = System.currentTimeMillis(),
      )

    // When
    val json = Json.encodeToString(CleanCodeResult.serializer(), result)
    val decoded = Json.decodeFromString(CleanCodeResult.serializer(), json)

    // Then
    assertEquals(3, decoded.operations.size)
    assertEquals("FORMAT", decoded.operations[0].type)
    assertEquals("OPTIMIZE_IMPORTS", decoded.operations[1].type)
    assertEquals("RUN_INSPECTIONS", decoded.operations[2].type)
    assertEquals(8, decoded.operations[0].filesAffected)
    assertEquals(6, decoded.operations[1].filesAffected)
    assertEquals(3, decoded.operations[2].filesAffected)
  }

  @Test
  fun `ErrorDetails serialization should be correct`() {
    // Given
    val errorDetails =
      ErrorDetails(type = "PERMISSION_DENIED", message = "File is locked", suggestions = listOf("Close other editors", "Check file permissions", "Restart IDE"))

    // When
    val json = Json.encodeToString(ErrorDetails.serializer(), errorDetails)
    val decoded = Json.decodeFromString(ErrorDetails.serializer(), json)

    // Then
    assertEquals(errorDetails.type, decoded.type)
    assertEquals(errorDetails.message, decoded.message)
    assertEquals(errorDetails.suggestions.size, decoded.suggestions.size)
    assertEquals("Close other editors", decoded.suggestions[0])
    assertEquals("Check file permissions", decoded.suggestions[1])
    assertEquals("Restart IDE", decoded.suggestions[2])
  }

  @Test
  fun `CleanCodeResult with empty operations should serialize correctly`() {
    // Given
    val result =
      CleanCodeResult(
        success = true,
        path = "/empty",
        processedFiles = 0,
        modifiedFiles = 0,
        operations = emptyList(),
        summary = "No files to process",
        executionTime = 100L,
        timestamp = System.currentTimeMillis(),
      )

    // When
    val json = Json.encodeToString(CleanCodeResult.serializer(), result)
    val decoded = Json.decodeFromString(CleanCodeResult.serializer(), json)

    // Then
    assertTrue(decoded.success)
    assertEquals(0, decoded.processedFiles)
    assertEquals(0, decoded.modifiedFiles)
    assertTrue(decoded.operations.isEmpty())
    assertEquals("No files to process", decoded.summary)
  }

  @Test
  fun `CleanCodeArgs with partial options should serialize correctly`() {
    // Given
    val args = CleanCodeArgs(path = "/src/test", formatCode = true, optimizeImports = false, runInspections = true)

    // When
    val json = Json.encodeToString(CleanCodeArgs.serializer(), args)
    val decoded = Json.decodeFromString(CleanCodeArgs.serializer(), json)

    // Then
    assertEquals("/src/test", decoded.path)
    assertTrue(decoded.formatCode)
    assertFalse(decoded.optimizeImports)
    assertTrue(decoded.runInspections)
  }
}
