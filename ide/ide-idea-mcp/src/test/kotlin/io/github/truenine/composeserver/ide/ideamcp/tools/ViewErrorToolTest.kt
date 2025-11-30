package io.github.truenine.composeserver.ide.ideamcp.tools

import io.github.truenine.composeserver.ide.ideamcp.common.ErrorDetails
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

/** ViewErrorTool unit tests. */
class ViewErrorToolTest {

  private val viewErrorTool = ViewErrorTool()

  @Test
  fun `tool name should be correct`() {
    assertEquals("view_error", viewErrorTool.name)
  }

  @Test
  fun `tool description should be correct`() {
    assertEquals("View all errors, warnings and weak warnings in files or directories", viewErrorTool.description)
  }

  @Test
  fun `ViewErrorArgs serialization should be correct`() {
    // Given
    val args = ViewErrorArgs(path = "/src/main/kotlin", includeWarnings = false, includeWeakWarnings = false)

    // When
    val json = Json.encodeToString(ViewErrorArgs.serializer(), args)
    val decoded = Json.decodeFromString(ViewErrorArgs.serializer(), json)

    // Then
    assertEquals(args.path, decoded.path)
    assertEquals(args.includeWarnings, decoded.includeWarnings)
    assertEquals(args.includeWeakWarnings, decoded.includeWeakWarnings)
  }

  @Test
  fun `ViewErrorResult serialization should be correct`() {
    // Given
    val errorInfo =
      ErrorInfo(
        line = 10,
        column = 5,
        severity = ErrorSeverity.ERROR,
        message = "Test error message",
        code = "TEST_ERROR",
        codeSnippet = "val test = null",
        quickFixes = listOf("Add null check", "Use non-null type"),
      )

    val fileErrorInfo =
      FileErrorInfo(
        filePath = "/src/Test.kt",
        relativePath = "src/Test.kt",
        errors = listOf(errorInfo),
        warnings = emptyList(),
        weakWarnings = emptyList(),
        summary = "1 error",
      )

    val result =
      ViewErrorResult(
        path = "/src",
        resolvedPath = "/project/src",
        totalErrors = 1,
        totalWarnings = 0,
        totalWeakWarnings = 0,
        files = listOf(fileErrorInfo),
        summary = "Total 1 error",
      )

    // When
    val json = Json.encodeToString(ViewErrorResult.serializer(), result)
    val decoded = Json.decodeFromString(ViewErrorResult.serializer(), json)

    // Then
    assertEquals(result.path, decoded.path)
    assertEquals(result.resolvedPath, decoded.resolvedPath)
    assertEquals(result.totalErrors, decoded.totalErrors)
    assertEquals(result.totalWarnings, decoded.totalWarnings)
    assertEquals(result.totalWeakWarnings, decoded.totalWeakWarnings)
    assertEquals(result.files.size, decoded.files.size)
    assertEquals(result.summary, decoded.summary)

    // Verify file error information
    val decodedFileInfo = decoded.files[0]
    assertEquals(fileErrorInfo.filePath, decodedFileInfo.filePath)
    assertEquals(fileErrorInfo.relativePath, decodedFileInfo.relativePath)
    assertEquals(fileErrorInfo.errors.size, decodedFileInfo.errors.size)
    assertEquals(fileErrorInfo.summary, decodedFileInfo.summary)

    // Verify error information
    val decodedErrorInfo = decodedFileInfo.errors[0]
    assertEquals(errorInfo.line, decodedErrorInfo.line)
    assertEquals(errorInfo.column, decodedErrorInfo.column)
    assertEquals(errorInfo.severity, decodedErrorInfo.severity)
    assertEquals(errorInfo.message, decodedErrorInfo.message)
    assertEquals(errorInfo.code, decodedErrorInfo.code)
    assertEquals(errorInfo.codeSnippet, decodedErrorInfo.codeSnippet)
    assertEquals(errorInfo.quickFixes, decodedErrorInfo.quickFixes)
  }

  @Test
  fun `ViewErrorErrorResponse serialization should be correct`() {
    // Given
    val errorResponse =
      ViewErrorErrorResponse(
        success = false,
        error = ErrorDetails(type = "INVALID_PATH", message = "Path does not exist", suggestions = listOf("Check path format", "Use absolute path")),
        path = "/invalid/path",
        timestamp = 1234567890L,
      )

    // When
    val json = Json.encodeToString(ViewErrorErrorResponse.serializer(), errorResponse)
    val decoded = Json.decodeFromString(ViewErrorErrorResponse.serializer(), json)

    // Then
    assertEquals(errorResponse.success, decoded.success)
    assertEquals(errorResponse.error.type, decoded.error.type)
    assertEquals(errorResponse.error.message, decoded.error.message)
    assertEquals(errorResponse.error.suggestions, decoded.error.suggestions)
    assertEquals(errorResponse.path, decoded.path)
    assertEquals(errorResponse.timestamp, decoded.timestamp)
  }

  @Test
  fun `default parameters should be set correctly`() {
    // Given
    val args = ViewErrorArgs(path = "/src")

    // Then
    assertEquals("/src", args.path)
    assertEquals(true, args.includeWarnings)
    assertEquals(true, args.includeWeakWarnings)
  }

  @Test
  fun `ErrorSeverity enum should serialize correctly`() {
    // Given
    val severities = listOf(ErrorSeverity.ERROR, ErrorSeverity.WARNING, ErrorSeverity.WEAK_WARNING, ErrorSeverity.INFO)

    // When & Then
    severities.forEach { severity ->
      val json = Json.encodeToString(ErrorSeverity.serializer(), severity)
      val decoded = Json.decodeFromString(ErrorSeverity.serializer(), json)
      assertEquals(severity, decoded)
    }
  }

  @Test
  fun `ErrorInfo with minimal parameters should serialize correctly`() {
    // Given
    val errorInfo = ErrorInfo(line = 1, column = 1, severity = ErrorSeverity.INFO, message = "Info", code = "INFO_CODE")

    // When
    val json = Json.encodeToString(ErrorInfo.serializer(), errorInfo)
    val decoded = Json.decodeFromString(ErrorInfo.serializer(), json)

    // Then
    assertEquals(errorInfo.line, decoded.line)
    assertEquals(errorInfo.column, decoded.column)
    assertEquals(errorInfo.severity, decoded.severity)
    assertEquals(errorInfo.message, decoded.message)
    assertEquals(errorInfo.code, decoded.code)
    assertEquals(null, decoded.codeSnippet)
    assertEquals(emptyList(), decoded.quickFixes)
  }
}
