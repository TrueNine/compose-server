package io.github.truenine.composeserver.ide.ideamcp.tools

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.common.ErrorDetails
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import io.github.truenine.composeserver.ide.ideamcp.services.CleanOptions
import io.github.truenine.composeserver.ide.ideamcp.services.CleanService
import io.github.truenine.composeserver.ide.ideamcp.services.FileManager
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.ide.mcp.Response
import org.jetbrains.mcpserverplugin.AbstractMcpTool

/**
 * Code clean-up tool.
 *
 * Provides code clean-up operations over MCP, including formatting,
 * import optimization, and inspections/fixes.
 */
class CleanCodeTool : AbstractMcpTool<CleanCodeArgs>(CleanCodeArgs.serializer()) {
  override val name: String = "clean_code"
  override val description: String = "Clean and format code using IDEA capabilities with comprehensive reporting"

  override fun handle(project: Project, args: CleanCodeArgs): Response {
    Logger.info("Start code clean-up: ${args.path}", "CleanCodeTool")
    Logger.debug(
      "Clean options - format: ${args.formatCode}, optimize imports: ${args.optimizeImports}, run inspections: ${args.runInspections}",
      "CleanCodeTool",
    )

    return try {
      // Validate arguments
      validateArgs(args, project)

      // Execute clean-up asynchronously
      val result = runBlocking { executeCleanOperation(args, project) }

      Logger.info("Code clean-up completed - processed: ${result.processedFiles}, modified: ${result.modifiedFiles}", "CleanCodeTool")
      Response(Json.encodeToString(CleanCodeResult.serializer(), result))
    } catch (e: Exception) {
      Logger.error("Code clean-up failed: ${args.path}", "CleanCodeTool", e)
      val errorResponse = createErrorResponse(e, args.path)
      Response(Json.encodeToString(CleanCodeErrorResponse.serializer(), errorResponse))
    }
  }

  /** Validate clean-up arguments. */
  private fun validateArgs(args: CleanCodeArgs, project: Project) {
    // Verify path is not blank
    if (args.path.isBlank()) {
      throw IllegalArgumentException("Path must not be blank")
    }

    // Verify at least one operation is enabled
    if (!args.formatCode && !args.optimizeImports && !args.runInspections) {
      throw IllegalArgumentException("At least one clean-up operation must be selected")
    }

    Logger.debug("Argument validation passed", "CleanCodeTool")
  }

  /** Execute clean-up operation. */
  private suspend fun executeCleanOperation(args: CleanCodeArgs, project: Project): CleanCodeResult {
    Logger.debug("Start executing clean-up operation", "CleanCodeTool")

    // Resolve service instances
    val cleanService = project.service<CleanService>()
    val fileManager = project.service<FileManager>()

    // Resolve path to VirtualFile
    val virtualFile =
      fileManager.resolvePathToVirtualFile(project, args.path)
        ?: throw IllegalArgumentException("Path does not exist or is not accessible: ${args.path}")

    // Build clean-up options
    val cleanOptions =
      CleanOptions(formatCode = args.formatCode, optimizeImports = args.optimizeImports, runInspections = args.runInspections, rearrangeCode = false)

    // Run clean-up
    val cleanResult = cleanService.cleanCode(project, virtualFile, cleanOptions)

    return CleanCodeResult(
      success = true,
      path = args.path,
      processedFiles = cleanResult.processedFiles,
      modifiedFiles = cleanResult.modifiedFiles,
      operations = cleanResult.operations,
      summary = cleanResult.summary,
      executionTime = cleanResult.executionTime,
      timestamp = System.currentTimeMillis(),
    )
  }

  /** Create error response. */
  private fun createErrorResponse(error: Throwable, path: String): CleanCodeErrorResponse {
    val errorType =
      when (error) {
        is IllegalArgumentException -> "INVALID_ARGUMENT"
        is SecurityException -> "PERMISSION_DENIED"
        is java.nio.file.NoSuchFileException -> "PATH_NOT_FOUND"
        else -> "EXECUTION_ERROR"
      }

    val suggestions =
      when (errorType) {
        "INVALID_ARGUMENT" -> listOf("Check path format", "Ensure at least one clean-up operation is selected")
        "PERMISSION_DENIED" -> listOf("Check file permissions", "Ensure the file is not locked by another process")
        "PATH_NOT_FOUND" -> listOf("Verify that the path exists", "Use a path relative to the project root")
        else -> listOf("Check file state", "Retry the operation", "Review detailed error information")
      }

    return CleanCodeErrorResponse(
      success = false,
      error = ErrorDetails(type = errorType, message = error.message ?: "Unknown error", suggestions = suggestions),
      path = path,
      timestamp = System.currentTimeMillis(),
    )
  }
}

/** Code clean-up arguments. */
@Serializable
data class CleanCodeArgs(
  /** File or directory path to clean. */
  val path: String,
  /** Whether to format code (default true). */
  val formatCode: Boolean = true,
  /** Whether to optimize imports (default true). */
  val optimizeImports: Boolean = true,
  /** Whether to run inspections and apply fixes (default true). */
  val runInspections: Boolean = true,
)

/** Code clean-up result. */
@Serializable
data class CleanCodeResult(
  /** Whether the operation succeeded. */
  val success: Boolean,
  /** Path that was cleaned. */
  val path: String,
  /** Number of processed files. */
  val processedFiles: Int,
  /** Number of modified files. */
  val modifiedFiles: Int,
  /** List of executed operations. */
  val operations: List<CleanOperation>,
  /** Operation summary. */
  val summary: String,
  /** Execution time in milliseconds. */
  val executionTime: Long,
  /** Timestamp. */
  val timestamp: Long,
)

/** Clean-up operation entry. */
@Serializable
data class CleanOperation(
  /** Operation type. */
  val type: String,
  /** Operation description. */
  val description: String,
  /** Number of affected files. */
  val filesAffected: Int,
)

/** Error response for code clean-up. */
@Serializable
data class CleanCodeErrorResponse(
  /** Whether the operation succeeded. */
  val success: Boolean,
  /** Error details. */
  val error: ErrorDetails,
  /** Path that was being cleaned. */
  val path: String,
  /** Timestamp. */
  val timestamp: Long,
)
