package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import io.github.truenine.composeserver.ide.ideamcp.tools.*
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.AbstractTableModel

/**
 * Error report dialog.
 *
 * Displays collected issues in a table view.
 */
class ErrorReportDialog(project: Project, private val errorReport: List<FileErrorInfo>, private val fileName: String) : DialogWrapper(project) {

  init {
    title = "Error report - $fileName"
    init()
  }

  override fun createCenterPanel(): JComponent {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

    // Summary statistics
    val totalErrors = errorReport.sumOf { it.errors.size }
    val totalWarnings = errorReport.sumOf { it.warnings.size }
    val totalWeakWarnings = errorReport.sumOf { it.weakWarnings.size }

    val summaryLabel =
      JLabel(
        buildString {
          append("Problems found: ")
          if (totalErrors > 0) append("$totalErrors errors ")
          if (totalWarnings > 0) append("$totalWarnings warnings ")
          if (totalWeakWarnings > 0) append("$totalWeakWarnings weak warnings")
        }
      )
    panel.add(summaryLabel)
    panel.add(Box.createVerticalStrut(10))

    // Create error table
    val errorTableModel = ErrorTableModel(errorReport)
    val errorTable = JBTable(errorTableModel)

    // Set column widths
    errorTable.columnModel.getColumn(0).preferredWidth = 200 // File
    errorTable.columnModel.getColumn(1).preferredWidth = 60 // Line
    errorTable.columnModel.getColumn(2).preferredWidth = 80 // Severity
    errorTable.columnModel.getColumn(3).preferredWidth = 400 // Message

    val scrollPane = JBScrollPane(errorTable)
    scrollPane.preferredSize = Dimension(800, 400)
    panel.add(scrollPane)

    return panel
  }

  override fun createActions(): Array<Action> {
    return arrayOf(okAction)
  }
}

/** Table model for displaying errors and warnings. */
private class ErrorTableModel(private val errorReport: List<FileErrorInfo>) : AbstractTableModel() {

  private val columnNames = arrayOf("File", "Line", "Severity", "Message")
  private val allErrors: List<ErrorRowData>

  init {
    allErrors =
      buildList {
          errorReport.forEach { fileInfo ->
            // Add errors
            fileInfo.errors.forEach { error -> add(ErrorRowData(fileInfo.relativePath, error)) }
            // Add warnings
            fileInfo.warnings.forEach { warning -> add(ErrorRowData(fileInfo.relativePath, warning)) }
            // Add weak warnings
            fileInfo.weakWarnings.forEach { weakWarning -> add(ErrorRowData(fileInfo.relativePath, weakWarning)) }
          }
        }
        .sortedWith(compareBy<ErrorRowData> { it.error.severity.ordinal }.thenBy { it.filePath }.thenBy { it.error.line })
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
      ErrorSeverity.ERROR -> "Error"
      ErrorSeverity.WARNING -> "Warning"
      ErrorSeverity.WEAK_WARNING -> "Weak warning"
      ErrorSeverity.INFO -> "Info"
    }
  }
}

/** Error row data. */
private data class ErrorRowData(val filePath: String, val error: ErrorInfo)
