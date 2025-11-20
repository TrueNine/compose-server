package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea

/** MCP 调试面板 简化版本 */
class McpDebugPanel(private val project: Project) : SimpleToolWindowPanel(true, true) {

  private val infoArea = JTextArea()

  init {
    setupUI()
  }

  private fun setupUI() {
    val panel = JPanel(BorderLayout())

    // 标题
    val titleLabel = JLabel("MCP 插件调试信息")
    titleLabel.font = titleLabel.font.deriveFont(Font.BOLD, 16f)

    // 信息区域
    infoArea.apply {
      isEditable = false
      text =
        """
        日志系统已简化为控制台输出模式

        查看详细日志信息请：
        1. 打开 IDEA 的 Help -> Show Log in Files
        2. 或查看 IDEA 控制台输出
        3. 日志级别可在 logback.xml 中配置

        当前配置：
        - 插件包: io.github.truenine.composeserver.ide.ideamcp
        - 日志级别: DEBUG
        - 输出格式: HH:mm:ss.SSS [thread] LEVEL logger - message
        """
          .trimIndent()
    }

    panel.add(titleLabel, BorderLayout.NORTH)
    panel.add(JBScrollPane(infoArea), BorderLayout.CENTER)

    setContent(panel)
  }
}
