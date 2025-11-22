package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeService
import java.awt.BorderLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.SwingUtilities
import javax.swing.border.TitledBorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * LibCodeService test panel.
 *
 * Provides a graphical interface to exercise various features of LibCodeService, including:
 * - Source extraction tests
 * - Decompilation tests
 * - Error handling tests
 * - Performance tests
 */
class LibCodeTestPanel(private val project: Project) : SimpleToolWindowPanel(true, true) {

  // UI components
  private val classNameField = JBTextField(30)
  private val memberNameField = JBTextField(30)
  private val testButton = JButton("Test LibCodeService")
  private val clearButton = JButton("Clear results")
  private val resultArea = JTextArea()
  private val enableLoggingCheckBox = JCheckBox("Enable verbose logging", true)
  private val statusLabel = JBLabel("Ready")

  // Coroutine scope
  private val coroutineScope = CoroutineScope(Dispatchers.Main)

  init {
    setupUI()
    setupEventHandlers()
    initializeDefaultValues()
  }

  /** Sets up the UI layout. */
  private fun setupUI() {
    val mainPanel = JPanel(BorderLayout())

    // Create input panel
    val inputPanel = createInputPanel()
    mainPanel.add(inputPanel, BorderLayout.NORTH)

    // Create result panel
    val resultPanel = createResultPanel()
    mainPanel.add(resultPanel, BorderLayout.CENTER)

    // Create status panel
    val statusPanel = createStatusPanel()
    mainPanel.add(statusPanel, BorderLayout.SOUTH)

    setContent(mainPanel)
  }

  /** Creates the input parameter panel. */
  private fun createInputPanel(): JPanel {
    val panel = JPanel(GridBagLayout())
    panel.border = TitledBorder("Test parameters")

    val gbc = GridBagConstraints()
    gbc.insets = Insets(5, 5, 5, 5)
    gbc.anchor = GridBagConstraints.WEST

    // Fully qualified class name
    gbc.gridx = 0
    gbc.gridy = 0
    gbc.fill = GridBagConstraints.NONE
    gbc.weightx = 0.0
    panel.add(JLabel("Fully qualified class name:"), gbc)
    gbc.gridx = 1
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.weightx = 1.0
    panel.add(classNameField, gbc)

    // Member name (optional)
    gbc.gridx = 0
    gbc.gridy = 1
    gbc.fill = GridBagConstraints.NONE
    gbc.weightx = 0.0
    panel.add(JLabel("Member name (optional):"), gbc)
    gbc.gridx = 1
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.weightx = 1.0
    panel.add(memberNameField, gbc)

    // Options
    gbc.gridx = 0
    gbc.gridy = 2
    gbc.gridwidth = 2
    gbc.fill = GridBagConstraints.NONE
    panel.add(enableLoggingCheckBox, gbc)

    // Button panel
    val buttonPanel = JPanel()
    buttonPanel.add(testButton)
    buttonPanel.add(clearButton)
    gbc.gridx = 0
    gbc.gridy = 3
    gbc.gridwidth = 2
    gbc.fill = GridBagConstraints.HORIZONTAL
    panel.add(buttonPanel, gbc)

    return panel
  }

  /** Creates the result display panel. */
  private fun createResultPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = TitledBorder("Test result")

    resultArea.apply {
      isEditable = false
      font = Font(Font.MONOSPACED, Font.PLAIN, 12)
      text = "Click 'Test LibCodeService' to start a test..."
    }

    val scrollPane = JBScrollPane(resultArea)
    panel.add(scrollPane, BorderLayout.CENTER)

    return panel
  }

  /** Creates the status panel. */
  private fun createStatusPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    statusLabel.font = statusLabel.font.deriveFont(Font.ITALIC)
    panel.add(statusLabel, BorderLayout.WEST)
    return panel
  }

  /** Sets up event handlers. */
  private fun setupEventHandlers() {
    testButton.addActionListener { performLibCodeTest() }

    clearButton.addActionListener { clearResults() }
  }

  /** Initializes default values. */
  private fun initializeDefaultValues() {
    classNameField.text = "java.util.ArrayList"
    memberNameField.text = ""
  }

  /** Runs a LibCodeService test. */
  private fun performLibCodeTest() {
    val className = classNameField.text.trim()
    val memberName = memberNameField.text.trim().takeIf { it.isNotEmpty() }

    // Parameter validation
    if (className.isEmpty()) {
      updateResult("Error: class name must not be empty")
      return
    }

    // Disable button and show progress
    testButton.isEnabled = false
    updateStatus("Running test...")
    updateResult("Starting LibCodeService test...\n")

    coroutineScope.launch {
      try {
        val startTime = System.currentTimeMillis()

        // Log test start
        if (enableLoggingCheckBox.isSelected) {
          Logger.info("Starting LibCodeService test - class: $className", "LibCodeTestPanel")
        }

        // Get service instance
        val libCodeService = project.service<LibCodeService>()

        // Execute test
        val result = withContext(Dispatchers.IO) { libCodeService.getLibraryCode(project, className, memberName) }

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Display result
        withContext(Dispatchers.Main) {
          displayTestResult(result, duration, className, memberName)
          updateStatus("Test completed")
        }

        if (enableLoggingCheckBox.isSelected) {
          Logger.info("LibCodeService test finished - duration: ${duration}ms", "LibCodeTestPanel")
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          displayError(e)
          updateStatus("Test failed")
        }

        if (enableLoggingCheckBox.isSelected) {
          Logger.error("LibCodeService test failed", "LibCodeTestPanel", e)
        }
      } finally {
        withContext(Dispatchers.Main) { testButton.isEnabled = true }
      }
    }
  }

  /** Displays the test result. */
  private fun displayTestResult(result: LibCodeResult, duration: Long, className: String, memberName: String?) {
    val resultText = buildString {
      appendLine("=== LibCodeService test result ===")
      appendLine("Test time: ${java.time.LocalDateTime.now()}")
      appendLine("Duration: ${duration}ms")
      appendLine()

      appendLine("Input parameters:")
      appendLine("  Class name: $className")
      appendLine("  Member name: ${memberName ?: "none"}")
      appendLine()

      appendLine("Result:")
      appendLine("  Language: ${result.language}")
      appendLine("  Decompiled: ${if (result.isDecompiled) "yes" else "no"}")
      appendLine("  Source type: ${result.metadata.sourceType}")
      appendLine("  Library: ${result.metadata.libraryName}")
      appendLine("  Version: ${result.metadata.version ?: "unknown"}")
      appendLine()

      appendLine("Source code:")
      appendLine("${"=".repeat(50)}")
      appendLine(result.sourceCode)
      appendLine("${"=".repeat(50)}")
      appendLine()
    }

    updateResult(resultText)
  }

  /** Displays an error. */
  private fun displayError(error: Throwable) {
    val errorText = buildString {
      appendLine("=== Test error ===")
      appendLine("Error type: ${error.javaClass.simpleName}")
      appendLine("Error message: ${error.message}")
      appendLine()
      appendLine("Stack trace:")
      error.stackTrace.take(10).forEach { element -> appendLine("  at $element") }
      appendLine()
    }

    updateResult(errorText)
  }

  /** Updates the result display. */
  private fun updateResult(text: String) {
    SwingUtilities.invokeLater {
      resultArea.text = text
      resultArea.caretPosition = 0
    }
  }

  /** Updates the status display. */
  private fun updateStatus(status: String) {
    SwingUtilities.invokeLater { statusLabel.text = status }
  }

  /** Clears the result area. */
  private fun clearResults() {
    resultArea.text = "Result cleared. Click 'Test LibCodeService' to start a new test..."
    updateStatus("Ready")
  }
}
