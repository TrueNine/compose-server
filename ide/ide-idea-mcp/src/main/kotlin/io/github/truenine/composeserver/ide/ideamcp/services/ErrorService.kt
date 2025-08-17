package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import io.github.truenine.composeserver.ide.ideamcp.tools.ErrorInfo
import io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity
import io.github.truenine.composeserver.ide.ideamcp.tools.FileErrorInfo
import org.slf4j.LoggerFactory

/**
 * 错误服务接口 - 提供从错误捕获过滤器获取错误信息的功能
 *
 * 通过 HighlightErrorFilter 机制捕获项目中的错误、警告信息
 */
interface ErrorService {
  /** 收集指定路径下的所有错误信息 */
  fun collectErrors(project: Project, virtualFile: VirtualFile): List<FileErrorInfo>

  /** 分析单个文件的错误信息 */
  fun analyzeFile(project: Project, virtualFile: VirtualFile): List<ErrorInfo>

  /** 从错误捕获过滤器获取捕获的语法错误 */
  fun getCapturedSyntaxErrors(project: Project, virtualFile: VirtualFile): List<ErrorInfo>
}

/**
 * 错误服务实现 - 使用 HighlightErrorFilter 捕获所有错误和警告
 *
 * 通过错误捕获过滤器获取实际的语法错误、编译错误、警告信息
 */
@Service(Service.Level.PROJECT)
class ErrorServiceImpl : ErrorService {

  private val logger = LoggerFactory.getLogger(ErrorServiceImpl::class.java)

  override fun collectErrors(project: Project, virtualFile: VirtualFile): List<FileErrorInfo> {
    logger.info("Collecting errors from HighlightErrorFilter - path: {}", virtualFile.path)

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

      logger.info(
        "Error collection completed - file count: {}, total issues: {}",
        result.size,
        result.sumOf { it.errors.size + it.warnings.size + it.weakWarnings.size },
      )
    } catch (e: Exception) {
      logger.error("Error collection failed: {}", virtualFile.path, e)
      throw e
    }

    return result
  }

  override fun analyzeFile(project: Project, virtualFile: VirtualFile): List<ErrorInfo> {
    if (!virtualFile.isValid || virtualFile.isDirectory) {
      return emptyList()
    }

    logger.debug("Analyzing file from ErrorCaptureFilter: {}", virtualFile.name)

    // 检查 Application 是否可用（在测试环境中可能为 null）
    val application = com.intellij.openapi.application.ApplicationManager.getApplication()
    if (application == null) {
      logger.debug("Application not available, returning empty list")
      return emptyList()
    }

    return ReadAction.compute<List<ErrorInfo>, Exception> {
      try {
        val allErrors = mutableListOf<ErrorInfo>()

        // 1. 从错误捕获过滤器获取语法错误
        val capturedErrors = getCapturedSyntaxErrors(project, virtualFile)
        allErrors.addAll(capturedErrors)
        logger.debug("Found {} syntax errors from ErrorCaptureFilter", capturedErrors.size)

        // 2. 从 IDE 代码分析器获取所有类型的警告和错误
        val highlightErrors = getHighlightErrors(project, virtualFile)
        allErrors.addAll(highlightErrors)
        logger.debug("Found {} highlight errors from DaemonCodeAnalyzer", highlightErrors.size)

        // 去重并排序
        val uniqueErrors = deduplicateAndSortErrors(allErrors)
        logger.info("Total unique problems found for {}: {}", virtualFile.name, uniqueErrors.size)

        return@compute uniqueErrors
      } catch (e: Exception) {
        logger.error("Failed to analyze file: {}", virtualFile.path, e)
        return@compute emptyList()
      }
    }
  }

  override fun getCapturedSyntaxErrors(project: Project, virtualFile: VirtualFile): List<ErrorInfo> {
    if (!virtualFile.isValid || virtualFile.isDirectory) {
      return emptyList()
    }

    logger.debug("Getting captured syntax errors for file: {}", virtualFile.name)

    return try {
      // 获取错误捕获过滤器实例
      val errorFilter = ErrorCaptureFilterManager.getInstance()
      logger.debug("ErrorCaptureFilter instance: {}", errorFilter)

      // 从过滤器获取捕获的错误
      val capturedErrors = errorFilter.getCapturedErrors(virtualFile.path)
      logger.debug("Found {} captured syntax errors for {}", capturedErrors.size, virtualFile.name)

      // 转换为 ErrorInfo 格式
      capturedErrors.map { capturedError ->
        ErrorInfo(
          line = capturedError.line,
          column = capturedError.column,
          severity = determineSeverityFromDescription(capturedError.errorDescription),
          message = capturedError.errorDescription,
          code = extractErrorCode(capturedError.errorDescription),
          codeSnippet = capturedError.elementText,
          quickFixes = emptyList(),
        )
      }
    } catch (e: Exception) {
      logger.error("Failed to get captured syntax errors for file: {}", virtualFile.path, e)
      emptyList()
    }
  }

  /** 根据错误描述确定严重程度 */
  private fun determineSeverityFromDescription(description: String): ErrorSeverity {
    val lowerDesc = description.lowercase()

    return when {
      // 错误关键词 - 影响编译或运行的问题
      lowerDesc.contains("error") ||
        lowerDesc.contains("cannot") ||
        lowerDesc.contains("unresolved") ||
        lowerDesc.contains("undefined") ||
        lowerDesc.contains("not found") ||
        lowerDesc.contains("syntax error") ||
        lowerDesc.contains("compilation") ||
        lowerDesc.contains("missing") ||
        lowerDesc.contains("invalid") ||
        lowerDesc.contains("illegal") -> ErrorSeverity.ERROR

      // 警告关键词 - 可能导致问题的代码
      lowerDesc.contains("warning") ||
        lowerDesc.contains("deprecated") ||
        lowerDesc.contains("should") ||
        lowerDesc.contains("might") ||
        lowerDesc.contains("potential") ||
        lowerDesc.contains("possible") ||
        lowerDesc.contains("consider") ||
        lowerDesc.contains("recommend") -> ErrorSeverity.WARNING

      // 弱警告关键词 - 代码质量和风格问题
      lowerDesc.contains("unused") ||
        lowerDesc.contains("never used") ||
        lowerDesc.contains("is never used") ||
        lowerDesc.contains("not used") ||
        lowerDesc.contains("redundant") ||
        lowerDesc.contains("unnecessary") ||
        lowerDesc.contains("can be") ||
        lowerDesc.contains("could be") ||
        lowerDesc.contains("typo") ||
        lowerDesc.contains("spelling") ||
        lowerDesc.contains("misspelled") ||
        lowerDesc.contains("simplify") ||
        lowerDesc.contains("replace") ||
        lowerDesc.contains("constant") ||
        lowerDesc.contains("literal") ||
        lowerDesc.contains("empty") ||
        lowerDesc.contains("blank") -> ErrorSeverity.WEAK_WARNING

      // 默认为信息
      else -> ErrorSeverity.INFO
    }
  }

  /** 从错误描述中提取错误代码 */
  private fun extractErrorCode(description: String): String {
    val lowerDesc = description.lowercase()
    return when {
      // 未使用代码相关
      lowerDesc.contains("unused variable") -> "unused-variable"
      lowerDesc.contains("unused parameter") -> "unused-parameter"
      lowerDesc.contains("unused import") -> "unused-import"
      lowerDesc.contains("unused function") -> "unused-function"
      lowerDesc.contains("unused property") -> "unused-property"
      lowerDesc.contains("unused") || lowerDesc.contains("never used") -> "unused-code"

      // 代码质量相关
      lowerDesc.contains("redundant") -> "redundant-code"
      lowerDesc.contains("unnecessary") -> "unnecessary-code"
      lowerDesc.contains("can be simplified") -> "simplify-code"
      lowerDesc.contains("can be replaced") -> "replace-code"

      // 拼写和语法相关
      lowerDesc.contains("typo") || lowerDesc.contains("spelling") || lowerDesc.contains("misspelled") -> "spelling-error"
      lowerDesc.contains("syntax error") -> "syntax-error"

      // 弃用相关
      lowerDesc.contains("deprecated") -> "deprecated"

      // 编译错误相关
      lowerDesc.contains("unresolved") -> "unresolved-reference"
      lowerDesc.contains("cannot resolve") -> "cannot-resolve"
      lowerDesc.contains("not found") -> "not-found"
      lowerDesc.contains("missing") -> "missing"

      // 通用分类
      lowerDesc.contains("warning") -> "warning"
      lowerDesc.contains("error") -> "error"
      else -> "code-issue"
    }
  }

  /** 从 IDE 代码分析器获取高亮错误信息 这可以捕获更多类型的警告，包括 unused 变量、拼写检查等 */
  private fun getHighlightErrors(project: Project, virtualFile: VirtualFile): List<ErrorInfo> {
    try {
      // 检查 PSI 文件是否存在
      PsiManager.getInstance(project).findFile(virtualFile) ?: return emptyList()
      val document = FileDocumentManager.getInstance().getDocument(virtualFile) ?: return emptyList()

      // 获取文件的高亮信息
      val highlightInfos =
        try {
          @Suppress("UnstableApiUsage") DaemonCodeAnalyzerImpl.getHighlights(document, null, project)
        } catch (e: Exception) {
          logger.debug("Failed to get highlight infos: {}", e.message)
          emptyList<HighlightInfo>()
        }

      return highlightInfos.mapNotNull { highlightInfo ->
        try {
          convertHighlightInfoToErrorInfo(highlightInfo, document)
        } catch (e: Exception) {
          logger.debug("Failed to convert highlight info: {}", e.message)
          null
        }
      }
    } catch (e: Exception) {
      logger.error("Failed to get highlight errors for file: {}", virtualFile.path, e)
      return emptyList()
    }
  }

  /** 将 HighlightInfo 转换为 ErrorInfo */
  private fun convertHighlightInfoToErrorInfo(highlightInfo: HighlightInfo, document: Document): ErrorInfo? {
    try {
      val startOffset = highlightInfo.startOffset
      val line = document.getLineNumber(startOffset) + 1
      val column = startOffset - document.getLineStartOffset(line - 1) + 1

      val description = highlightInfo.description ?: return null
      val severity = mapHighlightSeverityToErrorSeverity(highlightInfo)

      // 获取代码片段
      val endOffset = highlightInfo.endOffset
      val codeSnippet =
        try {
          document.getText(com.intellij.openapi.util.TextRange(startOffset, endOffset))
        } catch (_: Exception) {
          ""
        }

      return ErrorInfo(
        line = line,
        column = column,
        severity = severity,
        message = description,
        code = extractErrorCodeFromHighlightInfo(highlightInfo),
        codeSnippet = codeSnippet,
        // TODO: 可以后续添加快速修复信息
        quickFixes = emptyList(),
      )
    } catch (e: Exception) {
      logger.debug("Failed to convert HighlightInfo: {}", e.message)
      return null
    }
  }

  /** 将 IDE 的严重程度映射到我们的错误严重程度 */
  private fun mapHighlightSeverityToErrorSeverity(highlightInfo: HighlightInfo): ErrorSeverity {
    val severity = highlightInfo.severity
    val description = highlightInfo.description?.lowercase() ?: ""

    // 首先根据 IDE 的严重程度级别判断
    return when {
      severity.name.contains("ERROR", ignoreCase = true) -> ErrorSeverity.ERROR
      severity.name.contains("WARNING", ignoreCase = true) -> ErrorSeverity.WARNING
      severity.name.contains("WEAK_WARNING", ignoreCase = true) -> ErrorSeverity.WEAK_WARNING
      severity.name.contains("INFO", ignoreCase = true) -> ErrorSeverity.INFO

      // 根据描述进一步细化判断
      // 错误级别
      description.contains("error") ||
        description.contains("cannot") ||
        description.contains("unresolved") ||
        description.contains("undefined") ||
        description.contains("not found") ||
        description.contains("compilation") ||
        description.contains("missing") ||
        description.contains("invalid") ||
        description.contains("illegal") -> ErrorSeverity.ERROR

      // 警告级别
      description.contains("deprecated") ||
        description.contains("should") ||
        description.contains("might") ||
        description.contains("potential") ||
        description.contains("possible") ||
        description.contains("consider") ||
        description.contains("recommend") -> ErrorSeverity.WARNING

      // 弱警告级别 - 主要是代码质量和未使用的代码
      description.contains("unused") ||
        description.contains("never used") ||
        description.contains("not used") ||
        description.contains("redundant") ||
        description.contains("unnecessary") ||
        description.contains("can be") ||
        description.contains("could be") ||
        description.contains("typo") ||
        description.contains("spelling") ||
        description.contains("misspelled") ||
        description.contains("simplify") ||
        description.contains("replace") ||
        description.contains("constant") ||
        description.contains("literal") ||
        description.contains("empty") ||
        description.contains("blank") -> ErrorSeverity.WEAK_WARNING

      else -> ErrorSeverity.WEAK_WARNING
    }
  }

  /** 从 HighlightInfo 中提取错误代码 */
  private fun extractErrorCodeFromHighlightInfo(highlightInfo: HighlightInfo): String {
    val description = highlightInfo.description?.lowercase() ?: ""
    val inspectionTool = highlightInfo.inspectionToolId

    // 优先使用 IDE 的检查工具 ID，这更准确
    if (inspectionTool != null && inspectionTool.isNotBlank()) {
      return inspectionTool
    }

    // 根据描述内容进行详细分类
    return when {
      // 未使用代码相关
      description.contains("unused variable") -> "unused-variable"
      description.contains("unused parameter") -> "unused-parameter"
      description.contains("unused import") -> "unused-import"
      description.contains("unused function") -> "unused-function"
      description.contains("unused property") -> "unused-property"
      description.contains("unused") || description.contains("never used") -> "unused-code"

      // 代码质量相关
      description.contains("redundant") -> "redundant-code"
      description.contains("unnecessary") -> "unnecessary-code"
      description.contains("can be simplified") -> "simplify-code"
      description.contains("can be replaced") -> "replace-code"
      description.contains("can be") || description.contains("could be") -> "can-improve"

      // 拼写和语法相关
      description.contains("typo") || description.contains("spelling") || description.contains("misspelled") -> "spelling-error"
      description.contains("syntax error") -> "syntax-error"

      // 弃用相关
      description.contains("deprecated") -> "deprecated"

      // 编译错误相关
      description.contains("unresolved") -> "unresolved-reference"
      description.contains("cannot resolve") -> "cannot-resolve"
      description.contains("not found") -> "not-found"
      description.contains("missing") -> "missing"
      description.contains("compilation") -> "compilation-error"

      // 代码风格相关
      description.contains("constant") -> "use-constant"
      description.contains("literal") -> "literal-issue"
      description.contains("empty") -> "empty-code"
      description.contains("blank") -> "blank-code"

      // 通用分类
      description.contains("warning") -> "warning"
      description.contains("error") -> "error"
      else -> "highlight-issue"
    }
  }

  /** 去重和排序错误信息 */
  private fun deduplicateAndSortErrors(errors: List<ErrorInfo>): List<ErrorInfo> {
    val originalCount = errors.size
    val deduplicated =
      errors.distinctBy { "${it.line}:${it.column}:${it.message.take(100)}" }.sortedWith(compareBy({ it.line }, { it.column }, { it.severity.ordinal }))

    if (originalCount != deduplicated.size) {
      logger.debug("Deduplicated {} errors to {} unique errors", originalCount, deduplicated.size)
    }

    return deduplicated
  }

  /** 递归收集目录中的错误信息 */
  private fun collectErrorsFromDirectory(project: Project, directory: VirtualFile, result: MutableList<FileErrorInfo>) {
    if (!directory.isValid || !directory.isDirectory) {
      return
    }

    try {
      directory.children?.forEach { child ->
        if (child.isDirectory) {
          collectErrorsFromDirectory(project, child, result)
        } else {
          val fileErrors = analyzeFile(project, child)
          if (fileErrors.isNotEmpty()) {
            result.add(createFileErrorInfo(project, child, fileErrors))
          }
        }
      }
    } catch (e: Exception) {
      logger.error("Directory scanning failed: {}", directory.path, e)
    }
  }

  /** 创建文件错误信息对象 */
  private fun createFileErrorInfo(project: Project, virtualFile: VirtualFile, errors: List<ErrorInfo>): FileErrorInfo {
    val relativePath = getRelativePath(project, virtualFile)

    val errorList = errors.filter { it.severity == ErrorSeverity.ERROR }
    val warningList = errors.filter { it.severity == ErrorSeverity.WARNING }
    val weakWarningList = errors.filter { it.severity == ErrorSeverity.WEAK_WARNING }
    val infoList = errors.filter { it.severity == ErrorSeverity.INFO }

    val combinedWeakWarnings = weakWarningList + infoList

    val summary = buildString {
      if (errorList.isNotEmpty()) append("${errorList.size}个错误")
      if (warningList.isNotEmpty()) {
        if (isNotEmpty()) append(", ")
        append("${warningList.size}个警告")
      }
      if (combinedWeakWarnings.isNotEmpty()) {
        if (isNotEmpty()) append(", ")
        append("${combinedWeakWarnings.size}个弱警告")
      }
    }

    return FileErrorInfo(
      filePath = virtualFile.path,
      relativePath = relativePath,
      errors = errorList,
      warnings = warningList,
      weakWarnings = combinedWeakWarnings,
      summary = summary,
    )
  }

  /** 获取相对路径 */
  private fun getRelativePath(project: Project, virtualFile: VirtualFile): String {
    val basePath = project.basePath ?: return virtualFile.path
    val filePath = virtualFile.path

    return if (filePath.startsWith(basePath)) {
      filePath.substring(basePath.length).removePrefix("/").removePrefix("\\")
    } else {
      virtualFile.name
    }
  }
}

/** 错误捕获过滤器管理器 - 单例模式管理过滤器实例 */
object ErrorCaptureFilterManager {
  @Volatile private var filterInstance: ErrorCaptureFilter? = null

  fun getInstance(): ErrorCaptureFilter {
    return filterInstance ?: synchronized(this) { filterInstance ?: ErrorCaptureFilter().also { filterInstance = it } }
  }

  fun setInstance(filter: ErrorCaptureFilter) {
    synchronized(this) { filterInstance = filter }
  }
}

/**
 * 错误捕获过滤器 - 实现 HighlightErrorFilter 接口来捕获所有语法错误和警告
 *
 * 根据 JetBrains 文档：https://plugins.jetbrains.com/docs/intellij/syntax-errors.html#controlling-syntax-errors-highlighting 这个过滤器可以控制哪些 PsiErrorElement
 * 应该被高亮显示，并同时捕获它们用于后续分析
 *
 * @see com.intellij.codeInsight.highlighting.HighlightErrorFilter
 */
class ErrorCaptureFilter : HighlightErrorFilter() {

  private val logger = LoggerFactory.getLogger(ErrorCaptureFilter::class.java)

  // 存储捕获的错误信息，按文件路径分组
  private val capturedErrors = mutableMapOf<String, MutableList<CapturedErrorInfo>>()

  init {
    // 注册到管理器
    ErrorCaptureFilterManager.setInstance(this)
    logger.info("ErrorCaptureFilter initialized and registered")
  }

  /**
   * 决定是否应该高亮显示给定的 PsiErrorElement
   *
   * @param element 要检查的 PSI 错误元素
   * @return true 如果应该高亮显示，false 如果应该忽略
   */
  override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean {
    try {
      // 捕获错误信息
      captureErrorElement(element)

      // 返回 true 表示继续正常的高亮显示流程
      // 如果需要抑制某些错误的显示，可以在这里添加条件判断
      return shouldShowError(element)
    } catch (e: Exception) {
      logger.error("Error in ErrorCaptureFilter.shouldHighlightErrorElement", e)
      return true // 出错时默认显示错误
    }
  }

  /** 捕获错误元素的详细信息 */
  private fun captureErrorElement(element: PsiErrorElement) {
    try {
      val containingFile = element.containingFile
      if (containingFile?.virtualFile == null) {
        return
      }

      val filePath = containingFile.virtualFile.path
      val document = PsiDocumentManager.getInstance(containingFile.project).getDocument(containingFile)

      if (document != null) {
        val textOffset = element.textOffset
        val line = document.getLineNumber(textOffset) + 1
        val column = textOffset - document.getLineStartOffset(line - 1) + 1

        val errorInfo =
          CapturedErrorInfo(
            filePath = filePath,
            line = line,
            column = column,
            errorDescription = element.errorDescription,
            elementText = element.text,
            timestamp = System.currentTimeMillis(),
          )

        // 存储错误信息
        synchronized(capturedErrors) { capturedErrors.computeIfAbsent(filePath) { mutableListOf() }.add(errorInfo) }

        logger.debug("Captured syntax error in {}: {} at line {}, column {}", containingFile.name, element.errorDescription, line, column)
      }
    } catch (e: Exception) {
      logger.debug("Failed to capture error element: {}", e.message)
    }
  }

  /** 决定是否应该显示特定的错误 可以在这里添加自定义的过滤逻辑 */
  private fun shouldShowError(element: PsiErrorElement): Boolean {
    try {
      val errorDescription = element.errorDescription
      val containingFile = element.containingFile

      // 示例过滤规则：

      // 1. 在 Markdown 代码块中忽略语法错误
      if (containingFile?.name?.endsWith(".md") == true) {
        // 可以添加更复杂的逻辑来检查是否在代码块中
        logger.debug("Considering suppression of error in Markdown file: {}", errorDescription)
      }

      // 2. 忽略特定类型的错误
      if (errorDescription.contains("incomplete", ignoreCase = true) && isInTestContext(containingFile)) {
        logger.debug("Suppressing incomplete code error in test context: {}", errorDescription)
        return false
      }

      // 3. 在注释中的代码片段忽略错误
      if (isInCommentContext(element)) {
        logger.debug("Suppressing error in comment context: {}", errorDescription)
        return false
      }

      // 默认显示所有错误
      return true
    } catch (e: Exception) {
      logger.debug("Error in shouldShowError: {}", e.message)
      return true
    }
  }

  /** 检查是否在测试上下文中 */
  private fun isInTestContext(file: PsiFile?): Boolean {
    return file?.virtualFile?.path?.contains("/test/") == true || file?.virtualFile?.path?.contains("/tests/") == true || file?.name?.contains("Test") == true
  }

  /** 检查是否在注释上下文中 */
  private fun isInCommentContext(element: PsiErrorElement): Boolean {
    try {
      var parent = element.parent
      while (parent != null) {
        val elementType = parent.node?.elementType?.toString()
        if (elementType?.contains("COMMENT", ignoreCase = true) == true) {
          return true
        }
        parent = parent.parent
      }
      return false
    } catch (_: Exception) {
      return false
    }
  }

  /** 获取指定文件的捕获错误信息 */
  fun getCapturedErrors(filePath: String): List<CapturedErrorInfo> {
    return synchronized(capturedErrors) { capturedErrors[filePath]?.toList() ?: emptyList() }
  }

  /** 获取所有捕获的错误信息 */
  fun getAllCapturedErrors(): Map<String, List<CapturedErrorInfo>> {
    return synchronized(capturedErrors) { capturedErrors.mapValues { it.value.toList() } }
  }

  /** 清除指定文件的捕获错误信息 */
  fun clearCapturedErrors(filePath: String) {
    synchronized(capturedErrors) { capturedErrors.remove(filePath) }
  }

  /** 清除所有捕获的错误信息 */
  fun clearAllCapturedErrors() {
    synchronized(capturedErrors) { capturedErrors.clear() }
  }
}

/**
 * 捕获的错误信息数据类
 *
 * 用于存储从错误捕获过滤器中获取的错误信息
 */
data class CapturedErrorInfo(val filePath: String, val line: Int, val column: Int, val errorDescription: String, val elementText: String, val timestamp: Long)
