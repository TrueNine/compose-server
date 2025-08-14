package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

/**
 * 库代码显示对话框
 * 显示第三方库的源代码或反编译代码
 */
class LibCodeDialog(
  private val project: Project,
  private val result: LibCodeResult,
  private val className: String
) : DialogWrapper(project) {

  private var editor: EditorEx? = null

  init {
    title = "库代码 - $className"
    init()
  }

  override fun createCenterPanel(): JComponent {
    val panel = JPanel(BorderLayout())
    
    // 创建信息面板
    val infoPanel = createInfoPanel()
    panel.add(infoPanel, BorderLayout.NORTH)
    
    // 创建代码编辑器
    val codePanel = createCodePanel()
    panel.add(codePanel, BorderLayout.CENTER)
    
    panel.preferredSize = Dimension(900, 600)
    return panel
  }

  /**
   * 创建信息面板
   */
  private fun createInfoPanel(): JComponent {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
    panel.border = BorderFactory.createTitledBorder("库信息")
    
    // 库名称和版本
    val libraryInfo = buildString {
      append("库: ${result.metadata.libraryName}")
      result.metadata.version?.let { append(" (版本: $it)") }
    }
    panel.add(JLabel(libraryInfo))
    
    // 源码类型
    val sourceTypeInfo = when (result.metadata.sourceType) {
      SourceType.SOURCE_JAR -> "来源: Source JAR"
      SourceType.DECOMPILED -> "来源: 反编译代码"
      SourceType.NOT_FOUND -> "来源: 未找到"
    }
    panel.add(JLabel(sourceTypeInfo))
    
    // 语言信息
    panel.add(JLabel("语言: ${result.language}"))
    
    // 文档信息
    result.metadata.documentation?.let { doc ->
      panel.add(JLabel("文档: $doc"))
    }
    
    if (result.isDecompiled) {
      val warningLabel = JLabel("⚠️ 这是反编译的代码，可能与原始源码有差异")
      warningLabel.foreground = java.awt.Color.ORANGE
      panel.add(warningLabel)
    }
    
    return panel
  }

  /**
   * 创建代码面板
   */
  private fun createCodePanel(): JComponent {
    val panel = JPanel(BorderLayout())
    panel.border = BorderFactory.createTitledBorder("源代码")
    
    if (result.sourceCode.isBlank()) {
      val noCodeLabel = JLabel("无法获取源代码", SwingConstants.CENTER)
      noCodeLabel.foreground = java.awt.Color.GRAY
      panel.add(noCodeLabel, BorderLayout.CENTER)
      return panel
    }
    
    // 创建只读编辑器
    val document = EditorFactory.getInstance().createDocument(result.sourceCode)
    val fileType = FileTypeManager.getInstance().getFileTypeByExtension(
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
