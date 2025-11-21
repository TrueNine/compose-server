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

/**
 * Context menu action for viewing errors.
 *
 * Provides a right-click action in the project tree to view errors and warnings for a file or directory.
 */
class ViewErrorAction : AnAction("View Errors", "View errors and warnings in files or directories", null) {

  private val logger = LoggerFactory.getLogger(ViewErrorAction::class.java)

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  override fun update(e: AnActionEvent) {
    val project = e.project
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

    // Enable action only when a project exists and a file or directory is selected
    e.presentation.isEnabledAndVisible = project != null && virtualFile != null
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

    logger.info("Starting view error action - path: {}", virtualFile.path)

    // Show error-view options dialog
    val options = showErrorOptionsDialog(project) ?: return

    // Run error collection in a background task
    ProgressManager.getInstance()
      .run(
        object : Task.Backgroundable(project, "Collecting error information...", true) {
          private var currentReport: List<io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo>? = null

          override fun run(indicator: ProgressIndicator) {
            try {
              indicator.text = "Scanning file: ${virtualFile.name}"
              indicator.text2 = "Preparing analysis..."
              indicator.isIndeterminate = false
              indicator.fraction = 0.1

              // Check whether the operation was cancelled
              if (indicator.isCanceled) {
                logger.info("User cancelled error view operation")
                return
              }

              val errorService = project.service<ErrorService>()

              indicator.text2 = "Collecting error information..."
              indicator.fraction = 0.3

              val errorReport = errorService.collectErrors(project, virtualFile)

              // Check whether the operation was cancelled
              if (indicator.isCanceled) {
                logger.info("Error collection cancelled by user")
                return
              }

              indicator.text2 = "Filtering results..."
              indicator.fraction = 0.8

              // Filter results according to options
              val filteredReport = filterErrorReport(errorReport, options)
              currentReport = filteredReport

              indicator.text2 = "Analysis completed"
              indicator.fraction = 1.0

              // Show result on EDT
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

              // Show error on EDT
              com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                if (!indicator.isCanceled) {
                  showDetailedErrorDialog(project, e, virtualFile.name)
                }
              }
            }
          }

          override fun onCancel() {
            logger.info("Error view operation cancelled")

            // Show cancellation message on EDT
            com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
              Messages.showInfoMessage(project, "Error view operation was cancelled", "Operation cancelled")
            }
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

  /** Show error-view options dialog. */
  private fun showErrorOptionsDialog(project: Project): ErrorViewOptions? {
    val dialog = ErrorViewOptionsDialog(project)
    return if (dialog.showAndGet()) {
      dialog.getErrorViewOptions()
    } else {
      null
    }
  }

  /** Filter the error report based on options. */
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

  /** Show error-report dialog. */
  private fun showErrorReportDialog(project: Project, errorReport: List<io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo>, fileName: String) {
    if (errorReport.isEmpty()) {
      Messages.showInfoMessage(project, "No errors or warnings were found", "View result")
      return
    }

    val dialog = ErrorReportDialog(project, errorReport, fileName)
    dialog.show()
  }

  /** Show detailed error dialog. */
  private fun showDetailedErrorDialog(project: Project, error: Throwable, fileName: String) {
    val message = buildString {
      appendLine("An error occurred while analyzing file: $fileName")
      appendLine()
      appendLine("Error message: ${error.message}")
      appendLine()
      appendLine("Suggestions:")
      when (error) {
        is SecurityException -> {
          appendLine("• Check file access permissions")
          appendLine("• Ensure the project is loaded correctly")
        }

        is java.nio.file.NoSuchFileException -> {
          appendLine("• Verify that the file still exists")
          appendLine("• Refresh the project view")
        }

        is IllegalArgumentException -> {
          appendLine("• Check file path format")
          appendLine("• Ensure a valid file or directory is selected")
        }

        else -> {
          appendLine("• Check project index state")
          appendLine("• Rebuild the project")
          appendLine("• Review logs for more information")
        }
      }
    }

    Messages.showErrorDialog(project, message, "Analysis error")
  }
}

/** Error view options. */
data class ErrorViewOptions(val includeWarnings: Boolean = true, val includeWeakWarnings: Boolean = true)
