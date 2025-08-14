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
import com.intellij.openapi.vfs.VirtualFile
import io.github.truenine.composeserver.ide.ideamcp.McpLogManager
import io.github.truenine.composeserver.ide.ideamcp.services.CleanOptions
import io.github.truenine.composeserver.ide.ideamcp.services.CleanService
import kotlinx.coroutines.runBlocking

/** 代码清理右键菜单动作 提供在编辑器和项目树中右键执行代码清理的功能 */
class CleanCodeAction : AnAction("清理代码", "使用 IDEA 功能清理和格式化代码", null) {

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  override fun update(e: AnActionEvent) {
    val project = e.project
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

    // 只有在项目存在且选中了文件或文件夹时才启用动作
    e.presentation.isEnabledAndVisible = project != null && virtualFile != null && (virtualFile.isDirectory || isCodeFile(virtualFile))
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

    McpLogManager.info("开始执行代码清理动作 - 路径: ${virtualFile.path}", "CleanCodeAction")

    // 显示清理选项对话框
    val cleanOptions = showCleanOptionsDialog(project) ?: return

    // 在后台任务中执行清理操作
    ProgressManager.getInstance()
      .run(
        object : Task.Backgroundable(project, "正在清理代码...", true) {
          private var currentResult: io.github.truenine.composeserver.ide.ideamcp.services.CleanResult? = null

          override fun run(indicator: ProgressIndicator) {
            try {
              indicator.text = "正在分析文件: ${virtualFile.name}"
              indicator.text2 = "准备清理操作..."
              indicator.isIndeterminate = false
              indicator.fraction = 0.1

              // 检查是否被取消
              if (indicator.isCanceled) {
                McpLogManager.info("用户取消了代码清理操作", "CleanCodeAction")
                return
              }

              val cleanService = project.service<CleanService>()

              indicator.text2 = "正在执行清理操作..."
              indicator.fraction = 0.3

              val result = runBlocking { cleanService.cleanCode(project, virtualFile, cleanOptions) }

              currentResult = result

              // 检查是否被取消
              if (indicator.isCanceled) {
                McpLogManager.info("清理操作被用户取消", "CleanCodeAction")
                return
              }

              indicator.text2 = "清理完成"
              indicator.fraction = 1.0

              // 在 EDT 中显示结果
              com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                if (!indicator.isCanceled) {
                  showDetailedResultDialog(project, result, virtualFile.name)
                }
              }

              McpLogManager.info("代码清理完成 - 处理文件: ${result.processedFiles}, 修改文件: ${result.modifiedFiles}, 耗时: ${result.executionTime}ms", "CleanCodeAction")
            } catch (e: Exception) {
              McpLogManager.error("代码清理失败", "CleanCodeAction", e)

              // 在 EDT 中显示错误
              com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                if (!indicator.isCanceled) {
                  showErrorDialog(project, e, virtualFile.name)
                }
              }
            }
          }

          override fun onCancel() {
            McpLogManager.info("代码清理操作被取消", "CleanCodeAction")

            // 在 EDT 中显示取消消息
            com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater { Messages.showInfoMessage(project, "代码清理操作已取消", "操作取消") }
          }

          override fun onSuccess() {
            currentResult?.let { result -> McpLogManager.info("代码清理成功完成 - 总耗时: ${result.executionTime}ms", "CleanCodeAction") }
          }
        }
      )
  }

  /** 检查是否为代码文件 */
  private fun isCodeFile(virtualFile: VirtualFile): Boolean {
    val extension = virtualFile.extension?.lowercase()
    return extension in
      setOf(
        "kt",
        "kts",
        "java",
        "scala",
        "groovy",
        "js",
        "ts",
        "jsx",
        "tsx",
        "py",
        "rb",
        "php",
        "go",
        "rs",
        "cpp",
        "c",
        "h",
        "hpp",
        "cs",
        "xml",
        "html",
        "css",
        "scss",
        "less",
        "json",
        "yaml",
        "yml",
      )
  }

  /** 显示清理选项对话框 */
  private fun showCleanOptionsDialog(project: Project): CleanOptions? {
    val dialog = CleanOptionsDialog(project)
    return if (dialog.showAndGet()) {
      dialog.getCleanOptions()
    } else {
      null
    }
  }

  /** 显示详细结果对话框 */
  private fun showDetailedResultDialog(project: Project, result: io.github.truenine.composeserver.ide.ideamcp.services.CleanResult, fileName: String) {
    val dialog = CleanResultDialog(project, result, fileName)
    dialog.show()
  }

  /** 显示错误对话框 */
  private fun showErrorDialog(project: Project, error: Throwable, fileName: String) {
    val message = buildString {
      appendLine("清理文件时发生错误: $fileName")
      appendLine()
      appendLine("错误信息: ${error.message}")
      appendLine()
      appendLine("建议:")
      when (error) {
        is SecurityException -> {
          appendLine("• 检查文件权限")
          appendLine("• 确保文件未被其他进程锁定")
        }

        is java.nio.file.NoSuchFileException -> {
          appendLine("• 确认文件仍然存在")
          appendLine("• 刷新项目视图")
        }

        else -> {
          appendLine("• 检查文件状态")
          appendLine("• 重试操作")
          appendLine("• 查看日志获取更多信息")
        }
      }
    }

    Messages.showErrorDialog(project, message, "清理错误")
  }
}
