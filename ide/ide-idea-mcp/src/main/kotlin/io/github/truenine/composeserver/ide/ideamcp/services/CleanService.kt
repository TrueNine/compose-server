package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.codeInsight.actions.OptimizeImportsProcessor
import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import io.github.truenine.composeserver.ide.ideamcp.tools.CleanOperation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Code clean-up service interface.
 *
 * Provides code formatting, import optimization, inspections and fixes.
 */
interface CleanService {
  /** Execute code clean-up operation. */
  suspend fun cleanCode(project: Project, virtualFile: VirtualFile, options: CleanOptions): CleanResult
}

/** Clean-up options configuration. */
data class CleanOptions(
  /** Whether to format code. */
  val formatCode: Boolean = true,
  /** Whether to optimize imports. */
  val optimizeImports: Boolean = true,
  /** Whether to run code inspections and apply fixes. */
  val runInspections: Boolean = true,
  /** Whether to rearrange code. */
  val rearrangeCode: Boolean = false,
)

/** Clean-up result. */
data class CleanResult(
  /** Number of processed files. */
  val processedFiles: Int,
  /** Number of modified files. */
  val modifiedFiles: Int,
  /** List of executed operations. */
  val operations: List<CleanOperation>,
  /** List of error messages. */
  val errors: List<String>,
  /** Operation summary. */
  val summary: String,
  /** Execution time in milliseconds. */
  val executionTime: Long,
)

/** Code clean-up service implementation. */
@Service(Service.Level.PROJECT)
open class CleanServiceImpl(private val project: Project) : CleanService {
  protected open val fileManager: FileManager by lazy { project.service<FileManager>() }

  override suspend fun cleanCode(project: Project, virtualFile: VirtualFile, options: CleanOptions): CleanResult =
    withContext(Dispatchers.IO) {
      val startTime = System.currentTimeMillis()
      Logger.info("Start code clean-up - path: ${virtualFile.path}", "CleanService")
      Logger.debug(
        "Clean options - format: ${options.formatCode}, optimize imports: ${options.optimizeImports}, run inspections: ${options.runInspections}",
        "CleanService",
      )

      val operations = mutableListOf<CleanOperation>()
      val errors = mutableListOf<String>()
      var processedFiles = 0
      var modifiedFiles = 0

      try {
        // Collect files to process
        val filesToProcess = collectFilesToProcess(project, virtualFile)
        processedFiles = filesToProcess.size

        Logger.info("Found ${filesToProcess.size} files to process", "CleanService")

        // Process files in batch
        for (file in filesToProcess) {
          try {
            val fileModified = processFile(project, file, options, operations)
            if (fileModified) {
              modifiedFiles++
            }
          } catch (e: Exception) {
            val errorMsg = "Failed to process file: ${file.path} - ${e.message}"
            errors.add(errorMsg)
            Logger.error(errorMsg, "CleanService", e)
          }
        }

        val executionTime = System.currentTimeMillis() - startTime
        val summary = createSummary(processedFiles, modifiedFiles, operations, errors)

        Logger.info("Code clean-up completed - processed: $processedFiles, modified: $modifiedFiles, duration: ${executionTime}ms", "CleanService")

        CleanResult(
          processedFiles = processedFiles,
          modifiedFiles = modifiedFiles,
          operations = operations,
          errors = errors,
          summary = summary,
          executionTime = executionTime,
        )
      } catch (e: Exception) {
        Logger.error("Code clean-up failed: ${virtualFile.path}", "CleanService", e)
        throw e
      }
    }

  /** Collect files that need to be processed. */
  protected open fun collectFilesToProcess(project: Project, virtualFile: VirtualFile): List<VirtualFile> {
    return if (virtualFile.isDirectory) {
      fileManager.collectFilesRecursively(virtualFile) { file -> isCodeFile(file) }
    } else {
      if (isCodeFile(virtualFile)) listOf(virtualFile) else emptyList()
    }
  }

  /** Determine whether the file is a code file. */
  protected open fun isCodeFile(file: VirtualFile): Boolean {
    if (file.isDirectory) return false

    val extension = file.extension?.lowercase()
    return extension in
      setOf(
        "kt",
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
        "vb",
        "swift",
        "dart",
        "xml",
        "html",
        "css",
        "scss",
        "less",
        "json",
        "yaml",
        "yml",
        "properties",
        "gradle",
      )
  }

  /** Process a single file. */
  private suspend fun processFile(project: Project, virtualFile: VirtualFile, options: CleanOptions, operations: MutableList<CleanOperation>): Boolean =
    suspendCoroutine { continuation ->
      ApplicationManager.getApplication().invokeLater {
        try {
          var fileModified = false
          val psiFile = ReadAction.compute<PsiFile?, Exception> { PsiManager.getInstance(project).findFile(virtualFile) }

          if (psiFile == null) {
            continuation.resume(false)
            return@invokeLater
          }

          // Execute write operations
          WriteAction.run<Exception> {
            // Code formatting
            if (options.formatCode) {
              val beforeText = psiFile.text
              val processor = ReformatCodeProcessor(project, psiFile, null, false)
              processor.run()

              if (psiFile.text != beforeText) {
                fileModified = true
                updateOperationCount(operations, "FORMAT", "Code formatting")
                Logger.debug("Formatted file: ${virtualFile.name}", "CleanService")
              }
            }

            // Optimize imports
            if (options.optimizeImports) {
              val beforeText = psiFile.text
              val processor = OptimizeImportsProcessor(project, psiFile)
              processor.run()

              if (psiFile.text != beforeText) {
                fileModified = true
                updateOperationCount(operations, "OPTIMIZE_IMPORTS", "Optimize imports")
                Logger.debug("Optimized imports: ${virtualFile.name}", "CleanService")
              }
            }

            // Run code inspections and fixes
            if (options.runInspections) {
              val inspectionResult = runCodeInspections(project, psiFile)
              if (inspectionResult) {
                fileModified = true
                updateOperationCount(operations, "RUN_INSPECTIONS", "Code inspections and fixes")
                Logger.debug("Fixed inspection issues: ${virtualFile.name}", "CleanService")
              }
            }
          }

          continuation.resume(fileModified)
        } catch (e: Exception) {
          Logger.error("Exception while processing file: ${virtualFile.path}", "CleanService", e)
          continuation.resumeWithException(e)
        }
      }
    }

  /** Run code inspections and apply fixes. */
  private fun runCodeInspections(project: Project, psiFile: PsiFile): Boolean {
    return try {
      // Trigger code analysis
      DaemonCodeAnalyzer.getInstance(project).restart(psiFile)

      // TODO: Implement concrete inspection and fix logic.
      // This should use IDEA inspection APIs such as InspectionManager,
      // LocalInspectionTool, etc.

      Logger.debug("Triggered code inspections: ${psiFile.name}", "CleanService")
      false // Temporarily return false until implementation is provided
    } catch (e: Exception) {
      Logger.error("Code inspections failed: ${psiFile.name}", "CleanService", e)
      false
    }
  }

  /** Update operation count. */
  protected open fun updateOperationCount(operations: MutableList<CleanOperation>, type: String, description: String) {
    val existingOperation = operations.find { it.type == type }
    if (existingOperation != null) {
      val index = operations.indexOf(existingOperation)
      operations[index] = existingOperation.copy(filesAffected = existingOperation.filesAffected + 1)
    } else {
      operations.add(CleanOperation(type, description, 1))
    }
  }

  /** Create operation summary. */
  protected open fun createSummary(processedFiles: Int, modifiedFiles: Int, operations: List<CleanOperation>, errors: List<String>): String {
    return buildString {
      append("Processed $processedFiles files")
      if (modifiedFiles > 0) {
        append(", modified $modifiedFiles files")
      }

      if (operations.isNotEmpty()) {
        append(". Operations performed:")
        operations.forEach { operation -> append("\n- ${operation.description}: ${operation.filesAffected} files") }
      }

      if (errors.isNotEmpty()) {
        append("\nEncountered ${errors.size} errors")
      }
    }
  }
}
