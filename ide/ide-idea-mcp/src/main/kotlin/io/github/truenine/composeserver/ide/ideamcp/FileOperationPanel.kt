package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import io.github.truenine.composeserver.ide.ideamcp.services.CleanOptions
import io.github.truenine.composeserver.ide.ideamcp.services.CleanService
import io.github.truenine.composeserver.ide.ideamcp.services.ErrorService
import io.github.truenine.composeserver.ide.ideamcp.services.FileManager
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JSeparator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** 文件操作面板 提供文件选择和批量操作界面，集成错误查看和代码清理功能 */
class FileOperationPanel(private val project: Project) : SimpleToolWindowPanel(true, true), Disposable {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

  // 服务依赖
  private val errorService: ErrorService by lazy { project.service<ErrorService>() }
  private val cleanService: CleanService by lazy { project.service<CleanService>() }
  private val fileManager: FileManager by lazy { project.service<FileManager>() }

  // UI 组件
  private val pathField = JBTextField()
  private val browseButton = JButton("浏览")
  private val viewErrorsButton = JButton("查看错误")
  private val cleanCodeButton = JButton("清理代码")

  // 清理选项
  private val formatCodeCheckBox = JCheckBox("代码格式化", true)
  private val optimizeImportsCheckBox = JCheckBox("优化导入", true)
  private val runInspectionsCheckBox = JCheckBox("运行检查修复", true)
  private val rearrangeCodeCheckBox = JCheckBox("重新排列代码", false)

  // 结果显示
  private val resultArea = JBTextArea()

  init {
    setupUI()
    setupEventHandlers()
    McpLogManager.info("文件操作面板已初始化", LogSource.UI.displayName)
  }

  private fun setupUI() {
    layout = BorderLayout()

    // 创建主面板
    val mainPanel = JPanel(GridBagLayout())
    mainPanel.border = JBUI.Borders.empty(10)

    val gbc = GridBagConstraints()

    // 文件选择区域
    addFileSelectionSection(mainPanel, gbc)

    // 分隔线
    gbc.gridy++
    gbc.gridx = 0
    gbc.gridwidth = 3
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.insets = Insets(10, 0, 10, 0)
    mainPanel.add(JSeparator(), gbc)

    // 操作选项区域
    addOperationOptionsSection(mainPanel, gbc)

    // 分隔线
    gbc.gridy++
    gbc.gridx = 0
    gbc.gridwidth = 3
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.insets = Insets(10, 0, 10, 0)
    mainPanel.add(JSeparator(), gbc)

    // 操作按钮区域
    addActionButtonsSection(mainPanel, gbc)

    // 结果显示区域
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
    parent.add(JLabel("选择路径:"), gbc)

    gbc.gridx = 1
    gbc.gridwidth = 1
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.weightx = 1.0
    gbc.insets = Insets(5, 0, 5, 10)
    pathField.apply {
      preferredSize = Dimension(0, 28)
      toolTipText = "输入文件或文件夹路径，或点击浏览按钮选择"
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
      toolTipText = "浏览选择文件或文件夹"
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
    parent.add(JLabel("代码清理选项:"), gbc)

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
      toolTipText = "扫描并显示选定路径下的所有错误和警告"
    }

    cleanCodeButton.apply {
      preferredSize = Dimension(120, 32)
      toolTipText = "对选定路径下的代码文件执行清理操作"
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
    parent.add(JLabel("操作结果:"), gbc)

    resultArea.apply {
      isEditable = false
      lineWrap = true
      wrapStyleWord = true
      rows = 15
      text = "请选择文件或文件夹，然后执行相应操作..."
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
    // 浏览按钮点击事件
    browseButton.addActionListener { browseForPath() }

    // 查看错误按钮点击事件
    viewErrorsButton.addActionListener { viewErrors() }

    // 清理代码按钮点击事件
    cleanCodeButton.addActionListener { cleanCode() }
  }

  private fun browseForPath() {
    val descriptor = FileChooserDescriptor(true, true, false, false, false, false)
    descriptor.title = "选择文件或文件夹"
    descriptor.description = "选择要操作的文件或文件夹"

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
      McpLogManager.info("选择了路径: ${selectedFiles[0].path}", LogSource.UI.displayName)
    }
  }

  private fun viewErrors() {
    val path = pathField.text.trim()
    if (path.isEmpty()) {
      showError("请先选择文件或文件夹路径")
      return
    }

    val virtualFile = fileManager.resolvePathToVirtualFile(project, path)
    if (virtualFile == null) {
      showError("路径不存在或无法访问: $path")
      return
    }

    // 禁用按钮并显示进度
    setButtonsEnabled(false)
    resultArea.text = "正在扫描错误信息，请稍候..."

    scope.launch {
      try {
        val fileErrorInfos = withContext(Dispatchers.IO) { errorService.collectErrors(project, virtualFile) }

        ApplicationManager.getApplication().invokeLater {
          displayErrorResults(fileErrorInfos, virtualFile)
          setButtonsEnabled(true)
        }
      } catch (e: Exception) {
        ApplicationManager.getApplication().invokeLater {
          showError("扫描错误失败: ${e.message}")
          setButtonsEnabled(true)
        }
        McpLogManager.error("错误扫描失败", LogSource.UI.displayName, e)
      }
    }
  }

  private fun cleanCode() {
    val path = pathField.text.trim()
    if (path.isEmpty()) {
      showError("请先选择文件或文件夹路径")
      return
    }

    val virtualFile = fileManager.resolvePathToVirtualFile(project, path)
    if (virtualFile == null) {
      showError("路径不存在或无法访问: $path")
      return
    }

    val options =
      CleanOptions(
        formatCode = formatCodeCheckBox.isSelected,
        optimizeImports = optimizeImportsCheckBox.isSelected,
        runInspections = runInspectionsCheckBox.isSelected,
        rearrangeCode = rearrangeCodeCheckBox.isSelected,
      )

    // 禁用按钮并显示进度
    setButtonsEnabled(false)
    resultArea.text = "正在执行代码清理操作，请稍候..."

    scope.launch {
      try {
        val cleanResult = cleanService.cleanCode(project, virtualFile, options)

        ApplicationManager.getApplication().invokeLater {
          displayCleanResults(cleanResult, virtualFile)
          setButtonsEnabled(true)
        }
      } catch (e: Exception) {
        ApplicationManager.getApplication().invokeLater {
          showError("代码清理失败: ${e.message}")
          setButtonsEnabled(true)
        }
        McpLogManager.error("代码清理失败", LogSource.UI.displayName, e)
      }
    }
  }

  private fun displayErrorResults(fileErrorInfos: List<io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo>, virtualFile: VirtualFile) {
    val result = buildString {
      appendLine("=== 错误扫描结果 ===")
      appendLine("扫描路径: ${virtualFile.path}")
      appendLine("扫描时间: ${java.time.LocalDateTime.now()}")
      appendLine()

      if (fileErrorInfos.isEmpty()) {
        appendLine("✅ 未发现任何错误或警告")
      } else {
        val totalErrors = fileErrorInfos.sumOf { it.errors.size }
        val totalWarnings = fileErrorInfos.sumOf { it.warnings.size }
        val totalWeakWarnings = fileErrorInfos.sumOf { it.weakWarnings.size }

        appendLine("📊 统计信息:")
        appendLine("  - 文件数量: ${fileErrorInfos.size}")
        appendLine("  - 错误总数: $totalErrors")
        appendLine("  - 警告总数: $totalWarnings")
        appendLine("  - 弱警告总数: $totalWeakWarnings")
        appendLine()

        fileErrorInfos.forEach { fileInfo ->
          appendLine("📁 ${fileInfo.relativePath}")
          appendLine("   ${fileInfo.summary}")

          // 显示错误详情（限制数量避免过长）
          val allIssues = fileInfo.errors + fileInfo.warnings + fileInfo.weakWarnings
          allIssues.take(5).forEach { error ->
            val severityIcon =
              when (error.severity) {
                io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity.ERROR -> "❌"
                io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity.WARNING -> "⚠️"
                io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity.WEAK_WARNING -> "💡"
                else -> "ℹ️"
              }
            appendLine("   $severityIcon 第${error.line}行: ${error.message}")
          }

          if (allIssues.size > 5) {
            appendLine("   ... 还有 ${allIssues.size - 5} 个问题")
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
      appendLine("=== 代码清理结果 ===")
      appendLine("处理路径: ${virtualFile.path}")
      appendLine("完成时间: ${java.time.LocalDateTime.now()}")
      appendLine()

      appendLine("📊 统计信息:")
      appendLine("  - 处理文件: ${cleanResult.processedFiles} 个")
      appendLine("  - 修改文件: ${cleanResult.modifiedFiles} 个")
      appendLine("  - 执行时间: ${cleanResult.executionTime} ms")
      appendLine()

      if (cleanResult.operations.isNotEmpty()) {
        appendLine("🔧 执行的操作:")
        cleanResult.operations.forEach { operation -> appendLine("  - ${operation.description}: ${operation.filesAffected} 个文件") }
        appendLine()
      }

      if (cleanResult.errors.isNotEmpty()) {
        appendLine("❌ 错误信息:")
        cleanResult.errors.take(10).forEach { error -> appendLine("  - $error") }
        if (cleanResult.errors.size > 10) {
          appendLine("  ... 还有 ${cleanResult.errors.size - 10} 个错误")
        }
        appendLine()
      }

      appendLine("📝 操作摘要:")
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
    resultArea.text = "❌ 错误: $message"
    JOptionPane.showMessageDialog(this, message, "操作失败", JOptionPane.ERROR_MESSAGE)
    McpLogManager.warn("文件操作面板错误: $message", LogSource.UI.displayName)
  }

  override fun dispose() {
    // 取消所有协程
    scope.cancel()
    McpLogManager.debug("FileOperationPanel disposed", "FileOperationPanel")
  }
}
