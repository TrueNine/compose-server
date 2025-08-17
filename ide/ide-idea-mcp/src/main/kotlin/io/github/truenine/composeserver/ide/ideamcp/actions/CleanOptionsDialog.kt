package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import io.github.truenine.composeserver.ide.ideamcp.services.CleanOptions
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

/** 代码清理选项对话框 允许用户选择要执行的清理操作 */
class CleanOptionsDialog(project: Project) : DialogWrapper(project) {

  private val formatCodeCheckBox = JCheckBox("格式化代码", true)
  private val optimizeImportsCheckBox = JCheckBox("优化导入", true)
  private val runInspectionsCheckBox = JCheckBox("运行代码检查并修复", true)
  private val rearrangeCodeCheckBox = JCheckBox("重新排列代码", false)

  init {
    title = "代码清理选项"
    init()
  }

  override fun createCenterPanel(): JComponent {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

    panel.add(JLabel("选择要执行的清理操作:"))
    panel.add(Box.createVerticalStrut(10))

    panel.add(formatCodeCheckBox)
    panel.add(optimizeImportsCheckBox)
    panel.add(runInspectionsCheckBox)
    panel.add(rearrangeCodeCheckBox)

    panel.add(Box.createVerticalStrut(10))
    panel.add(JLabel("<html><i>注意: 至少需要选择一个选项</i></html>"))

    return panel
  }

  override fun doValidate(): ValidationInfo? {
    if (!formatCodeCheckBox.isSelected && !optimizeImportsCheckBox.isSelected && !runInspectionsCheckBox.isSelected && !rearrangeCodeCheckBox.isSelected) {
      return ValidationInfo("至少需要选择一个清理选项", formatCodeCheckBox)
    }
    return null
  }

  /** 获取用户选择的清理选项 */
  fun getCleanOptions(): CleanOptions {
    return CleanOptions(
      formatCode = formatCodeCheckBox.isSelected,
      optimizeImports = optimizeImportsCheckBox.isSelected,
      runInspections = runInspectionsCheckBox.isSelected,
      rearrangeCode = rearrangeCodeCheckBox.isSelected,
    )
  }
}
