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

/** 库代码结果 */
data class LibCodeResult(
  val sourceCode: String,
  val isDecompiled: Boolean,
  val language: String,
  val metadata: LibCodeMetadata,
)

/** 库代码元数据 */
data class LibCodeMetadata(
  val libraryName: String,
  val version: String?,
  val sourceType: SourceType,
  val documentation: String?,
)

/** 库代码服务接口 */
interface LibCodeService {
  /** 获取库代码 */
  suspend fun getLibraryCode(project: Project, fullyQualifiedName: String, memberName: String? = null): LibCodeResult
}

/** 库代码服务实现 */
@Service(Service.Level.PROJECT)
class LibCodeServiceImpl : LibCodeService {

  override suspend fun getLibraryCode(project: Project, fullyQualifiedName: String, memberName: String?): LibCodeResult {
    Logger.info("开始查找库代码 - 类: $fullyQualifiedName, 成员: ${memberName ?: "全部"}", "LibCodeService")

    return withContext(Dispatchers.IO) {
      // 1. 尝试从 source jar 提取源码
      val sourceResult = tryExtractFromSourceJar(project, fullyQualifiedName, memberName)
      if (sourceResult != null) {
        Logger.info("成功从 source jar 提取源码", "LibCodeService")
        return@withContext sourceResult
      }

      // 2. 返回未找到结果（暂时跳过反编译功能以避免 API 兼容性问题）
      Logger.info("未找到源码，返回默认结果", "LibCodeService")
      createNotFoundResult(fullyQualifiedName)
    }
  }

  /** 尝试从 source jar 提取源码 */
  private fun tryExtractFromSourceJar(project: Project, fullyQualifiedName: String, memberName: String?): LibCodeResult? {
    try {
      Logger.debug("尝试从 source jar 提取源码: $fullyQualifiedName", "LibCodeService")

      val sourceFiles = findSourceFiles(project, fullyQualifiedName)
      Logger.debug("找到 ${sourceFiles.size} 个源文件", "LibCodeService")

      // 通常只会找到一个，但以防万一，我们取第一个有效的
      return sourceFiles.firstNotNullOfOrNull { sourceFile ->
        val sourceCode = sourceFile.inputStream.use { readInputStream(it) }
        val processedCode = if (memberName != null) {
          extractMemberFromSourceCode(sourceCode, memberName)
        } else {
          sourceCode
        }

        val libraryInfo = extractLibraryInfoFromSourceFile(sourceFile)
        Logger.info("成功提取源码 - 类: $fullyQualifiedName, 库: ${libraryInfo.first}, 版本: ${libraryInfo.second}", "LibCodeService")
        Logger.debug("源码内容长度: ${sourceCode.length} 字符", "LibCodeService")

        LibCodeResult(
          sourceCode = processedCode,
          isDecompiled = false,
          language = determineLanguageFromSourceCode(sourceCode),
          metadata = LibCodeMetadata(
            libraryName = libraryInfo.first,
            version = libraryInfo.second,
            sourceType = SourceType.SOURCE_JAR,
            documentation = null
          )
        )
      }
    } catch (e: Exception) {
      Logger.debug("从 source jar 提取失败: ${e.message}", "LibCodeService")
    }

    return null
  }

  /** 查找相关的源文件 */
  private fun findSourceFiles(project: Project, fullyQualifiedName: String): List<VirtualFile> {
    try {
      Logger.info("开始查找类 $fullyQualifiedName 的源码", "LibCodeService")
      val classPath = fullyQualifiedName.replace('.', '/')
      val javaPath = "$classPath.java"
      val kotlinPath = "$classPath.kt"

      // 使用 sourcesRoots 可以直接获取到源码的根目录，比 classesRoots 更准确
      val sourceRoots = OrderEnumerator.orderEntries(project).librariesOnly().sourceRoots
      Logger.debug("找到 ${sourceRoots.size} 个源码根目录", "LibCodeService")

      return sourceRoots.mapNotNull { root ->
        root.findFileByRelativePath(javaPath) ?: root.findFileByRelativePath(kotlinPath)
      }
    } catch (e: Exception) {
      Logger.debug("查找 source jar 失败: ${e.message}", "LibCodeService")
      return emptyList()
    }
  }

  /** 创建未找到结果 */
  private fun createNotFoundResult(fullyQualifiedName: String): LibCodeResult {
    return LibCodeResult(
      sourceCode = "// 未找到类 $fullyQualifiedName 的源码\n// 请检查类名是否正确，或确保相关库在项目的类路径中",
      isDecompiled = false,
      language = "text",
      metadata = LibCodeMetadata(
        libraryName = extractLibraryNameFromClassName(fullyQualifiedName),
        version = null,
        sourceType = SourceType.NOT_FOUND,
        documentation = null
      )
    )
  }

  /** 从成员名提取特定成员的源码 */
  private fun extractMemberFromSourceCode(sourceCode: String, memberName: String): String {
    // 简化实现：查找包含成员名的行
    val lines = sourceCode.lines()
    val relevantLines = lines.filter { line ->
      line.contains(memberName) && (
        line.contains("fun ") ||
          line.contains("val ") ||
          line.contains("var ") ||
          line.contains("def ") ||
          line.contains("public ") ||
          line.contains("private ")
        )
    }

    return if (relevantLines.isNotEmpty()) {
      "// 提取的成员: $memberName\n\n" + relevantLines.joinToString("\n")
    } else {
      "// 未找到成员 $memberName，返回完整类源码\n\n$sourceCode"
    }
  }

  /** 从源文件路径提取库信息 */
  private fun extractLibraryInfoFromSourceFile(sourceFile: VirtualFile): Pair<String, String?> {
    // 路径通常是 .../caches/modules-2/files-2.1/group/artifact/version/.../artifact-version-sources.jar!/path/to/class
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

  /** 从源码确定语言 */
  private fun determineLanguageFromSourceCode(sourceCode: String): String {
    return when {
      sourceCode.contains("package ") && sourceCode.contains("fun ") -> "kotlin"
      sourceCode.contains("package ") && sourceCode.contains("class ") -> "java"
      sourceCode.contains("object ") || sourceCode.contains("trait ") -> "scala"
      sourceCode.contains("def ") && sourceCode.contains("class ") -> "groovy"
      else -> "java"
    }
  }

  /** 从类名提取库名 */
  private fun extractLibraryNameFromClassName(fullyQualifiedName: String): String {
    val parts = fullyQualifiedName.split('.')
    return when {
      parts.size >= 3 -> "${parts[0]}.${parts[1]}.${parts[2]}"
      parts.size >= 2 -> "${parts[0]}.${parts[1]}"
      else -> parts[0]
    }
  }

  /** 读取输入流内容 */
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
