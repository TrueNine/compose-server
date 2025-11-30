package io.github.truenine.composeserver.ide.ideamcp.tools

import io.github.truenine.composeserver.ide.ideamcp.common.ErrorDetails
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/** TerminalTool unit tests. */
class TerminalToolTest {

  private val terminalTool = TerminalTool()

  @Test
  fun `tool name should be correct`() {
    assertEquals("terminal", terminalTool.name)
  }

  @Test
  fun `tool description should be correct`() {
    assertEquals("Execute terminal commands with clean output for AI processing", terminalTool.description)
  }

  @Test
  fun `TerminalArgs serialization should be correct`() {
    // Given
    val args = TerminalArgs(command = "echo hello", workingDirectory = "/tmp", timeout = 5000, cleanOutput = false)

    // When
    val json = Json.encodeToString(TerminalArgs.serializer(), args)
    val decoded = Json.decodeFromString(TerminalArgs.serializer(), json)

    // Then
    assertEquals(args.command, decoded.command)
    assertEquals(args.workingDirectory, decoded.workingDirectory)
    assertEquals(args.timeout, decoded.timeout)
    assertEquals(args.cleanOutput, decoded.cleanOutput)
  }

  @Test
  fun `TerminalResult serialization should be correct`() {
    // Given
    val result = TerminalResult(command = "echo test", exitCode = 0, output = "test", errorOutput = "", executionTime = 100L, workingDirectory = "/tmp")

    // When
    val json = Json.encodeToString(TerminalResult.serializer(), result)
    val decoded = Json.decodeFromString(TerminalResult.serializer(), json)

    // Then
    assertEquals(result.command, decoded.command)
    assertEquals(result.exitCode, decoded.exitCode)
    assertEquals(result.output, decoded.output)
    assertEquals(result.errorOutput, decoded.errorOutput)
    assertEquals(result.executionTime, decoded.executionTime)
    assertEquals(result.workingDirectory, decoded.workingDirectory)
  }

  @Test
  fun `TerminalErrorResponse serialization should be correct`() {
    // Given
    val errorResponse =
      TerminalErrorResponse(
        success = false,
        error = ErrorDetails(type = "INVALID_ARGUMENT", message = "Test error", suggestions = listOf("Suggestion1", "Suggestion2")),
        command = "test command",
        timestamp = 1234567890L,
      )

    // When
    val json = Json.encodeToString(TerminalErrorResponse.serializer(), errorResponse)
    val decoded = Json.decodeFromString(TerminalErrorResponse.serializer(), json)

    // Then
    assertEquals(errorResponse.success, decoded.success)
    assertEquals(errorResponse.error.type, decoded.error.type)
    assertEquals(errorResponse.error.message, decoded.error.message)
    assertEquals(errorResponse.error.suggestions, decoded.error.suggestions)
    assertEquals(errorResponse.command, decoded.command)
    assertEquals(errorResponse.timestamp, decoded.timestamp)
  }

  @Test
  fun `default arguments should be set correctly`() {
    // Given
    val args = TerminalArgs(command = "echo test")

    // Then
    assertEquals("echo test", args.command)
    assertEquals(null, args.workingDirectory)
    assertEquals(30000L, args.timeout)
    assertEquals(true, args.cleanOutput)
  }
}
