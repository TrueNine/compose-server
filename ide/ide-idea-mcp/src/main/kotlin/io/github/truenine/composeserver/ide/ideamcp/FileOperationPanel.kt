package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import io.github.truenine.composeserver.ide.ideamcp.services.CleanOptions
import io.github.truenine.composeserver.ide.ideamcp.services.CleanService
import io.github.truenine.composeserver.ide.ideamcp.services.ErrorService
import io.github.truenine.composeserver.ide.ideamcp.services.FileManager
import io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import javax.swing.*
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableCellRenderer

/**
 * 文件操作面板
 * 提供文件选择和批量操作界面，集成错误查看和代码清理功能
 */
class FileOperationPanel(private val project: Project) : SimpleToolWindowPanel(true, true) {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  
  // 服务依赖
  private val errorService = project.getService(ErrorService::class.java)
  private val cleanService = project.getService(CleanService::class.java)
  private val fileManager = project.getService(FileManager::class.java)
  
  // UI 组件
  private val pathField = JBTextField()
  private val browseButton = JButton("浏览")
  private val viewErrorsButton = JButton("查看错误")
  private val cleanCodeButton = JButton("清理代码")
  private val refreshButton = JButton("刷新")
  
  // 错误显示表格
  private val errorTableModel = ErrorTableModel()
  private val errorTable = JBTable(errorTableModel)
  
  // 清理选项
  private val formatCodeCheckBox = JCheckBox("代码格式化", true)
  private val optimizeImportsCheckBox = JCheckBox("优化导入", true)
  private val runInspectionsCheckBox = JCheckBox("运行检查", true)
  
  // 状态显示
  private val statusLabel = JLabel("就绪")
  private val progressBar = JProgressBar()
  
  // 当前选中的文件
  private var currentFile: VirtualFile? = null
  private var currentErrors: List<FileErrorInfo> = emptyList()

  init {
    setupUI()
    setupEventHandlers()
    initializeDefaultPath()
    McpLogManager.info("文件操作面板已初始化", LogSource.UI.displayName)
  }

  private fun setupUI() {
    layout = BorderLayout()
    
    // 创建主面板
    val mainPanel = JPanel(BorderLayout())
    
    // 顶部：文件选择区域
    val topPanel = createFileSelectionPanel()
    mainPanel.add(topPanel, BorderLayout.NORTH)
    
    // 中间：错误显示表格
    val centerPanel = createErrorDisplayPanel()
    mainPanel.add(centerPanel, BorderLayout.CENTER)
    
    // 底部：操作选项和状态
    val bottomPanel = createOperationPanel()
    mainPanel.add(bottomPanel, BorderLayout.SOUTH)
    
    add(mainPanel, BorderLayout.CENTER)
  }

  private fun createFileSelectionPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = JBUI.Borders.empty(10, 10, 5, 10)
    
    // 路径输入区域
    val pathPanel = JPanel(BorderLayout())
    pathPanel.add(JLabel("文件/目录路径: "), BorderLayout.WEST)
    
    pathField.apply {
      preferredSize = Dimension(0, 28)
      toolTipText = "输入文件或目录路径，支持相对路径和绝对路径"
    }
    pathPanel.add(pathField, BorderLayout.CENTER)
    
    browseButton.apply {
      preferredSize = Dimension(80, 28)
      toolTipText = "浏览选择文件或目录"
    }
    pathPanel.add(browseButton, BorderLayout.EAST)
    
    // 操作按钮区域
    val buttonPanel = JPanel()
    buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.X_AXIS)
    
    viewErrorsButton.apply {
      preferredSize = Dimension(100, 32)
      toolTipText = "扫描并显示选中路径的错误信息"
    }
    
    cleanCodeButton.apply {
      preferredSize = Dimension(100, 32)
      toolTipText = "对选中路径执行代码清理操作"
    }
    
    refreshButton.apply {
      preferredSize = Dimension(80, 32)
      toolTipText = "刷新错误信息"
    }
    
    buttonPanel.add(viewErrorsButton)
    buttonPanel.add(Box.createHorizontalStrut(10))
    buttonPanel.add(cleanCodeButton)
    buttonPanel.add(Box.createHorizontalStrut(10))
    buttonPanel.add(refreshButton)
    buttonPanel.add(Box.createHorizontalGlue())
    
    panel.add(pathPanel, BorderLayout.CENTER)
    panel.add(buttonPanel, BorderLayout.SOUTH)
    
    return panel
  }

  private fun createErrorDisplayPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = JBUI.Borders.empty(5, 10, 5, 10)
    
    // 表格标题
    val titlePanel = JPanel(BorderLayout())
    titlePanel.add(JLabel("错误和警告信息:"), BorderLayout.WEST)
    
    val countLabel = JLabel("0 个文件")
    countLabel.font = countLabel.font.deriveFont(Font.PLAIN, 11f)
    titlePanel.add(countLabel, BorderLayout.EAST)
    
    panel.add(titlePanel, BorderLayout.NORTH)
    
    // 设置表格
    setupErrorTable()
    val scrollPane = JBScrollPane(errorTable)
    scrollPane.preferredSize = Dimension(0, 300)
    panel.add(scrollPane, BorderLayout.CENTER)
    
    return panel
  }

  private fun setupErrorTable() {
    errorTable.apply {
      // 设置列宽
      columnModel.getColumn(0).preferredWidth = 200 // 文件路径
      columnModel.getColumn(1).preferredWidth = 60  // 错误数
      columnModel.getColumn(2).preferredWidth = 60  // 警告数
      columnModel.getColumn(3).preferredWidth = 300 // 摘要
      
      // 设置行高
      rowHeight = 24
      
      // 设置单元格渲染器
      setDefaultRenderer(Any::class.java, ErrorCellRenderer())
      
      // 允许选择多行
      selectionModel.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
      
      // 自动调整列宽
      autoResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN
    }
  }

  private fun createOperationPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = JBUI.Borders.empty(5, 10, 10, 10)
    
    // 清理选项
    val optionsPanel = JPanel()
    optionsPanel.layout = BoxLayout(optionsPanel, BoxLayout.X_AXIS)
    optionsPanel.border = JBUI.Borders.compound(
      JBUI.Borders.customLine(UIManager.getColor("Component.borderColor"), 1, 0, 0, 0),
      JBUI.Borders.empty(10, 0, 5, 0)
    )
    
    optionsPanel.add(JLabel("清理选项: "))
    optionsPanel.add(formatCodeCheckBox)
    optionsPanel.add(Box.createHorizontalStrut(10))
    optionsPanel.add(optimizeImportsCheckBox)
    optionsPanel.add(Box.createHorizontalStrut(10))
    optionsPanel.add(runInspectionsCheckBox)
    optionsPanel.add(Box.createHorizontalGlue())
    
    // 状态栏
    val statusPanel = JPanel(BorderLayout())
    statusPanel.add(statusLabel, BorderLayout.WEST)
    
    progressBar.apply {
      isVisible = false
      preferredSize = Dimension(200, 20)
    }
    statusPanel.add(progressBar, BorderLayout.EAST)
    
    panel.add(optionsPanel, BorderLayout.NORTH)
    panel.add(statusPanel, BorderLayout.SOUTH)
    
    return panel
  }

  private fun setupEventHandlers() {
    // 浏览按钮
    browseButton.addActionListener { browseForFile() }
    
    // 查看错误按钮
    viewErrorsButton.addActionListener { viewErrors() }
    
    // 清理代码按钮
    cleanCodeButton.addActionListener { cleanCode() }
    
    // 刷新按钮
    refreshButton.addActionListener { refreshErrors() }
    
    // 路径输入框回车
    pathField.addActionListener { viewErrors() }
    
    // 表格双击
    errorTable.addMouseListener(object : java.awt.event.MouseAdapter() {
      override fun mouseClicked(e: java.awt.event.MouseEvent) {
        if (e.clickCount == 2) {
          openSelectedFile()
        }
      }
    })
  }

  private fun initializeDefaultPath() {
    // 设置默认路径为项目根目录
    project.basePath?.let { basePath ->
      pathField.text = basePath
    }
  }

  private fun browseForFile() {
    val descriptor = FileChooserDescriptor(true, true, false, false, false, false)
    descriptor.title = "选择文件或目录"
    descriptor.description = "选择要操作的文件或目录"
    
    val selectedFile = FileChooser.chooseFile(descriptor, project, currentFile)
    selectedFile?.let { file ->
      pathField.text = file.path
      currentFile = file
      McpLogManager.info("选择文件: ${file.path}", LogSource.UI.displayName)
    }
  }

  private fun viewErrors() {
    val path = pathField.text.trim()
    if (path.isEmpty()) {
      showMessage("请输入文件或目录路径", MessageType.WARNING)
      return
    }
    
    setOperationInProgress(true, "正在扫描错误...")
    
    scope.launch {
      try {
        val virtualFile = fileManager.resolvePathToVirtualFile(project, path)
        if (virtualFile == null) {
          ApplicationManager.getApplication().invokeLater {
            showMessage("路径不存在或无法访问: $path", MessageType.ERROR)
            setOperationInProgress(false)
          }
          return@launch
        }
        
        currentFile = virtualFile
        val errors = errorService.collectErrors(project, virtualFile)
        
        ApplicationManager.getApplication().invokeLater {
          currentErrors = errors
          errorTableModel.updateErrors(errors)
          updateErrorCount(errors)
          showMessage("扫描完成，找到 ${errors.size} 个有问题的文件", MessageType.INFO)
          setOperationInProgress(false)
          
          McpLogManager.info("错误扫描完成 - 文件: ${errors.size}, 路径: $path", LogSource.UI.displayName)
        }
      } catch (e: Exception) {
        ApplicationManager.getApplication().invokeLater {
          showMessage("扫描失败: ${e.message}", MessageType.ERROR)
          setOperationInProgress(false)
          McpLogManager.error("错误扫描失败: $path", LogSource.UI.displayName, e)
        }
      }
    }
  }

  private fun cleanCode() {
    val path = pathField.text.trim()
    if (path.isEmpty()) {
      showMessage("请输入文件或目录路径", MessageType.WARNING)
      return
    }
    
    val virtualFile = fileManager.resolvePathToVirtualFile(project, path)
    if (virtualFile == null) {
      showMessage("路径不存在或无法访问: $path", MessageType.ERROR)
      return
    }
    
    val options = CleanOptions(
      formatCode = formatCodeCheckBox.isSelected,
      optimizeImports = optimizeImportsCheckBox.isSelected,
      runInspections = runInspectionsCheckBox.isSelected
    )
    
    setOperationInProgress(true, "正在清理代码...")
    
    scope.launch {
      try {
        val result = cleanService.cleanCode(project, virtualFile, options)
        
        ApplicationManager.getApplication().invokeLater {
          val message = "清理完成 - 处理: ${result.processedFiles} 个文件, 修改: ${result.modifiedFiles} 个文件"
          showMessage(message, MessageType.INFO)
          setOperationInProgress(false)
          
          // 如果有错误，自动刷新错误列表
          if (currentErrors.isNotEmpty()) {
            refreshErrors()
          }
          
          McpLogManager.info("代码清理完成 - ${result.summary}", LogSource.UI.displayName)
        }
      } catch (e: Exception) {
        ApplicationManager.getApplication().invokeLater {
          showMessage("清理失败: ${e.message}", MessageType.ERROR)
          setOperationInProgress(false)
          McpLogManager.error("代码清理失败: $path", LogSource.UI.displayName, e)
        }
      }
    }
  }

  private fun refreshErrors() {
    if (currentFile != null) {
      viewErrors()
    }
  }

  private fun openSelectedFile() {
    val selectedRow = errorTable.selectedRow
    if (selectedRow >= 0 && selectedRow < currentErrors.size) {
      val errorInfo = currentErrors[selectedRow]
      val virtualFile = fileManager.resolvePathToVirtualFile(project, errorInfo.filePath)
      
      virtualFile?.let { file ->
        // 在编辑器中打开文件
        ApplicationManager.getApplication().invokeLater {
          com.intellij.openapi.fileEditor.FileEditorManager.getInstance(project).openFile(file, true)
          McpLogManager.info("打开文件: ${file.path}", LogSource.UI.displayName)
        }
      }
    }
  }

  private fun updateErrorCount(errors: List<FileErrorInfo>) {
    val totalErrors = errors.sumOf { it.errors.size }
    val totalWarnings = errors.sumOf { it.warnings.size }
    
    val countText = buildString {
      append("${errors.size} 个文件")
      if (totalErrors > 0 || totalWarnings > 0) {
        append(" (")
        if (totalErrors > 0) append("${totalErrors} 错误")
        if (totalWarnings > 0) {
          if (totalErrors > 0) append(", ")
          append("${totalWarnings} 警告")
        }
        append(")")
      }
    }
    
    // 更新计数标签
    SwingUtilities.invokeLater {
      // 查找并更新计数标签
      findCountLabel()?.text = countText
    }
  }

  private fun findCountLabel(): JLabel? {
    // 递归查找计数标签
    return findComponentByType(this, JLabel::class.java) { label ->
      label.text.contains("个文件")
    }
  }

  private fun <T : Component> findComponentByType(
    container: java.awt.Container,
    type: Class<T>,
    predicate: (T) -> Boolean = { true }
  ): T? {
    for (component in container.components) {
      if (type.isInstance(component) && predicate(type.cast(component))) {
        return type.cast(component)
      }
      if (component is java.awt.Container) {
        val found = findComponentByType(component, type, predicate)
        if (found != null) return found
      }
    }
    return null
  }

  private fun setOperationInProgress(inProgress: Boolean, message: String = "") {
    SwingUtilities.invokeLater {
      progressBar.isVisible = inProgress
      if (inProgress) {
        progressBar.isIndeterminate = true
        statusLabel.text = message
      } else {
        progressBar.isIndeterminate = false
        statusLabel.text = "就绪"
      }
      
      // 禁用/启用操作按钮
      viewErrorsButton.isEnabled = !inProgress
      cleanCodeButton.isEnabled = !inProgress
      refreshButton.isEnabled = !inProgress
      browseButton.isEnabled = !inProgress
    }
  }

  private fun showMessage(message: String, type: MessageType) {
    SwingUtilities.invokeLater {
      statusLabel.text = message
      
      // 根据消息类型设置不同的颜色
      statusLabel.foreground = when (type) {
        MessageType.ERROR -> UIManager.getColor("Component.errorFocusColor") ?: java.awt.Color.RED
        MessageType.WARNING -> UIManager.getColor("Component.warningFocusColor") ?: java.awt.Color.ORANGE
        MessageType.INFO -> UIManager.getColor("Label.foreground") ?: java.awt.Color.BLACK
      }
      
      // 3秒后恢复默认状态
      Timer(3000) {
        SwingUtilities.invokeLater {
          if (statusLabel.text == message) {
            statusLabel.text = "就绪"
            statusLabel.foreground = UIManager.getColor("Label.foreground")
          }
        }
      }.apply {
        isRepeats = false
        start()
      }
    }
  }

  /**
   * 消息类型枚举
   */
  private enum class MessageType {
    INFO, WARNING, ERROR
  }

  /**
   * 错误表格模型
   */
  private class ErrorTableModel : AbstractTableModel() {
    private val columnNames = arrayOf("文件路径", "错误", "警告", "摘要")
    private var errors: List<FileErrorInfo> = emptyList()

    fun updateErrors(newErrors: List<FileErrorInfo>) {
      errors = newErrors
      fireTableDataChanged()
    }

    override fun getRowCount(): Int = errors.size

    override fun getColumnCount(): Int = columnNames.size

    override fun getColumnName(column: Int): String = columnNames[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
      if (rowIndex >= errors.size) return ""
      
      val errorInfo = errors[rowIndex]
      return when (columnIndex) {
        0 -> errorInfo.filePath
        1 -> errorInfo.errors.size
        2 -> errorInfo.warnings.size
        3 -> errorInfo.summary
        else -> ""
      }
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
      return when (columnIndex) {
        1, 2 -> Int::class.java
        else -> String::class.java
      }
    }
  }

  /**
   * 错误表格单元格渲染器
   */
  private class ErrorCellRenderer : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
      table: JTable,
      value: Any?,
      isSelected: Boolean,
      hasFocus: Boolean,
      row: Int,
      column: Int
    ): Component {
      val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
      
      if (!isSelected) {
        // 根据错误数量设置背景色
        when (column) {
          1 -> { // 错误列
            val errorCount = value as? Int ?: 0
            background = when {
              errorCount > 0 -> java.awt.Color(255, 240, 240) // 浅红色
              else -> table.background
            }
          }
          2 -> { // 警告列
            val warningCount = value as? Int ?: 0
            background = when {
              warningCount > 0 -> java.awt.Color(255, 248, 220) // 浅黄色
              else -> table.background
            }
          }
          else -> background = table.background
        }
      }
      
      // 设置文本对齐
      horizontalAlignment = when (column) {
        1, 2 -> SwingConstants.CENTER // 数字列居中
        else -> SwingConstants.LEFT
      }
      
      return component
    }
  }
}
