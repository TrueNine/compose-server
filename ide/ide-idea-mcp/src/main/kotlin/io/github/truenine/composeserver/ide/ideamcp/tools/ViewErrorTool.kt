package io.github.truenine.composeserver.ide.ideamcp.tools

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.McpLogManager
import io.github.truenine.composeserver.ide.ideamcp.common.ErrorDetails
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.ide.mcp.Response
import org.jetbrains.mcpserverplugin.AbstractMcpTool

/** 错误查看工具 提供通过 MCP 协议查看项目中错误、警告和弱警告信息的功能 */
class ViewErrorTool : AbstractMcpTool<ViewErrorArgs>(ViewErrorArgs.serializer()) {
  override val name: String = "view_error"
  override val description: String = "View all errors, warnings and weak warnings in files or directories"

  override fun handle(project: Project, args: ViewErrorArgs): Response {
    McpLogManager.info("开始查看错误信息 - 路径: ${args.path}", "ViewErrorTool")
    McpLogManager.debug("查看参数 - 包含警告: ${args.includeWarnings}, 包含弱警告: ${args.includeWeakWarnings}", "ViewErrorTool")

    return try {
      // 参数验证
      validateArgs(args, project)

      // TODO: 实现错误收集逻辑
      val errorReport = collectErrors(args, project)

      McpLogManager.info("错误查看完成 - 总错误数: ${errorReport.totalErrors}, 总警告数: ${errorReport.totalWarnings}", "ViewErrorTool")
      Response(Json.encodeToString(ViewErrorResult.serializer(), errorReport))
    } catch (e: Exception) {
      McpLogManager.error("错误查看失败: ${args.path}", "ViewErrorTool", e)
      val errorResponse = createErrorResponse(e, args.path)
      Response(Json.encodeToString(ViewErrorErrorResponse.serializer(), errorResponse))
    }
  }

  /** 验证参数 */
  private fun validateArgs(args: ViewErrorArgs, project: Project) {
    // 验证路径不为空
    if (args.path.isBlank()) {
      throw IllegalArgumentException("路径不能为空")
    }

    // 验证路径存在性和权限
    val resolvedPath = resolvePath(args.path, project)
    if (!resolvedPath.exists()) {
      throw IllegalArgumentException("指定的路径不存在: ${resolvedPath.absolutePath}")
    }

    if (!resolvedPath.canRead()) {
      throw SecurityException("没有权限访问路径: ${resolvedPath.absolutePath}")
    }

    McpLogManager.debug("参数验证通过 - 解析路径: ${resolvedPath.absolutePath}", "ViewErrorTool")
  }

  /** 解析路径 */
  private fun resolvePath(path: String, project: Project): File {
    return if (File(path).isAbsolute) {
      File(path)
    } else {
      File(project.basePath, path)
    }
  }

  /** 收集错误信息 */
  private fun collectErrors(args: ViewErrorArgs, project: Project): ViewErrorResult {
    val resolvedPath = resolvePath(args.path, project)

    // 使用 FileManager 解析路径
    val fileManager = project.service<io.github.truenine.composeserver.ide.ideamcp.services.FileManager>()
    val virtualFile = fileManager.resolvePathToVirtualFile(project, args.path) ?: throw IllegalArgumentException("无法解析路径: ${args.path}")

    // 使用 ErrorService 收集错误
    val errorService = project.service<io.github.truenine.composeserver.ide.ideamcp.services.ErrorService>()
    val fileErrors = errorService.collectErrors(project, virtualFile)

    // 根据参数过滤错误类型
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

  /** 构建摘要信息 */
  private fun buildSummary(errors: Int, warnings: Int, weakWarnings: Int): String {
    val parts = mutableListOf<String>()
    if (errors > 0) parts.add("${errors}个错误")
    if (warnings > 0) parts.add("${warnings}个警告")
    if (weakWarnings > 0) parts.add("${weakWarnings}个弱警告")

    return if (parts.isEmpty()) "无问题" else parts.joinToString(", ")
  }

  /** 创建错误响应 */
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
        "INVALID_PATH" -> listOf("检查路径格式", "使用绝对路径或相对于项目根目录的路径")
        "PERMISSION_DENIED" -> listOf("检查文件权限", "以适当权限运行 IDEA")
        "PATH_NOT_FOUND" -> listOf("确认路径存在", "检查路径拼写")
        else -> listOf("检查路径是否正确", "查看详细错误信息", "重试操作")
      }

    return ViewErrorErrorResponse(
      success = false,
      error = ErrorDetails(type = errorType, message = error.message ?: "未知错误", suggestions = suggestions),
      path = path,
      timestamp = System.currentTimeMillis(),
    )
  }
}

/** 错误查看参数 */
@Serializable
data class ViewErrorArgs(
  /** 要查看的文件或目录路径 */
  val path: String,
  /** 是否包含警告，默认为true */
  val includeWarnings: Boolean = true,
  /** 是否包含弱警告，默认为true */
  val includeWeakWarnings: Boolean = true,
)

/** 错误查看结果 */
@Serializable
data class ViewErrorResult(
  /** 原始路径 */
  val path: String,
  /** 解析后的绝对路径 */
  val resolvedPath: String,
  /** 总错误数 */
  val totalErrors: Int,
  /** 总警告数 */
  val totalWarnings: Int,
  /** 总弱警告数 */
  val totalWeakWarnings: Int,
  /** 文件错误信息列表 */
  val files: List<FileErrorInfo>,
  /** 摘要信息 */
  val summary: String,
)

/** 文件错误信息 */
@Serializable
data class FileErrorInfo(
  /** 文件路径 */
  val filePath: String,
  /** 相对路径 */
  val relativePath: String,
  /** 错误列表 */
  val errors: List<ErrorInfo>,
  /** 警告列表 */
  val warnings: List<ErrorInfo>,
  /** 弱警告列表 */
  val weakWarnings: List<ErrorInfo>,
  /** 文件摘要 */
  val summary: String,
)

/** 错误信息 */
@Serializable
data class ErrorInfo(
  /** 行号 */
  val line: Int,
  /** 列号 */
  val column: Int,
  /** 错误严重程度 */
  val severity: ErrorSeverity,
  /** 错误消息 */
  val message: String,
  /** 错误代码 */
  val code: String,
  /** 相关代码行 */
  val codeSnippet: String? = null,
  /** 快速修复建议 */
  val quickFixes: List<String> = emptyList(),
)

/** 错误严重程度 */
@Serializable
enum class ErrorSeverity {
  ERROR,
  WARNING,
  WEAK_WARNING,
  INFO,
}

/** 错误查看错误响应 */
@Serializable
data class ViewErrorErrorResponse(
  /** 是否成功 */
  val success: Boolean,
  /** 错误详情 */
  val error: ErrorDetails,
  /** 查看的路径 */
  val path: String,
  /** 时间戳 */
  val timestamp: Long,
)
