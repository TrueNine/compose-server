package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import io.github.truenine.composeserver.ide.ideamcp.tools.ErrorInfo
import io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity
import io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.AbstractTableModel

/**
 * 错误报告显示对话框
 * 以表格形式显示收集到的错误信息
 */
class ErrorReportDialog(
  project: Project,
  private val errorReport: List<FileErrorInfo>,
  private val fileName: String
) : DialogWrapper(project) {

  init {
    title = "错误报告 - $fileName"
    init()
  }

  override fun createCenterPanel(): JComponent {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
    
    // 统计信息
    val totalErrors = errorReport.sumOf { it.errors.size }
    val totalWarnings = errorReport.sumOf { it.warnings.size }
    val totalWeakWarnings = errorReport.sumOf { it.weakWarnings.size }
    
    val summaryLabel = JLabel(buildString {
      append("发现问题: ")
      if (totalErrors > 0) append("${totalErrors}个错误 ")
      if (totalWarnings > 0) append("${totalWarnings}个警告 ")
      if (totalWeakWarnings > 0) append("${totalWeakWarnings}个弱警告")
    })
    panel.add(summaryLabel)
    panel.add(Box.createVerticalStrut(10))
    
    // 创建错误表格
    val errorTableModel = ErrorTableModel(errorReport)
    val errorTable = JBTable(errorTableModel)
    
    // 设置列宽
    errorTable.columnModel.getColumn(0).preferredWidth = 200 // 文件
    errorTable.columnModel.getColumn(1).preferredWidth = 60  // 行号
    errorTable.columnModel.getColumn(2).preferredWidth = 80  // 严重程度
    errorTable.columnModel.getColumn(3).preferredWidth = 400 // 消息
    
    val scrollPane = JBScrollPane(errorTable)
    scrollPane.preferredSize = Dimension(800, 400)
    panel.add(scrollPane)
    
    return panel
  }

  override fun createActions(): Array<Action> {
    return arrayOf(okAction)
  }
}

/**
 * 错误表格模型
 */
private class ErrorTableModel(private val errorReport: List<FileErrorInfo>) : AbstractTableModel() {
  
  private val columnNames = arrayOf("文件", "行号", "严重程度", "消息")
  private val allErrors: List<ErrorRowData>
  
  init {
    allErrors = buildList {
      errorReport.forEach { fileInfo ->
        // 添加错误
        fileInfo.errors.forEach { error ->
          add(ErrorRowData(fileInfo.relativePath, error))
        }
        // 添加警告
        fileInfo.warnings.forEach { warning ->
          add(ErrorRowData(fileInfo.relativePath, warning))
        }
        // 添加弱警告
        fileInfo.weakWarnings.forEach { weakWarning ->
          add(ErrorRowData(fileInfo.relativePath, weakWarning))
        }
      }
    }.sortedWith(compareBy<ErrorRowData> { it.error.severity.ordinal }.thenBy { it.filePath }.thenBy { it.error.line })
  }
  
  override fun getRowCount(): Int = allErrors.size
  
  override fun getColumnCount(): Int = columnNames.size
  
  override fun getColumnName(column: Int): String = columnNames[column]
  
  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
    val errorData = allErrors[rowIndex]
    return when (columnIndex) {
      0 -> errorData.filePath
      1 -> errorData.error.line
      2 -> getSeverityDisplayName(errorData.error.severity)
      3 -> errorData.error.message
      else -> ""
    }
  }
  
  private fun getSeverityDisplayName(severity: ErrorSeverity): String {
    return when (severity) {
      ErrorSeverity.ERROR -> "错误"
      ErrorSeverity.WARNING -> "警告"
      ErrorSeverity.WEAK_WARNING -> "弱警告"
      ErrorSeverity.INFO -> "信息"
    }
  }
}

/**
 * 错误行数据
 */
private data class ErrorRowData(
  val filePath: String,
  val error: ErrorInfo
)
