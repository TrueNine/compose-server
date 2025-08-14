package io.github.truenine.composeserver.ide.ideamcp.tools

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.McpLogManager
import io.github.truenine.composeserver.ide.ideamcp.common.ErrorDetails
import io.github.truenine.composeserver.ide.ideamcp.services.CleanOptions
import io.github.truenine.composeserver.ide.ideamcp.services.CleanService
import io.github.truenine.composeserver.ide.ideamcp.services.FileManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.jetbrains.ide.mcp.Response
import org.jetbrains.mcpserverplugin.AbstractMcpTool
import kotlinx.coroutines.runBlocking

/**
 * 代码清理工具
 * 提供通过 MCP 协议执行代码清理操作的功能，包括格式化、导入优化、检查修复等
 */
class CleanCodeTool : AbstractMcpTool<CleanCodeArgs>(CleanCodeArgs.serializer()) {
  override val name: String = "clean_code"
  override val description: String = "Clean and format code using IDEA capabilities with comprehensive reporting"

  override fun handle(project: Project, args: CleanCodeArgs): Response {
    McpLogManager.info("开始执行代码清理: ${args.path}", "CleanCodeTool")
    McpLogManager.debug("清理参数 - 格式化: ${args.formatCode}, 优化导入: ${args.optimizeImports}, 运行检查: ${args.runInspections}", "CleanCodeTool")

    return try {
      // 参数验证
      validateArgs(args, project)
      
      // 异步执行清理操作
      val result = runBlocking {
        executeCleanOperation(args, project)
      }
      
      McpLogManager.info("代码清理完成 - 处理文件: ${result.processedFiles}, 修改文件: ${result.modifiedFiles}", "CleanCodeTool")
      Response(Json.encodeToString(CleanCodeResult.serializer(), result))
    } catch (e: Exception) {
      McpLogManager.error("代码清理失败: ${args.path}", "CleanCodeTool", e)
      val errorResponse = createErrorResponse(e, args.path)
      Response(Json.encodeToString(CleanCodeErrorResponse.serializer(), errorResponse))
    }
  }

  /**
   * 验证清理参数
   */
  private fun validateArgs(args: CleanCodeArgs, project: Project) {
    // 验证路径不为空
    if (args.path.isBlank()) {
      throw IllegalArgumentException("路径不能为空")
    }

    // 验证至少选择一种清理操作
    if (!args.formatCode && !args.optimizeImports && !args.runInspections) {
      throw IllegalArgumentException("必须至少选择一种清理操作")
    }

    McpLogManager.debug("参数验证通过", "CleanCodeTool")
  }

  /**
   * 执行清理操作
   */
  private suspend fun executeCleanOperation(args: CleanCodeArgs, project: Project): CleanCodeResult {
    McpLogManager.debug("开始执行清理操作", "CleanCodeTool")
    
    // 获取服务实例
    val cleanService = project.service<CleanService>()
    val fileManager = project.service<FileManager>()
    
    // 解析路径到 VirtualFile
    val virtualFile = fileManager.resolvePathToVirtualFile(project, args.path)
      ?: throw IllegalArgumentException("路径不存在或无法访问: ${args.path}")
    
    // 创建清理选项
    val cleanOptions = CleanOptions(
      formatCode = args.formatCode,
      optimizeImports = args.optimizeImports,
      runInspections = args.runInspections,
      rearrangeCode = false
    )
    
    // 执行清理操作
    val cleanResult = cleanService.cleanCode(project, virtualFile, cleanOptions)
    
    return CleanCodeResult(
      success = true,
      path = args.path,
      processedFiles = cleanResult.processedFiles,
      modifiedFiles = cleanResult.modifiedFiles,
      operations = cleanResult.operations,
      summary = cleanResult.summary,
      executionTime = cleanResult.executionTime,
      timestamp = System.currentTimeMillis()
    )
  }

  /**
   * 创建错误响应
   */
  private fun createErrorResponse(error: Throwable, path: String): CleanCodeErrorResponse {
    val errorType = when (error) {
      is IllegalArgumentException -> "INVALID_ARGUMENT"
      is SecurityException -> "PERMISSION_DENIED"
      is java.nio.file.NoSuchFileException -> "PATH_NOT_FOUND"
      else -> "EXECUTION_ERROR"
    }

    val suggestions = when (errorType) {
      "INVALID_ARGUMENT" -> listOf("检查路径格式", "确保至少选择一种清理操作")
      "PERMISSION_DENIED" -> listOf("检查文件权限", "确保文件未被其他进程锁定")
      "PATH_NOT_FOUND" -> listOf("检查路径是否存在", "使用相对于项目根目录的路径")
      else -> listOf("检查文件状态", "重试操作", "查看详细错误信息")
    }

    return CleanCodeErrorResponse(
      success = false,
      error = ErrorDetails(
        type = errorType,
        message = error.message ?: "未知错误",
        suggestions = suggestions
      ),
      path = path,
      timestamp = System.currentTimeMillis()
    )
  }
}

/**
 * 代码清理参数
 */
@Serializable
data class CleanCodeArgs(
  /** 要清理的文件或目录路径 */
  val path: String,
  /** 是否执行代码格式化，默认为true */
  val formatCode: Boolean = true,
  /** 是否优化导入，默认为true */
  val optimizeImports: Boolean = true,
  /** 是否运行代码检查并修复，默认为true */
  val runInspections: Boolean = true
)

/**
 * 代码清理结果
 */
@Serializable
data class CleanCodeResult(
  /** 是否成功 */
  val success: Boolean,
  /** 清理的路径 */
  val path: String,
  /** 处理的文件数量 */
  val processedFiles: Int,
  /** 修改的文件数量 */
  val modifiedFiles: Int,
  /** 执行的操作列表 */
  val operations: List<CleanOperation>,
  /** 操作摘要 */
  val summary: String,
  /** 执行时间（毫秒） */
  val executionTime: Long,
  /** 时间戳 */
  val timestamp: Long
)

/**
 * 清理操作
 */
@Serializable
data class CleanOperation(
  /** 操作类型 */
  val type: String,
  /** 操作描述 */
  val description: String,
  /** 影响的文件数量 */
  val filesAffected: Int
)

/**
 * 代码清理错误响应
 */
@Serializable
data class CleanCodeErrorResponse(
  /** 是否成功 */
  val success: Boolean,
  /** 错误详情 */
  val error: ErrorDetails,
  /** 清理的路径 */
  val path: String,
  /** 时间戳 */
  val timestamp: Long
)
