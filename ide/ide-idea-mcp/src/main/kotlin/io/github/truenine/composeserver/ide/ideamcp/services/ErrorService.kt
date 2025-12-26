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
import com.intellij.psi.*
import io.github.truenine.composeserver.ide.ideamcp.tools.*
import org.slf4j.LoggerFactory

/**
 * Error service interface that provides error information from the error capture filter.
 *
 * Uses the HighlightErrorFilter mechanism to capture errors and warnings in the project.
 */
interface ErrorService {
  /** Collects all error information for the given path. */
  fun collectErrors(project: Project, virtualFile: VirtualFile): List<FileErrorInfo>

  /** Analyzes error information for a single file. */
  fun analyzeFile(project: Project, virtualFile: VirtualFile): List<ErrorInfo>

  /** Gets captured syntax errors from the error capture filter. */
  fun getCapturedSyntaxErrors(project: Project, virtualFile: VirtualFile): List<ErrorInfo>
}

/**
 * Error service implementation that uses HighlightErrorFilter to capture all errors and warnings.
 *
 * Retrieves actual syntax errors, compilation errors, and warnings through the error capture filter.
 */
@Service(Service.Level.PROJECT)
class ErrorServiceImpl : ErrorService {

  private val logger = LoggerFactory.getLogger(ErrorServiceImpl::class.java)

  override fun collectErrors(project: Project, virtualFile: VirtualFile): List<FileErrorInfo> {
    logger.info("Collecting errors from HighlightErrorFilter - path: {}", virtualFile.path)

    val result = mutableListOf<FileErrorInfo>()

    try {
      if (virtualFile.isDirectory) {
        // Recursively process directories
        collectErrorsFromDirectory(project, virtualFile, result)
      } else {
        // Process a single file
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

    // Check if the Application is available (may be null in test environment)
    val application = com.intellij.openapi.application.ApplicationManager.getApplication()
    if (application == null) {
      logger.debug("Application not available, returning empty list")
      return emptyList()
    }

    return ReadAction.compute<List<ErrorInfo>, Exception> {
      try {
        val allErrors = mutableListOf<ErrorInfo>()

        // 1. Get syntax errors from the error capture filter
        val capturedErrors = getCapturedSyntaxErrors(project, virtualFile)
        allErrors.addAll(capturedErrors)
        logger.debug("Found {} syntax errors from ErrorCaptureFilter", capturedErrors.size)

        // 2. Get all types of warnings and errors from the IDE code analyzer
        val highlightErrors = getHighlightErrors(project, virtualFile)
        allErrors.addAll(highlightErrors)
        logger.debug("Found {} highlight errors from DaemonCodeAnalyzer", highlightErrors.size)

        // Remove duplicates and sort errors
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
      // Get the error capture filter instance
      val errorFilter = ErrorCaptureFilterManager.getInstance()
      logger.debug("ErrorCaptureFilter instance: {}", errorFilter)

      // Get captured errors from the filter
      val capturedErrors = errorFilter.getCapturedErrors(virtualFile.path)
      logger.debug("Found {} captured syntax errors for {}", capturedErrors.size, virtualFile.name)

      // Convert to ErrorInfo format
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

  /** Determines error severity from the error description. */
  private fun determineSeverityFromDescription(description: String): ErrorSeverity {
    val lowerDesc = description.lowercase()

    return when {
      // Error keywords - issues that affect compilation or execution
      lowerDesc.contains("error") ||
        lowerDesc.contains("cannot") ||
        lowerDesc.contains("unresolved") ||
        lowerDesc.contains("undefined") ||
        lowerDesc.contains("not found") ||
        lowerDesc.contains("compilation") ||
        lowerDesc.contains("missing") ||
        lowerDesc.contains("invalid") ||
        lowerDesc.contains("illegal") -> ErrorSeverity.ERROR

      // Warning keywords - code that may cause problems
      lowerDesc.contains("warning") ||
        lowerDesc.contains("deprecated") ||
        lowerDesc.contains("should") ||
        lowerDesc.contains("might") ||
        lowerDesc.contains("potential") ||
        lowerDesc.contains("possible") ||
        lowerDesc.contains("consider") ||
        lowerDesc.contains("recommend") -> ErrorSeverity.WARNING

      // Weak warning keywords - code quality and style issues
      lowerDesc.contains("unused") ||
        lowerDesc.contains("never used") ||
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

      // Default to informational
      else -> ErrorSeverity.INFO
    }
  }

  /** Extracts an error code from the error description. */
  private fun extractErrorCode(description: String): String {
    val lowerDesc = description.lowercase()
    return when {
      // Unused code related
      lowerDesc.contains("unused variable") -> "unused-variable"
      lowerDesc.contains("unused parameter") -> "unused-parameter"
      lowerDesc.contains("unused import") -> "unused-import"
      lowerDesc.contains("unused function") -> "unused-function"
      lowerDesc.contains("unused property") -> "unused-property"
      lowerDesc.contains("unused") || lowerDesc.contains("never used") -> "unused-code"

      // Code quality related
      lowerDesc.contains("redundant") -> "redundant-code"
      lowerDesc.contains("unnecessary") -> "unnecessary-code"
      lowerDesc.contains("can be simplified") -> "simplify-code"
      lowerDesc.contains("can be replaced") -> "replace-code"

      // Spelling and syntax related
      lowerDesc.contains("typo") || lowerDesc.contains("spelling") || lowerDesc.contains("misspelled") -> "spelling-error"
      lowerDesc.contains("syntax error") -> "syntax-error"

      // Deprecation related
      lowerDesc.contains("deprecated") -> "deprecated"

      // Compilation error related
      lowerDesc.contains("unresolved") -> "unresolved-reference"
      lowerDesc.contains("cannot resolve") -> "cannot-resolve"
      lowerDesc.contains("not found") -> "not-found"
      lowerDesc.contains("missing") -> "missing"

      // Generic classification
      lowerDesc.contains("warning") -> "warning"
      lowerDesc.contains("error") -> "error"
      else -> "code-issue"
    }
  }

  /** Gets highlighted error information from the IDE analyzer (including unused code and spelling checks). */
  private fun getHighlightErrors(project: Project, virtualFile: VirtualFile): List<ErrorInfo> {
    try {
      // Check if the PSI file exists
      PsiManager.getInstance(project).findFile(virtualFile) ?: return emptyList()
      val document = FileDocumentManager.getInstance().getDocument(virtualFile) ?: return emptyList()

      // Get file highlights
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

  /** Converts HighlightInfo to ErrorInfo. */
  private fun convertHighlightInfoToErrorInfo(highlightInfo: HighlightInfo, document: Document): ErrorInfo? {
    try {
      val startOffset = highlightInfo.startOffset
      val line = document.getLineNumber(startOffset) + 1
      val column = startOffset - document.getLineStartOffset(line - 1) + 1

      val description = highlightInfo.description ?: return null
      val severity = mapHighlightSeverityToErrorSeverity(highlightInfo)

      // Get code snippet
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
        // TODO: Quick-fix information can be added later
        quickFixes = emptyList(),
      )
    } catch (e: Exception) {
      logger.debug("Failed to convert HighlightInfo: {}", e.message)
      return null
    }
  }

  /** Maps IDE highlight severity to our ErrorSeverity. */
  private fun mapHighlightSeverityToErrorSeverity(highlightInfo: HighlightInfo): ErrorSeverity {
    val severity = highlightInfo.severity
    val description = highlightInfo.description?.lowercase() ?: ""

    // First, use the IDE severity level
    return when {
      severity.name.contains("ERROR", ignoreCase = true) -> ErrorSeverity.ERROR
      severity.name.contains("WARNING", ignoreCase = true) -> ErrorSeverity.WARNING
      severity.name.contains("WEAK_WARNING", ignoreCase = true) -> ErrorSeverity.WEAK_WARNING
      severity.name.contains("INFO", ignoreCase = true) -> ErrorSeverity.INFO

      // Further refine based on description
      // Error level
      description.contains("error") ||
        description.contains("cannot") ||
        description.contains("unresolved") ||
        description.contains("undefined") ||
        description.contains("not found") ||
        description.contains("compilation") ||
        description.contains("missing") ||
        description.contains("invalid") ||
        description.contains("illegal") -> ErrorSeverity.ERROR

      // Warning level
      description.contains("deprecated") ||
        description.contains("should") ||
        description.contains("might") ||
        description.contains("potential") ||
        description.contains("possible") ||
        description.contains("consider") ||
        description.contains("recommend") -> ErrorSeverity.WARNING

      // Weak warning level - mostly code quality and unused code
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

  /** Extracts an error code from HighlightInfo. */
  private fun extractErrorCodeFromHighlightInfo(highlightInfo: HighlightInfo): String {
    val description = highlightInfo.description?.lowercase() ?: ""
    val inspectionTool = highlightInfo.inspectionToolId

    // Prefer to use the IDE inspection tool ID when available
    if (inspectionTool != null && inspectionTool.isNotBlank()) {
      return inspectionTool
    }

    // Classify based on description content
    return when {
      // Unused code related
      description.contains("unused variable") -> "unused-variable"
      description.contains("unused parameter") -> "unused-parameter"
      description.contains("unused import") -> "unused-import"
      description.contains("unused function") -> "unused-function"
      description.contains("unused property") -> "unused-property"
      description.contains("unused") || description.contains("never used") -> "unused-code"

      // Code quality related
      description.contains("redundant") -> "redundant-code"
      description.contains("unnecessary") -> "unnecessary-code"
      description.contains("can be simplified") -> "simplify-code"
      description.contains("can be replaced") -> "replace-code"
      description.contains("can be") || description.contains("could be") -> "can-improve"

      // Spelling and syntax related
      description.contains("typo") || description.contains("spelling") || description.contains("misspelled") -> "spelling-error"
      description.contains("syntax error") -> "syntax-error"

      // Deprecation related
      description.contains("deprecated") -> "deprecated"

      // Compilation error related
      description.contains("unresolved") -> "unresolved-reference"
      description.contains("cannot resolve") -> "cannot-resolve"
      description.contains("not found") -> "not-found"
      description.contains("missing") -> "missing"
      description.contains("compilation") -> "compilation-error"

      // Code style related
      description.contains("constant") -> "use-constant"
      description.contains("literal") -> "literal-issue"
      description.contains("empty") -> "empty-code"
      description.contains("blank") -> "blank-code"

      // Generic classification
      description.contains("warning") -> "warning"
      description.contains("error") -> "error"
      else -> "highlight-issue"
    }
  }

  /** Removes duplicates and sorts error information. */
  private fun deduplicateAndSortErrors(errors: List<ErrorInfo>): List<ErrorInfo> {
    val originalCount = errors.size
    val deduplicated =
      errors.distinctBy { "${it.line}:${it.column}:${it.message.take(100)}" }.sortedWith(compareBy({ it.line }, { it.column }, { it.severity.ordinal }))

    if (originalCount != deduplicated.size) {
      logger.debug("Deduplicated {} errors to {} unique errors", originalCount, deduplicated.size)
    }

    return deduplicated
  }

  /** Recursively collects error information from a directory. */
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

  /** Creates a FileErrorInfo instance for a given file. */
  private fun createFileErrorInfo(project: Project, virtualFile: VirtualFile, errors: List<ErrorInfo>): FileErrorInfo {
    val relativePath = getRelativePath(project, virtualFile)

    val errorList = errors.filter { it.severity == ErrorSeverity.ERROR }
    val warningList = errors.filter { it.severity == ErrorSeverity.WARNING }
    val weakWarningList = errors.filter { it.severity == ErrorSeverity.WEAK_WARNING }
    val infoList = errors.filter { it.severity == ErrorSeverity.INFO }

    val combinedWeakWarnings = weakWarningList + infoList

    val summary = buildString {
      if (errorList.isNotEmpty()) append("${errorList.size} errors")
      if (warningList.isNotEmpty()) {
        if (isNotEmpty()) append(", ")
        append("${warningList.size} warnings")
      }
      if (combinedWeakWarnings.isNotEmpty()) {
        if (isNotEmpty()) append(", ")
        append("${combinedWeakWarnings.size} weak warnings")
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

  /** Gets the relative path of a file within the project. */
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

/** Error capture filter manager - singleton that manages the filter instance. */
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
 * Error capture filter implementation based on HighlightErrorFilter.
 *
 * According to JetBrains documentation: https://plugins.jetbrains.com/docs/intellij/syntax-errors.html#controlling-syntax-errors-highlighting this filter
 * controls which PsiErrorElement instances should be highlighted and records them for analysis.
 *
 * @see com.intellij.codeInsight.highlighting.HighlightErrorFilter
 */
class ErrorCaptureFilter : HighlightErrorFilter() {

  private val logger = LoggerFactory.getLogger(ErrorCaptureFilter::class.java)

  // Stores captured error information grouped by file path
  private val capturedErrors = mutableMapOf<String, MutableList<CapturedErrorInfo>>()

  init {
    // Register in the manager
    ErrorCaptureFilterManager.setInstance(this)
    logger.info("ErrorCaptureFilter initialized and registered")
  }

  /**
   * Decides whether a given PsiErrorElement should be highlighted.
   *
   * @param element PSI error element to check
   * @return true if the error should be highlighted, false if it should be ignored
   */
  override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean {
    try {
      // Capture error information
      captureErrorElement(element)

      // Return true to continue the normal highlighting flow
      // If some errors should be suppressed, add conditions here
      return shouldShowError(element)
    } catch (e: Exception) {
      logger.error("Error in ErrorCaptureFilter.shouldHighlightErrorElement", e)
      return true // Default to showing the error when something goes wrong
    }
  }

  /** Captures detailed information for a PsiErrorElement. */
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

        // Store the error information
        synchronized(capturedErrors) { capturedErrors.computeIfAbsent(filePath) { mutableListOf() }.add(errorInfo) }

        logger.debug("Captured syntax error in {}: {} at line {}, column {}", containingFile.name, element.errorDescription, line, column)
      }
    } catch (e: Exception) {
      logger.debug("Failed to capture error element: {}", e.message)
    }
  }

  /** Decides whether a specific error should be shown; custom filtering logic can be added here. */
  private fun shouldShowError(element: PsiErrorElement): Boolean {
    try {
      val errorDescription = element.errorDescription
      val containingFile = element.containingFile

      // Example filtering rules:

      // 1. Ignore syntax errors in Markdown code blocks
      if (containingFile?.name?.endsWith(".md") == true) {
        // Can add more complex logic to check if it's in a code block
        logger.debug("Considering suppression of error in Markdown file: {}", errorDescription)
      }

      // 2. Ignore specific types of errors
      if (errorDescription.contains("incomplete", ignoreCase = true) && isInTestContext(containingFile)) {
        logger.debug("Suppressing incomplete code error in test context: {}", errorDescription)
        return false
      }

      // 3. Ignore errors inside comment code fragments
      if (isInCommentContext(element)) {
        logger.debug("Suppressing error in comment context: {}", errorDescription)
        return false
      }

      // Show all other errors by default
      return true
    } catch (e: Exception) {
      logger.debug("Error in shouldShowError: {}", e.message)
      return true
    }
  }

  /** Checks whether the file is in a test context. */
  private fun isInTestContext(file: PsiFile?): Boolean {
    return file?.virtualFile?.path?.contains("/test/") == true || file?.virtualFile?.path?.contains("/tests/") == true || file?.name?.contains("Test") == true
  }

  /** Checks whether the error element is in a comment context. */
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

  /** Gets captured errors for a specific file. */
  fun getCapturedErrors(filePath: String): List<CapturedErrorInfo> {
    return synchronized(capturedErrors) { capturedErrors[filePath]?.toList() ?: emptyList() }
  }

  /** Gets all captured errors for all files. */
  fun getAllCapturedErrors(): Map<String, List<CapturedErrorInfo>> {
    return synchronized(capturedErrors) { capturedErrors.mapValues { it.value.toList() } }
  }

  /** Clears captured errors for a specific file. */
  fun clearCapturedErrors(filePath: String) {
    synchronized(capturedErrors) { capturedErrors.remove(filePath) }
  }

  /** Clears all captured errors. */
  fun clearAllCapturedErrors() {
    synchronized(capturedErrors) { capturedErrors.clear() }
  }
}

/** Data class representing captured error information from the error capture filter. */
data class CapturedErrorInfo(val filePath: String, val line: Int, val column: Int, val errorDescription: String, val elementText: String, val timestamp: Long)
