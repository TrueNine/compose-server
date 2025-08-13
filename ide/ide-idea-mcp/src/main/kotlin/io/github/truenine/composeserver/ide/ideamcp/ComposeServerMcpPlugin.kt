package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.project.Project
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import org.jetbrains.ide.mcp.Response
import org.jetbrains.mcpserverplugin.AbstractMcpTool

class ComposeServerMcpPlugin : AbstractMcpTool<MyArgs>(MyArgs.serializer()) {
  override val name: String = "compose_server_tool"
  override val description: String = "Compose Server MCP integration tool"

  override fun handle(project: Project, args: MyArgs): Response {
    McpLogManager.info("开始处理 MCP 请求", "ComposeServerMcpPlugin")
    McpLogManager.debug("请求参数 - param1: ${args.param1}, param2: ${args.param2}", "ComposeServerMcpPlugin")

    return try {
      val result = processRequest(args)
      McpLogManager.info("MCP 请求处理成功", "ComposeServerMcpPlugin")
      Response(result)
    } catch (e: Exception) {
      McpLogManager.error("MCP 请求处理失败", "ComposeServerMcpPlugin", e)
      Response("处理失败: ${e.message}")
    }
  }

  private fun processRequest(args: MyArgs): String {
    McpLogManager.debug("正在处理业务逻辑", "ComposeServerMcpPlugin")

    // 这里可以添加具体的业务逻辑
    val result = "Compose Server处理完成，参数: ${args.param1}, 数值: ${args.param2}"

    McpLogManager.debug("业务逻辑处理完成，结果长度: ${result.length}", "ComposeServerMcpPlugin")
    return result
  }
}

// 定义参数数据类
@Serializable data class MyArgs(val param1: String, val param2: Int)
