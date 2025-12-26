package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.*
import com.intellij.util.ui.JBUI
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import kotlinx.coroutines.*

/** MCP terminal clean-up panel providing command input and output cleaning features. */
class McpTerminalPanel(private val project: Project) : SimpleToolWindowPanel(true, true), Disposable {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private val interceptor by lazy { project.service<TerminalOutputInterceptor>() }

  // UI components
  private val commandField = JBTextField()
  private val executeButton = JButton("Execute")
  private val clearButton = JButton("Clear")
  private val outputArea = JBTextArea()
  private val cleanedOutputArea = JBTextArea()

  // Command history
  private val commandHistory = mutableListOf<String>()
  private var historyIndex = -1

  init {
    setupUI()
    setupEventHandlers()
    Logger.info("Terminal clean-up panel initialized")
  }

  private fun setupUI() {
    layout = BorderLayout()

    // Create input panel
    val inputPanel = createInputPanel()

    // Create output panel
    val outputPanel = createOutputPanel()

    add(inputPanel, BorderLayout.NORTH)
    add(outputPanel, BorderLayout.CENTER)
  }

  private fun createInputPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = JBUI.Borders.empty(5)

    // Command input area
    val inputContainer = JPanel(BorderLayout())
    inputContainer.add(JLabel("Command: "), BorderLayout.WEST)

    commandField.apply {
      preferredSize = Dimension(0, 28)
      toolTipText = "Enter terminal command; use Up/Down to navigate history"
    }
    inputContainer.add(commandField, BorderLayout.CENTER)

    // Button area
    val buttonPanel = JPanel()
    buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.X_AXIS)

    executeButton.apply {
      preferredSize = Dimension(80, 28)
      toolTipText = "Execute command"
    }

    clearButton.apply {
      preferredSize = Dimension(80, 28)
      toolTipText = "Clear output"
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

    // Create split panel
    val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT)
    splitPane.resizeWeight = 0.5

    // Raw output area
    val rawOutputPanel = JPanel(BorderLayout())
    rawOutputPanel.add(JLabel("Raw output:"), BorderLayout.NORTH)

    outputArea.apply {
      isEditable = false
      font = Font(Font.MONOSPACED, Font.PLAIN, 12)
      background = UIManager.getColor("Panel.background")
      lineWrap = true
      wrapStyleWord = true
    }
    rawOutputPanel.add(JBScrollPane(outputArea), BorderLayout.CENTER)

    // Cleaned output area
    val cleanedOutputPanel = JPanel(BorderLayout())
    cleanedOutputPanel.add(JLabel("Cleaned output:"), BorderLayout.NORTH)

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
    // Execute button click
    executeButton.addActionListener { executeCommand() }

    // Clear button click
    clearButton.addActionListener { clearOutput() }

    // Command input key events
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

    // Add to history
    addToHistory(command)

    // Disable input while command runs
    setInputEnabled(false)

    // Show execution status
    appendToOutput("$ $command\n")
    appendToCleanedOutput("Executing command: $command\n")

    // Use project path as working directory
    val workingDir = project.basePath

    // Execute command
    interceptor.executeCommand(command, workingDir) { result ->
      ApplicationManager.getApplication().invokeLater {
        handleCommandResult(result)
        setInputEnabled(true)
      }
    }
  }

  private fun handleCommandResult(result: TerminalOutputInterceptor.CommandResult) {
    // Show raw output
    val rawOutput = buildString {
      if (result.stdout.isNotEmpty()) {
        append("=== Standard output ===\n")
        append(result.stdout)
        append("\n")
      }
      if (result.stderr.isNotEmpty()) {
        append("=== Error output ===\n")
        append(result.stderr)
        append("\n")
      }
      append("=== Exit code: ${result.exitCode} ===\n\n")
    }
    appendToOutput(rawOutput)

    // Show cleaned output
    val cleanedOutput =
      if (result.cleanedOutput.isNotEmpty()) {
        result.cleanedOutput + "\n\n"
      } else {
        "(no output)\n\n"
      }
    appendToCleanedOutput(cleanedOutput)

    // Log result
    if (result.exitCode == 0) {
      Logger.info("Command executed successfully: ${result.command}")
    } else {
      Logger.warn("Command execution failed: ${result.command} (exit code: ${result.exitCode})")
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
    Logger.info("Terminal output cleared")
  }

  private fun setInputEnabled(enabled: Boolean) {
    commandField.isEnabled = enabled
    executeButton.isEnabled = enabled
  }

  private fun addToHistory(command: String) {
    // Avoid adding duplicate consecutive commands
    if (commandHistory.isEmpty() || commandHistory.last() != command) {
      commandHistory.add(command)
      // Limit history size
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

  override fun dispose() {
    // Cancel all coroutines
    scope.cancel()
    Logger.debug("McpTerminalPanel disposed", "McpTerminalPanel")
  }
}
