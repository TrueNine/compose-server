package io.github.truenine.composeserver.ide.ideamcp.tools

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.common.ErrorDetails
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.ide.mcp.Response
import org.jetbrains.mcpserverplugin.AbstractMcpTool

/**
 * Error view tool.
 *
 * Provides an MCP endpoint to view errors, warnings, and weak warnings in files or directories.
 */
class ViewErrorTool : AbstractMcpTool<ViewErrorArgs>(ViewErrorArgs.serializer()) {
  override val name: String = "view_error"
  override val description: String = "View all errors, warnings and weak warnings in files or directories"

  override fun handle(project: Project, args: ViewErrorArgs): Response {
    Logger.info("Starting error inspection - path: ${args.path}", "ViewErrorTool")
    Logger.debug(
      "View parameters - includeWarnings: ${args.includeWarnings}, includeWeakWarnings: ${args.includeWeakWarnings}",
      "ViewErrorTool",
    )

    return try {
      // Validate arguments
      validateArgs(args, project)

      val errorReport = collectErrors(args, project)

      Logger.info(
        "Error inspection completed - totalErrors: ${errorReport.totalErrors}, totalWarnings: ${errorReport.totalWarnings}",
        "ViewErrorTool",
      )
      Response(Json.encodeToString(ViewErrorResult.serializer(), errorReport))
    } catch (e: Exception) {
      Logger.error("Error inspection failed for path: ${args.path}", "ViewErrorTool", e)
      val errorResponse = createErrorResponse(e, args.path)
      Response(Json.encodeToString(ViewErrorErrorResponse.serializer(), errorResponse))
    }
  }

  /** Validates tool arguments. */
  private fun validateArgs(args: ViewErrorArgs, project: Project) {
    // Validate that path is not blank
    if (args.path.isBlank()) {
      throw IllegalArgumentException("Path must not be blank")
    }

    // Validate path existence and permissions
    val resolvedPath = resolvePath(args.path, project)
    if (!resolvedPath.exists()) {
      throw IllegalArgumentException("Specified path does not exist: ${resolvedPath.absolutePath}")
    }

    if (!resolvedPath.canRead()) {
      throw SecurityException("Permission denied for path: ${resolvedPath.absolutePath}")
    }

    Logger.debug("Argument validation succeeded - resolved path: ${resolvedPath.absolutePath}", "ViewErrorTool")
  }

  /** Resolves a path to an absolute File based on the project root when necessary. */
  private fun resolvePath(path: String, project: Project): File {
    return if (File(path).isAbsolute) {
      File(path)
    } else {
      File(project.basePath, path)
    }
  }

  /** Collects error information according to the given arguments. */
  private fun collectErrors(args: ViewErrorArgs, project: Project): ViewErrorResult {
    val resolvedPath = resolvePath(args.path, project)

    // Use FileManager to resolve the path to a VirtualFile
    val fileManager = project.service<io.github.truenine.composeserver.ide.ideamcp.services.FileManager>()
    val virtualFile =
      fileManager.resolvePathToVirtualFile(project, args.path)
        ?: throw IllegalArgumentException("Failed to resolve path: ${args.path}")

    // Use ErrorService to collect errors
    val errorService = project.service<io.github.truenine.composeserver.ide.ideamcp.services.ErrorService>()
    val fileErrors = errorService.collectErrors(project, virtualFile)

    // Filter by severity based on arguments
    val filteredFiles =
      fileErrors
        .map { fileInfo ->
          val errors = fileInfo.errors
          val warnings = if (args.includeWarnings) fileInfo.warnings else emptyList()
          val weakWarnings = if (args.includeWeakWarnings) fileInfo.weakWarnings else emptyList()

          fileInfo.copy(warnings = warnings, weakWarnings = weakWarnings, summary = buildSummary(errors.size, warnings.size, weakWarnings.size))
        }
        .filter { it.errors.isNotEmpty() || it.warnings.isNotEmpty() || it.weakWarnings.isNotEmpty() }

    val totalErrors = filteredFiles.sumOf { it.errors.size }
    val totalWarnings = filteredFiles.sumOf { it.warnings.size }
    val totalWeakWarnings = filteredFiles.sumOf { it.weakWarnings.size }

    return ViewErrorResult(
      path = args.path,
      resolvedPath = resolvedPath.absolutePath,
      totalErrors = totalErrors,
      totalWarnings = totalWarnings,
      totalWeakWarnings = totalWeakWarnings,
      files = filteredFiles,
      summary = buildSummary(totalErrors, totalWarnings, totalWeakWarnings),
    )
  }

  /** Builds a human-readable summary string for counts. */
  private fun buildSummary(errors: Int, warnings: Int, weakWarnings: Int): String {
    val parts = mutableListOf<String>()
    if (errors > 0) parts.add("$errors errors")
    if (warnings > 0) parts.add("$warnings warnings")
    if (weakWarnings > 0) parts.add("$weakWarnings weak warnings")

    return if (parts.isEmpty()) "No issues" else parts.joinToString(", ")
  }

  /** Creates an error response payload. */
  private fun createErrorResponse(error: Throwable, path: String): ViewErrorErrorResponse {
    val errorType =
      when (error) {
        is IllegalArgumentException -> "INVALID_PATH"
        is SecurityException -> "PERMISSION_DENIED"
        is java.io.FileNotFoundException -> "PATH_NOT_FOUND"
        else -> "COLLECTION_ERROR"
      }

    val suggestions =
      when (errorType) {
        "INVALID_PATH" -> listOf("Check path format", "Use an absolute path or one relative to the project root")
        "PERMISSION_DENIED" -> listOf("Check file permissions", "Run the IDE with sufficient privileges")
        "PATH_NOT_FOUND" -> listOf("Confirm that the path exists", "Check for typos in the path")
        else -> listOf("Verify that the path is correct", "Inspect detailed error information", "Retry the operation")
      }

    return ViewErrorErrorResponse(
      success = false,
      error = ErrorDetails(type = errorType, message = error.message ?: "Unknown error", suggestions = suggestions),
      path = path,
      timestamp = System.currentTimeMillis(),
    )
  }
}

/** Arguments for the error view tool. */
@Serializable
data class ViewErrorArgs(
  /** File or directory path to inspect. */
  val path: String,
  /** Whether to include warnings; defaults to true. */
  val includeWarnings: Boolean = true,
  /** Whether to include weak warnings; defaults to true. */
  val includeWeakWarnings: Boolean = true,
)

/** Result of an error view operation. */
@Serializable
data class ViewErrorResult(
  /** Original path argument. */
  val path: String,
  /** Resolved absolute path. */
  val resolvedPath: String,
  /** Total number of errors. */
  val totalErrors: Int,
  /** Total number of warnings. */
  val totalWarnings: Int,
  /** Total number of weak warnings. */
  val totalWeakWarnings: Int,
  /** Per-file error information. */
  val files: List<FileErrorInfo>,
  /** Human-readable summary. */
  val summary: String,
)

/** Error information for a single file. */
@Serializable
data class FileErrorInfo(
  /** File path. */
  val filePath: String,
  /** Relative path. */
  val relativePath: String,
  /** List of errors. */
  val errors: List<ErrorInfo>,
  /** List of warnings. */
  val warnings: List<ErrorInfo>,
  /** List of weak warnings. */
  val weakWarnings: List<ErrorInfo>,
  /** Per-file summary. */
  val summary: String,
)

/** Detailed error information. */
@Serializable
data class ErrorInfo(
  /** Line number. */
  val line: Int,
  /** Column number. */
  val column: Int,
  /** Error severity. */
  val severity: ErrorSeverity,
  /** Error message. */
  val message: String,
  /** Machine-readable error code. */
  val code: String,
  /** Relevant code snippet. */
  val codeSnippet: String? = null,
  /** Suggested quick fixes. */
  val quickFixes: List<String> = emptyList(),
)

/** Error severity levels. */
@Serializable
enum class ErrorSeverity {
  ERROR,
  WARNING,
  WEAK_WARNING,
  INFO,
}

/** Error response payload for the view-error tool. */
@Serializable
data class ViewErrorErrorResponse(
  /** Whether the operation succeeded. */
  val success: Boolean,
  /** Error details. */
  val error: ErrorDetails,
  /** Path that was inspected. */
  val path: String,
  /** Timestamp when the response was generated. */
  val timestamp: Long,
)
