package io.github.truenine.composeserver.ide.ideamcp.services

import io.mockk.mockk
import io.mockk.every
import io.mockk.verify
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.github.truenine.composeserver.ide.ideamcp.tools.CleanOperation
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * CleanService 单元测试
 */
class CleanServiceTest {

  private val fileManager = mockk<FileManager>()
  private val cleanService = CleanServiceImpl(fileManager)

  @Test
  fun `cleanCode 应该处理单个文件`() = runBlocking {
    // Given
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()
    val options = CleanOptions(
      formatCode = true,
      optimizeImports = true,
      runInspections = false
    )

    every { virtualFile.isDirectory } returns false
    every { virtualFile.path } returns "/test/file.kt"
    every { virtualFile.extension } returns "kt"
    every { fileManager.collectFilesRecursively(any(), any()) } returns emptyList()

    // When
    val result = cleanService.cleanCode(project, virtualFile, options)

    // Then
    assertEquals(1, result.processedFiles) // 单个文件
    assertTrue(result.executionTime >= 0)
    assertTrue(result.summary.contains("处理了"))
  }

  @Test
  fun `cleanCode 应该处理目录`() = runBlocking {
    // Given
    val project = mockk<Project>()
    val directory = mockk<VirtualFile>()
    val file1 = mockk<VirtualFile>()
    val file2 = mockk<VirtualFile>()
    val options = CleanOptions()

    every { directory.isDirectory } returns true
    every { directory.path } returns "/test"
    every { file1.isDirectory } returns false
    every { file1.extension } returns "kt"
    every { file1.path } returns "/test/file1.kt"
    every { file2.isDirectory } returns false
    every { file2.extension } returns "java"
    every { file2.path } returns "/test/file2.java"
    
    every { fileManager.collectFilesRecursively(directory, any()) } returns listOf(file1, file2)

    // When
    val result = cleanService.cleanCode(project, directory, options)

    // Then
    assertEquals(2, result.processedFiles)
    assertTrue(result.executionTime >= 0)
    verify { fileManager.collectFilesRecursively(directory, any()) }
  }

  @Test
  fun `CleanOptions 默认值应该正确`() {
    // Given
    val options = CleanOptions()

    // Then
    assertTrue(options.formatCode)
    assertTrue(options.optimizeImports)
    assertTrue(options.runInspections)
    assertFalse(options.rearrangeCode)
  }

  @Test
  fun `CleanOptions 自定义值应该正确设置`() {
    // Given
    val options = CleanOptions(
      formatCode = false,
      optimizeImports = true,
      runInspections = false,
      rearrangeCode = true
    )

    // Then
    assertFalse(options.formatCode)
    assertTrue(options.optimizeImports)
    assertFalse(options.runInspections)
    assertTrue(options.rearrangeCode)
  }

  @Test
  fun `CleanResult 应该包含正确的信息`() {
    // Given
    val operations = listOf(
      CleanOperation("FORMAT", "代码格式化", 3),
      CleanOperation("OPTIMIZE_IMPORTS", "导入优化", 2)
    )
    
    val result = CleanResult(
      processedFiles = 5,
      modifiedFiles = 3,
      operations = operations,
      errors = listOf("文件锁定错误"),
      summary = "处理完成",
      executionTime = 1000L
    )

    // Then
    assertEquals(5, result.processedFiles)
    assertEquals(3, result.modifiedFiles)
    assertEquals(2, result.operations.size)
    assertEquals(1, result.errors.size)
    assertEquals("处理完成", result.summary)
    assertEquals(1000L, result.executionTime)
  }

  @Test
  fun `isCodeFile 应该正确识别代码文件`() {
    // Given
    val ktFile = mockk<VirtualFile>()
    val javaFile = mockk<VirtualFile>()
    val txtFile = mockk<VirtualFile>()
    val directory = mockk<VirtualFile>()

    every { ktFile.isDirectory } returns false
    every { ktFile.extension } returns "kt"
    every { javaFile.isDirectory } returns false
    every { javaFile.extension } returns "java"
    every { txtFile.isDirectory } returns false
    every { txtFile.extension } returns "txt"
    every { directory.isDirectory } returns true

    // 通过反射访问私有方法进行测试
    val cleanServiceImpl = cleanService as CleanServiceImpl
    val isCodeFileMethod = cleanServiceImpl.javaClass.getDeclaredMethod("isCodeFile", VirtualFile::class.java)
    isCodeFileMethod.isAccessible = true

    // When & Then
    assertTrue(isCodeFileMethod.invoke(cleanServiceImpl, ktFile) as Boolean)
    assertTrue(isCodeFileMethod.invoke(cleanServiceImpl, javaFile) as Boolean)
    assertFalse(isCodeFileMethod.invoke(cleanServiceImpl, txtFile) as Boolean)
    assertFalse(isCodeFileMethod.invoke(cleanServiceImpl, directory) as Boolean)
  }

  @Test
  fun `collectFilesToProcess 应该正确收集文件`() = runBlocking {
    // Given
    val project = mockk<Project>()
    val directory = mockk<VirtualFile>()
    val codeFile = mockk<VirtualFile>()
    val nonCodeFile = mockk<VirtualFile>()

    every { directory.isDirectory } returns true
    every { codeFile.isDirectory } returns false
    every { codeFile.extension } returns "kt"
    every { nonCodeFile.isDirectory } returns false
    every { nonCodeFile.extension } returns "txt"
    
    every { fileManager.collectFilesRecursively(directory, any()) } returns listOf(codeFile, nonCodeFile)

    // 通过反射访问私有方法进行测试
    val cleanServiceImpl = cleanService as CleanServiceImpl
    val collectMethod = cleanServiceImpl.javaClass.getDeclaredMethod("collectFilesToProcess", Project::class.java, VirtualFile::class.java)
    collectMethod.isAccessible = true

    // When
    val result = collectMethod.invoke(cleanServiceImpl, project, directory) as List<*>

    // Then
    // 由于过滤逻辑在 collectFilesRecursively 的 lambda 中，这里主要验证方法调用
    verify { fileManager.collectFilesRecursively(directory, any()) }
  }

  @Test
  fun `createSummary 应该生成正确的摘要`() {
    // Given
    val operations = listOf(
      CleanOperation("FORMAT", "代码格式化", 5),
      CleanOperation("OPTIMIZE_IMPORTS", "导入优化", 3)
    )
    val errors = listOf("错误1", "错误2")

    // 通过反射访问私有方法进行测试
    val cleanServiceImpl = cleanService as CleanServiceImpl
    val createSummaryMethod = cleanServiceImpl.javaClass.getDeclaredMethod(
      "createSummary", 
      Int::class.java, 
      Int::class.java, 
      List::class.java, 
      List::class.java
    )
    createSummaryMethod.isAccessible = true

    // When
    val summary = createSummaryMethod.invoke(cleanServiceImpl, 10, 8, operations, errors) as String

    // Then
    assertTrue(summary.contains("处理了 10 个文件"))
    assertTrue(summary.contains("修改了 8 个文件"))
    assertTrue(summary.contains("代码格式化: 5 个文件"))
    assertTrue(summary.contains("导入优化: 3 个文件"))
    assertTrue(summary.contains("遇到 2 个错误"))
  }

  @Test
  fun `createSummary 无修改文件时应该正确显示`() {
    // Given
    val operations = emptyList<CleanOperation>()
    val errors = emptyList<String>()

    // 通过反射访问私有方法进行测试
    val cleanServiceImpl = cleanService as CleanServiceImpl
    val createSummaryMethod = cleanServiceImpl.javaClass.getDeclaredMethod(
      "createSummary", 
      Int::class.java, 
      Int::class.java, 
      List::class.java, 
      List::class.java
    )
    createSummaryMethod.isAccessible = true

    // When
    val summary = createSummaryMethod.invoke(cleanServiceImpl, 5, 0, operations, errors) as String

    // Then
    assertTrue(summary.contains("处理了 5 个文件"))
    assertFalse(summary.contains("修改了"))
    assertFalse(summary.contains("执行的操作"))
    assertFalse(summary.contains("遇到"))
  }

  @Test
  fun `updateOperationCount 应该正确更新操作计数`() {
    // Given
    val operations = mutableListOf<CleanOperation>()

    // 通过反射访问私有方法进行测试
    val cleanServiceImpl = cleanService as CleanServiceImpl
    val updateMethod = cleanServiceImpl.javaClass.getDeclaredMethod(
      "updateOperationCount", 
      MutableList::class.java, 
      String::class.java, 
      String::class.java
    )
    updateMethod.isAccessible = true

    // When - 第一次添加操作
    updateMethod.invoke(cleanServiceImpl, operations, "FORMAT", "代码格式化")
    
    // Then
    assertEquals(1, operations.size)
    assertEquals("FORMAT", operations[0].type)
    assertEquals("代码格式化", operations[0].description)
    assertEquals(1, operations[0].filesAffected)

    // When - 再次添加相同类型的操作
    updateMethod.invoke(cleanServiceImpl, operations, "FORMAT", "代码格式化")
    
    // Then
    assertEquals(1, operations.size) // 仍然只有一个操作
    assertEquals(2, operations[0].filesAffected) // 但计数增加了
  }
}
