package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.vfs.VirtualFile
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

/** Result of library code lookup. */
data class LibCodeResult(val sourceCode: String, val isDecompiled: Boolean, val language: String, val metadata: LibCodeMetadata)

/** Metadata for library code. */
data class LibCodeMetadata(val libraryName: String, val version: String?, val sourceType: SourceType, val documentation: String?)

/** Service interface for library code lookup. */
interface LibCodeService {
  /** Get library code for a fully-qualified class name and optional member. */
  suspend fun getLibraryCode(project: Project, fullyQualifiedName: String, memberName: String? = null): LibCodeResult
}

/** Project-level implementation of LibCodeService. */
@Service(Service.Level.PROJECT)
class LibCodeServiceImpl : LibCodeService {

  override suspend fun getLibraryCode(project: Project, fullyQualifiedName: String, memberName: String?): LibCodeResult {
    Logger.info("Start searching library code - class: $fullyQualifiedName, member: ${memberName ?: "all"}", "LibCodeService")

    return withContext(Dispatchers.IO) {
      // 1. Try to extract source from source JAR
      val sourceResult = tryExtractFromSourceJar(project, fullyQualifiedName, memberName)
      if (sourceResult != null) {
        Logger.info("Successfully extracted source from source JAR", "LibCodeService")
        return@withContext sourceResult
      }

      // 2. Return not-found result (skip decompilation for now to avoid API compatibility issues)
      Logger.info("Source not found, returning default result", "LibCodeService")
      createNotFoundResult(fullyQualifiedName)
    }
  }

  /** Try to extract source from a source JAR. */
  private fun tryExtractFromSourceJar(project: Project, fullyQualifiedName: String, memberName: String?): LibCodeResult? {
    try {
      Logger.debug("Attempting to extract source from source JAR: $fullyQualifiedName", "LibCodeService")

      val sourceFiles = findSourceFiles(project, fullyQualifiedName)
      Logger.debug("Found ${sourceFiles.size} source files", "LibCodeService")

      // Typically a single file is found; take the first valid one as a safeguard
      return sourceFiles.firstNotNullOfOrNull { sourceFile ->
        val sourceCode = sourceFile.inputStream.use { readInputStream(it) }
        val processedCode =
          if (memberName != null) {
            extractMemberFromSourceCode(sourceCode, memberName)
          } else {
            sourceCode
          }

        val libraryInfo = extractLibraryInfoFromSourceFile(sourceFile)
        Logger.info(
          "Successfully extracted source - class: $fullyQualifiedName, library: ${libraryInfo.first}, version: ${libraryInfo.second}",
          "LibCodeService",
        )
        Logger.debug("Source length: ${sourceCode.length} characters", "LibCodeService")

        LibCodeResult(
          sourceCode = processedCode,
          isDecompiled = false,
          language = determineLanguageFromSourceCode(sourceCode),
          metadata = LibCodeMetadata(libraryName = libraryInfo.first, version = libraryInfo.second, sourceType = SourceType.SOURCE_JAR, documentation = null),
        )
      }
    } catch (e: Exception) {
      Logger.debug("Extraction from source JAR failed: ${e.message}", "LibCodeService")
    }

    return null
  }

  /** Find related source files for the given fully-qualified class name. */
  private fun findSourceFiles(project: Project, fullyQualifiedName: String): List<VirtualFile> {
    try {
      Logger.info("Start searching for source of class $fullyQualifiedName", "LibCodeService")
      val classPath = fullyQualifiedName.replace('.', '/')
      val javaPath = "$classPath.java"
      val kotlinPath = "$classPath.kt"

      // Use sourceRoots to directly obtain source roots; more accurate than classesRoots
      val sourceRoots = OrderEnumerator.orderEntries(project).librariesOnly().sourceRoots
      Logger.debug("Found ${sourceRoots.size} source roots", "LibCodeService")

      return sourceRoots.mapNotNull { root -> root.findFileByRelativePath(javaPath) ?: root.findFileByRelativePath(kotlinPath) }
    } catch (e: Exception) {
      Logger.debug("Failed to search source JAR: ${e.message}", "LibCodeService")
      return emptyList()
    }
  }

  /** Create result for class whose source cannot be found. */
  private fun createNotFoundResult(fullyQualifiedName: String): LibCodeResult {
    return LibCodeResult(
      sourceCode = "// Class not found: $fullyQualifiedName\n// Please check the class name and ensure the library is on the project classpath",
      isDecompiled = false,
      language = "text",
      metadata =
        LibCodeMetadata(
          libraryName = extractLibraryNameFromClassName(fullyQualifiedName),
          version = null,
          sourceType = SourceType.NOT_FOUND,
          documentation = null,
        ),
    )
  }

  /** Extract specific member source from the full source code. */
  private fun extractMemberFromSourceCode(sourceCode: String, memberName: String): String {
    // Simplified implementation: search for lines containing the member name
    val lines = sourceCode.lines()
    val relevantLines =
      lines.filter { line ->
        line.contains(memberName) &&
          (line.contains("fun ") ||
            line.contains("val ") ||
            line.contains("var ") ||
            line.contains("def ") ||
            line.contains("public ") ||
            line.contains("private "))
      }

    return if (relevantLines.isNotEmpty()) {
      "// Extracted member: $memberName\n\n" + relevantLines.joinToString("\n")
    } else {
      "// Member $memberName not found, returning full class source\n\n$sourceCode"
    }
  }

  /** Extract library name and version from the source file path. */
  private fun extractLibraryInfoFromSourceFile(sourceFile: VirtualFile): Pair<String, String?> {
    // Path is usually .../caches/modules-2/files-2.1/group/artifact/version/.../artifact-version-sources.jar!/path/to/class
    val path = sourceFile.path
    val regex = """.*/caches/modules-2/files-2.1/([^/]+)/([^/]+)/([^/]+)/.*""".toRegex()
    val match = regex.find(path)

    if (match != null) {
      val group = match.groupValues[1]
      val artifact = match.groupValues[2]
      val version = match.groupValues[3]
      return "$group:$artifact" to version
    }

    // Fallback for non-standard paths
    val jarPath = path.substringBefore("!/")
    val fileName = jarPath.substringAfterLast('/')
    val libraryName = fileName.replace("-sources.jar", "").replace(".jar", "")
    val versionRegex = """(.+)-(\d+\.\d+(?:\.\d+.*)?)""".toRegex()
    val matchResult = versionRegex.find(libraryName)

    return if (matchResult != null) {
      val (name, version) = matchResult.destructured
      name to version
    } else {
      libraryName to null
    }
  }

  /** Determine language from the source code contents. */
  private fun determineLanguageFromSourceCode(sourceCode: String): String {
    return when {
      sourceCode.contains("package ") && sourceCode.contains("fun ") -> "kotlin"
      sourceCode.contains("package ") && sourceCode.contains("class ") -> "java"
      sourceCode.contains("object ") || sourceCode.contains("trait ") -> "scala"
      sourceCode.contains("def ") && sourceCode.contains("class ") -> "groovy"
      else -> "java"
    }
  }

  /** Extract library name guess from a fully-qualified class name. */
  private fun extractLibraryNameFromClassName(fullyQualifiedName: String): String {
    val parts = fullyQualifiedName.split('.')
    return when {
      parts.size >= 3 -> "${parts[0]}.${parts[1]}.${parts[2]}"
      parts.size >= 2 -> "${parts[0]}.${parts[1]}"
      else -> parts[0]
    }
  }

  /** Read entire input stream into a UTF-8 string. */
  private fun readInputStream(inputStream: InputStream): String {
    val buffer = ByteArrayOutputStream()
    val data = ByteArray(1024)
    var bytesRead: Int

    while (inputStream.read(data, 0, data.size).also { bytesRead = it } != -1) {
      buffer.write(data, 0, bytesRead)
    }

    return buffer.toString("UTF-8")
  }
}
