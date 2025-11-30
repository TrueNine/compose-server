package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import io.github.truenine.composeserver.ide.ideamcp.services.CleanOptions
import io.github.truenine.composeserver.ide.ideamcp.services.CleanService
import kotlinx.coroutines.runBlocking

/**
 * Context menu action for code clean-up.
 *
 * Provides a right-click action in editor and project tree to clean and format code using IDEA capabilities.
 */
class CleanCodeAction : AnAction("Clean Code", "Clean and format code using IDEA features", null) {

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  override fun update(e: AnActionEvent) {
    val project = e.project
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

    // Enable action only when a project exists and a file or directory is selected
    e.presentation.isEnabledAndVisible = project != null && virtualFile != null && (virtualFile.isDirectory || isCodeFile(virtualFile))
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

    Logger.info("Start executing code clean-up action - path: ${virtualFile.path}", "CleanCodeAction")

    // Show clean-up options dialog
    val cleanOptions = showCleanOptionsDialog(project) ?: return

    // Run clean-up in a background task
    ProgressManager.getInstance()
      .run(
        object : Task.Backgroundable(project, "Cleaning code...", true) {
          private var currentResult: io.github.truenine.composeserver.ide.ideamcp.services.CleanResult? = null

          override fun run(indicator: ProgressIndicator) {
            try {
              indicator.text = "Analyzing file: ${virtualFile.name}"
              indicator.text2 = "Preparing clean-up operations..."
              indicator.isIndeterminate = false
              indicator.fraction = 0.1

              // Check whether the operation was cancelled
              if (indicator.isCanceled) {
                Logger.info("User cancelled code clean-up operation", "CleanCodeAction")
                return
              }

              val cleanService = project.service<CleanService>()

              indicator.text2 = "Executing clean-up operations..."
              indicator.fraction = 0.3

              val result = runBlocking { cleanService.cleanCode(project, virtualFile, cleanOptions) }

              currentResult = result

              // Check whether the operation was cancelled
              if (indicator.isCanceled) {
                Logger.info("Clean-up operation was cancelled by user", "CleanCodeAction")
                return
              }

              indicator.text2 = "Clean-up completed"
              indicator.fraction = 1.0

              // Show result on EDT
              com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                if (!indicator.isCanceled) {
                  showDetailedResultDialog(project, result, virtualFile.name)
                }
              }

              Logger.info(
                "Code clean-up completed - processed: ${result.processedFiles}, modified: ${result.modifiedFiles}, duration: ${result.executionTime}ms",
                "CleanCodeAction",
              )
            } catch (e: Exception) {
              Logger.error("Code clean-up failed", "CleanCodeAction", e)

              // Show error on EDT
              com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                if (!indicator.isCanceled) {
                  showErrorDialog(project, e, virtualFile.name)
                }
              }
            }
          }

          override fun onCancel() {
            Logger.info("Code clean-up operation cancelled", "CleanCodeAction")

            // Show cancellation message on EDT
            com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
              Messages.showInfoMessage(project, "Code clean-up operation was cancelled", "Operation cancelled")
            }
          }

          override fun onSuccess() {
            currentResult?.let { result -> Logger.info("Code clean-up successfully completed - total duration: ${result.executionTime}ms", "CleanCodeAction") }
          }
        }
      )
  }

  /** Check whether the given file is a code file. */
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

  /** Show the clean-up options dialog. */
  private fun showCleanOptionsDialog(project: Project): CleanOptions? {
    val dialog = CleanOptionsDialog(project)
    return if (dialog.showAndGet()) {
      dialog.getCleanOptions()
    } else {
      null
    }
  }

  /** Show detailed result dialog. */
  private fun showDetailedResultDialog(project: Project, result: io.github.truenine.composeserver.ide.ideamcp.services.CleanResult, fileName: String) {
    val dialog = CleanResultDialog(project, result, fileName)
    dialog.show()
  }

  /** Show error dialog. */
  private fun showErrorDialog(project: Project, error: Throwable, fileName: String) {
    val message = buildString {
      appendLine("An error occurred while cleaning file: $fileName")
      appendLine()
      appendLine("Error message: ${error.message}")
      appendLine()
      appendLine("Suggestions:")
      when (error) {
        is SecurityException -> {
          appendLine("• Check file permissions")
          appendLine("• Ensure the file is not locked by another process")
        }

        is java.nio.file.NoSuchFileException -> {
          appendLine("• Verify that the file still exists")
          appendLine("• Refresh the project view")
        }

        else -> {
          appendLine("• Check file state")
          appendLine("• Retry the operation")
          appendLine("• Review logs for more information")
        }
      }
    }

    Messages.showErrorDialog(project, message, "Clean-up error")
  }
}
