package io.github.truenine.composeserver.ide.ideamcp.services

import io.mockk.mockk
import io.mockk.every
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * FileManager 单元测试
 */
class FileManagerTest {

  private val fileManager = FileManager()

  @Test
  fun `isValidPath 应该验证有效路径`() {
    // Given - 使用当前工作目录作为有效路径
    val validPath = System.getProperty("user.dir")

    // When
    val result = fileManager.isValidPath(validPath)

    // Then
    assertTrue(result)
  }

  @Test
  fun `isValidPath 应该拒绝无效路径`() {
    // Given
    val invalidPath = "/this/path/does/not/exist/at/all"

    // When
    val result = fileManager.isValidPath(invalidPath)

    // Then
    assertFalse(result)
  }

  @Test
  fun `hasReadPermission 应该检查文件权限`() {
    // Given
    val virtualFile = mockk<VirtualFile>()
    every { virtualFile.isValid } returns true
    every { virtualFile.exists() } returns true

    // When
    val result = fileManager.hasReadPermission(virtualFile)

    // Then
    assertTrue(result)
  }

  @Test
  fun `hasReadPermission 应该拒绝无效文件`() {
    // Given
    val virtualFile = mockk<VirtualFile>()
    every { virtualFile.isValid } returns false

    // When
    val result = fileManager.hasReadPermission(virtualFile)

    // Then
    assertFalse(result)
  }

  @Test
  fun `getRelativePath 应该计算相对路径`() {
    // Given
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()
    
    every { project.basePath } returns "/project/root"
    every { virtualFile.path } returns "/project/root/src/main/kotlin/Test.kt"
    every { virtualFile.name } returns "Test.kt"

    // When
    val result = fileManager.getRelativePath(project, virtualFile)

    // Then
    assertEquals("src/main/kotlin/Test.kt", result)
  }

  @Test
  fun `getRelativePath 应该处理项目外的文件`() {
    // Given
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()
    
    every { project.basePath } returns "/project/root"
    every { virtualFile.path } returns "/other/path/Test.kt"
    every { virtualFile.name } returns "Test.kt"

    // When
    val result = fileManager.getRelativePath(project, virtualFile)

    // Then
    assertEquals("Test.kt", result)
  }

  @Test
  fun `getRelativePath 应该处理空基础路径`() {
    // Given
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()
    
    every { project.basePath } returns null
    every { virtualFile.name } returns "Test.kt"

    // When
    val result = fileManager.getRelativePath(project, virtualFile)

    // Then
    assertEquals("Test.kt", result)
  }

  @Test
  fun `collectFilesRecursively 应该收集单个文件`() {
    // Given
    val virtualFile = mockk<VirtualFile>()
    every { virtualFile.isValid } returns true
    every { virtualFile.isDirectory } returns false
    every { virtualFile.path } returns "/test/file.kt"

    // When
    val result = fileManager.collectFilesRecursively(virtualFile)

    // Then
    assertEquals(1, result.size)
    assertEquals(virtualFile, result[0])
  }

  @Test
  fun `collectFilesRecursively 应该递归收集目录中的文件`() {
    // Given
    val directory = mockk<VirtualFile>()
    val childFile1 = mockk<VirtualFile>()
    val childFile2 = mockk<VirtualFile>()
    val subDirectory = mockk<VirtualFile>()
    val subFile = mockk<VirtualFile>()

    every { directory.isValid } returns true
    every { directory.isDirectory } returns true
    every { directory.path } returns "/test/directory"
    every { directory.children } returns arrayOf(childFile1, childFile2, subDirectory)

    every { childFile1.isValid } returns true
    every { childFile1.isDirectory } returns false
    every { childFile1.path } returns "/test/directory/file1.kt"

    every { childFile2.isValid } returns true
    every { childFile2.isDirectory } returns false
    every { childFile2.path } returns "/test/directory/file2.java"

    every { subDirectory.isValid } returns true
    every { subDirectory.isDirectory } returns true
    every { subDirectory.path } returns "/test/directory/sub"
    every { subDirectory.children } returns arrayOf(subFile)

    every { subFile.isValid } returns true
    every { subFile.isDirectory } returns false
    every { subFile.path } returns "/test/directory/sub/file3.kt"

    // When
    val result = fileManager.collectFilesRecursively(directory)

    // Then
    assertEquals(3, result.size)
    assertTrue(result.contains(childFile1))
    assertTrue(result.contains(childFile2))
    assertTrue(result.contains(subFile))
  }

  @Test
  fun `collectFilesRecursively 应该应用过滤器`() {
    // Given
    val directory = mockk<VirtualFile>()
    val kotlinFile = mockk<VirtualFile>()
    val javaFile = mockk<VirtualFile>()

    every { directory.isValid } returns true
    every { directory.isDirectory } returns true
    every { directory.path } returns "/test/directory"
    every { directory.children } returns arrayOf(kotlinFile, javaFile)

    every { kotlinFile.isValid } returns true
    every { kotlinFile.isDirectory } returns false
    every { kotlinFile.path } returns "/test/directory/file.kt"
    every { kotlinFile.extension } returns "kt"

    every { javaFile.isValid } returns true
    every { javaFile.isDirectory } returns false
    every { javaFile.path } returns "/test/directory/file.java"
    every { javaFile.extension } returns "java"

    // When - 只收集 Kotlin 文件
    val result = fileManager.collectFilesRecursively(directory, FileManager.Filters.kotlinFiles)

    // Then
    assertEquals(1, result.size)
    assertEquals(kotlinFile, result[0])
  }

  @Test
  fun `PathUtils normalizePath 应该标准化路径分隔符`() {
    // Given
    val windowsPath = "C:\\Users\\test\\file.txt"

    // When
    val result = FileManager.PathUtils.normalizePath(windowsPath)

    // Then
    assertEquals("C:/Users/test/file.txt", result)
  }

  @Test
  fun `PathUtils getExtension 应该获取文件扩展名`() {
    // Given
    val filePath = "/path/to/file.kt"

    // When
    val result = FileManager.PathUtils.getExtension(filePath)

    // Then
    assertEquals("kt", result)
  }

  @Test
  fun `PathUtils getExtension 应该处理无扩展名的文件`() {
    // Given
    val filePath = "/path/to/file"

    // When
    val result = FileManager.PathUtils.getExtension(filePath)

    // Then
    assertEquals(null, result)
  }

  @Test
  fun `PathUtils getNameWithoutExtension 应该获取不含扩展名的文件名`() {
    // Given
    val filePath = "/path/to/file.kt"

    // When
    val result = FileManager.PathUtils.getNameWithoutExtension(filePath)

    // Then
    assertEquals("file", result)
  }

  @Test
  fun `PathUtils isUnder 应该检查路径包含关系`() {
    // Given
    val parentPath = "/project/root"
    val childPath = "/project/root/src/main/kotlin"

    // When
    val result = FileManager.PathUtils.isUnder(childPath, parentPath)

    // Then
    assertTrue(result)
  }

  @Test
  fun `PathUtils isUnder 应该拒绝不相关的路径`() {
    // Given
    val parentPath = "/project/root"
    val childPath = "/other/path"

    // When
    val result = FileManager.PathUtils.isUnder(childPath, parentPath)

    // Then
    assertFalse(result)
  }

  @Test
  fun `Filters sourceFiles 应该识别源代码文件`() {
    // Given
    val kotlinFile = mockk<VirtualFile>()
    val javaFile = mockk<VirtualFile>()
    val textFile = mockk<VirtualFile>()

    every { kotlinFile.extension } returns "kt"
    every { javaFile.extension } returns "java"
    every { textFile.extension } returns "txt"

    // When & Then
    assertTrue(FileManager.Filters.sourceFiles(kotlinFile))
    assertTrue(FileManager.Filters.sourceFiles(javaFile))
    assertFalse(FileManager.Filters.sourceFiles(textFile))
  }

  @Test
  fun `Filters combine 应该组合多个过滤器`() {
    // Given
    val kotlinFile = mockk<VirtualFile>()
    val hiddenKotlinFile = mockk<VirtualFile>()

    every { kotlinFile.extension } returns "kt"
    every { kotlinFile.name } returns "Test.kt"

    every { hiddenKotlinFile.extension } returns "kt"
    every { hiddenKotlinFile.name } returns ".hidden.kt"

    val combinedFilter = FileManager.Filters.combine(
      FileManager.Filters.kotlinFiles,
      FileManager.Filters.excludeHidden
    )

    // When & Then
    assertTrue(combinedFilter(kotlinFile))
    assertFalse(combinedFilter(hiddenKotlinFile))
  }
}
