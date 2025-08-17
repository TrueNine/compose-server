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
 * LibCodeService 测试面板
 *
 * 提供图形界面来测试 LibCodeService 的各种功能，包括：
 * - 源码提取测试
 * - 反编译功能测试
 * - 错误处理测试
 * - 性能测试
 */
class LibCodeTestPanel(private val project: Project) : SimpleToolWindowPanel(true, true) {

  // UI 组件
  private val classNameField = JBTextField(30)
  private val memberNameField = JBTextField(30)
  private val testButton = JButton("测试 LibCodeService")
  private val clearButton = JButton("清空结果")
  private val resultArea = JTextArea()
  private val enableLoggingCheckBox = JCheckBox("启用详细日志", true)
  private val statusLabel = JBLabel("就绪")

  // 协程作用域
  private val coroutineScope = CoroutineScope(Dispatchers.Main)

  init {
    setupUI()
    setupEventHandlers()
    initializeDefaultValues()
  }

  /** 设置UI界面 */
  private fun setupUI() {
    val mainPanel = JPanel(BorderLayout())

    // 创建输入面板
    val inputPanel = createInputPanel()
    mainPanel.add(inputPanel, BorderLayout.NORTH)

    // 创建结果面板
    val resultPanel = createResultPanel()
    mainPanel.add(resultPanel, BorderLayout.CENTER)

    // 创建状态面板
    val statusPanel = createStatusPanel()
    mainPanel.add(statusPanel, BorderLayout.SOUTH)

    setContent(mainPanel)
  }

  /** 创建输入参数面板 */
  private fun createInputPanel(): JPanel {
    val panel = JPanel(GridBagLayout())
    panel.border = TitledBorder("测试参数")

    val gbc = GridBagConstraints()
    gbc.insets = Insets(5, 5, 5, 5)
    gbc.anchor = GridBagConstraints.WEST

    // 完全限定类名
    gbc.gridx = 0
    gbc.gridy = 0
    gbc.fill = GridBagConstraints.NONE
    gbc.weightx = 0.0
    panel.add(JLabel("完全限定类名:"), gbc)
    gbc.gridx = 1
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.weightx = 1.0
    panel.add(classNameField, gbc)

    // 成员名（可选）
    gbc.gridx = 0
    gbc.gridy = 1
    gbc.fill = GridBagConstraints.NONE
    gbc.weightx = 0.0
    panel.add(JLabel("成员名（可选）:"), gbc)
    gbc.gridx = 1
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.weightx = 1.0
    panel.add(memberNameField, gbc)

    // 选项
    gbc.gridx = 0
    gbc.gridy = 2
    gbc.gridwidth = 2
    gbc.fill = GridBagConstraints.NONE
    panel.add(enableLoggingCheckBox, gbc)

    // 按钮面板
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

  /** 创建结果显示面板 */
  private fun createResultPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = TitledBorder("测试结果")

    resultArea.apply {
      isEditable = false
      font = Font(Font.MONOSPACED, Font.PLAIN, 12)
      text = "点击 '测试 LibCodeService' 按钮开始测试..."
    }

    val scrollPane = JBScrollPane(resultArea)
    panel.add(scrollPane, BorderLayout.CENTER)

    return panel
  }

  /** 创建状态面板 */
  private fun createStatusPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    statusLabel.font = statusLabel.font.deriveFont(Font.ITALIC)
    panel.add(statusLabel, BorderLayout.WEST)
    return panel
  }

  /** 设置事件处理器 */
  private fun setupEventHandlers() {
    testButton.addActionListener { performLibCodeTest() }

    clearButton.addActionListener { clearResults() }
  }

  /** 初始化默认值 */
  private fun initializeDefaultValues() {
    classNameField.text = "java.util.ArrayList"
    memberNameField.text = ""
  }

  /** 执行 LibCodeService 测试 */
  private fun performLibCodeTest() {
    val className = classNameField.text.trim()
    val memberName = memberNameField.text.trim().takeIf { it.isNotEmpty() }

    // 参数验证
    if (className.isEmpty()) {
      updateResult("错误: 类名不能为空")
      return
    }

    // 禁用按钮，显示进度
    testButton.isEnabled = false
    updateStatus("正在测试...")
    updateResult("开始测试 LibCodeService...\n")

    coroutineScope.launch {
      try {
        val startTime = System.currentTimeMillis()

        // 记录测试开始
        if (enableLoggingCheckBox.isSelected) {
          Logger.info("开始 LibCodeService 测试 - 类: $className", "LibCodeTestPanel")
        }

        // 获取服务实例
        val libCodeService = project.service<LibCodeService>()

        // 执行测试
        val result = withContext(Dispatchers.IO) { libCodeService.getLibraryCode(project, className, memberName) }

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // 显示结果
        withContext(Dispatchers.Main) {
          displayTestResult(result, duration, className, memberName)
          updateStatus("测试完成")
        }

        if (enableLoggingCheckBox.isSelected) {
          Logger.info("LibCodeService 测试完成 - 耗时: ${duration}ms", "LibCodeTestPanel")
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          displayError(e)
          updateStatus("测试失败")
        }

        if (enableLoggingCheckBox.isSelected) {
          Logger.error("LibCodeService 测试失败", "LibCodeTestPanel", e)
        }
      } finally {
        withContext(Dispatchers.Main) { testButton.isEnabled = true }
      }
    }
  }

  /** 显示测试结果 */
  private fun displayTestResult(result: LibCodeResult, duration: Long, className: String, memberName: String?) {
    val resultText = buildString {
      appendLine("=== LibCodeService 测试结果 ===")
      appendLine("测试时间: ${java.time.LocalDateTime.now()}")
      appendLine("执行耗时: ${duration}ms")
      appendLine()

      appendLine("输入参数:")
      appendLine("  类名: $className")
      appendLine("  成员名: ${memberName ?: "无"}")
      appendLine()

      appendLine("结果信息:")
      appendLine("  语言: ${result.language}")
      appendLine("  是否反编译: ${if (result.isDecompiled) "是" else "否"}")
      appendLine("  源码类型: ${result.metadata.sourceType}")
      appendLine("  库名: ${result.metadata.libraryName}")
      appendLine("  版本: ${result.metadata.version ?: "未知"}")
      appendLine()

      appendLine("源码内容:")
      appendLine("${"=".repeat(50)}")
      appendLine(result.sourceCode)
      appendLine("${"=".repeat(50)}")
      appendLine()
    }

    updateResult(resultText)
  }

  /** 显示错误信息 */
  private fun displayError(error: Throwable) {
    val errorText = buildString {
      appendLine("=== 测试错误 ===")
      appendLine("错误类型: ${error.javaClass.simpleName}")
      appendLine("错误消息: ${error.message}")
      appendLine()
      appendLine("堆栈跟踪:")
      error.stackTrace.take(10).forEach { element -> appendLine("  at $element") }
      appendLine()
    }

    updateResult(errorText)
  }

  /** 更新结果显示 */
  private fun updateResult(text: String) {
    SwingUtilities.invokeLater {
      resultArea.text = text
      resultArea.caretPosition = 0
    }
  }

  /** 更新状态显示 */
  private fun updateStatus(status: String) {
    SwingUtilities.invokeLater { statusLabel.text = status }
  }

  /** 清空结果 */
  private fun clearResults() {
    resultArea.text = "结果已清空，点击 '测试 LibCodeService' 按钮开始新的测试..."
    updateStatus("就绪")
  }
}
