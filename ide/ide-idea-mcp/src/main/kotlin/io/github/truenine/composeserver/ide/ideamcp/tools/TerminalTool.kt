package io.github.truenine.composeserver.ide.ideamcp.tools

import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.McpLogManager
import io.github.truenine.composeserver.ide.ideamcp.TerminalOutputInterceptor
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.jetbrains.ide.mcp.Response
import org.jetbrains.mcpserverplugin.AbstractMcpTool
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * 终端命令执行工具
 * 提供通过 MCP 协议执行终端命令的功能，支持输出清洗和错误处理
 */
class TerminalTool : AbstractMcpTool<TerminalArgs>(TerminalArgs.serializer()) {
  override val name: String = "terminal"
  override val description: String = "Execute terminal commands with clean output for AI processing"

  private val outputInterceptor = TerminalOutputInterceptor()

  override fun handle(project: Project, args: TerminalArgs): Response {
    McpLogManager.info("开始执行终端命令: ${args.command}", "TerminalTool")
    McpLogManager.debug("命令参数 - 工作目录: ${args.workingDirectory}, 超时: ${args.timeout}ms, 清洗输出: ${args.cleanOutput}", "TerminalTool")

    return try {
      // 参数验证
      validateArgs(args, project)
      
      // 执行命令
      val result = executeCommandWithTimeout(args, project)
      
      McpLogManager.info("终端命令执行完成 - 退出码: ${result.exitCode}", "TerminalTool")
      Response(kotlinx.serialization.json.Json.encodeToString(TerminalResult.serializer(), result))
    } catch (e: Exception) {
      McpLogManager.error("终端命令执行失败: ${args.command}", "TerminalTool", e)
      val errorResponse = createErrorResponse(e, args.command)
      Response(kotlinx.serialization.json.Json.encodeToString(TerminalErrorResponse.serializer(), errorResponse))
    }
  }

  /**
   * 验证命令参数
   */
  private fun validateArgs(args: TerminalArgs, project: Project) {
    // 验证命令不为空
    if (args.command.isBlank()) {
      throw IllegalArgumentException("命令不能为空")
    }

    // 验证超时时间
    if (args.timeout <= 0) {
      throw IllegalArgumentException("超时时间必须大于0，当前值: ${args.timeout}")
    }

    // 验证工作目录
    args.workingDirectory?.let { workDir ->
      val resolvedDir = if (File(workDir).isAbsolute) {
        File(workDir)
      } else {
        File(project.basePath, workDir)
      }
      
      if (!resolvedDir.exists()) {
        throw IllegalArgumentException("工作目录不存在: ${resolvedDir.absolutePath}")
      }
      
      if (!resolvedDir.isDirectory) {
        throw IllegalArgumentException("指定的工作目录不是一个目录: ${resolvedDir.absolutePath}")
      }
    }

    McpLogManager.debug("参数验证通过", "TerminalTool")
  }

  /**
   * 执行命令并处理超时
   */
  private fun executeCommandWithTimeout(args: TerminalArgs, project: Project): TerminalResult {
    val future = CompletableFuture<TerminalResult>()
    
    // 解析工作目录
    val workingDirectory = args.workingDirectory?.let { workDir ->
      if (File(workDir).isAbsolute) {
        workDir
      } else {
        File(project.basePath, workDir).absolutePath
      }
    }

    // 执行命令
    outputInterceptor.executeCommand(
      command = args.command,
      workingDirectory = workingDirectory
    ) { commandResult ->
      try {
        val terminalResult = TerminalResult(
          command = commandResult.command,
          exitCode = commandResult.exitCode,
          output = if (args.cleanOutput) commandResult.cleanedOutput else commandResult.stdout,
          errorOutput = commandResult.stderr,
          executionTime = 0L, // TODO: 添加执行时间计算
          workingDirectory = workingDirectory ?: project.basePath ?: ""
        )
        
        future.complete(terminalResult)
      } catch (e: Exception) {
        future.completeExceptionally(e)
      }
    }

    // 等待结果或超时
    return try {
      future.get(args.timeout, TimeUnit.MILLISECONDS)
    } catch (e: java.util.concurrent.TimeoutException) {
      McpLogManager.error("命令执行超时: ${args.command}", "TerminalTool", e)
      throw RuntimeException("命令执行超时 (${args.timeout}ms): ${args.command}")
    }
  }

  /**
   * 创建错误响应
   */
  private fun createErrorResponse(error: Throwable, command: String): TerminalErrorResponse {
    val errorType = when (error) {
      is IllegalArgumentException -> "INVALID_ARGUMENT"
      is java.util.concurrent.TimeoutException -> "TIMEOUT"
      is SecurityException -> "PERMISSION_DENIED"
      else -> "EXECUTION_ERROR"
    }

    val suggestions = when (errorType) {
      "INVALID_ARGUMENT" -> listOf("检查命令格式和参数", "确认工作目录路径正确")
      "TIMEOUT" -> listOf("增加超时时间", "检查命令是否会长时间运行", "使用更简单的命令")
      "PERMISSION_DENIED" -> listOf("检查文件权限", "以适当权限运行 IDEA")
      else -> listOf("检查命令是否正确", "查看详细错误信息", "重试执行")
    }

    return TerminalErrorResponse(
      success = false,
      error = ErrorDetails(
        type = errorType,
        message = error.message ?: "未知错误",
        suggestions = suggestions
      ),
      command = command,
      timestamp = System.currentTimeMillis()
    )
  }
}

/**
 * 终端命令参数
 */
@Serializable
data class TerminalArgs(
  /** 要执行的命令 */
  val command: String,
  /** 工作目录，可以是绝对路径或相对于项目根目录的路径 */
  val workingDirectory: String? = null,
  /** 超时时间（毫秒），默认30秒 */
  val timeout: Long = 30000,
  /** 是否清洗输出，默认为true */
  val cleanOutput: Boolean = true
)

/**
 * 终端命令执行结果
 */
@Serializable
data class TerminalResult(
  /** 执行的命令 */
  val command: String,
  /** 退出码 */
  val exitCode: Int,
  /** 标准输出（可能已清洗） */
  val output: String,
  /** 错误输出 */
  val errorOutput: String,
  /** 执行时间（毫秒） */
  val executionTime: Long,
  /** 工作目录 */
  val workingDirectory: String
)

/**
 * 终端命令错误响应
 */
@Serializable
data class TerminalErrorResponse(
  /** 是否成功 */
  val success: Boolean,
  /** 错误详情 */
  val error: ErrorDetails,
  /** 执行的命令 */
  val command: String,
  /** 时间戳 */
  val timestamp: Long
)

/**
 * 错误详情
 */
@Serializable
data class ErrorDetails(
  /** 错误类型 */
  val type: String,
  /** 错误消息 */
  val message: String,
  /** 建议的解决方案 */
  val suggestions: List<String>
)
