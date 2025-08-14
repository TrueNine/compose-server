package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import io.github.truenine.composeserver.ide.ideamcp.McpLogManager
import io.github.truenine.composeserver.ide.ideamcp.tools.ErrorInfo
import io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity
import io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo

/**
 * 错误服务接口
 * 提供扫描和收集项目中错误、警告信息的功能
 */
interface ErrorService {
  /**
   * 收集指定路径下的所有错误信息
   */
  fun collectErrors(project: Project, virtualFile: VirtualFile): List<FileErrorInfo>
  
  /**
   * 分析单个文件的错误信息
   */
  fun analyzeFile(project: Project, virtualFile: VirtualFile): List<ErrorInfo>
}

/**
 * 错误服务实现类
 */
@Service(Service.Level.PROJECT)
class ErrorServiceImpl : ErrorService {
  
  override fun collectErrors(project: Project, virtualFile: VirtualFile): List<FileErrorInfo> {
    McpLogManager.info("开始收集错误信息 - 路径: ${virtualFile.path}", "ErrorService")
    
    val result = mutableListOf<FileErrorInfo>()
    
    try {
      if (virtualFile.isDirectory) {
        // 递归处理目录
        collectErrorsFromDirectory(project, virtualFile, result)
      } else {
        // 处理单个文件
        val fileErrors = analyzeFile(project, virtualFile)
        if (fileErrors.isNotEmpty()) {
          result.add(createFileErrorInfo(project, virtualFile, fileErrors))
        }
      }
      
      McpLogManager.info("错误收集完成 - 文件数: ${result.size}", "ErrorService")
    } catch (e: Exception) {
      McpLogManager.error("错误收集失败: ${virtualFile.path}", "ErrorService", e)
      throw e
    }
    
    return result
  }
  
  override fun analyzeFile(project: Project, virtualFile: VirtualFile): List<ErrorInfo> {
    if (!virtualFile.isValid || virtualFile.isDirectory) {
      return emptyList()
    }
    
    try {
      val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
        ?: return emptyList()
      
      return analyzeFileWithPsi(psiFile)
    } catch (e: Exception) {
      McpLogManager.error("文件分析失败: ${virtualFile.path}", "ErrorService", e)
      return emptyList()
    }
  }
  
  /**
   * 递归收集目录中的错误信息
   */
  private fun collectErrorsFromDirectory(
    project: Project, 
    directory: VirtualFile, 
    result: MutableList<FileErrorInfo>
  ) {
    if (!directory.isValid || !directory.isDirectory) {
      return
    }
    
    try {
      directory.children?.forEach { child ->
        if (child.isDirectory) {
          // 递归处理子目录
          collectErrorsFromDirectory(project, child, result)
        } else {
          // 分析文件
          val fileErrors = analyzeFile(project, child)
          if (fileErrors.isNotEmpty()) {
            result.add(createFileErrorInfo(project, child, fileErrors))
          }
        }
      }
    } catch (e: Exception) {
      McpLogManager.error("目录扫描失败: ${directory.path}", "ErrorService", e)
    }
  }
  
  /**
   * 使用 PSI 分析文件错误
   */
  private fun analyzeFileWithPsi(psiFile: PsiFile): List<ErrorInfo> {
    val errors = mutableListOf<ErrorInfo>()
    
    try {
      // TODO: 实现实际的 PSI 错误分析
      // 这里需要使用 IDEA 的代码检查 API 来获取实际的错误信息
      // 目前返回模拟数据
      
      McpLogManager.debug("分析文件: ${psiFile.name}", "ErrorService")
      
      // 模拟一些错误信息用于测试
      if (psiFile.name.endsWith(".kt") || psiFile.name.endsWith(".java")) {
        // 这里应该使用实际的代码检查 API
        // 例如: DaemonCodeAnalyzer, HighlightingLevelManager 等
      }
      
    } catch (e: Exception) {
      McpLogManager.error("PSI 分析失败: ${psiFile.name}", "ErrorService", e)
    }
    
    return errors
  }
  
  /**
   * 创建文件错误信息对象
   */
  private fun createFileErrorInfo(
    project: Project, 
    virtualFile: VirtualFile, 
    errors: List<ErrorInfo>
  ): FileErrorInfo {
    val relativePath = getRelativePath(project, virtualFile)
    
    val errorList = errors.filter { it.severity == ErrorSeverity.ERROR }
    val warningList = errors.filter { it.severity == ErrorSeverity.WARNING }
    val weakWarningList = errors.filter { it.severity == ErrorSeverity.WEAK_WARNING }
    
    val summary = buildString {
      if (errorList.isNotEmpty()) append("${errorList.size}个错误")
      if (warningList.isNotEmpty()) {
        if (isNotEmpty()) append(", ")
        append("${warningList.size}个警告")
      }
      if (weakWarningList.isNotEmpty()) {
        if (isNotEmpty()) append(", ")
        append("${weakWarningList.size}个弱警告")
      }
    }
    
    return FileErrorInfo(
      filePath = virtualFile.path,
      relativePath = relativePath,
      errors = errorList,
      warnings = warningList,
      weakWarnings = weakWarningList,
      summary = summary
    )
  }
  
  /**
   * 获取相对路径
   */
  private fun getRelativePath(project: Project, virtualFile: VirtualFile): String {
    val basePath = project.basePath ?: return virtualFile.path
    val filePath = virtualFile.path
    
    return if (filePath.startsWith(basePath)) {
      filePath.substring(basePath.length).removePrefix("/").removePrefix("\\")
    } else {
      virtualFile.name
    }
  }
  
  /**
   * 转换 IDEA 的严重程度到我们的枚举
   */
  private fun convertSeverity(severity: HighlightSeverity): ErrorSeverity {
    return when {
      severity >= HighlightSeverity.ERROR -> ErrorSeverity.ERROR
      severity >= HighlightSeverity.WARNING -> ErrorSeverity.WARNING
      severity >= HighlightSeverity.WEAK_WARNING -> ErrorSeverity.WEAK_WARNING
      else -> ErrorSeverity.INFO
    }
  }
  
  /**
   * 转换问题高亮类型到我们的枚举
   */
  private fun convertHighlightType(highlightType: ProblemHighlightType): ErrorSeverity {
    return when (highlightType) {
      ProblemHighlightType.ERROR -> ErrorSeverity.ERROR
      ProblemHighlightType.GENERIC_ERROR_OR_WARNING -> ErrorSeverity.WARNING
      ProblemHighlightType.WARNING -> ErrorSeverity.WARNING
      ProblemHighlightType.WEAK_WARNING -> ErrorSeverity.WEAK_WARNING
      ProblemHighlightType.INFORMATION -> ErrorSeverity.INFO
      else -> ErrorSeverity.INFO
    }
  }
}
