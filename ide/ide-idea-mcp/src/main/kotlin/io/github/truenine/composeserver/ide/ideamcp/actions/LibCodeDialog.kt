package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.Action
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

/**
 * Dialog for displaying library code.
 *
 * Shows third-party library source code or decompiled code.
 */
class LibCodeDialog(private val project: Project, private val result: LibCodeResult, private val className: String) : DialogWrapper(project) {

  private var editor: EditorEx? = null

  init {
    title = "Library code - $className"
    init()
  }

  override fun createCenterPanel(): JComponent {
    val panel = JPanel(BorderLayout())

    // Create information panel
    val infoPanel = createInfoPanel()
    panel.add(infoPanel, BorderLayout.NORTH)

    // Create code editor
    val codePanel = createCodePanel()
    panel.add(codePanel, BorderLayout.CENTER)

    panel.preferredSize = Dimension(900, 600)
    return panel
  }

  /** Create information panel. */
  private fun createInfoPanel(): JComponent {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
    panel.border = BorderFactory.createTitledBorder("Library Information")

    // Library name and version
    val libraryInfo = buildString {
      append("Library: ${result.metadata.libraryName}")
      result.metadata.version?.let { append(" (Version: $it)") }
    }
    panel.add(JLabel(libraryInfo))

    // Source type
    val sourceTypeInfo =
      when (result.metadata.sourceType) {
        SourceType.SOURCE_JAR -> "Source: Source JAR"
        SourceType.DECOMPILED -> "Source: Decompiled code"
        SourceType.NOT_FOUND -> "Source: Not found"
      }
    panel.add(JLabel(sourceTypeInfo))

    // Language information
    panel.add(JLabel("Language: ${result.language}"))

    // Documentation information
    result.metadata.documentation?.let { doc -> panel.add(JLabel("Documentation: $doc")) }

    if (result.isDecompiled) {
      val warningLabel = JLabel("[WARNING] This is decompiled code and may differ from the original source")
      warningLabel.foreground = JBColor.ORANGE
      panel.add(warningLabel)
    }

    return panel
  }

  /** Create code panel. */
  private fun createCodePanel(): JComponent {
    val panel = JPanel(BorderLayout())
    panel.border = BorderFactory.createTitledBorder("Source Code")

    if (result.sourceCode.isBlank()) {
      val noCodeLabel = JLabel("No source code available", SwingConstants.CENTER)
      noCodeLabel.foreground = JBColor.GRAY
      panel.add(noCodeLabel, BorderLayout.CENTER)
      return panel
    }

    // Create read-only editor
    val document = EditorFactory.getInstance().createDocument(result.sourceCode)
    val fileType =
      FileTypeManager.getInstance()
        .getFileTypeByExtension(
          when (result.language.lowercase()) {
            "kotlin" -> "kt"
            "java" -> "java"
            "scala" -> "scala"
            "groovy" -> "groovy"
            else -> "txt"
          }
        )

    editor = EditorFactory.getInstance().createEditor(document, project, fileType, true) as EditorEx
    editor?.let { ed ->
      ed.settings.isLineNumbersShown = true
      ed.settings.isLineMarkerAreaShown = true
      ed.settings.isFoldingOutlineShown = true
      ed.settings.isRightMarginShown = false

      val scrollPane = JBScrollPane(ed.component)
      panel.add(scrollPane, BorderLayout.CENTER)
    }

    return panel
  }

  override fun createActions(): Array<Action> {
    return arrayOf(okAction)
  }

  override fun dispose() {
    editor?.let { EditorFactory.getInstance().releaseEditor(it) }
    super.dispose()
  }
}
