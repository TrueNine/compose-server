package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import io.github.truenine.composeserver.ide.ideamcp.services.CleanResult
import io.github.truenine.composeserver.ide.ideamcp.tools.CleanOperation
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.AbstractTableModel

/** Dialog that displays detailed results and statistics for code clean-up operations. */
class CleanResultDialog(project: Project, private val result: CleanResult, private val fileName: String) : DialogWrapper(project) {

  init {
    title = "Clean-up result - $fileName"
    init()
  }

  override fun createCenterPanel(): JComponent {
    val panel = JPanel(BorderLayout())

    // Create statistics panel
    val statsPanel = createStatsPanel()
    panel.add(statsPanel, BorderLayout.NORTH)

    // Create operation-details panel
    val detailsPanel = createDetailsPanel()
    panel.add(detailsPanel, BorderLayout.CENTER)

    // If there are errors, show the error panel
    if (result.errors.isNotEmpty()) {
      val errorPanel = createErrorPanel()
      panel.add(errorPanel, BorderLayout.SOUTH)
    }

    panel.preferredSize = Dimension(700, 500)
    return panel
  }

  /** Create statistics panel. */
  private fun createStatsPanel(): JComponent {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
    panel.border = BorderFactory.createTitledBorder("Clean-up statistics")

    // Basic statistics
    panel.add(JLabel("Processed files: ${result.processedFiles}"))
    panel.add(JLabel("Modified files: ${result.modifiedFiles}"))
    panel.add(JLabel("Execution time: ${result.executionTime} ms"))

    // Success rate
    val successRate =
      if (result.processedFiles > 0) {
        ((result.processedFiles - result.errors.size) * 100.0 / result.processedFiles).toInt()
      } else {
        100
      }
    panel.add(JLabel("Success rate: $successRate%"))

    return panel
  }

  /** Create operation-details panel. */
  private fun createDetailsPanel(): JComponent {
    val panel = JPanel(BorderLayout())
    panel.border = BorderFactory.createTitledBorder("Operation details")

    if (result.operations.isEmpty()) {
      val noOperationsLabel = JLabel("No operations were performed", SwingConstants.CENTER)
      noOperationsLabel.foreground = JBColor.GRAY
      panel.add(noOperationsLabel, BorderLayout.CENTER)
      return panel
    }

    // Create operation table
    val operationTableModel = OperationTableModel(result.operations)
    val operationTable = JBTable(operationTableModel)

    // Set column widths
    operationTable.columnModel.getColumn(0).preferredWidth = 150 // Operation type
    operationTable.columnModel.getColumn(1).preferredWidth = 300 // Description
    operationTable.columnModel.getColumn(2).preferredWidth = 100 // Files affected

    val scrollPane = JBScrollPane(operationTable)
    scrollPane.preferredSize = Dimension(550, 200)
    panel.add(scrollPane, BorderLayout.CENTER)

    return panel
  }

  /** Create error panel. */
  private fun createErrorPanel(): JComponent {
    val panel = JPanel(BorderLayout())
    panel.border = BorderFactory.createTitledBorder("Errors (${result.errors.size})")

    val errorListModel = DefaultListModel<String>()
    result.errors.forEach { error -> errorListModel.addElement(error) }

    val errorList = JList(errorListModel)
    errorList.selectionMode = ListSelectionModel.SINGLE_SELECTION

    val scrollPane = JBScrollPane(errorList)
    scrollPane.preferredSize = Dimension(650, 100)
    panel.add(scrollPane, BorderLayout.CENTER)

    return panel
  }

  override fun createActions(): Array<Action> {
    return arrayOf(okAction)
  }
}

/** Table model for displaying clean-up operations. */
private class OperationTableModel(private val operations: List<CleanOperation>) : AbstractTableModel() {

  private val columnNames = arrayOf("Operation type", "Description", "Files affected")

  override fun getRowCount(): Int = operations.size

  override fun getColumnCount(): Int = columnNames.size

  override fun getColumnName(column: Int): String = columnNames[column]

  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
    val operation = operations[rowIndex]
    return when (columnIndex) {
      0 -> getOperationTypeDisplayName(operation.type)
      1 -> operation.description
      2 -> operation.filesAffected
      else -> ""
    }
  }

  private fun getOperationTypeDisplayName(type: String): String {
    return when (type) {
      "FORMAT" -> "Format"
      "OPTIMIZE_IMPORTS" -> "Optimize imports"
      "RUN_INSPECTIONS" -> "Run inspections"
      "REARRANGE" -> "Rearrange code"
      else -> type
    }
  }
}
