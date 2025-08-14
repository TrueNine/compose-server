package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import io.github.truenine.composeserver.ide.ideamcp.McpLogManager
import java.io.File
import java.nio.file.Paths

/** 文件管理器 提供路径解析、文件收集、权限检查等文件操作功能 */
@Service(Service.Level.PROJECT)
class FileManager {

  /** 将路径解析为 VirtualFile */
  fun resolvePathToVirtualFile(project: Project, path: String): VirtualFile? {
    McpLogManager.debug("解析路径: $path", "FileManager")

    return try {
      val resolvedPath = resolvePath(project, path)
      val virtualFile = LocalFileSystem.getInstance().findFileByPath(resolvedPath.absolutePath)

      if (virtualFile == null) {
        McpLogManager.debug("未找到虚拟文件: $resolvedPath", "FileManager")
      } else {
        McpLogManager.debug("成功解析虚拟文件: ${virtualFile.path}", "FileManager")
      }

      virtualFile
    } catch (e: Exception) {
      McpLogManager.error("路径解析失败: $path", "FileManager", e)
      null
    }
  }

  /** 递归收集文件 */
  fun collectFilesRecursively(virtualFile: VirtualFile, filter: (VirtualFile) -> Boolean = { true }): List<VirtualFile> {
    McpLogManager.debug("开始递归收集文件: ${virtualFile.path}", "FileManager")

    val result = mutableListOf<VirtualFile>()

    try {
      collectFilesRecursivelyInternal(virtualFile, filter, result)
      McpLogManager.debug("文件收集完成，共收集 ${result.size} 个文件", "FileManager")
    } catch (e: Exception) {
      McpLogManager.error("文件收集失败: ${virtualFile.path}", "FileManager", e)
    }

    return result
  }

  /** 验证路径是否有效 */
  fun isValidPath(path: String): Boolean {
    return try {
      val file = File(path)
      file.exists() && (file.isFile || file.isDirectory)
    } catch (e: Exception) {
      McpLogManager.debug("路径验证失败: $path", "FileManager")
      false
    }
  }

  /** 检查文件权限 */
  fun hasReadPermission(virtualFile: VirtualFile): Boolean {
    return try {
      // 检查文件是否有效且存在
      virtualFile.isValid && virtualFile.exists()
    } catch (e: Exception) {
      McpLogManager.debug("权限检查失败: ${virtualFile.path}", "FileManager")
      false
    }
  }

  /** 获取相对路径 */
  fun getRelativePath(project: Project, virtualFile: VirtualFile): String {
    val basePath = project.basePath
    if (basePath == null) {
      return virtualFile.name
    }

    val filePath = virtualFile.path
    return if (filePath.startsWith(basePath)) {
      val relativePath = filePath.substring(basePath.length)
      // 移除开头的路径分隔符
      relativePath.removePrefix("/").removePrefix("\\")
    } else {
      virtualFile.name
    }
  }

  /** 解析路径（支持相对路径和绝对路径） */
  private fun resolvePath(project: Project, path: String): File {
    val file = File(path)

    return if (file.isAbsolute) {
      file
    } else {
      // 相对路径，基于项目根目录
      val basePath = project.basePath ?: throw IllegalStateException("项目基础路径为空")
      File(basePath, path)
    }
  }

  /** 递归收集文件的内部实现 */
  private fun collectFilesRecursivelyInternal(virtualFile: VirtualFile, filter: (VirtualFile) -> Boolean, result: MutableList<VirtualFile>) {
    if (!virtualFile.isValid) {
      return
    }

    if (virtualFile.isDirectory) {
      // 处理目录
      try {
        virtualFile.children?.forEach { child -> collectFilesRecursivelyInternal(child, filter, result) }
      } catch (e: Exception) {
        McpLogManager.error("处理目录失败: ${virtualFile.path}", "FileManager", e)
      }
    } else {
      // 处理文件
      if (filter(virtualFile)) {
        result.add(virtualFile)
      }
    }
  }

  /** 创建常用的文件过滤器 */
  object Filters {
    /** 只包含源代码文件 */
    val sourceFiles: (VirtualFile) -> Boolean = { file ->
      val extension = file.extension?.lowercase()
      extension in setOf("kt", "java", "js", "ts", "py", "cpp", "c", "h", "hpp", "cs", "go", "rs", "php", "rb", "swift")
    }

    /** 只包含 Kotlin 文件 */
    val kotlinFiles: (VirtualFile) -> Boolean = { file -> file.extension?.lowercase() == "kt" }

    /** 只包含 Java 文件 */
    val javaFiles: (VirtualFile) -> Boolean = { file -> file.extension?.lowercase() == "java" }

    /** 排除隐藏文件和目录 */
    val excludeHidden: (VirtualFile) -> Boolean = { file -> !file.name.startsWith(".") }

    /** 排除构建目录 */
    val excludeBuildDirs: (VirtualFile) -> Boolean = { file ->
      val name = file.name.lowercase()
      name !in setOf("build", "target", "out", "bin", "dist", "node_modules", ".gradle", ".idea")
    }

    /** 组合多个过滤器 */
    fun combine(vararg filters: (VirtualFile) -> Boolean): (VirtualFile) -> Boolean = { file -> filters.all { it(file) } }
  }

  /** 路径操作工具 */
  object PathUtils {
    /** 标准化路径分隔符 */
    fun normalizePath(path: String): String {
      return path.replace('\\', '/')
    }

    /** 获取文件扩展名 */
    fun getExtension(path: String): String? {
      val lastDot = path.lastIndexOf('.')
      val lastSeparator = maxOf(path.lastIndexOf('/'), path.lastIndexOf('\\'))

      return if (lastDot > lastSeparator && lastDot < path.length - 1) {
        path.substring(lastDot + 1)
      } else {
        null
      }
    }

    /** 获取不含扩展名的文件名 */
    fun getNameWithoutExtension(path: String): String {
      val name = Paths.get(path).fileName.toString()
      val lastDot = name.lastIndexOf('.')

      return if (lastDot > 0) {
        name.substring(0, lastDot)
      } else {
        name
      }
    }

    /** 检查路径是否在指定目录下 */
    fun isUnder(childPath: String, parentPath: String): Boolean {
      val normalizedChild = normalizePath(childPath)
      val normalizedParent = normalizePath(parentPath)

      return normalizedChild.startsWith(normalizedParent) &&
        (normalizedChild.length == normalizedParent.length || normalizedChild[normalizedParent.length] == '/')
    }
  }
}
