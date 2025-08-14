package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.UIManager

/** MCP 终端清洗面板 提供命令输入和输出清洗功能 */
class McpTerminalPanel(private val project: Project) : SimpleToolWindowPanel(true, true) {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private val interceptor = TerminalOutputInterceptor()

  // UI 组件
  private val commandField = JBTextField()
  private val executeButton = JButton("执行")
  private val clearButton = JButton("清空")
  private val outputArea = JBTextArea()
  private val cleanedOutputArea = JBTextArea()

  // 命令历史
  private val commandHistory = mutableListOf<String>()
  private var historyIndex = -1

  init {
    setupUI()
    setupEventHandlers()
    McpLogManager.info("终端清洗面板已初始化", LogSource.UI.displayName)
  }

  private fun setupUI() {
    layout = BorderLayout()

    // 创建输入面板
    val inputPanel = createInputPanel()

    // 创建输出面板
    val outputPanel = createOutputPanel()

    add(inputPanel, BorderLayout.NORTH)
    add(outputPanel, BorderLayout.CENTER)
  }

  private fun createInputPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = JBUI.Borders.empty(5)

    // 命令输入区域
    val inputContainer = JPanel(BorderLayout())
    inputContainer.add(JLabel("命令: "), BorderLayout.WEST)

    commandField.apply {
      preferredSize = Dimension(0, 28)
      toolTipText = "输入终端命令，支持上下键浏览历史"
    }
    inputContainer.add(commandField, BorderLayout.CENTER)

    // 按钮区域
    val buttonPanel = JPanel()
    buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.X_AXIS)

    executeButton.apply {
      preferredSize = Dimension(80, 28)
      toolTipText = "执行命令"
    }

    clearButton.apply {
      preferredSize = Dimension(80, 28)
      toolTipText = "清空输出"
    }

    buttonPanel.add(Box.createHorizontalStrut(5))
    buttonPanel.add(executeButton)
    buttonPanel.add(Box.createHorizontalStrut(5))
    buttonPanel.add(clearButton)

    panel.add(inputContainer, BorderLayout.CENTER)
    panel.add(buttonPanel, BorderLayout.EAST)

    return panel
  }

  private fun createOutputPanel(): JPanel {
    val panel = JPanel(BorderLayout())

    // 创建分割面板
    val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT)
    splitPane.resizeWeight = 0.5

    // 原始输出区域
    val rawOutputPanel = JPanel(BorderLayout())
    rawOutputPanel.add(JLabel("原始输出:"), BorderLayout.NORTH)

    outputArea.apply {
      isEditable = false
      font = Font(Font.MONOSPACED, Font.PLAIN, 12)
      background = UIManager.getColor("Panel.background")
      lineWrap = true
      wrapStyleWord = true
    }
    rawOutputPanel.add(JBScrollPane(outputArea), BorderLayout.CENTER)

    // 清洗后输出区域
    val cleanedOutputPanel = JPanel(BorderLayout())
    cleanedOutputPanel.add(JLabel("清洗后输出:"), BorderLayout.NORTH)

    cleanedOutputArea.apply {
      isEditable = false
      font = Font(Font.MONOSPACED, Font.PLAIN, 12)
      background = UIManager.getColor("Panel.background")
      lineWrap = true
      wrapStyleWord = true
    }
    cleanedOutputPanel.add(JBScrollPane(cleanedOutputArea), BorderLayout.CENTER)

    splitPane.topComponent = rawOutputPanel
    splitPane.bottomComponent = cleanedOutputPanel

    panel.add(splitPane, BorderLayout.CENTER)

    return panel
  }

  private fun setupEventHandlers() {
    // 执行按钮点击
    executeButton.addActionListener { executeCommand() }

    // 清空按钮点击
    clearButton.addActionListener { clearOutput() }

    // 命令输入框按键事件
    commandField.addKeyListener(
      object : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {
          when (e.keyCode) {
            KeyEvent.VK_ENTER -> {
              if (!e.isShiftDown) {
                executeCommand()
              }
            }

            KeyEvent.VK_UP -> {
              navigateHistory(-1)
            }

            KeyEvent.VK_DOWN -> {
              navigateHistory(1)
            }
          }
        }
      }
    )
  }

  private fun executeCommand() {
    val command = commandField.text.trim()
    if (command.isEmpty()) {
      return
    }

    // 添加到历史记录
    addToHistory(command)

    // 禁用输入
    setInputEnabled(false)

    // 显示执行状态
    appendToOutput("$ $command\n")
    appendToCleanedOutput("执行命令: $command\n")

    // 获取项目路径作为工作目录
    val workingDir = project.basePath

    // 执行命令
    interceptor.executeCommand(command, workingDir) { result ->
      ApplicationManager.getApplication().invokeLater {
        handleCommandResult(result)
        setInputEnabled(true)
      }
    }
  }

  private fun handleCommandResult(result: TerminalOutputInterceptor.CommandResult) {
    // 显示原始输出
    val rawOutput = buildString {
      if (result.stdout.isNotEmpty()) {
        append("=== 标准输出 ===\n")
        append(result.stdout)
        append("\n")
      }
      if (result.stderr.isNotEmpty()) {
        append("=== 错误输出 ===\n")
        append(result.stderr)
        append("\n")
      }
      append("=== 退出代码: ${result.exitCode} ===\n\n")
    }
    appendToOutput(rawOutput)

    // 显示清洗后输出
    val cleanedOutput =
      if (result.cleanedOutput.isNotEmpty()) {
        result.cleanedOutput + "\n\n"
      } else {
        "（无输出内容）\n\n"
      }
    appendToCleanedOutput(cleanedOutput)

    // 记录到日志
    if (result.exitCode == 0) {
      McpLogManager.info("命令执行成功: ${result.command}", LogSource.TERMINAL.displayName)
    } else {
      McpLogManager.warn("命令执行失败: ${result.command} (退出代码: ${result.exitCode})", LogSource.TERMINAL.displayName)
    }
  }

  private fun appendToOutput(text: String) {
    outputArea.append(text)
    outputArea.caretPosition = outputArea.document.length
  }

  private fun appendToCleanedOutput(text: String) {
    cleanedOutputArea.append(text)
    cleanedOutputArea.caretPosition = cleanedOutputArea.document.length
  }

  private fun clearOutput() {
    outputArea.text = ""
    cleanedOutputArea.text = ""
    McpLogManager.info("终端输出已清空", LogSource.UI.displayName)
  }

  private fun setInputEnabled(enabled: Boolean) {
    commandField.isEnabled = enabled
    executeButton.isEnabled = enabled
  }

  private fun addToHistory(command: String) {
    // 避免重复添加相同命令
    if (commandHistory.isEmpty() || commandHistory.last() != command) {
      commandHistory.add(command)
      // 限制历史记录数量
      if (commandHistory.size > 50) {
        commandHistory.removeAt(0)
      }
    }
    historyIndex = commandHistory.size
    commandField.text = ""
  }

  private fun navigateHistory(direction: Int) {
    if (commandHistory.isEmpty()) return

    historyIndex = (historyIndex + direction).coerceIn(0, commandHistory.size)

    commandField.text =
      if (historyIndex < commandHistory.size) {
        commandHistory[historyIndex]
      } else {
        ""
      }
  }
}
