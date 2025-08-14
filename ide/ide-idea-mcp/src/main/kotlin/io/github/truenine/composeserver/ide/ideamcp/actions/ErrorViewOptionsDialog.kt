package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*

/**
 * 错误查看选项对话框
 * 允许用户选择要查看的错误类型
 */
class ErrorViewOptionsDialog(project: Project) : DialogWrapper(project) {
  
  private val includeWarningsCheckBox = JCheckBox("包含警告", true)
  private val includeWeakWarningsCheckBox = JCheckBox("包含弱警告", true)

  init {
    title = "错误查看选项"
    init()
  }

  override fun createCenterPanel(): JComponent {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
    
    panel.add(JLabel("选择要查看的问题类型:"))
    panel.add(Box.createVerticalStrut(10))
    
    panel.add(JCheckBox("包含错误", true).apply { isEnabled = false }) // 错误总是包含
    panel.add(includeWarningsCheckBox)
    panel.add(includeWeakWarningsCheckBox)
    
    panel.add(Box.createVerticalStrut(10))
    panel.add(JLabel("<html><i>注意: 错误信息总是会被包含</i></html>"))
    
    return panel
  }

  /**
   * 获取用户选择的错误查看选项
   */
  fun getErrorViewOptions(): ErrorViewOptions {
    return ErrorViewOptions(
      includeWarnings = includeWarningsCheckBox.isSelected,
      includeWeakWarnings = includeWeakWarningsCheckBox.isSelected
    )
  }
}
