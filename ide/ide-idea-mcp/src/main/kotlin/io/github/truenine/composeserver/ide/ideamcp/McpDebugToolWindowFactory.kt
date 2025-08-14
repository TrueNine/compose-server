package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/** MCP 调试工具窗口工厂 负责创建和初始化调试面板工具窗口 */
class McpDebugToolWindowFactory : ToolWindowFactory {

  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val contentFactory = ContentFactory.getInstance()

    // 创建终端清洗面板
    val terminalPanel = McpTerminalPanel(project)
    val terminalContent = contentFactory.createContent(terminalPanel, "终端清洗", false)
    terminalContent.isCloseable = false

    // 创建日志面板
    val debugPanel = McpDebugPanel(project)
    val logContent = contentFactory.createContent(debugPanel, "日志", false)
    logContent.isCloseable = false

    // 创建文件操作面板
    val fileOperationPanel = FileOperationPanel(project)
    val fileOperationContent = contentFactory.createContent(fileOperationPanel, "文件操作", false)
    fileOperationContent.isCloseable = false

    // 添加到工具窗口
    toolWindow.contentManager.addContent(terminalContent)
    toolWindow.contentManager.addContent(logContent)
    toolWindow.contentManager.addContent(fileOperationContent)

    // 设置默认选中日志标签页
    toolWindow.contentManager.setSelectedContent(logContent)

    // 记录工具窗口创建日志
    McpLogManager.info("MCP 调试工具窗口已创建 - 包含终端清洗、日志和文件操作三个标签页", "ToolWindow")
  }

  override fun shouldBeAvailable(project: Project): Boolean {
    // 始终可用
    return true
  }
}
