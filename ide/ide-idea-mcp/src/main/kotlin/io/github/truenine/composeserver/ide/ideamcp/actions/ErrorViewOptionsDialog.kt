package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*

/**
 * Error view options dialog.
 *
 * Lets the user choose which types of issues to display.
 */
class ErrorViewOptionsDialog(project: Project) : DialogWrapper(project) {

  private val includeWarningsCheckBox = JCheckBox("Include warnings", true)
  private val includeWeakWarningsCheckBox = JCheckBox("Include weak warnings", true)

  init {
    title = "Error view options"
    init()
  }

  override fun createCenterPanel(): JComponent {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

    panel.add(JLabel("Select issue types to display:"))
    panel.add(Box.createVerticalStrut(10))

    panel.add(JCheckBox("Include errors", true).apply { isEnabled = false }) // Errors are always included
    panel.add(includeWarningsCheckBox)
    panel.add(includeWeakWarningsCheckBox)

    panel.add(Box.createVerticalStrut(10))
    panel.add(JLabel("<html><i>Note: Errors are always included</i></html>"))

    return panel
  }

  /** Get error-view options selected by the user. */
  fun getErrorViewOptions(): ErrorViewOptions {
    return ErrorViewOptions(includeWarnings = includeWarningsCheckBox.isSelected, includeWeakWarnings = includeWeakWarningsCheckBox.isSelected)
  }
}
