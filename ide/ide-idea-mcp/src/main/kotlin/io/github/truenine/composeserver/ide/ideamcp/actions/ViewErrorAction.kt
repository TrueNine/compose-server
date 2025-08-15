package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.github.truenine.composeserver.ide.ideamcp.services.ErrorService
import org.slf4j.LoggerFactory

/** 查看错误右键菜单动作 提供在项目树中右键查看文件或文件夹错误信息的功能 */
class ViewErrorAction : AnAction("查看错误", "查看文件或文件夹中的错误、警告信息", null) {

  private val logger = LoggerFactory.getLogger(ViewErrorAction::class.java)

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  override fun update(e: AnActionEvent) {
    val project = e.project
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

    // 只有在项目存在且选中了文件或文件夹时才启用动作
    e.presentation.isEnabledAndVisible = project != null && virtualFile != null
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

    logger.info("Starting view error action - path: {}", virtualFile.path)

    // 显示错误查看选项对话框
    val options = showErrorOptionsDialog(project) ?: return

    // 在后台任务中执行错误收集
    ProgressManager.getInstance()
      .run(
        object : Task.Backgroundable(project, "正在收集错误信息...", true) {
          private var currentReport: List<io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo>? = null

          override fun run(indicator: ProgressIndicator) {
            try {
              indicator.text = "正在扫描文件: ${virtualFile.name}"
              indicator.text2 = "准备分析..."
              indicator.isIndeterminate = false
              indicator.fraction = 0.1

              // 检查是否被取消
              if (indicator.isCanceled) {
                logger.info("User cancelled error view operation")
                return
              }

              val errorService = project.service<ErrorService>()

              indicator.text2 = "正在收集错误信息..."
              indicator.fraction = 0.3

              val errorReport = errorService.collectErrors(project, virtualFile)

              // 检查是否被取消
              if (indicator.isCanceled) {
                logger.info("Error collection cancelled by user")
                return
              }

              indicator.text2 = "正在过滤结果..."
              indicator.fraction = 0.8

              // 根据选项过滤结果
              val filteredReport = filterErrorReport(errorReport, options)
              currentReport = filteredReport

              indicator.text2 = "分析完成"
              indicator.fraction = 1.0

              // 在 EDT 中显示结果
              com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                if (!indicator.isCanceled) {
                  showErrorReportDialog(project, filteredReport, virtualFile.name)
                }
              }

              val totalErrors = filteredReport.sumOf { it.errors.size }
              val totalWarnings = filteredReport.sumOf { it.warnings.size }
              logger.info("Error view completed - errors: {}, warnings: {}", totalErrors, totalWarnings)
            } catch (e: Exception) {
              logger.error("Error view failed", e)

              // 在 EDT 中显示错误
              com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                if (!indicator.isCanceled) {
                  showDetailedErrorDialog(project, e, virtualFile.name)
                }
              }
            }
          }

          override fun onCancel() {
            logger.info("Error view operation cancelled")

            // 在 EDT 中显示取消消息
            com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater { Messages.showInfoMessage(project, "错误查看操作已取消", "操作取消") }
          }

          override fun onSuccess() {
            currentReport?.let { report ->
              val totalIssues = report.sumOf { it.errors.size + it.warnings.size + it.weakWarnings.size }
              logger.info("Error view completed successfully - found {} issues", totalIssues)
            }
          }
        }
      )
  }

  /** 显示错误查看选项对话框 */
  private fun showErrorOptionsDialog(project: Project): ErrorViewOptions? {
    val dialog = ErrorViewOptionsDialog(project)
    return if (dialog.showAndGet()) {
      dialog.getErrorViewOptions()
    } else {
      null
    }
  }

  /** 根据选项过滤错误报告 */
  private fun filterErrorReport(
    errorReport: List<io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo>,
    options: ErrorViewOptions,
  ): List<io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo> {
    return errorReport
      .map { fileInfo ->
        fileInfo.errors
        val warnings = if (options.includeWarnings) fileInfo.warnings else emptyList()
        val weakWarnings = if (options.includeWeakWarnings) fileInfo.weakWarnings else emptyList()

        fileInfo.copy(warnings = warnings, weakWarnings = weakWarnings)
      }
      .filter { it.errors.isNotEmpty() || it.warnings.isNotEmpty() || it.weakWarnings.isNotEmpty() }
  }

  /** 显示错误报告对话框 */
  private fun showErrorReportDialog(project: Project, errorReport: List<io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo>, fileName: String) {
    if (errorReport.isEmpty()) {
      Messages.showInfoMessage(project, "未发现任何错误或警告", "查看结果")
      return
    }

    val dialog = ErrorReportDialog(project, errorReport, fileName)
    dialog.show()
  }

  /** 显示详细错误对话框 */
  private fun showDetailedErrorDialog(project: Project, error: Throwable, fileName: String) {
    val message = buildString {
      appendLine("分析文件时发生错误: $fileName")
      appendLine()
      appendLine("错误信息: ${error.message}")
      appendLine()
      appendLine("建议:")
      when (error) {
        is SecurityException -> {
          appendLine("• 检查文件访问权限")
          appendLine("• 确保项目已正确加载")
        }

        is java.nio.file.NoSuchFileException -> {
          appendLine("• 确认文件仍然存在")
          appendLine("• 刷新项目视图")
        }

        is IllegalArgumentException -> {
          appendLine("• 检查文件路径格式")
          appendLine("• 确保选择了有效的文件或文件夹")
        }

        else -> {
          appendLine("• 检查项目索引状态")
          appendLine("• 重新构建项目")
          appendLine("• 查看日志获取更多信息")
        }
      }
    }

    Messages.showErrorDialog(project, message, "分析错误")
  }
}

/** 错误查看选项 */
data class ErrorViewOptions(val includeWarnings: Boolean = true, val includeWeakWarnings: Boolean = true)
