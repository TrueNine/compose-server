package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.*
import com.intellij.util.ui.JBUI
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import io.github.truenine.composeserver.ide.ideamcp.services.*
import java.awt.*
import javax.swing.*
import kotlinx.coroutines.*

/** File operation panel that provides file selection and batch operations, integrating error view and code clean-up features. */
class FileOperationPanel(private val project: Project) : SimpleToolWindowPanel(true, true), Disposable {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

  // Service dependencies
  private val errorService: ErrorService by lazy { project.service<ErrorService>() }
  private val cleanService: CleanService by lazy { project.service<CleanService>() }
  private val fileManager: FileManager by lazy { project.service<FileManager>() }

  // UI components
  private val pathField = JBTextField()
  private val browseButton = JButton("Browse")
  private val viewErrorsButton = JButton("View Errors")
  private val cleanCodeButton = JButton("Clean Code")

  // Clean-up options
  private val formatCodeCheckBox = JCheckBox("Format code", true)
  private val optimizeImportsCheckBox = JCheckBox("Optimize imports", true)
  private val runInspectionsCheckBox = JCheckBox("Run inspections and quick fixes", true)
  private val rearrangeCodeCheckBox = JCheckBox("Rearrange code", false)

  // Result display
  private val resultArea = JBTextArea()

  init {
    setupUI()
    setupEventHandlers()
    Logger.info("FileOperationPanel initialized")
  }

  private fun setupUI() {
    layout = BorderLayout()

    // Create main panel
    val mainPanel = JPanel(GridBagLayout())
    mainPanel.border = JBUI.Borders.empty(10)

    val gbc = GridBagConstraints()

    // File selection area
    addFileSelectionSection(mainPanel, gbc)

    // Separator
    gbc.gridy++
    gbc.gridx = 0
    gbc.gridwidth = 3
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.insets = Insets(10, 0, 10, 0)
    mainPanel.add(JSeparator(), gbc)

    // Operation options area
    addOperationOptionsSection(mainPanel, gbc)

    // Separator
    gbc.gridy++
    gbc.gridx = 0
    gbc.gridwidth = 3
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.insets = Insets(10, 0, 10, 0)
    mainPanel.add(JSeparator(), gbc)

    // Action buttons area
    addActionButtonsSection(mainPanel, gbc)

    // Result display area
    addResultDisplaySection(mainPanel, gbc)

    add(JBScrollPane(mainPanel), BorderLayout.CENTER)
  }

  private fun addFileSelectionSection(parent: JPanel, gbc: GridBagConstraints) {
    gbc.gridy = 0
    gbc.gridx = 0
    gbc.gridwidth = 1
    gbc.fill = GridBagConstraints.NONE
    gbc.anchor = GridBagConstraints.WEST
    gbc.insets = Insets(5, 0, 5, 10)
    parent.add(JLabel("Select path:"), gbc)

    gbc.gridx = 1
    gbc.gridwidth = 1
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.weightx = 1.0
    gbc.insets = Insets(5, 0, 5, 10)
    pathField.apply {
      preferredSize = Dimension(0, 28)
      toolTipText = "Enter a file or directory path, or click Browse to select"
      text = project.basePath ?: ""
    }
    parent.add(pathField, gbc)

    gbc.gridx = 2
    gbc.gridwidth = 1
    gbc.fill = GridBagConstraints.NONE
    gbc.weightx = 0.0
    gbc.insets = Insets(5, 0, 5, 0)
    browseButton.apply {
      preferredSize = Dimension(80, 28)
      toolTipText = "Browse to select a file or directory"
    }
    parent.add(browseButton, gbc)
  }

  private fun addOperationOptionsSection(parent: JPanel, gbc: GridBagConstraints) {
    gbc.gridy++
    gbc.gridx = 0
    gbc.gridwidth = 3
    gbc.fill = GridBagConstraints.NONE
    gbc.anchor = GridBagConstraints.WEST
    gbc.insets = Insets(5, 0, 5, 0)
    parent.add(JLabel("Code clean-up options:"), gbc)

    val optionsPanel = JPanel()
    optionsPanel.layout = BoxLayout(optionsPanel, BoxLayout.Y_AXIS)

    optionsPanel.add(formatCodeCheckBox)
    optionsPanel.add(optimizeImportsCheckBox)
    optionsPanel.add(runInspectionsCheckBox)
    optionsPanel.add(rearrangeCodeCheckBox)

    gbc.gridy++
    gbc.gridx = 0
    gbc.gridwidth = 3
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.insets = Insets(0, 20, 5, 0)
    parent.add(optionsPanel, gbc)
  }

  private fun addActionButtonsSection(parent: JPanel, gbc: GridBagConstraints) {
    val buttonPanel = JPanel()
    buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.X_AXIS)

    viewErrorsButton.apply {
      preferredSize = Dimension(120, 32)
      toolTipText = "Scan and display all errors and warnings under the selected path"
    }

    cleanCodeButton.apply {
      preferredSize = Dimension(120, 32)
      toolTipText = "Run code clean-up on files under the selected path"
    }

    buttonPanel.add(viewErrorsButton)
    buttonPanel.add(Box.createHorizontalStrut(10))
    buttonPanel.add(cleanCodeButton)
    buttonPanel.add(Box.createHorizontalGlue())

    gbc.gridy++
    gbc.gridx = 0
    gbc.gridwidth = 3
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.insets = Insets(10, 0, 10, 0)
    parent.add(buttonPanel, gbc)
  }

  private fun addResultDisplaySection(parent: JPanel, gbc: GridBagConstraints) {
    gbc.gridy++
    gbc.gridx = 0
    gbc.gridwidth = 3
    gbc.fill = GridBagConstraints.NONE
    gbc.anchor = GridBagConstraints.WEST
    gbc.insets = Insets(5, 0, 5, 0)
    parent.add(JLabel("Operation result:"), gbc)

    resultArea.apply {
      isEditable = false
      lineWrap = true
      wrapStyleWord = true
      rows = 15
      text = "Select a file or directory, then run an operation..."
    }

    gbc.gridy++
    gbc.gridx = 0
    gbc.gridwidth = 3
    gbc.fill = GridBagConstraints.BOTH
    gbc.weightx = 1.0
    gbc.weighty = 1.0
    gbc.insets = Insets(0, 0, 0, 0)
    parent.add(JBScrollPane(resultArea), gbc)
  }

  private fun setupEventHandlers() {
    // Browse button click
    browseButton.addActionListener { browseForPath() }

    // View errors button click
    viewErrorsButton.addActionListener { viewErrors() }

    // Clean code button click
    cleanCodeButton.addActionListener { cleanCode() }
  }

  private fun browseForPath() {
    val descriptor = FileChooserDescriptor(true, true, false, false, false, false)
    descriptor.title = "Select file or directory"
    descriptor.description = "Select the target file or directory to operate on"

    val currentPath = pathField.text.trim()
    val initialFile =
      if (currentPath.isNotEmpty()) {
        fileManager.resolvePathToVirtualFile(project, currentPath)
      } else {
        project.projectFile?.parent
      }

    val selectedFiles = FileChooser.chooseFiles(descriptor, project, initialFile)
    if (selectedFiles.isNotEmpty()) {
      pathField.text = selectedFiles[0].path
      Logger.info("Selected path: ${selectedFiles[0].path}")
    }
  }

  private fun viewErrors() {
    val path = pathField.text.trim()
    if (path.isEmpty()) {
      showError("Please select a file or directory path first")
      return
    }

    val virtualFile = fileManager.resolvePathToVirtualFile(project, path)
    if (virtualFile == null) {
      showError("Path does not exist or is not accessible: $path")
      return
    }

    // Disable buttons and show progress
    setButtonsEnabled(false)
    resultArea.text = "Scanning errors, please wait..."

    scope.launch {
      try {
        val fileErrorInfos = withContext(Dispatchers.IO) { errorService.collectErrors(project, virtualFile) }

        ApplicationManager.getApplication().invokeLater {
          displayErrorResults(fileErrorInfos, virtualFile)
          setButtonsEnabled(true)
        }
      } catch (e: Exception) {
        ApplicationManager.getApplication().invokeLater {
          showError("Failed to scan errors: ${e.message}")
          setButtonsEnabled(true)
        }
        Logger.error("Error scan failed", e)
      }
    }
  }

  private fun cleanCode() {
    val path = pathField.text.trim()
    if (path.isEmpty()) {
      showError("Please select a file or directory path first")
      return
    }

    val virtualFile = fileManager.resolvePathToVirtualFile(project, path)
    if (virtualFile == null) {
      showError("Path does not exist or is not accessible: $path")
      return
    }

    val options =
      CleanOptions(
        formatCode = formatCodeCheckBox.isSelected,
        optimizeImports = optimizeImportsCheckBox.isSelected,
        runInspections = runInspectionsCheckBox.isSelected,
        rearrangeCode = rearrangeCodeCheckBox.isSelected,
      )

    // Disable buttons and show progress
    setButtonsEnabled(false)
    resultArea.text = "Running code clean-up, please wait..."

    scope.launch {
      try {
        val cleanResult = cleanService.cleanCode(project, virtualFile, options)

        ApplicationManager.getApplication().invokeLater {
          displayCleanResults(cleanResult, virtualFile)
          setButtonsEnabled(true)
        }
      } catch (e: Exception) {
        ApplicationManager.getApplication().invokeLater {
          showError("Code clean-up failed: ${e.message}")
          setButtonsEnabled(true)
        }
        Logger.error("Code clean-up failed", e)
      }
    }
  }

  private fun displayErrorResults(fileErrorInfos: List<io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo>, virtualFile: VirtualFile) {
    val result = buildString {
      appendLine("=== Error scan result ===")
      appendLine("Scanned path: ${virtualFile.path}")
      appendLine("Scan time: ${java.time.LocalDateTime.now()}")
      appendLine()

      if (fileErrorInfos.isEmpty()) {
        appendLine("[OK] No errors or warnings found")
      } else {
        val totalErrors = fileErrorInfos.sumOf { it.errors.size }
        val totalWarnings = fileErrorInfos.sumOf { it.warnings.size }
        val totalWeakWarnings = fileErrorInfos.sumOf { it.weakWarnings.size }

        appendLine("Statistics:")
        appendLine("  - File count: ${fileErrorInfos.size}")
        appendLine("  - Total errors: $totalErrors")
        appendLine("  - Total warnings: $totalWarnings")
        appendLine("  - Total weak warnings: $totalWeakWarnings")
        appendLine()

        fileErrorInfos.forEach { fileInfo ->
          appendLine("File: ${fileInfo.relativePath}")
          appendLine("   ${fileInfo.summary}")

          // Show error details (limited to avoid overly long output)
          val allIssues = fileInfo.errors + fileInfo.warnings + fileInfo.weakWarnings
          allIssues.take(5).forEach { error ->
            val severityIcon =
              when (error.severity) {
                io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity.ERROR -> "[ERROR]"
                io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity.WARNING -> "[WARN]"
                io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity.WEAK_WARNING -> "[INFO]"
                else -> "[NOTE]"
              }
            appendLine("   $severityIcon line ${error.line}: ${error.message}")
          }

          if (allIssues.size > 5) {
            appendLine("   ... ${allIssues.size - 5} more issues")
          }
          appendLine()
        }
      }
    }

    resultArea.text = result
    resultArea.caretPosition = 0
  }

  private fun displayCleanResults(cleanResult: io.github.truenine.composeserver.ide.ideamcp.services.CleanResult, virtualFile: VirtualFile) {
    val result = buildString {
      appendLine("=== Code clean-up result ===")
      appendLine("Processed path: ${virtualFile.path}")
      appendLine("Completed at: ${java.time.LocalDateTime.now()}")
      appendLine()

      appendLine("Statistics:")
      appendLine("  - Processed files: ${cleanResult.processedFiles}")
      appendLine("  - Modified files: ${cleanResult.modifiedFiles}")
      appendLine("  - Execution time: ${cleanResult.executionTime} ms")
      appendLine()

      if (cleanResult.operations.isNotEmpty()) {
        appendLine("Operations performed:")
        cleanResult.operations.forEach { operation -> appendLine("  - ${operation.description}: ${operation.filesAffected} files") }
        appendLine()
      }

      if (cleanResult.errors.isNotEmpty()) {
        appendLine("Errors:")
        cleanResult.errors.take(10).forEach { error -> appendLine("  - $error") }
        if (cleanResult.errors.size > 10) {
          appendLine("  ... ${cleanResult.errors.size - 10} more errors")
        }
        appendLine()
      }

      appendLine("Summary:")
      appendLine(cleanResult.summary)
    }

    resultArea.text = result
    resultArea.caretPosition = 0
  }

  private fun setButtonsEnabled(enabled: Boolean) {
    browseButton.isEnabled = enabled
    viewErrorsButton.isEnabled = enabled
    cleanCodeButton.isEnabled = enabled
  }

  private fun showError(message: String) {
    resultArea.text = "[ERROR] $message"
    JOptionPane.showMessageDialog(this, message, "Operation failed", JOptionPane.ERROR_MESSAGE)
    Logger.warn("FileOperationPanel error: $message")
  }

  override fun dispose() {
    // Cancel all coroutines
    scope.cancel()
    Logger.debug("FileOperationPanel disposed", "FileOperationPanel")
  }
}
