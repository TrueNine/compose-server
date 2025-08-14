package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.util.Key
import java.nio.charset.Charset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** 终端输出拦截器 负责拦截和处理终端命令的输出结果 */
class TerminalOutputInterceptor(private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)) {

  /** 执行命令并拦截输出的结果数据类 */
  data class CommandResult(val command: String, val exitCode: Int, val stdout: String, val stderr: String, val cleanedOutput: String)

  /** 执行命令并拦截输出 */
  fun executeCommand(command: String, workingDirectory: String? = null, onResult: (CommandResult) -> Unit) {
    scope.launch {
      try {
        McpLogManager.logTerminalCommand(command)

        // 解析命令行参数
        val parts = command.split(" ").filter { it.isNotEmpty() }
        if (parts.isEmpty()) {
          throw IllegalArgumentException("Empty command")
        }

        val commandLine =
          GeneralCommandLine().apply {
            exePath = parts[0]
            if (parts.size > 1) {
              addParameters(parts.drop(1))
            }
            workingDirectory?.let { withWorkDirectory(it) }
            charset = Charset.forName("UTF-8")
          }

        val processHandler = OSProcessHandler(commandLine)
        val stdout = StringBuilder()
        val stderr = StringBuilder()

        processHandler.addProcessListener(
          object : ProcessListener {
            override fun processTerminated(event: ProcessEvent) {
              val stdoutText = stdout.toString()
              val stderrText = stderr.toString()

              // 记录原始输出
              if (stdoutText.isNotEmpty()) {
                McpLogManager.logTerminalOutput(stdoutText, false)
              }
              if (stderrText.isNotEmpty()) {
                McpLogManager.logTerminalOutput(stderrText, true)
              }

              // 清洗输出
              val cleanedOutput = cleanOutput(stdoutText, stderrText)
              McpLogManager.logTerminalCleanOutput(stdoutText + stderrText, cleanedOutput)

              val result = CommandResult(command = command, exitCode = event.exitCode, stdout = stdoutText, stderr = stderrText, cleanedOutput = cleanedOutput)

              onResult(result)
            }

            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
              val text = event.text
              if (outputType.toString().contains("STDOUT")) {
                stdout.append(text)
              } else if (outputType.toString().contains("STDERR")) {
                stderr.append(text)
              }
            }
          }
        )

        processHandler.startNotify()
      } catch (e: Exception) {
        McpLogManager.error("执行命令失败: $command", LogSource.INTERCEPTOR.displayName, e)
        val errorResult =
          CommandResult(command = command, exitCode = -1, stdout = "", stderr = e.message ?: "Unknown error", cleanedOutput = "命令执行失败: ${e.message}")
        onResult(errorResult)
      }
    }
  }

  /** 清洗输出内容 移除不必要的控制字符和格式化内容 */
  private fun cleanOutput(stdout: String, stderr: String): String {
    val combinedOutput =
      if (stderr.isNotEmpty()) {
        "标准输出:\n$stdout\n\n错误输出:\n$stderr"
      } else {
        stdout
      }

    return combinedOutput
      .lines()
      .filter { line -> line.trim().isNotEmpty() } // 移除空行
      .joinToString("\n") { line ->
        line
          .replace(Regex("\u001B\\[[;\\d]*m"), "") // 移除 ANSI 颜色代码
          .replace(Regex("\\r"), "") // 移除回车符
          .trim()
      }
      .let { cleaned ->
        // 限制输出长度，避免过长的日志
        if (cleaned.length > 5000) {
          cleaned.take(5000) + "\n...(输出被截断)"
        } else {
          cleaned
        }
      }
  }

  /** 高级清洗功能 可扩展为 AI 处理 */
  fun enhancedCleanOutput(output: String): String {
    // TODO: 这里可以集成 AI 服务进行智能清洗
    // 当前使用基础的文本处理规则
    return output
      .lines()
      .filter { line ->
        val trimmed = line.trim()
        // 过滤掉常见的无用输出
        trimmed.isNotEmpty() &&
          !trimmed.startsWith("[INFO]") &&
          !trimmed.startsWith("[DEBUG]") &&
          !trimmed.contains("BUILD SUCCESSFUL") &&
          !trimmed.contains("seconds")
      }
      .joinToString("\n")
  }
}
