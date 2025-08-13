package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/** MCP 调试工具窗口工厂 负责创建和初始化调试面板工具窗口 */
class McpDebugToolWindowFactory : ToolWindowFactory {

  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    // 创建调试面板
    val debugPanel = McpDebugPanel(project)

    // 创建内容
    val contentFactory = ContentFactory.getInstance()
    val content = contentFactory.createContent(debugPanel, "日志", false)
    content.isCloseable = false

    // 添加到工具窗口
    toolWindow.contentManager.addContent(content)

    // 记录工具窗口创建日志
    McpLogManager.info("MCP 调试工具窗口已创建", "ToolWindow")
  }

  override fun shouldBeAvailable(project: Project): Boolean {
    // 始终可用
    return true
  }
}
