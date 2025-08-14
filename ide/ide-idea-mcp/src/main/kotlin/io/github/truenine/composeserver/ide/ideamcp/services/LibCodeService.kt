package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import io.github.truenine.composeserver.ide.ideamcp.McpLogManager
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.jar.JarFile
import java.util.zip.ZipEntry

/**
 * 库代码服务接口
 */
interface LibCodeService {
  /**
   * 获取库代码
   */
  suspend fun getLibraryCode(
    project: Project,
    filePath: String,
    fullyQualifiedName: String,
    memberName: String? = null
  ): LibCodeResult
}

/**
 * 库代码服务实现
 */
@Service(Service.Level.PROJECT)
class LibCodeServiceImpl : LibCodeService {

  override suspend fun getLibraryCode(
    project: Project,
    filePath: String,
    fullyQualifiedName: String,
    memberName: String?
  ): LibCodeResult = withContext(Dispatchers.IO) {
    McpLogManager.info("开始获取库代码 - 类: $fullyQualifiedName", "LibCodeService")
    
    try {
      // 尝试从 source jar 中提取源码
      val sourceJarResult = extractFromSourceJar(project, fullyQualifiedName, memberName)
      if (sourceJarResult != null) {
        McpLogManager.info("从 source jar 中提取源码成功", "LibCodeService")
        return@withContext sourceJarResult
      }
      
      // 尝试反编译字节码
      val decompileResult = decompileFromBytecode(project, fullyQualifiedName, memberName)
      if (decompileResult != null) {
        McpLogManager.info("字节码反编译成功", "LibCodeService")
        return@withContext decompileResult
      }
      
      // 都失败了，返回未找到结果
      McpLogManager.warn("无法找到类的源码或字节码: $fullyQualifiedName", "LibCodeService")
      return@withContext createNotFoundResult(fullyQualifiedName)
      
    } catch (e: Exception) {
      McpLogManager.error("获取库代码失败: $fullyQualifiedName", "LibCodeService", e)
      throw e
    }
  }



  /**
   * 从 source jar 中提取源码
   */
  private fun extractFromSourceJar(
    project: Project,
    fullyQualifiedName: String,
    memberName: String?
  ): LibCodeResult? {
    McpLogManager.debug("尝试从 source jar 提取源码: $fullyQualifiedName", "LibCodeService")
    
    try {
      val sourceJars = findSourceJars(project, fullyQualifiedName)
      
      for (sourceJar in sourceJars) {
        val sourceCode = extractFromJar(sourceJar, fullyQualifiedName)
        if (sourceCode != null) {
          val processedCode = if (memberName != null) {
            extractMemberFromSourceCode(sourceCode, memberName)
          } else {
            sourceCode
          }
          
          val libraryInfo = extractLibraryInfoFromJar("/mock/path/library.jar")
          
          return LibCodeResult(
            sourceCode = processedCode,
            isDecompiled = false,
            language = "java", // 大多数 source jar 是 Java
            metadata = LibCodeMetadata(
              libraryName = libraryInfo.first,
              version = libraryInfo.second,
              sourceType = SourceType.SOURCE_JAR,
              documentation = null
            )
          )
        }
      }
    } catch (e: Exception) {
      McpLogManager.debug("从 source jar 提取失败: ${e.message}", "LibCodeService")
    }
    
    return null
  }

  /**
   * 查找相关的 source jar 文件
   */
  private fun findSourceJars(project: Project, fullyQualifiedName: String): List<VirtualFile> {
    val result = mutableListOf<VirtualFile>()
    
    try {
      // 简化实现：在实际项目中，这里应该扫描项目的依赖库
      // 目前返回空列表，表示没有找到 source jar
      McpLogManager.debug("查找 source jar - 当前为简化实现", "LibCodeService")
    } catch (e: Exception) {
      McpLogManager.debug("查找 source jar 失败: ${e.message}", "LibCodeService")
    }
    
    return result
  }

  /**
   * 从 JAR 文件中提取源码
   */
  private fun extractFromJar(jarFile: VirtualFile, fullyQualifiedName: String): String? {
    val classPath = fullyQualifiedName.replace('.', '/') + ".java"
    
    return try {
      jarFile.inputStream?.use { inputStream ->
        val jarInputStream = java.util.jar.JarInputStream(inputStream)
        var entry: java.util.jar.JarEntry?
        
        while (jarInputStream.nextJarEntry.also { entry = it } != null) {
          if (entry?.name == classPath) {
            return readInputStream(jarInputStream)
          }
        }
        null
      }
    } catch (e: Exception) {
      McpLogManager.debug("从 JAR 提取源码失败: ${e.message}", "LibCodeService")
      null
    }
  }

  /**
   * 反编译字节码
   */
  private fun decompileFromBytecode(
    project: Project,
    fullyQualifiedName: String,
    memberName: String?
  ): LibCodeResult? {
    McpLogManager.debug("尝试反编译字节码: $fullyQualifiedName", "LibCodeService")
    
    try {
      // 这里应该集成 IDEA 的反编译器
      // 由于复杂性，先返回一个简单的占位实现
      val decompiled = decompileClass(project, fullyQualifiedName)
      if (decompiled != null) {
        val processedCode = if (memberName != null) {
          extractMemberFromSourceCode(decompiled, memberName)
        } else {
          decompiled
        }
        
        return LibCodeResult(
          sourceCode = processedCode,
          isDecompiled = true,
          language = "java",
          metadata = LibCodeMetadata(
            libraryName = extractLibraryNameFromClassName(fullyQualifiedName),
            version = null,
            sourceType = SourceType.DECOMPILED,
            documentation = null
          )
        )
      }
    } catch (e: Exception) {
      McpLogManager.debug("字节码反编译失败: ${e.message}", "LibCodeService")
    }
    
    return null
  }

  /**
   * 反编译类（简化实现）
   */
  private fun decompileClass(project: Project, fullyQualifiedName: String): String? {
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
    """.trimIndent()
  }

  /**
   * 创建未找到结果
   */
  private fun createNotFoundResult(fullyQualifiedName: String): LibCodeResult {
    return LibCodeResult(
      sourceCode = "// 未找到类 $fullyQualifiedName 的源码或字节码\n// 请检查类名是否正确，或确保相关库在项目的类路径中",
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

  /**
   * 从源码中提取特定成员（简化实现）
   */
  private fun extractMemberFromSourceCode(sourceCode: String, memberName: String): String {
    // 这是一个简化的实现，实际应该使用更复杂的解析逻辑
    val lines = sourceCode.lines()
    val memberLines = mutableListOf<String>()
    var inMember = false
    var braceCount = 0
    
    for (line in lines) {
      if (line.contains(memberName) && (line.contains("public") || line.contains("private") || line.contains("protected"))) {
        inMember = true
        memberLines.add(line)
        braceCount += line.count { it == '{' } - line.count { it == '}' }
      } else if (inMember) {
        memberLines.add(line)
        braceCount += line.count { it == '{' } - line.count { it == '}' }
        
        if (braceCount <= 0) {
          break
        }
      }
    }
    
    return if (memberLines.isNotEmpty()) {
      memberLines.joinToString("\n")
    } else {
      sourceCode // 如果没找到特定成员，返回整个源码
    }
  }

  /**
   * 确定编程语言
   */
  private fun determineLanguage(extension: String?): String {
    return when (extension?.lowercase()) {
      "kt" -> "kotlin"
      "java" -> "java"
      "scala" -> "scala"
      "groovy" -> "groovy"
      else -> "java" // 默认为 Java
    }
  }

  /**
   * 提取库信息
   */
  private fun extractLibraryInfo(path: String): Pair<String, String?> {
    // 尝试从路径中提取库名和版本
    val jarPattern = Regex("""([^/\\]+)-(\d+(?:\.\d+)*(?:-[^/\\]*)?)\.(jar|zip)""")
    val match = jarPattern.find(path)
    
    return if (match != null) {
      Pair(match.groupValues[1], match.groupValues[2])
    } else {
      val fileName = path.substringAfterLast('/').substringAfterLast('\\')
      Pair(fileName.substringBeforeLast('.'), null)
    }
  }

  /**
   * 从 JAR 文件名提取库信息
   */
  private fun extractLibraryInfoFromJar(jarPath: String): Pair<String, String?> {
    return extractLibraryInfo(jarPath)
  }

  /**
   * 从类名提取库名
   */
  private fun extractLibraryNameFromClassName(fullyQualifiedName: String): String {
    val parts = fullyQualifiedName.split('.')
    return if (parts.size >= 2) {
      parts.take(2).joinToString(".")
    } else {
      fullyQualifiedName
    }
  }



  /**
   * 读取输入流内容
   */
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

/**
 * 库代码结果
 */
data class LibCodeResult(
  /** 源代码内容 */
  val sourceCode: String,
  /** 是否为反编译代码 */
  val isDecompiled: Boolean,
  /** 编程语言 */
  val language: String,
  /** 元数据信息 */
  val metadata: LibCodeMetadata
)

/**
 * 库代码元数据
 */
data class LibCodeMetadata(
  /** 库名称 */
  val libraryName: String,
  /** 版本号 */
  val version: String?,
  /** 源码类型 */
  val sourceType: SourceType,
  /** 文档信息 */
  val documentation: String?
)
