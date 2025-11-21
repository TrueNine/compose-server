package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import io.github.truenine.composeserver.ide.ideamcp.common.Logger
import java.io.File
import java.nio.file.Paths

/**
 * File manager.
 *
 * Provides path resolution, file collection, permission checks, and related file operations.
 */
@Service(Service.Level.PROJECT)
class FileManager {

  /** Resolve a path to a VirtualFile. */
  fun resolvePathToVirtualFile(project: Project, path: String): VirtualFile? {
    Logger.debug("Resolving path: $path", "FileManager")

    return try {
      val resolvedPath = resolvePath(project, path)
      val virtualFile = LocalFileSystem.getInstance().findFileByPath(resolvedPath.absolutePath)

      if (virtualFile == null) {
        Logger.debug("Virtual file not found: $resolvedPath", "FileManager")
      } else {
        Logger.debug("Successfully resolved virtual file: ${virtualFile.path}", "FileManager")
      }

      virtualFile
    } catch (e: Exception) {
      Logger.error("Failed to resolve path: $path", "FileManager", e)
      null
    }
  }

  /** Recursively collect files. */
  fun collectFilesRecursively(virtualFile: VirtualFile, filter: (VirtualFile) -> Boolean = { true }): List<VirtualFile> {
    Logger.debug("Start recursively collecting files: ${virtualFile.path}", "FileManager")

    val result = mutableListOf<VirtualFile>()

    try {
      collectFilesRecursivelyInternal(virtualFile, filter, result)
      Logger.debug("File collection completed, total ${result.size} files", "FileManager")
    } catch (e: Exception) {
      Logger.error("File collection failed: ${virtualFile.path}", "FileManager", e)
    }

    return result
  }

  /** Validate whether a path is valid. */
  fun isValidPath(path: String): Boolean {
    return try {
      val file = File(path)
      file.exists() && (file.isFile || file.isDirectory)
    } catch (e: Exception) {
      Logger.debug("Path validation failed: $path", "FileManager")
      false
    }
  }

  /** Check read permission for a file. */
  fun hasReadPermission(virtualFile: VirtualFile): Boolean {
    return try {
      // Check that the file is valid and exists
      virtualFile.isValid && virtualFile.exists()
    } catch (e: Exception) {
      Logger.debug("Permission check failed: ${virtualFile.path}", "FileManager")
      false
    }
  }

  /** Get the path of a file relative to the project base path. */
  fun getRelativePath(project: Project, virtualFile: VirtualFile): String {
    val basePath = project.basePath
    if (basePath == null) {
      return virtualFile.name
    }

    val filePath = virtualFile.path
    return if (filePath.startsWith(basePath)) {
      val relativePath = filePath.substring(basePath.length)
      // Remove leading path separator
      relativePath.removePrefix("/").removePrefix("\\")
    } else {
      virtualFile.name
    }
  }

  /** Resolve a path (supports both relative and absolute paths). */
  private fun resolvePath(project: Project, path: String): File {
    val file = File(path)

    return if (file.isAbsolute) {
      file
    } else {
      // Relative path based on project root
      val basePath = project.basePath ?: throw IllegalStateException("Project base path is null")
      File(basePath, path)
    }
  }

  /** Internal implementation for recursively collecting files. */
  private fun collectFilesRecursivelyInternal(virtualFile: VirtualFile, filter: (VirtualFile) -> Boolean, result: MutableList<VirtualFile>) {
    if (!virtualFile.isValid) {
      return
    }

    if (virtualFile.isDirectory) {
      // Handle directory
      try {
        virtualFile.children?.forEach { child -> collectFilesRecursivelyInternal(child, filter, result) }
      } catch (e: Exception) {
        Logger.error("Failed to process directory: ${virtualFile.path}", "FileManager", e)
      }
    } else {
      // Handle file
      if (filter(virtualFile)) {
        result.add(virtualFile)
      }
    }
  }

  /** Common file filters. */
  object Filters {
    /** Only include source code files. */
    val sourceFiles: (VirtualFile) -> Boolean = { file ->
      val extension = file.extension?.lowercase()
      extension in setOf("kt", "java", "js", "ts", "py", "cpp", "c", "h", "hpp", "cs", "go", "rs", "php", "rb", "swift")
    }

    /** Only include Kotlin files. */
    val kotlinFiles: (VirtualFile) -> Boolean = { file -> file.extension?.lowercase() == "kt" }

    /** Only include Java files. */
    val javaFiles: (VirtualFile) -> Boolean = { file -> file.extension?.lowercase() == "java" }

    /** Exclude hidden files and directories. */
    val excludeHidden: (VirtualFile) -> Boolean = { file -> !file.name.startsWith(".") }

    /** Exclude build directories. */
    val excludeBuildDirs: (VirtualFile) -> Boolean = { file ->
      val name = file.name.lowercase()
      name !in setOf("build", "target", "out", "bin", "dist", "node_modules", ".gradle", ".idea")
    }

    /** Combine multiple filters into one. */
    fun combine(vararg filters: (VirtualFile) -> Boolean): (VirtualFile) -> Boolean = { file -> filters.all { it(file) } }
  }

  /** Path utility helpers. */
  object PathUtils {
    /** Normalize path separators. */
    fun normalizePath(path: String): String {
      return path.replace('\\', '/')
    }

    /** Get the file extension from a path. */
    fun getExtension(path: String): String? {
      val lastDot = path.lastIndexOf('.')
      val lastSeparator = maxOf(path.lastIndexOf('/'), path.lastIndexOf('\\'))

      return if (lastDot > lastSeparator && lastDot < path.length - 1) {
        path.substring(lastDot + 1)
      } else {
        null
      }
    }

    /** Get file name without extension. */
    fun getNameWithoutExtension(path: String): String {
      val name = Paths.get(path).fileName.toString()
      val lastDot = name.lastIndexOf('.')

      return if (lastDot > 0) {
        name.substring(0, lastDot)
      } else {
        name
      }
    }

    /** Check whether a path is under the given parent path. */
    fun isUnder(childPath: String, parentPath: String): Boolean {
      val normalizedChild = normalizePath(childPath)
      val normalizedParent = normalizePath(parentPath)

      return normalizedChild.startsWith(normalizedParent) &&
        (normalizedChild.length == normalizedParent.length || normalizedChild[normalizedParent.length] == '/')
    }
  }
}
