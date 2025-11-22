package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import kotlinx.serialization.Serializable
import org.jetbrains.ide.mcp.Response
import org.jetbrains.mcpserverplugin.AbstractMcpTool

class ComposeServerMcpPlugin : AbstractMcpTool<MyArgs>(MyArgs.serializer()) {
  override val name: String = "compose_server_tool"
  override val description: String = "Compose Server MCP integration tool"

  override fun handle(project: Project, args: MyArgs): Response {
    Logger.info("Start handling MCP request", "ComposeServerMcpPlugin")
    Logger.debug("Request arguments - param1: ${args.param1}, param2: ${args.param2}", "ComposeServerMcpPlugin")

    return try {
      val result = processRequest(args)
      Logger.info("MCP request processed successfully", "ComposeServerMcpPlugin")
      Response(result)
    } catch (e: Exception) {
      Logger.error("MCP request processing failed", "ComposeServerMcpPlugin", e)
      Response("Processing failed: ${e.message}")
    }
  }

  private fun processRequest(args: MyArgs): String {
    Logger.debug("Processing business logic", "ComposeServerMcpPlugin")

    // Business logic can be added here
    val result = "Compose Server processing completed, param: ${args.param1}, value: ${args.param2}"

    Logger.debug("Business logic processed, result length: ${result.length}", "ComposeServerMcpPlugin")
    return result
  }
}

// Parameter data class
@Serializable data class MyArgs(val param1: String, val param2: Int)
