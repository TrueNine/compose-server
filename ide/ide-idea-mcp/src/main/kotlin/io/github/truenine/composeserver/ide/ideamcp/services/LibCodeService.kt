package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

/** 库代码结果 */
data class LibCodeResult(val sourceCode: String, val isDecompiled: Boolean, val language: String, val metadata: LibCodeMetadata)

/** 库代码元数据 */
data class LibCodeMetadata(val libraryName: String, val version: String?, val sourceType: SourceType, val documentation: String?)

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

      // 2. 尝试反编译字节码
      val decompileResult = tryDecompileFromBytecode(project, fullyQualifiedName, memberName)
      if (decompileResult != null) {
        Logger.info("成功反编译字节码", "LibCodeService")
        return@withContext decompileResult
      }

      // 3. 返回未找到结果
      Logger.info("未找到源码或字节码，返回默认结果", "LibCodeService")
      createNotFoundResult(fullyQualifiedName)
    }
  }

  /** 尝试从 source jar 提取源码 */
  private fun tryExtractFromSourceJar(project: Project, fullyQualifiedName: String, memberName: String?): LibCodeResult? {
    try {
      Logger.debug("尝试从 source jar 提取源码: $fullyQualifiedName", "LibCodeService")

      val sourceJars = findSourceJars(project, fullyQualifiedName)
      Logger.debug("找到 ${sourceJars.size} 个 source jar", "LibCodeService")

      for (sourceJar in sourceJars) {
        val sourceCode = extractFromJar(sourceJar, fullyQualifiedName)
        if (sourceCode != null) {
          val processedCode =
            if (memberName != null) {
              extractMemberFromSourceCode(sourceCode, memberName)
            } else {
              sourceCode
            }

          val libraryInfo = extractLibraryInfoFromJar(sourceJar)
          Logger.info("成功提取源码 - 类: $fullyQualifiedName, 库: ${libraryInfo.first}, 版本: ${libraryInfo.second}", "LibCodeService")
          Logger.debug("源码内容长度: ${sourceCode.length} 字符", "LibCodeService")

          return LibCodeResult(
            sourceCode = processedCode,
            isDecompiled = false,
            language = determineLanguageFromSourceCode(sourceCode),
            metadata = LibCodeMetadata(libraryName = libraryInfo.first, version = libraryInfo.second, sourceType = SourceType.SOURCE_JAR, documentation = null),
          )
        }
      }
    } catch (e: Exception) {
      Logger.debug("从 source jar 提取失败: ${e.message}", "LibCodeService")
    }

    return null
  }

  /** 查找相关的 source jar 文件 */
  private fun findSourceJars(project: Project, fullyQualifiedName: String): List<VirtualFile> {
    val result = mutableListOf<VirtualFile>()

    try {
      Logger.info("开始查找类 $fullyQualifiedName 的源码", "LibCodeService")

      // 获取项目的所有依赖库
      val orderEnumerator = OrderEnumerator.orderEntries(project)
      val libraryRoots = orderEnumerator.librariesOnly().classesRoots

      Logger.debug("项目依赖库数量: ${libraryRoots.size}", "LibCodeService")

      for (libraryRoot in libraryRoots) {
        Logger.debug("检查依赖库: ${libraryRoot.path}", "LibCodeService")
        if (!libraryRoot.isValid) {
          Logger.debug("libraryRoot: {} not valid", libraryRoot)
          continue
        }

        // TODO libraryRoot.children 可以获取到 com.intellij.openapi.vfs.VirtualFile[]
        // TODO libraryRoot.url 可以获取到 String 的 url 表示 大多情况可能为 jar 协议路径
        // TODO libraryRoot.path 可以获取到 String 的 path 表示 大多情况可能为 jar
        // TODO libraryRoot.charset 可以获取到 Charset 以便于读取文件时使用正确的字符集
        // TODO libraryRoot.extension 可以获取到文件后缀路径 举例为 jar


        // 尝试找到对应的 source jar
        val sourceJar = findCorrespondingSourceJar(libraryRoot)
        if (sourceJar != null) {
          Logger.debug("找到对应的 source jar: ${sourceJar.path}", "LibCodeService")

          // 检查这个 source jar 是否包含我们要找的类
          if (containsClass(sourceJar, fullyQualifiedName)) {
            result.add(sourceJar)
            Logger.info("找到包含类 $fullyQualifiedName 的 source jar: ${sourceJar.path}", "LibCodeService")
          }
        } else {
          Logger.debug("未找到对应的 source jar", "LibCodeService")
        }
      }
    } catch (e: Exception) {
      Logger.debug("查找 source jar 失败: ${e.message}", "LibCodeService")
    }

    return result
  }

  /** 查找对应的 source jar */
  private fun findCorrespondingSourceJar(libraryRoot: VirtualFile): VirtualFile? {
    /*
    * TODO
    * 此处有可能是: C:/Users/truen/.gradle/caches/modules-2/files-2.1/org.babyfish.jimmer/jimmer-spring-boot-starter/0.9.105/74f5e22333b644b797e5c5571f15c425fefae835/jimmer-spring-boot-starter-0.9.105.jar!/
    * */
    val path = libraryRoot.path
    if (path.endsWith(".jar")) {
      val sourcePath = path.replace(".jar", "-sources.jar")
      return VirtualFileManager.getInstance().findFileByUrl("file://$sourcePath")
    }
    return null
  }

  /** 检查 JAR 是否包含指定类 */
  private fun containsClass(jarFile: VirtualFile, fullyQualifiedName: String): Boolean {
    val javaClassPath = fullyQualifiedName.replace('.', '/') + ".java"
    val kotlinClassPath = fullyQualifiedName.replace('.', '/') + ".kt"

    try {
      jarFile.inputStream?.use { inputStream ->
        val jarInputStream = java.util.jar.JarInputStream(inputStream)
        var entry: java.util.jar.JarEntry?

        while (jarInputStream.nextJarEntry.also { entry = it } != null) {
          val entryName = entry?.name
          if (entryName == javaClassPath || entryName == kotlinClassPath) {
            return true
          }
        }
      }
    } catch (e: Exception) {
      Logger.debug("检查 JAR 包含类失败: ${e.message}", "LibCodeService")
    }

    return false
  }

  /** 从 JAR 文件中提取源码 */
  private fun extractFromJar(jarFile: VirtualFile, fullyQualifiedName: String): String? {
    val javaClassPath = fullyQualifiedName.replace('.', '/') + ".java"
    val kotlinClassPath = fullyQualifiedName.replace('.', '/') + ".kt"

    return try {
      jarFile.inputStream?.use { inputStream ->
        val jarInputStream = java.util.jar.JarInputStream(inputStream)
        var entry: java.util.jar.JarEntry?

        while (jarInputStream.nextJarEntry.also { entry = it } != null) {
          val entryName = entry?.name
          if (entryName == javaClassPath || entryName == kotlinClassPath) {
            return readInputStream(jarInputStream)
          }
        }
        null
      }
    } catch (e: Exception) {
      Logger.debug("从 JAR 提取源码失败: ${e.message}", "LibCodeService")
      null
    }
  }

  /** 尝试反编译字节码 */
  private fun tryDecompileFromBytecode(project: Project, fullyQualifiedName: String, memberName: String?): LibCodeResult? {
    Logger.info("尝试反编译字节码: $fullyQualifiedName", "LibCodeService")

    try {
      // 首先尝试从项目依赖中查找类文件
      val classFile = findClassFileInDependencies(project, fullyQualifiedName)
      if (classFile != null) {
        Logger.info("在项目依赖中找到类文件: ${classFile.path}", "LibCodeService")

        val decompiled = decompileFromClassFile(classFile, fullyQualifiedName)
        if (decompiled != null) {
          val processedCode =
            if (memberName != null) {
              extractMemberFromSourceCode(decompiled, memberName)
            } else {
              decompiled
            }

          Logger.info("成功反编译字节码 - 类: $fullyQualifiedName", "LibCodeService")
          Logger.debug("反编译内容长度: ${decompiled.length} 字符", "LibCodeService")

          return LibCodeResult(
            sourceCode = processedCode,
            isDecompiled = true,
            language = "java",
            metadata =
              LibCodeMetadata(
                libraryName = extractLibraryNameFromClassName(fullyQualifiedName),
                version = null,
                sourceType = SourceType.DECOMPILED,
                documentation = null,
              ),
          )
        }
      }

      // 如果没有找到类文件，尝试简单的反编译实现
      val decompiled = decompileClass(project, fullyQualifiedName)
      val processedCode =
        if (memberName != null) {
          extractMemberFromSourceCode(decompiled, memberName)
        } else {
          decompiled
        }

      Logger.info("使用简化反编译 - 类: $fullyQualifiedName", "LibCodeService")

      return LibCodeResult(
        sourceCode = processedCode,
        isDecompiled = true,
        language = "java",
        metadata =
          LibCodeMetadata(
            libraryName = extractLibraryNameFromClassName(fullyQualifiedName),
            version = null,
            sourceType = SourceType.DECOMPILED,
            documentation = null,
          ),
      )
    } catch (e: Exception) {
      Logger.debug("字节码反编译失败: ${e.message}", "LibCodeService")
    }

    return null
  }

  /** 在项目依赖中查找类文件 */
  private fun findClassFileInDependencies(project: Project, fullyQualifiedName: String): VirtualFile? {
    try {
      val classPath = fullyQualifiedName.replace('.', '/') + ".class"
      Logger.debug("查找类文件路径: $classPath", "LibCodeService")

      val orderEnumerator = OrderEnumerator.orderEntries(project)
      val libraryRoots = orderEnumerator.librariesOnly().classesRoots

      for (libraryRoot in libraryRoots) {
        Logger.debug("检查库根目录: ${libraryRoot.path}", "LibCodeService")

        if (libraryRoot.name.endsWith(".jar")) {
          // 在 JAR 文件中查找
          val classFile = findClassInJar(libraryRoot, classPath)
          if (classFile != null) {
            Logger.info("在 JAR 中找到类文件: ${libraryRoot.path}!/$classPath", "LibCodeService")
            return classFile
          }
        } else {
          // 在目录中查找
          val classFile = libraryRoot.findFileByRelativePath(classPath)
          if (classFile != null) {
            Logger.info("在目录中找到类文件: ${classFile.path}", "LibCodeService")
            return classFile
          }
        }
      }
    } catch (e: Exception) {
      Logger.debug("查找类文件失败: ${e.message}", "LibCodeService")
    }

    return null
  }

  /** 在 JAR 文件中查找类文件 */
  private fun findClassInJar(jarFile: VirtualFile, classPath: String): VirtualFile? {
    return try {
      jarFile.findFileByRelativePath(classPath)
    } catch (e: Exception) {
      Logger.debug("在 JAR 中查找类文件失败: ${e.message}", "LibCodeService")
      null
    }
  }

  /** 从类文件反编译 */
  private fun decompileFromClassFile(classFile: VirtualFile, fullyQualifiedName: String): String? {
    return try {
      // 这里应该使用 IDEA 的反编译器 API
      // 目前返回一个更详细的占位符
      """
        // 反编译的代码 - $fullyQualifiedName
        // 类文件位置: ${classFile.path}
        // 注意：这是一个简化的占位实现
        // 实际实现需要集成 IDEA 的反编译器

        public class ${fullyQualifiedName.substringAfterLast('.')} {
            // 反编译的内容将在这里显示
            // 请使用 IDEA 的 "Go to Declaration" 功能查看完整的反编译代码

            // 类文件已找到，可以进行完整的反编译
            // 文件大小: ${classFile.length} 字节
        }
      """
        .trimIndent()
    } catch (e: Exception) {
      Logger.debug("从类文件反编译失败: ${e.message}", "LibCodeService")
      null
    }
  }

  /** 反编译类（简化实现） */
  private fun decompileClass(project: Project, fullyQualifiedName: String): String {
    // 这里应该使用 IDEA 的反编译器 API
    // 目前返回一个占位符
    return """
      // 反编译的代码 - $fullyQualifiedName
      // 注意：这是一个简化的占位实现
      // 实际实现需要集成 IDEA 的反编译器

      public class ${fullyQualifiedName.substringAfterLast('.')} {
          // 反编译的内容将在这里显示
          // 请使用 IDEA 的 "Go to Declaration" 功能查看完整的反编译代码
      }
    """
      .trimIndent()
  }

  /** 创建未找到结果 */
  private fun createNotFoundResult(fullyQualifiedName: String): LibCodeResult {
    return LibCodeResult(
      sourceCode = "// 未找到类 $fullyQualifiedName 的源码或字节码\n// 请检查类名是否正确，或确保相关库在项目的类路径中",
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

  /** 从成员名提取特定成员的源码 */
  private fun extractMemberFromSourceCode(sourceCode: String, memberName: String): String {
    // 简化实现：查找包含成员名的行
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
      "// 提取的成员: $memberName\n\n" + relevantLines.joinToString("\n")
    } else {
      "// 未找到成员 $memberName，返回完整类源码\n\n$sourceCode"
    }
  }

  /** 从库名提取库信息 */
  private fun extractLibraryInfoFromJar(jarFile: VirtualFile): Pair<String, String?> {
    val fileName = jarFile.name
    val libraryName = fileName.replace("-sources.jar", "").replace(".jar", "")

    // 尝试从文件名中提取版本信息
    val versionRegex = """(.+)-(\d+\.\d+(?:\.\d+)?)""".toRegex()
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
