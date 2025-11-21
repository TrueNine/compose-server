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

/**
 * Code clean-up options dialog.
 *
 * Lets the user choose which clean-up operations to perform.
 */
class CleanOptionsDialog(project: Project) : DialogWrapper(project) {

  private val formatCodeCheckBox = JCheckBox("Format code", true)
  private val optimizeImportsCheckBox = JCheckBox("Optimize imports", true)
  private val runInspectionsCheckBox = JCheckBox("Run code inspections and apply fixes", true)
  private val rearrangeCodeCheckBox = JCheckBox("Rearrange code", false)

  init {
    title = "Code clean-up options"
    init()
  }

  override fun createCenterPanel(): JComponent {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

    panel.add(JLabel("Select clean-up operations to perform:"))
    panel.add(Box.createVerticalStrut(10))

    panel.add(formatCodeCheckBox)
    panel.add(optimizeImportsCheckBox)
    panel.add(runInspectionsCheckBox)
    panel.add(rearrangeCodeCheckBox)

    panel.add(Box.createVerticalStrut(10))
    panel.add(JLabel("<html><i>Note: At least one option must be selected</i></html>"))

    return panel
  }

  override fun doValidate(): ValidationInfo? {
    if (!formatCodeCheckBox.isSelected && !optimizeImportsCheckBox.isSelected && !runInspectionsCheckBox.isSelected && !rearrangeCodeCheckBox.isSelected) {
      return ValidationInfo("At least one clean-up option must be selected", formatCodeCheckBox)
    }
    return null
  }

  /** Get clean-up options selected by the user. */
  fun getCleanOptions(): CleanOptions {
    return CleanOptions(
      formatCode = formatCodeCheckBox.isSelected,
      optimizeImports = optimizeImportsCheckBox.isSelected,
      runInspections = runInspectionsCheckBox.isSelected,
      rearrangeCode = rearrangeCodeCheckBox.isSelected,
    )
  }
}
