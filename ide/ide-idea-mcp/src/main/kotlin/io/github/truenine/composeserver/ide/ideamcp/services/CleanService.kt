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

/** 代码清理服务接口 提供代码格式化、导入优化、检查修复等功能 */
interface CleanService {
  /** 执行代码清理操作 */
  suspend fun cleanCode(project: Project, virtualFile: VirtualFile, options: CleanOptions): CleanResult
}

/** 清理选项配置 */
data class CleanOptions(
  /** 是否执行代码格式化 */
  val formatCode: Boolean = true,
  /** 是否优化导入 */
  val optimizeImports: Boolean = true,
  /** 是否运行代码检查并修复 */
  val runInspections: Boolean = true,
  /** 是否重新排列代码 */
  val rearrangeCode: Boolean = false,
)

/** 清理结果 */
data class CleanResult(
  /** 处理的文件数量 */
  val processedFiles: Int,
  /** 修改的文件数量 */
  val modifiedFiles: Int,
  /** 执行的操作列表 */
  val operations: List<CleanOperation>,
  /** 错误信息列表 */
  val errors: List<String>,
  /** 操作摘要 */
  val summary: String,
  /** 执行时间（毫秒） */
  val executionTime: Long,
)

/** 代码清理服务实现类 */
@Service(Service.Level.PROJECT)
open class CleanServiceImpl(private val project: Project) : CleanService {
  protected open val fileManager: FileManager by lazy { project.service<FileManager>() }

  override suspend fun cleanCode(project: Project, virtualFile: VirtualFile, options: CleanOptions): CleanResult =
    withContext(Dispatchers.IO) {
      val startTime = System.currentTimeMillis()
      Logger.info("开始代码清理 - 路径: ${virtualFile.path}", "CleanService")
      Logger.debug("清理选项 - 格式化: ${options.formatCode}, 优化导入: ${options.optimizeImports}, 检查修复: ${options.runInspections}", "CleanService")

      val operations = mutableListOf<CleanOperation>()
      val errors = mutableListOf<String>()
      var processedFiles = 0
      var modifiedFiles = 0

      try {
        // 收集要处理的文件
        val filesToProcess = collectFilesToProcess(project, virtualFile)
        processedFiles = filesToProcess.size

        Logger.info("找到 ${filesToProcess.size} 个文件需要处理", "CleanService")

        // 批量处理文件
        for (file in filesToProcess) {
          try {
            val fileModified = processFile(project, file, options, operations)
            if (fileModified) {
              modifiedFiles++
            }
          } catch (e: Exception) {
            val errorMsg = "处理文件失败: ${file.path} - ${e.message}"
            errors.add(errorMsg)
            Logger.error(errorMsg, "CleanService", e)
          }
        }

        val executionTime = System.currentTimeMillis() - startTime
        val summary = createSummary(processedFiles, modifiedFiles, operations, errors)

        Logger.info("代码清理完成 - 处理: $processedFiles, 修改: $modifiedFiles, 耗时: ${executionTime}ms", "CleanService")

        CleanResult(
          processedFiles = processedFiles,
          modifiedFiles = modifiedFiles,
          operations = operations,
          errors = errors,
          summary = summary,
          executionTime = executionTime,
        )
      } catch (e: Exception) {
        Logger.error("代码清理失败: ${virtualFile.path}", "CleanService", e)
        throw e
      }
    }

  /** 收集需要处理的文件 */
  protected open fun collectFilesToProcess(project: Project, virtualFile: VirtualFile): List<VirtualFile> {
    return if (virtualFile.isDirectory) {
      fileManager.collectFilesRecursively(virtualFile) { file -> isCodeFile(file) }
    } else {
      if (isCodeFile(virtualFile)) listOf(virtualFile) else emptyList()
    }
  }

  /** 判断是否为代码文件 */
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

  /** 处理单个文件 */
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

          // 执行写操作
          WriteAction.run<Exception> {
            // 代码格式化
            if (options.formatCode) {
              val beforeText = psiFile.text
              val processor = ReformatCodeProcessor(project, psiFile, null, false)
              processor.run()

              if (psiFile.text != beforeText) {
                fileModified = true
                updateOperationCount(operations, "FORMAT", "代码格式化")
                Logger.debug("格式化文件: ${virtualFile.name}", "CleanService")
              }
            }

            // 优化导入
            if (options.optimizeImports) {
              val beforeText = psiFile.text
              val processor = OptimizeImportsProcessor(project, psiFile)
              processor.run()

              if (psiFile.text != beforeText) {
                fileModified = true
                updateOperationCount(operations, "OPTIMIZE_IMPORTS", "导入优化")
                Logger.debug("优化导入: ${virtualFile.name}", "CleanService")
              }
            }

            // 运行代码检查修复
            if (options.runInspections) {
              val inspectionResult = runCodeInspections(project, psiFile)
              if (inspectionResult) {
                fileModified = true
                updateOperationCount(operations, "RUN_INSPECTIONS", "代码检查修复")
                Logger.debug("修复检查问题: ${virtualFile.name}", "CleanService")
              }
            }
          }

          continuation.resume(fileModified)
        } catch (e: Exception) {
          Logger.error("处理文件异常: ${virtualFile.path}", "CleanService", e)
          continuation.resumeWithException(e)
        }
      }
    }

  /** 运行代码检查并修复 */
  private fun runCodeInspections(project: Project, psiFile: PsiFile): Boolean {
    return try {
      // 触发代码分析
      DaemonCodeAnalyzer.getInstance(project).restart(psiFile)

      // TODO: 实现具体的检查修复逻辑
      // 这里需要使用 IDEA 的检查 API 来执行具体的修复操作
      // 例如使用 InspectionManager, LocalInspectionTool 等

      Logger.debug("触发代码检查: ${psiFile.name}", "CleanService")
      false // 暂时返回 false，等待具体实现
    } catch (e: Exception) {
      Logger.error("代码检查失败: ${psiFile.name}", "CleanService", e)
      false
    }
  }

  /** 更新操作计数 */
  protected open fun updateOperationCount(operations: MutableList<CleanOperation>, type: String, description: String) {
    val existingOperation = operations.find { it.type == type }
    if (existingOperation != null) {
      val index = operations.indexOf(existingOperation)
      operations[index] = existingOperation.copy(filesAffected = existingOperation.filesAffected + 1)
    } else {
      operations.add(CleanOperation(type, description, 1))
    }
  }

  /** 创建操作摘要 */
  protected open fun createSummary(processedFiles: Int, modifiedFiles: Int, operations: List<CleanOperation>, errors: List<String>): String {
    return buildString {
      append("处理了 $processedFiles 个文件")
      if (modifiedFiles > 0) {
        append("，修改了 $modifiedFiles 个文件")
      }

      if (operations.isNotEmpty()) {
        append("。执行的操作：")
        operations.forEach { operation -> append("\n- ${operation.description}: ${operation.filesAffected} 个文件") }
      }

      if (errors.isNotEmpty()) {
        append("\n遇到 ${errors.size} 个错误")
      }
    }
  }
}
