package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import javax.swing.*
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableCellRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/** MCP 调试面板 UI 组件 显示日志信息，支持过滤、搜索和清空功能 */
class McpDebugPanel(private val project: Project) : SimpleToolWindowPanel(true, true) {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private val logTableModel = LogTableModel()
  private val logTable = JBTable(logTableModel)
  private val levelFilter = JComboBox<LogLevel?>()
  private val searchField = JTextField()
  private val clearButton = JButton("清空日志")
  private val exportButton = JButton("导出日志")

  private var currentLogs = emptyList<LogEntry>()
  private var filteredLogs = emptyList<LogEntry>()

  init {
    setupUI()
    setupEventHandlers()
    observeLogs()
  }

  private fun setupUI() {
    // 设置表格
    setupTable()

    // 创建工具栏
    val toolbar = createToolbar()

    // 布局
    setLayout(BorderLayout())
    add(toolbar, BorderLayout.NORTH)
    add(JBScrollPane(logTable), BorderLayout.CENTER)
  }

  private fun setupTable() {
    logTable.apply {
      // 设置列宽
      columnModel.getColumn(0).preferredWidth = 80 // 时间
      columnModel.getColumn(1).preferredWidth = 60 // 级别
      columnModel.getColumn(2).preferredWidth = 80 // 来源
      columnModel.getColumn(3).preferredWidth = 400 // 消息

      // 设置行高
      rowHeight = 24

      // 设置单元格渲染器
      setDefaultRenderer(Any::class.java, LogCellRenderer())

      // 允许选择多行
      selectionModel.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION

      // 自动调整列宽
      autoResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN
    }
  }

  private fun createToolbar(): JPanel {
    val toolbar = JPanel()
    toolbar.layout = BoxLayout(toolbar, BoxLayout.X_AXIS)
    toolbar.border = JBUI.Borders.empty(5)

    // 级别过滤器
    levelFilter.apply {
      addItem(null) // 全部
      LogLevel.values().forEach { addItem(it) }
      renderer =
        object : DefaultListCellRenderer() {
          override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
            val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            text = (value as? LogLevel)?.displayName ?: "全部级别"
            return component
          }
        }
    }

    // 搜索框
    searchField.apply {
      preferredSize = Dimension(200, 28)
      toolTipText = "搜索日志内容"
    }

    // 按钮
    clearButton.apply { toolTipText = "清空所有日志" }

    exportButton.apply { toolTipText = "导出日志到文件" }

    toolbar.apply {
      add(JLabel("级别: "))
      add(levelFilter)
      add(Box.createHorizontalStrut(10))
      add(JLabel("搜索: "))
      add(searchField)
      add(Box.createHorizontalStrut(10))
      add(clearButton)
      add(Box.createHorizontalStrut(5))
      add(exportButton)
      add(Box.createHorizontalGlue())
    }

    return toolbar
  }

  private fun setupEventHandlers() {
    // 级别过滤器变化
    levelFilter.addActionListener { applyFilters() }

    // 搜索框变化
    searchField.document.addDocumentListener(
      object : javax.swing.event.DocumentListener {
        override fun insertUpdate(e: javax.swing.event.DocumentEvent?) = applyFilters()

        override fun removeUpdate(e: javax.swing.event.DocumentEvent?) = applyFilters()

        override fun changedUpdate(e: javax.swing.event.DocumentEvent?) = applyFilters()
      }
    )

    // 清空按钮
    clearButton.addActionListener { McpLogManager.clearLogs() }

    // 导出按钮
    exportButton.addActionListener { exportLogs() }
  }

  private fun observeLogs() {
    McpLogManager.logs
      .onEach { logs ->
        ApplicationManager.getApplication().invokeLater {
          currentLogs = logs
          applyFilters()
        }
      }
      .launchIn(scope)
  }

  private fun applyFilters() {
    val selectedLevel = levelFilter.selectedItem as? LogLevel
    val searchText = searchField.text.trim()

    filteredLogs =
      currentLogs.filter { log ->
        // 级别过滤
        val levelMatch = selectedLevel == null || log.level == selectedLevel

        // 搜索过滤
        val searchMatch = searchText.isEmpty() || log.message.contains(searchText, ignoreCase = true) || log.source.contains(searchText, ignoreCase = true)

        levelMatch && searchMatch
      }

    logTableModel.updateLogs(filteredLogs)

    // 自动滚动到最新条目
    if (filteredLogs.isNotEmpty()) {
      SwingUtilities.invokeLater {
        val lastRow = logTable.rowCount - 1
        if (lastRow >= 0) {
          logTable.scrollRectToVisible(logTable.getCellRect(lastRow, 0, true))
        }
      }
    }
  }

  private fun exportLogs() {
    val fileChooser = JFileChooser()
    fileChooser.selectedFile = java.io.File("mcp_logs.txt")

    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        val file = fileChooser.selectedFile
        val content = filteredLogs.joinToString("\n") { log -> "[${log.formattedTime}] [${log.level.displayName}] [${log.source}] ${log.message}" }
        file.writeText(content)

        JOptionPane.showMessageDialog(this, "日志已导出到: ${file.absolutePath}", "导出成功", JOptionPane.INFORMATION_MESSAGE)
      } catch (e: Exception) {
        JOptionPane.showMessageDialog(this, "导出失败: ${e.message}", "错误", JOptionPane.ERROR_MESSAGE)
      }
    }
  }
}

/** 日志表格模型 */
private class LogTableModel : AbstractTableModel() {
  private var logs = emptyList<LogEntry>()

  private val columnNames = arrayOf("时间", "级别", "来源", "消息")

  fun updateLogs(newLogs: List<LogEntry>) {
    logs = newLogs
    fireTableDataChanged()
  }

  override fun getRowCount(): Int = logs.size

  override fun getColumnCount(): Int = columnNames.size

  override fun getColumnName(column: Int): String = columnNames[column]

  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
    val log = logs[rowIndex]
    return when (columnIndex) {
      0 -> log.formattedTime
      1 -> log.level
      2 -> log.source
      3 -> log.message
      else -> ""
    }
  }

  override fun getColumnClass(columnIndex: Int): Class<*> {
    return when (columnIndex) {
      1 -> LogLevel::class.java
      else -> String::class.java
    }
  }
}

/** 日志单元格渲染器 */
private class LogCellRenderer : DefaultTableCellRenderer() {

  override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
    val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)

    if (table != null && !isSelected) {
      val model = table.model as LogTableModel
      val logLevel = model.getValueAt(row, 1) as LogLevel

      // 根据日志级别设置颜色
      foreground = java.awt.Color.decode(logLevel.color)

      // 错误级别使用粗体
      if (logLevel == LogLevel.ERROR) {
        font = font.deriveFont(Font.BOLD)
      } else {
        font = font.deriveFont(Font.PLAIN)
      }
    }

    return component
  }
}
