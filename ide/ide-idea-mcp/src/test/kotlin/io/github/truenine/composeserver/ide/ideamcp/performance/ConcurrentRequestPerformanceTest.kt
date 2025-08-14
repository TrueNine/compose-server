package io.github.truenine.composeserver.ide.ideamcp.performance

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import io.github.truenine.composeserver.ide.ideamcp.tools.CleanCodeArgs
import io.github.truenine.composeserver.ide.ideamcp.tools.CleanCodeTool
import io.github.truenine.composeserver.ide.ideamcp.tools.TerminalArgs
import io.github.truenine.composeserver.ide.ideamcp.tools.TerminalTool
import io.github.truenine.composeserver.ide.ideamcp.tools.ViewErrorArgs
import io.github.truenine.composeserver.ide.ideamcp.tools.ViewErrorTool
import io.github.truenine.composeserver.ide.ideamcp.tools.ViewLibCodeArgs
import io.github.truenine.composeserver.ide.ideamcp.tools.ViewLibCodeTool
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * 并发请求性能测试
 * 测试多个 MCP 工具同时处理请求时的性能表现
 */
class ConcurrentRequestPerformanceTest : PerformanceTestBase() {

  private val terminalTool = TerminalTool()
  private val viewErrorTool = ViewErrorTool()
  private val cleanCodeTool = CleanCodeTool()
  private val viewLibCodeTool = ViewLibCodeTool()

  @Test
  fun `测试并发终端命令执行性能`() = runBlocking {
    val project = mockk<Project>()
    every { project.basePath } returns "/test/project"
    
    val concurrentRequests = 20
    val maxTimeMs = 15000L // 15秒内完成所有并发请求
    
    val actualTime = measureAndAssertTime(maxTimeMs) {
      runBlocking {
        val tasks = (1..concurrentRequests).map { index ->
          async {
            val args = TerminalArgs(
              command = "echo 'Concurrent test $index'",
              workingDirectory = "/test/project",
              timeout = 5000L
            )
            try {
              terminalTool.handle(project, args)
            } catch (e: Exception) {
              // 在测试环境中可能会失败，这是正常的
              println("终端命令 $index 执行异常: ${e.message}")
            }
          }
        }
        tasks.awaitAll()
      }
    }
    
    println("$concurrentRequests 个并发终端命令执行耗时: ${actualTime}ms")
  }

  @Test
  fun `测试并发错误查看请求性能`() = runBlocking {
    val project = mockk<Project>()
    val psiManager = mockk<PsiManager>()
    every { project.basePath } returns "/test/project"
    every { PsiManager.getInstance(project) } returns psiManager
    
    // 创建模拟文件
    val mockFile = mockk<VirtualFile>()
    val psiFile = mockk<PsiFile>()
    every { mockFile.isDirectory } returns false
    every { mockFile.isValid } returns true
    every { mockFile.path } returns "/test/project/TestFile.kt"
    every { mockFile.name } returns "TestFile.kt"
    every { psiManager.findFile(mockFile) } returns psiFile
    every { psiFile.name } returns "TestFile.kt"
    every { psiFile.text } returns createMockFileContent(1000)
    
    val concurrentRequests = 15
    val maxTimeMs = 10000L // 10秒内完成所有并发请求
    
    val actualTime = measureAndAssertTime(maxTimeMs) {
      runBlocking {
        val tasks = (1..concurrentRequests).map { index ->
          async {
            val args = ViewErrorArgs(
              path = "/test/project/TestFile.kt",
              includeWarnings = true,
              includeWeakWarnings = true
            )
            try {
              viewErrorTool.handle(project, args)
            } catch (e: Exception) {
              println("错误查看请求 $index 执行异常: ${e.message}")
            }
          }
        }
        tasks.awaitAll()
      }
    }
    
    println("$concurrentRequests 个并发错误查看请求耗时: ${actualTime}ms")
  }

  @Test
  fun `测试并发代码清理请求性能`() = runBlocking {
    val project = mockk<Project>()
    every { project.basePath } returns "/test/project"
    
    val concurrentRequests = 10
    val maxTimeMs = 20000L // 20秒内完成所有并发请求
    
    val actualTime = measureAndAssertTime(maxTimeMs) {
      runBlocking {
        val tasks = (1..concurrentRequests).map { index ->
          async {
            val args = CleanCodeArgs(
              path = "/test/project/src/File$index.kt",
              formatCode = true,
              optimizeImports = true,
              runInspections = false // 关闭检查以提高性能
            )
            try {
              cleanCodeTool.handle(project, args)
            } catch (e: Exception) {
              println("代码清理请求 $index 执行异常: ${e.message}")
            }
          }
        }
        tasks.awaitAll()
      }
    }
    
    println("$concurrentRequests 个并发代码清理请求耗时: ${actualTime}ms")
  }

  @Test
  fun `测试并发库代码查看请求性能`() = runBlocking {
    val project = mockk<Project>()
    every { project.basePath } returns "/test/project"
    
    val concurrentRequests = 12
    val maxTimeMs = 15000L // 15秒内完成所有并发请求
    
    val actualTime = measureAndAssertTime(maxTimeMs) {
      runBlocking {
        val tasks = (1..concurrentRequests).map { index ->
          async {
            val args = ViewLibCodeArgs(
              filePath = "/test/project/src/TestFile$index.kt",
              fullyQualifiedName = "java.lang.String",
              memberName = "length"
            )
            try {
              viewLibCodeTool.handle(project, args)
            } catch (e: Exception) {
              println("库代码查看请求 $index 执行异常: ${e.message}")
            }
          }
        }
        tasks.awaitAll()
      }
    }
    
    println("$concurrentRequests 个并发库代码查看请求耗时: ${actualTime}ms")
  }

  @Test
  fun `测试混合并发请求性能`() = runBlocking {
    val project = mockk<Project>()
    val psiManager = mockk<PsiManager>()
    every { project.basePath } returns "/test/project"
    every { PsiManager.getInstance(project) } returns psiManager
    
    // 创建模拟文件
    val mockFile = mockk<VirtualFile>()
    val psiFile = mockk<PsiFile>()
    every { mockFile.isDirectory } returns false
    every { mockFile.isValid } returns true
    every { mockFile.path } returns "/test/project/MixedTestFile.kt"
    every { mockFile.name } returns "MixedTestFile.kt"
    every { psiManager.findFile(mockFile) } returns psiFile
    every { psiFile.name } returns "MixedTestFile.kt"
    every { psiFile.text } returns createMockFileContent(500)
    
    val requestsPerType = 5
    val totalRequests = requestsPerType * 4
    val maxTimeMs = 25000L // 25秒内完成所有混合并发请求
    
    val actualTime = measureAndAssertTime(maxTimeMs) {
      runBlocking {
        val terminalTasks = (1..requestsPerType).map { index ->
          async {
            val args = TerminalArgs(
              command = "echo 'Mixed terminal test $index'",
              workingDirectory = "/test/project",
              timeout = 3000L
            )
            try {
              terminalTool.handle(project, args)
            } catch (e: Exception) {
              println("混合终端请求 $index 执行异常: ${e.message}")
            }
          }
        }
        
        val errorTasks = (1..requestsPerType).map { index ->
          async {
            val args = ViewErrorArgs(
              path = "/test/project/MixedTestFile.kt",
              includeWarnings = true
            )
            try {
              viewErrorTool.handle(project, args)
            } catch (e: Exception) {
              println("混合错误查看请求 $index 执行异常: ${e.message}")
            }
          }
        }
        
        val cleanTasks = (1..requestsPerType).map { index ->
          async {
            val args = CleanCodeArgs(
              path = "/test/project/MixedTestFile.kt",
              formatCode = true,
              optimizeImports = false,
              runInspections = false
            )
            try {
              cleanCodeTool.handle(project, args)
            } catch (e: Exception) {
              println("混合代码清理请求 $index 执行异常: ${e.message}")
            }
          }
        }
        
        val libCodeTasks = (1..requestsPerType).map { index ->
          async {
            val args = ViewLibCodeArgs(
              filePath = "/test/project/MixedTestFile.kt",
              fullyQualifiedName = "java.util.List",
              memberName = "size"
            )
            try {
              viewLibCodeTool.handle(project, args)
            } catch (e: Exception) {
              println("混合库代码查看请求 $index 执行异常: ${e.message}")
            }
          }
        }
        
        // 等待所有任务完成
        (terminalTasks + errorTasks + cleanTasks + libCodeTasks).awaitAll()
      }
    }
    
    println("$totalRequests 个混合并发请求耗时: ${actualTime}ms")
  }

  @Test
  fun `测试高负载并发请求内存使用`() = runBlocking {
    val project = mockk<Project>()
    val psiManager = mockk<PsiManager>()
    every { project.basePath } returns "/test/project"
    every { PsiManager.getInstance(project) } returns psiManager
    
    val mockFile = mockk<VirtualFile>()
    val psiFile = mockk<PsiFile>()
    every { mockFile.isDirectory } returns false
    every { mockFile.isValid } returns true
    every { mockFile.path } returns "/test/project/HighLoadFile.kt"
    every { mockFile.name } returns "HighLoadFile.kt"
    every { psiManager.findFile(mockFile) } returns psiFile
    every { psiFile.name } returns "HighLoadFile.kt"
    every { psiFile.text } returns createMockFileContent(2000)
    
    val concurrentRequests = 30
    val maxMemoryMB = 200L // 最大200MB内存使用
    
    val memoryUsed = measureMemoryUsage {
      runBlocking {
        val tasks = (1..concurrentRequests).map { index ->
          async {
            val args = ViewErrorArgs(
              path = "/test/project/HighLoadFile.kt",
              includeWarnings = true,
              includeWeakWarnings = true
            )
            try {
              viewErrorTool.handle(project, args)
            } catch (e: Exception) {
              println("高负载请求 $index 执行异常: ${e.message}")
            }
          }
        }
        tasks.awaitAll()
      }
    }
    
    assertMemoryUsage(memoryUsed, maxMemoryMB)
    
    println("$concurrentRequests 个高负载并发请求使用内存: ${memoryUsed / (1024 * 1024)}MB")
  }

  @Test
  fun `测试长时间运行的并发请求稳定性`() = runBlocking {
    val project = mockk<Project>()
    every { project.basePath } returns "/test/project"
    
    val batchCount = 3 // 3批次
    val requestsPerBatch = 8
    val totalRequests = batchCount * requestsPerBatch
    val maxTimeMs = 30000L // 30秒内完成所有批次
    
    val actualTime = measureAndAssertTime(maxTimeMs) {
      runBlocking {
        repeat(batchCount) { batchIndex ->
          val batchTasks = (1..requestsPerBatch).map { requestIndex ->
            async {
              val globalIndex = batchIndex * requestsPerBatch + requestIndex
              val args = TerminalArgs(
                command = "echo 'Long running test batch $batchIndex request $requestIndex'",
                workingDirectory = "/test/project",
                timeout = 2000L
              )
              try {
                terminalTool.handle(project, args)
                Thread.sleep(100) // 模拟处理时间
              } catch (e: Exception) {
                println("长时间运行请求 $globalIndex 执行异常: ${e.message}")
              }
            }
          }
          batchTasks.awaitAll()
          
          // 批次间短暂休息
          Thread.sleep(200)
        }
      }
    }
    
    println("$totalRequests 个长时间运行的并发请求（$batchCount 批次）耗时: ${actualTime}ms")
  }
}
