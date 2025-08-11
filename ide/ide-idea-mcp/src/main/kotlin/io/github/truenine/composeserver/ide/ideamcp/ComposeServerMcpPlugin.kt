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
    return Response("Compose Server处理完成，参数: ${args.param1}, 数值: ${args.param2}")
  }
}

// 定义参数数据类
@Serializable data class MyArgs(val param1: String, val param2: Int)
