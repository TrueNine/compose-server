package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import io.github.truenine.composeserver.ide.ideamcp.services.CleanResult
import io.github.truenine.composeserver.ide.ideamcp.tools.CleanOperation
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.AbstractTableModel

/**
 * 代码清理结果显示对话框
 * 显示清理操作的详细结果和统计信息
 */
class CleanResultDialog(
  project: Project,
  private val result: CleanResult,
  private val fileName: String
) : DialogWrapper(project) {

  init {
    title = "清理结果 - $fileName"
    init()
  }

  override fun createCenterPanel(): JComponent {
    val panel = JPanel(BorderLayout())
    
    // 创建统计信息面板
    val statsPanel = createStatsPanel()
    panel.add(statsPanel, BorderLayout.NORTH)
    
    // 创建操作详情面板
    val detailsPanel = createDetailsPanel()
    panel.add(detailsPanel, BorderLayout.CENTER)
    
    // 如果有错误，显示错误面板
    if (result.errors.isNotEmpty()) {
      val errorPanel = createErrorPanel()
      panel.add(errorPanel, BorderLayout.SOUTH)
    }
    
    panel.preferredSize = Dimension(700, 500)
    return panel
  }

  /**
   * 创建统计信息面板
   */
  private fun createStatsPanel(): JComponent {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
    panel.border = BorderFactory.createTitledBorder("清理统计")
    
    // 基本统计
    panel.add(JLabel("处理文件数: ${result.processedFiles}"))
    panel.add(JLabel("修改文件数: ${result.modifiedFiles}"))
    panel.add(JLabel("执行时间: ${result.executionTime} 毫秒"))
    
    // 成功率
    val successRate = if (result.processedFiles > 0) {
      ((result.processedFiles - result.errors.size) * 100.0 / result.processedFiles).toInt()
    } else {
      100
    }
    panel.add(JLabel("成功率: $successRate%"))
    
    return panel
  }

  /**
   * 创建操作详情面板
   */
  private fun createDetailsPanel(): JComponent {
    val panel = JPanel(BorderLayout())
    panel.border = BorderFactory.createTitledBorder("操作详情")
    
    if (result.operations.isEmpty()) {
      val noOperationsLabel = JLabel("未执行任何操作", SwingConstants.CENTER)
      noOperationsLabel.foreground = java.awt.Color.GRAY
      panel.add(noOperationsLabel, BorderLayout.CENTER)
      return panel
    }
    
    // 创建操作表格
    val operationTableModel = OperationTableModel(result.operations)
    val operationTable = JBTable(operationTableModel)
    
    // 设置列宽
    operationTable.columnModel.getColumn(0).preferredWidth = 150 // 操作类型
    operationTable.columnModel.getColumn(1).preferredWidth = 300 // 描述
    operationTable.columnModel.getColumn(2).preferredWidth = 100 // 影响文件数
    
    val scrollPane = JBScrollPane(operationTable)
    scrollPane.preferredSize = Dimension(550, 200)
    panel.add(scrollPane, BorderLayout.CENTER)
    
    return panel
  }

  /**
   * 创建错误面板
   */
  private fun createErrorPanel(): JComponent {
    val panel = JPanel(BorderLayout())
    panel.border = BorderFactory.createTitledBorder("错误信息 (${result.errors.size})")
    
    val errorListModel = DefaultListModel<String>()
    result.errors.forEach { error ->
      errorListModel.addElement(error)
    }
    
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

/**
 * 操作表格模型
 */
private class OperationTableModel(private val operations: List<CleanOperation>) : AbstractTableModel() {
  
  private val columnNames = arrayOf("操作类型", "描述", "影响文件数")
  
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
      "FORMAT" -> "格式化"
      "OPTIMIZE_IMPORTS" -> "导入优化"
      "RUN_INSPECTIONS" -> "检查修复"
      "REARRANGE" -> "代码重排"
      else -> type
    }
  }
}
