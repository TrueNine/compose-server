package io.github.truenine.composeserver.ide.ideamcp.tools

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.common.ErrorDetails
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.ide.mcp.Response
import org.jetbrains.mcpserverplugin.AbstractMcpTool

/**
 * Library code view tool.
 *
 * Provides viewing of third-party library source code or decompiled code through the MCP protocol, together with metadata.
 */
class ViewLibCodeTool : AbstractMcpTool<ViewLibCodeArgs>(ViewLibCodeArgs.serializer()) {
  override val name: String = "view_lib_code"
  override val description: String = "View library source code or decompiled code with metadata information"

  override fun handle(project: Project, args: ViewLibCodeArgs): Response {
    Logger.info("Start viewing library code - class: ${args.fullyQualifiedName}", "ViewLibCodeTool")
    Logger.debug("View arguments - member name: ${args.memberName}", "ViewLibCodeTool")

    return try {
      // Validate arguments
      validateArgs(args, project)

      // Fetch library code
      val libCodeResult = kotlinx.coroutines.runBlocking { getLibraryCode(args, project) }

      Logger.info("Library code view completed - type: ${libCodeResult.sourceType}, decompiled: ${libCodeResult.isDecompiled}", "ViewLibCodeTool")
      Logger.info("Returned source length: ${libCodeResult.sourceCode.length} characters", "ViewLibCodeTool")

      Response(Json.encodeToString(ViewLibCodeResult.serializer(), libCodeResult))
    } catch (e: Exception) {
      Logger.error("Library code view failed: ${args.fullyQualifiedName}", "ViewLibCodeTool", e)
      val errorResponse = createErrorResponse(e, args.fullyQualifiedName)
      Response(Json.encodeToString(ViewLibCodeErrorResponse.serializer(), errorResponse))
    }
  }

  /** Validate tool arguments. */
  private fun validateArgs(args: ViewLibCodeArgs, project: Project) {
    // Verify fully-qualified class name is not blank
    if (args.fullyQualifiedName.isBlank()) {
      throw IllegalArgumentException("Fully-qualified class name must not be blank")
    }

    // Verify class name format
    if (!isValidClassName(args.fullyQualifiedName)) {
      throw IllegalArgumentException("Invalid class name format: ${args.fullyQualifiedName}")
    }

    Logger.debug("Argument validation passed", "ViewLibCodeTool")
  }

  /** Validate class name format. */
  private fun isValidClassName(className: String): Boolean {
    // Basic class name format validation
    return className.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z0-9_]*)*$"))
  }

  /** Get library code from LibCodeService. */
  private suspend fun getLibraryCode(args: ViewLibCodeArgs, project: Project): ViewLibCodeResult {
    // Use LibCodeService to get library code
    val libCodeService = project.service<io.github.truenine.composeserver.ide.ideamcp.services.LibCodeService>()
    val result = libCodeService.getLibraryCode(project, args.fullyQualifiedName, args.memberName)

    return ViewLibCodeResult(
      success = true,
      fullyQualifiedName = args.fullyQualifiedName,
      memberName = args.memberName,
      sourceCode = result.sourceCode,
      isDecompiled = result.isDecompiled,
      language = result.language,
      sourceType = result.metadata.sourceType,
      metadata =
        ViewLibCodeMetadata(
          libraryName = result.metadata.libraryName,
          version = result.metadata.version,
          sourceType = result.metadata.sourceType,
          documentation = result.metadata.documentation,
        ),
      timestamp = System.currentTimeMillis(),
    )
  }

  /** Create error response. */
  private fun createErrorResponse(error: Throwable, className: String): ViewLibCodeErrorResponse {
    val errorType =
      when (error) {
        is IllegalArgumentException -> "INVALID_ARGUMENT"
        is ClassNotFoundException -> "CLASS_NOT_FOUND"
        is SecurityException -> "PERMISSION_DENIED"
        else -> "EXTRACTION_ERROR"
      }

    val suggestions =
      when (errorType) {
        "INVALID_ARGUMENT" -> listOf("Check class name format", "Use fully-qualified class name", "Ensure the file path is correct")
        "CLASS_NOT_FOUND" -> listOf("Check class name spelling", "Verify the class is on the classpath", "Refresh project dependencies")
        "PERMISSION_DENIED" -> listOf("Check file permissions", "Ensure the library file is accessible")
        else -> listOf("Check library file integrity", "Retry the operation", "Review detailed error information")
      }

    return ViewLibCodeErrorResponse(
      success = false,
      error = ErrorDetails(type = errorType, message = error.message ?: "Unknown error", suggestions = suggestions),
      fullyQualifiedName = className,
      timestamp = System.currentTimeMillis(),
    )
  }
}

/** Arguments for viewing library code. */
@Serializable
data class ViewLibCodeArgs(
  /** Fully-qualified class name. */
  val fullyQualifiedName: String,
  /** Optional member name, such as method or field. */
  val memberName: String? = null,
)

/** Result of viewing library code. */
@Serializable
data class ViewLibCodeResult(
  /** Whether the operation succeeded. */
  val success: Boolean,
  /** Fully-qualified class name. */
  val fullyQualifiedName: String,
  /** Member name. */
  val memberName: String?,
  /** Source code content. */
  val sourceCode: String,
  /** Whether the code is decompiled. */
  val isDecompiled: Boolean,
  /** Programming language. */
  val language: String,
  /** Source type. */
  val sourceType: SourceType,
  /** Metadata information. */
  val metadata: ViewLibCodeMetadata,
  /** Timestamp. */
  val timestamp: Long,
)

/** Library code metadata. */
@Serializable
data class ViewLibCodeMetadata(
  /** Library name. */
  val libraryName: String,
  /** Version. */
  val version: String?,
  /** Source type. */
  val sourceType: SourceType,
  /** Documentation information. */
  val documentation: String?,
)

/** Type of source code. */
@Serializable
enum class SourceType {
  /** From source JAR. */
  SOURCE_JAR,

  /** Decompiled from bytecode. */
  DECOMPILED,

  /** Source not found. */
  NOT_FOUND,
}

/** Error response for library code view. */
@Serializable
data class ViewLibCodeErrorResponse(
  /** Whether the operation succeeded. */
  val success: Boolean,
  /** Error details. */
  val error: ErrorDetails,
  /** Fully-qualified class name. */
  val fullyQualifiedName: String,
  /** Timestamp. */
  val timestamp: Long,
)
