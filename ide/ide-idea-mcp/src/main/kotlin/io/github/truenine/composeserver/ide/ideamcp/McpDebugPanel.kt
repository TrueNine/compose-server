package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.*

/** Simplified MCP debug panel. */
class McpDebugPanel(private val project: Project) : SimpleToolWindowPanel(true, true) {

  private val infoArea = JTextArea()

  init {
    setupUI()
  }

  private fun setupUI() {
    val panel = JPanel(BorderLayout())

    // Title
    val titleLabel = JLabel("MCP plugin debug information")
    titleLabel.font = titleLabel.font.deriveFont(Font.BOLD, 16f)

    // Information area
    infoArea.apply {
      isEditable = false
      text =
        """
        Logging is simplified to console-output mode.

        To view detailed logs:
        1. Open IDEA: Help -> Show Log in Files
        2. Or inspect the IDEA console output
        3. Log level can be configured in logback.xml

        Current configuration:
        - Plugin package: io.github.truenine.composeserver.ide.ideamcp
        - Log level: DEBUG
        - Output format: HH:mm:ss.SSS [thread] LEVEL logger - message
        """
          .trimIndent()
    }

    panel.add(titleLabel, BorderLayout.NORTH)
    panel.add(JBScrollPane(infoArea), BorderLayout.CENTER)

    setContent(panel)
  }
}
