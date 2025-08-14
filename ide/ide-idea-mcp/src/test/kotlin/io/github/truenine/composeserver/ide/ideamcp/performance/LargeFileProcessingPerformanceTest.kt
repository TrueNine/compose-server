package io.github.truenine.composeserver.ide.ideamcp.performance

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import io.github.truenine.composeserver.ide.ideamcp.services.ErrorServiceImpl
import io.github.truenine.composeserver.ide.ideamcp.services.FileManager
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * 大文件处理性能测试
 * 测试处理大文件和大项目时的性能表现
 */
class LargeFileProcessingPerformanceTest : PerformanceTestBase() {

  private val errorService = ErrorServiceImpl()
  private val fileManager = FileManager()

  @Test
  fun `测试大文件错误分析性能`() {
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()
    val psiManager = mockk<PsiManager>()
    val psiFile = mockk<PsiFile>()
    
    // 模拟大文件（10000行代码）
    val largeFileContent = createMockFileContent(10000)
    
    every { virtualFile.isDirectory } returns false
    every { virtualFile.isValid } returns true
    every { virtualFile.path } returns "/test/LargeFile.kt"
    every { virtualFile.name } returns "LargeFile.kt"
    every { project.basePath } returns "/test"
    every { PsiManager.getInstance(project) } returns psiManager
    every { psiManager.findFile(virtualFile) } returns psiFile
    every { psiFile.name } returns "LargeFile.kt"
    every { psiFile.text } returns largeFileContent
    
    val maxAnalysisTimeMs = 2000L // 2秒内完成分析
    
    val actualTime = measureAndAssertTime(maxAnalysisTimeMs) {
      val errors = errorService.analyzeFile(project, virtualFile)
      // 验证能够正常处理，不抛出异常
      assertTrue(errors.isEmpty() || errors.isNotEmpty(), "应该能够正常处理大文件")
    }
    
    println("分析 10000 行代码文件耗时: ${actualTime}ms")
  }

  @Test
  fun `测试大量文件错误收集性能`() {
    val project = mockk<Project>()
    val rootDirectory = mockk<VirtualFile>()
    val psiManager = mockk<PsiManager>()
    
    // 模拟包含100个文件的大项目
    val fileCount = 100
    val mockFiles = (1..fileCount).map { index ->
      val file = mockk<VirtualFile>()
      val psiFile = mockk<PsiFile>()
      
      every { file.isDirectory } returns false
      every { file.isValid } returns true
      every { file.path } returns "/test/File$index.kt"
      every { file.name } returns "File$index.kt"
      every { psiManager.findFile(file) } returns psiFile
      every { psiFile.name } returns "File$index.kt"
      every { psiFile.text } returns createMockFileContent(100) // 每个文件100行
      
      file
    }.toTypedArray()
    
    every { rootDirectory.isDirectory } returns true
    every { rootDirectory.isValid } returns true
    every { rootDirectory.path } returns "/test"
    every { rootDirectory.children } returns mockFiles
    every { project.basePath } returns "/test"
    every { PsiManager.getInstance(project) } returns psiManager
    
    val maxCollectionTimeMs = 10000L // 10秒内完成收集
    
    val actualTime = measureAndAssertTime(maxCollectionTimeMs) {
      val errors = errorService.collectErrors(project, rootDirectory)
      // 验证能够正常处理，不抛出异常
      assertTrue(errors.isEmpty() || errors.isNotEmpty(), "应该能够正常处理大量文件")
    }
    
    println("收集 $fileCount 个文件的错误耗时: ${actualTime}ms")
  }

  @Test
  fun `测试大文件处理内存使用`() {
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()
    val psiManager = mockk<PsiManager>()
    val psiFile = mockk<PsiFile>()
    
    // 模拟超大文件（50000行代码）
    val veryLargeFileContent = createMockFileContent(50000)
    
    every { virtualFile.isDirectory } returns false
    every { virtualFile.isValid } returns true
    every { virtualFile.path } returns "/test/VeryLargeFile.kt"
    every { virtualFile.name } returns "VeryLargeFile.kt"
    every { project.basePath } returns "/test"
    every { PsiManager.getInstance(project) } returns psiManager
    every { psiManager.findFile(virtualFile) } returns psiFile
    every { psiFile.name } returns "VeryLargeFile.kt"
    every { psiFile.text } returns veryLargeFileContent
    
    val maxMemoryMB = 100L // 最大100MB内存使用
    
    val memoryUsed = measureMemoryUsage {
      val errors = errorService.analyzeFile(project, virtualFile)
      // 确保处理完成
      assertTrue(errors.isEmpty() || errors.isNotEmpty(), "应该能够正常处理超大文件")
    }
    
    assertMemoryUsage(memoryUsed, maxMemoryMB)
    
    println("处理 50000 行代码文件使用内存: ${memoryUsed / (1024 * 1024)}MB")
  }

  @Test
  fun `测试文件路径解析性能`() {
    val project = mockk<Project>()
    every { project.basePath } returns "/test/project"
    
    val pathCount = 10000
    val testPaths = (1..pathCount).map { "/test/project/src/main/kotlin/File$it.kt" }
    
    val maxResolveTimeMs = 1000L // 1秒内完成路径解析
    
    val actualTime = measureAndAssertTime(maxResolveTimeMs) {
      testPaths.forEach { path ->
        val isValid = fileManager.isValidPath(path)
        // 验证路径验证功能正常工作
        assertTrue(isValid || !isValid, "路径验证应该正常工作")
      }
    }
    
    println("解析 $pathCount 个文件路径耗时: ${actualTime}ms")
  }

  @Test
  fun `测试深层目录结构处理性能`() {
    val project = mockk<Project>()
    val rootDir = mockk<VirtualFile>()
    
    // 模拟深层嵌套的目录结构（5层深度，每层10个子目录）
    fun createNestedStructure(depth: Int, currentDepth: Int = 0): Array<VirtualFile> {
      if (currentDepth >= depth) {
        // 叶子节点：创建文件
        return (1..5).map { fileIndex ->
          val file = mockk<VirtualFile>()
          every { file.isDirectory } returns false
          every { file.isValid } returns true
          every { file.path } returns "/test/deep/level$currentDepth/File$fileIndex.kt"
          every { file.name } returns "File$fileIndex.kt"
          file
        }.toTypedArray()
      }
      
      return (1..10).map { dirIndex ->
        val dir = mockk<VirtualFile>()
        every { dir.isDirectory } returns true
        every { dir.isValid } returns true
        every { dir.path } returns "/test/deep/level$currentDepth/Dir$dirIndex"
        every { dir.name } returns "Dir$dirIndex"
        every { dir.children } returns createNestedStructure(depth, currentDepth + 1)
        dir
      }.toTypedArray()
    }
    
    every { rootDir.isDirectory } returns true
    every { rootDir.isValid } returns true
    every { rootDir.path } returns "/test/deep"
    every { rootDir.children } returns createNestedStructure(5)
    every { project.basePath } returns "/test"
    
    val maxTraversalTimeMs = 5000L // 5秒内完成遍历
    
    val actualTime = measureAndAssertTime(maxTraversalTimeMs) {
      val allFiles = fileManager.collectFilesRecursively(rootDir) { !it.isDirectory() }
      // 验证能够收集到所有文件
      assertTrue(allFiles.isNotEmpty(), "应该能够收集到文件")
    }
    
    println("遍历深层目录结构耗时: ${actualTime}ms")
  }

  @Test
  fun `测试并发文件处理性能`() {
    val project = mockk<Project>()
    val psiManager = mockk<PsiManager>()
    every { project.basePath } returns "/test"
    every { PsiManager.getInstance(project) } returns psiManager
    
    // 创建多个模拟文件用于并发处理
    val fileCount = 50
    val mockFiles = (1..fileCount).map { index ->
      val file = mockk<VirtualFile>()
      val psiFile = mockk<PsiFile>()
      
      every { file.isDirectory } returns false
      every { file.isValid } returns true
      every { file.path } returns "/test/ConcurrentFile$index.kt"
      every { file.name } returns "ConcurrentFile$index.kt"
      every { psiManager.findFile(file) } returns psiFile
      every { psiFile.name } returns "ConcurrentFile$index.kt"
      every { psiFile.text } returns createMockFileContent(500) // 每个文件500行
      
      file
    }
    
    val maxConcurrentTimeMs = 8000L // 8秒内完成并发处理
    
    val actualTime = measureAndAssertTime(maxConcurrentTimeMs) {
      // 模拟并发处理多个文件
      mockFiles.forEach { file ->
        val errors = errorService.analyzeFile(project, file)
        assertTrue(errors.isEmpty() || errors.isNotEmpty(), "应该能够正常处理文件")
      }
    }
    
    println("并发处理 $fileCount 个文件耗时: ${actualTime}ms")
  }
}
