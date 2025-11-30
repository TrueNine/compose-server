package io.github.truenine.composeserver.ide.ideamcp.tools

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.TerminalOutputInterceptor
import io.github.truenine.composeserver.ide.ideamcp.common.ErrorDetails
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import kotlinx.serialization.Serializable
import org.jetbrains.ide.mcp.Response
import org.jetbrains.mcpserverplugin.AbstractMcpTool
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * Terminal command execution tool.
 *
 * Provides terminal command execution over MCP with cleaned output and structured error handling.
 */
class TerminalTool : AbstractMcpTool<TerminalArgs>(TerminalArgs.serializer()) {
  override val name: String = "terminal"
  override val description: String = "Execute terminal commands with clean output for AI processing"

  override fun handle(project: Project, args: TerminalArgs): Response {
    Logger.info("Start executing terminal command: ${args.command}", "TerminalTool")
    Logger.debug(
      "Command arguments - working directory: ${args.workingDirectory}, timeout: ${args.timeout}ms, clean output: ${args.cleanOutput}",
      "TerminalTool",
    )

    return try {
      // Validate arguments
      validateArgs(args, project)

      // Execute command
      val result = executeCommandWithTimeout(args, project)

      Logger.info("Terminal command completed - exit code: ${result.exitCode}", "TerminalTool")
      Response(kotlinx.serialization.json.Json.encodeToString(TerminalResult.serializer(), result))
    } catch (e: Exception) {
      Logger.error("Terminal command execution failed: ${args.command}", "TerminalTool", e)
      val errorResponse = createErrorResponse(e, args.command)
      Response(kotlinx.serialization.json.Json.encodeToString(TerminalErrorResponse.serializer(), errorResponse))
    }
  }

  /** Validate terminal command arguments. */
  private fun validateArgs(args: TerminalArgs, project: Project) {
    // Verify command is not blank
    if (args.command.isBlank()) {
      throw IllegalArgumentException("Command must not be blank")
    }

    // Verify timeout
    if (args.timeout <= 0) {
      throw IllegalArgumentException("Timeout must be greater than 0, current value: ${args.timeout}")
    }

    // Verify working directory
    args.workingDirectory?.let { workDir ->
      val resolvedDir =
        if (File(workDir).isAbsolute) {
          File(workDir)
        } else {
          File(project.basePath, workDir)
        }

      if (!resolvedDir.exists()) {
        throw IllegalArgumentException("Working directory does not exist: ${resolvedDir.absolutePath}")
      }

      if (!resolvedDir.isDirectory) {
        throw IllegalArgumentException("Specified working directory is not a directory: ${resolvedDir.absolutePath}")
      }
    }

    Logger.debug("Argument validation passed", "TerminalTool")
  }

  /** Execute command and handle timeout. */
  private fun executeCommandWithTimeout(args: TerminalArgs, project: Project): TerminalResult {
    val future = CompletableFuture<TerminalResult>()

    // Resolve working directory
    val workingDirectory =
      args.workingDirectory?.let { workDir ->
        if (File(workDir).isAbsolute) {
          workDir
        } else {
          File(project.basePath, workDir).absolutePath
        }
      }

    // Get terminal output interceptor service
    val outputInterceptor = project.service<TerminalOutputInterceptor>()

    // Execute command
    outputInterceptor.executeCommand(command = args.command, workingDirectory = workingDirectory) { commandResult ->
      try {
        val terminalResult =
          TerminalResult(
            command = commandResult.command,
            exitCode = commandResult.exitCode,
            output = if (args.cleanOutput) commandResult.cleanedOutput else commandResult.stdout,
            errorOutput = commandResult.stderr,
            executionTime = 0L, // TODO: add execution time calculation
            workingDirectory = workingDirectory ?: project.basePath ?: "",
          )

        future.complete(terminalResult)
      } catch (e: Exception) {
        future.completeExceptionally(e)
      }
    }

    // Wait for result or timeout
    return try {
      future.get(args.timeout, TimeUnit.MILLISECONDS)
    } catch (e: java.util.concurrent.TimeoutException) {
      Logger.error("Command execution timed out: ${args.command}", "TerminalTool", e)
      throw RuntimeException("Command execution timed out (${args.timeout}ms): ${args.command}")
    }
  }

  /** Create error response. */
  private fun createErrorResponse(error: Throwable, command: String): TerminalErrorResponse {
    val errorType =
      when (error) {
        is IllegalArgumentException -> "INVALID_ARGUMENT"
        is java.util.concurrent.TimeoutException -> "TIMEOUT"
        is SecurityException -> "PERMISSION_DENIED"
        else -> "EXECUTION_ERROR"
      }

    val suggestions =
      when (errorType) {
        "INVALID_ARGUMENT" -> listOf("Check command format and arguments", "Verify working directory path is correct")
        "TIMEOUT" -> listOf("Increase timeout", "Check whether the command is long-running", "Use a simpler command")
        "PERMISSION_DENIED" -> listOf("Check file permissions", "Run IDEA with appropriate privileges")
        else -> listOf("Verify the command is correct", "Review detailed error information", "Retry execution")
      }

    return TerminalErrorResponse(
      success = false,
      error = ErrorDetails(type = errorType, message = error.message ?: "Unknown error", suggestions = suggestions),
      command = command,
      timestamp = System.currentTimeMillis(),
    )
  }
}

/** Terminal command arguments. */
@Serializable
data class TerminalArgs(
  /** Command to execute. */
  val command: String,
  /** Working directory; absolute or relative to project root. */
  val workingDirectory: String? = null,
  /** Timeout in milliseconds (default 30 seconds). */
  val timeout: Long = 30000,
  /** Whether to clean output (default true). */
  val cleanOutput: Boolean = true,
)

/** Terminal command result. */
@Serializable
data class TerminalResult(
  /** Executed command. */
  val command: String,
  /** Exit code. */
  val exitCode: Int,
  /** Standard output (possibly cleaned). */
  val output: String,
  /** Error output. */
  val errorOutput: String,
  /** Execution time in milliseconds. */
  val executionTime: Long,
  /** Working directory. */
  val workingDirectory: String,
)

/** Error response for terminal commands. */
@Serializable
data class TerminalErrorResponse(
  /** Whether the operation succeeded. */
  val success: Boolean,
  /** Error details. */
  val error: ErrorDetails,
  /** Executed command. */
  val command: String,
  /** Timestamp. */
  val timestamp: Long,
)
