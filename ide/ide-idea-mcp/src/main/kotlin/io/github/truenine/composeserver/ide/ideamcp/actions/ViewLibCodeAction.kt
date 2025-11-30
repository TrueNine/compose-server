package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeService
import kotlinx.coroutines.runBlocking

/**
 * Context menu action to view library code.
 *
 * Allows users to right-click in the editor to view the source or decompiled code of third-party libraries.
 */
class ViewLibCodeAction : AnAction("View Library Code", "View source or decompiled code for third-party libraries", null) {

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  override fun update(e: AnActionEvent) {
    val project = e.project
    val editor = e.getData(CommonDataKeys.EDITOR)
    val psiFile = e.getData(CommonDataKeys.PSI_FILE)

    // Only enable the action when the caret is at a resolvable reference in an editor.
    e.presentation.isEnabledAndVisible = project != null && editor != null && psiFile != null && canResolveReference(editor, psiFile)
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val editor = e.getData(CommonDataKeys.EDITOR) ?: return
    val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return

    Logger.info("Starting view-library-code action", "ViewLibCodeAction")

    // Get reference information at caret
    val referenceInfo = getReferenceInfo(editor, psiFile)
    if (referenceInfo == null) {
      Messages.showWarningDialog(project, "Cannot detect a class or method reference at the current caret position", "View Library Code")
      return
    }

    Logger.debug("Resolved reference - class: ${referenceInfo.className}, member: ${referenceInfo.memberName}", "ViewLibCodeAction")

    // Retrieve library code in a background task
    ProgressManager.getInstance()
      .run(
        object : Task.Backgroundable(project, "Fetching library code...", true) {
          private var currentResult: io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult? = null

          override fun run(indicator: ProgressIndicator) {
            try {
              indicator.text = "Resolving class reference: ${referenceInfo.className}"
              indicator.text2 = "Preparing to locate source code..."
              indicator.isIndeterminate = false
              indicator.fraction = 0.1

              // Check for cancellation
              if (indicator.isCanceled) {
                Logger.info("User cancelled library code view operation", "ViewLibCodeAction")
                return
              }

              val libCodeService = project.service<LibCodeService>()

              indicator.text2 = "Searching for source code..."
              indicator.fraction = 0.3

              val result = runBlocking { libCodeService.getLibraryCode(project, referenceInfo.className, referenceInfo.memberName) }

              currentResult = result

              // Check for cancellation
              if (indicator.isCanceled) {
                Logger.info("Library code retrieval was cancelled by user", "ViewLibCodeAction")
                return
              }

              indicator.text2 =
                when (result.metadata.sourceType) {
                  io.github.truenine.composeserver.ide.ideamcp.tools.SourceType.SOURCE_JAR -> "Successfully loaded from source JAR"
                  io.github.truenine.composeserver.ide.ideamcp.tools.SourceType.DECOMPILED -> "Decompilation completed"
                  io.github.truenine.composeserver.ide.ideamcp.tools.SourceType.NOT_FOUND -> "Source code not found"
                }
              indicator.fraction = 1.0

              // Show result in EDT
              com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                if (!indicator.isCanceled) {
                  showLibCodeDialog(project, result, referenceInfo.className)
                }
              }

              Logger.info("Library code view completed - sourceType: ${result.metadata.sourceType}, decompiled: ${result.isDecompiled}", "ViewLibCodeAction")
            } catch (e: Exception) {
              Logger.error("Library code view failed", "ViewLibCodeAction", e)

              // Show error in EDT
              com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                if (!indicator.isCanceled) {
                  showDetailedErrorDialog(project, e, referenceInfo.className)
                }
              }
            }
          }

          override fun onCancel() {
            Logger.info("Library code view operation cancelled", "ViewLibCodeAction")

            // Show cancellation message in EDT
            com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
              Messages.showInfoMessage(project, "Library code view operation was cancelled", "Operation cancelled")
            }
          }

          override fun onSuccess() {
            currentResult?.let { result ->
              Logger.info("Library code view finished successfully - source length: ${result.sourceCode.length} characters", "ViewLibCodeAction")
            }
          }
        }
      )
  }

  /** Checks whether a reference at the caret can be resolved. */
  private fun canResolveReference(editor: Editor, psiFile: PsiFile): Boolean {
    val offset = editor.caretModel.offset
    val element = psiFile.findElementAt(offset) ?: return false

    // Find the element that holds the reference
    val referenceElement = element.parent as? PsiReference

    return referenceElement != null
  }

  /** Gets reference information for the caret position. */
  private fun getReferenceInfo(editor: Editor, psiFile: PsiFile): ReferenceInfo? {
    val offset = editor.caretModel.offset
    val element = psiFile.findElementAt(offset) ?: return null

    // Try different strategies depending on language
    return when (psiFile.language.id) {
      "kotlin" -> getKotlinReferenceInfo(element)
      "JAVA" -> getJavaReferenceInfo(element)
      else -> null
    }
  }

  /** Gets reference information for Kotlin code. */
  private fun getKotlinReferenceInfo(element: PsiElement): ReferenceInfo? {
    // Locate the reference element
    val referenceElement = element.parent as? PsiReference ?: return null
    val resolved = referenceElement.resolve() ?: return null

    // Get containing class name - simplified implementation to avoid complex PSI logic
    val className = extractClassNameFromElement(resolved)
    val memberName = extractMemberNameFromElement(resolved)

    return if (className != null) ReferenceInfo(className, memberName) else null
  }

  /** Gets reference information for Java code. */
  private fun getJavaReferenceInfo(element: PsiElement): ReferenceInfo? {
    val referenceElement = element.parent as? PsiReference ?: return null
    val resolved = referenceElement.resolve() ?: return null

    // Simplified implementation to avoid complex PSI operations
    val className = extractClassNameFromElement(resolved)
    val memberName = extractMemberNameFromElement(resolved)

    return if (className != null) ReferenceInfo(className, memberName) else null
  }

  /** Extracts a class name from a PSI element (simplified heuristic). */
  private fun extractClassNameFromElement(element: PsiElement): String? {
    // Simplified heuristic - real implementation would use detailed PSI analysis
    return when {
      element.text.contains("String") -> "java.lang.String"
      element.text.contains("List") -> "java.util.List"
      element.text.contains("Map") -> "java.util.Map"
      else -> null
    }
  }

  /** Extracts a member name from a PSI element (not yet implemented). */
  private fun extractMemberNameFromElement(element: PsiElement): String? {
    // Simplified placeholder - a real implementation would analyze PSI in detail
    return null
  }

  /** Shows the library code dialog. */
  private fun showLibCodeDialog(project: Project, result: io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult, className: String) {
    val dialog = LibCodeDialog(project, result, className)
    dialog.show()
  }

  /** Shows a detailed error dialog when library code retrieval fails. */
  private fun showDetailedErrorDialog(project: Project, error: Throwable, className: String) {
    val message = buildString {
      appendLine("An error occurred while retrieving library code for: $className")
      appendLine()
      appendLine("Error message: ${error.message}")
      appendLine()
      appendLine("Suggestions:")
      when (error) {
        is ClassNotFoundException -> {
          appendLine("• Check that the class name is spelled correctly")
          appendLine("• Ensure the class is on the project classpath")
          appendLine("• Refresh project dependencies")
        }

        is SecurityException -> {
          appendLine("• Check permissions on the library files")
          appendLine("• Ensure the library files are not locked")
        }

        is IllegalArgumentException -> {
          appendLine("• Check the class name format")
          appendLine("• Use a fully qualified class name")
          appendLine("• Ensure the file path is correct")
        }

        else -> {
          appendLine("• Verify the integrity of library files")
          appendLine("• Re-download the dependencies")
          appendLine("• Inspect logs for more details")
        }
      }
    }

    Messages.showErrorDialog(project, message, "Library code retrieval error")
  }
}

/** Reference information for a resolved symbol. */
private data class ReferenceInfo(val className: String, val memberName: String?)
