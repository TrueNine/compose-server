package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import io.github.truenine.composeserver.ide.ideamcp.common.Logger

/**
 * MCP debug tool window factory.
 *
 * Responsible for creating and initializing debug tool window panels.
 */
class McpDebugToolWindowFactory : ToolWindowFactory {

  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val contentFactory = ContentFactory.getInstance()

    // Create terminal clean-up panel
    val terminalPanel = McpTerminalPanel(project)
    val terminalContent = contentFactory.createContent(terminalPanel, "Terminal Clean-up", false)
    terminalContent.isCloseable = false

    // Create log panel
    val debugPanel = McpDebugPanel(project)
    val logContent = contentFactory.createContent(debugPanel, "Logs", false)
    logContent.isCloseable = false

    // Create file-operations panel
    val fileOperationPanel = FileOperationPanel(project)
    val fileOperationContent = contentFactory.createContent(fileOperationPanel, "File Operations", false)
    fileOperationContent.isCloseable = false

    // Create LibCodeService test panel
    val libCodeTestPanel = LibCodeTestPanel(project)
    val libCodeTestContent = contentFactory.createContent(libCodeTestPanel, "LibCode Tests", false)
    libCodeTestContent.isCloseable = false

    // Add contents to tool window
    toolWindow.contentManager.addContent(terminalContent)
    toolWindow.contentManager.addContent(logContent)
    toolWindow.contentManager.addContent(fileOperationContent)
    toolWindow.contentManager.addContent(libCodeTestContent)

    // Select LibCode tests tab by default
    toolWindow.contentManager.setSelectedContent(libCodeTestContent)

    // Log tool window creation
    Logger.info("MCP debug tool window created - includes Terminal Clean-up, Logs, File Operations and LibCode Tests tabs", "ToolWindow")
  }

  override fun shouldBeAvailable(project: Project): Boolean {
    // Always available
    return true
  }
}
