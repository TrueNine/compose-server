package io.github.truenine.composeserver.ide.ideamcp.tools

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.McpLogManager
import io.github.truenine.composeserver.ide.ideamcp.common.ErrorDetails
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.ide.mcp.Response
import org.jetbrains.mcpserverplugin.AbstractMcpTool

/** 库代码查看工具 提供通过 MCP 协议查看第三方库源代码或反编译代码的功能 */
class ViewLibCodeTool : AbstractMcpTool<ViewLibCodeArgs>(ViewLibCodeArgs.serializer()) {
  override val name: String = "view_lib_code"
  override val description: String = "View library source code or decompiled code with metadata information"

  override fun handle(project: Project, args: ViewLibCodeArgs): Response {
    McpLogManager.info("开始查看库代码 - 文件: ${args.filePath}, 类: ${args.fullyQualifiedName}", "ViewLibCodeTool")
    McpLogManager.debug("查看参数 - 成员名: ${args.memberName}", "ViewLibCodeTool")

    return try {
      // 参数验证
      validateArgs(args, project)

      // 获取库代码
      val libCodeResult = kotlinx.coroutines.runBlocking { getLibraryCode(args, project) }

      McpLogManager.info("库代码查看完成 - 类型: ${libCodeResult.sourceType}, 反编译: ${libCodeResult.isDecompiled}", "ViewLibCodeTool")
      Response(Json.encodeToString(ViewLibCodeResult.serializer(), libCodeResult))
    } catch (e: Exception) {
      McpLogManager.error("库代码查看失败: ${args.fullyQualifiedName}", "ViewLibCodeTool", e)
      val errorResponse = createErrorResponse(e, args.fullyQualifiedName)
      Response(Json.encodeToString(ViewLibCodeErrorResponse.serializer(), errorResponse))
    }
  }

  /** 验证参数 */
  private fun validateArgs(args: ViewLibCodeArgs, project: Project) {
    // 验证文件路径不为空
    if (args.filePath.isBlank()) {
      throw IllegalArgumentException("文件路径不能为空")
    }

    // 验证完全限定类名不为空
    if (args.fullyQualifiedName.isBlank()) {
      throw IllegalArgumentException("完全限定类名不能为空")
    }

    // 验证类名格式
    if (!isValidClassName(args.fullyQualifiedName)) {
      throw IllegalArgumentException("无效的类名格式: ${args.fullyQualifiedName}")
    }

    McpLogManager.debug("参数验证通过", "ViewLibCodeTool")
  }

  /** 验证类名格式 */
  private fun isValidClassName(className: String): Boolean {
    // 基本的类名格式验证
    return className.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z0-9_]*)*$"))
  }

  /** 获取库代码 */
  private suspend fun getLibraryCode(args: ViewLibCodeArgs, project: Project): ViewLibCodeResult {
    // 使用 LibCodeService 获取库代码
    val libCodeService = project.service<io.github.truenine.composeserver.ide.ideamcp.services.LibCodeService>()
    val result = libCodeService.getLibraryCode(project, args.filePath, args.fullyQualifiedName, args.memberName)

    return ViewLibCodeResult(
      success = true,
      filePath = args.filePath,
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

  /** 创建错误响应 */
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
        "INVALID_ARGUMENT" -> listOf("检查类名格式", "使用完全限定类名", "确保文件路径正确")
        "CLASS_NOT_FOUND" -> listOf("检查类名拼写", "确认类在类路径中", "刷新项目依赖")
        "PERMISSION_DENIED" -> listOf("检查文件权限", "确保库文件可访问")
        else -> listOf("检查库文件完整性", "重试操作", "查看详细错误信息")
      }

    return ViewLibCodeErrorResponse(
      success = false,
      error = ErrorDetails(type = errorType, message = error.message ?: "未知错误", suggestions = suggestions),
      fullyQualifiedName = className,
      timestamp = System.currentTimeMillis(),
    )
  }
}

/** 库代码查看参数 */
@Serializable
data class ViewLibCodeArgs(
  /** 文件路径 */
  val filePath: String,
  /** 完全限定类名 */
  val fullyQualifiedName: String,
  /** 成员名（可选，如方法名或字段名） */
  val memberName: String? = null,
)

/** 库代码查看结果 */
@Serializable
data class ViewLibCodeResult(
  /** 是否成功 */
  val success: Boolean,
  /** 文件路径 */
  val filePath: String,
  /** 完全限定类名 */
  val fullyQualifiedName: String,
  /** 成员名 */
  val memberName: String?,
  /** 源代码内容 */
  val sourceCode: String,
  /** 是否为反编译代码 */
  val isDecompiled: Boolean,
  /** 编程语言 */
  val language: String,
  /** 源码类型 */
  val sourceType: SourceType,
  /** 元数据信息 */
  val metadata: ViewLibCodeMetadata,
  /** 时间戳 */
  val timestamp: Long,
)

/** 库代码元数据 */
@Serializable
data class ViewLibCodeMetadata(
  /** 库名称 */
  val libraryName: String,
  /** 版本号 */
  val version: String?,
  /** 源码类型 */
  val sourceType: SourceType,
  /** 文档信息 */
  val documentation: String?,
)

/** 源码类型 */
@Serializable
enum class SourceType {
  /** 来自 source jar */
  SOURCE_JAR,

  /** 反编译得到 */
  DECOMPILED,

  /** 未找到 */
  NOT_FOUND,
}

/** 库代码查看错误响应 */
@Serializable
data class ViewLibCodeErrorResponse(
  /** 是否成功 */
  val success: Boolean,
  /** 错误详情 */
  val error: ErrorDetails,
  /** 完全限定类名 */
  val fullyQualifiedName: String,
  /** 时间戳 */
  val timestamp: Long,
)
