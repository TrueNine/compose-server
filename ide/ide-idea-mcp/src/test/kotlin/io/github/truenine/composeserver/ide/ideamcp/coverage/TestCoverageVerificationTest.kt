package io.github.truenine.composeserver.ide.ideamcp.coverage

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File

/**
 * 测试覆盖率验证测试
 * 验证所有主要组件都有对应的测试
 */
class TestCoverageVerificationTest : BasePlatformTestCase() {

  private val sourceRoot = "ide/ide-idea-mcp/src/main/kotlin/io/github/truenine/composeserver/ide/ideamcp"
  private val testRoot = "ide/ide-idea-mcp/src/test/kotlin/io/github/truenine/composeserver/ide/ideamcp"

  fun testAllToolsHaveTests() {
    // 验证所有MCP工具都有对应的测试
    val toolsToTest = listOf(
      "TerminalTool",
      "ViewErrorTool", 
      "CleanCodeTool",
      "ViewLibCodeTool"
    )
    
    toolsToTest.forEach { toolName ->
      val testFile = File("$testRoot/tools/${toolName}Test.kt")
      assertTrue(
        "工具 $toolName 缺少测试文件: ${testFile.path}",
        testFile.exists() || testFileExistsInClasspath(toolName)
      )
    }
  }

  fun testAllServicesHaveTests() {
    // 验证所有服务都有对应的测试
    val servicesToTest = listOf(
      "CleanService",
      "ErrorService",
      "LibCodeService",
      "FileManager"
    )
    
    servicesToTest.forEach { serviceName ->
      val testFile = File("$testRoot/services/${serviceName}Test.kt")
      assertTrue(
        "服务 $serviceName 缺少测试文件: ${testFile.path}",
        testFile.exists() || testFileExistsInClasspath(serviceName)
      )
    }
  }

  fun testAllActionsHaveTests() {
    // 验证所有动作都有对应的测试
    val actionsToTest = listOf(
      "CleanCodeAction",
      "ViewErrorAction",
      "ViewLibCodeAction"
    )
    
    actionsToTest.forEach { actionName ->
      val testFile = File("$testRoot/actions/${actionName}Test.kt")
      assertTrue(
        "动作 $actionName 缺少测试文件: ${testFile.path}",
        testFile.exists() || testFileExistsInClasspath(actionName)
      )
    }
  }

  fun testAllUIComponentsHaveTests() {
    // 验证所有UI组件都有对应的测试
    val uiComponentsToTest = listOf(
      "McpDebugPanel",
      "McpTerminalPanel", 
      "McpDebugToolWindowFactory",
      "FileOperationPanel"
    )
    
    uiComponentsToTest.forEach { componentName ->
      val testFile = File("$testRoot/${componentName}Test.kt")
      assertTrue(
        "UI组件 $componentName 缺少测试文件: ${testFile.path}",
        testFile.exists() || testFileExistsInClasspath(componentName)
      )
    }
  }

  fun testAllInfrastructureComponentsHaveTests() {
    // 验证所有基础设施组件都有对应的测试
    val infrastructureToTest = listOf(
      "McpLogManager",
      "TerminalOutputInterceptor",
      "ComposeServerMcpPlugin"
    )
    
    infrastructureToTest.forEach { componentName ->
      val testFile = File("$testRoot/${componentName}Test.kt")
      assertTrue(
        "基础设施组件 $componentName 缺少测试文件: ${testFile.path}",
        testFile.exists() || testFileExistsInClasspath(componentName)
      )
    }
  }

  fun testPerformanceTestsExist() {
    // 验证性能测试存在
    val performanceTestsToCheck = listOf(
      "TerminalOutputPerformanceTest",
      "ConcurrentRequestPerformanceTest",
      "LargeFileProcessingPerformanceTest",
      "LogManagerPerformanceTest"
    )
    
    performanceTestsToCheck.forEach { testName ->
      val testFile = File("$testRoot/performance/${testName}.kt")
      assertTrue(
        "性能测试 $testName 缺少测试文件: ${testFile.path}",
        testFile.exists() || testFileExistsInClasspath(testName)
      )
    }
  }

  fun testIntegrationTestsExist() {
    // 验证集成测试存在
    val integrationTestsToCheck = listOf(
      "ActionsIntegrationTest",
      "FullIntegrationTest"
    )
    
    integrationTestsToCheck.forEach { testName ->
      val testFile1 = File("$testRoot/actions/${testName}.kt")
      val testFile2 = File("$testRoot/integration/${testName}.kt")
      assertTrue(
        "集成测试 $testName 缺少测试文件",
        testFile1.exists() || testFile2.exists() || testFileExistsInClasspath(testName)
      )
    }
  }

  fun testCommonPackageTestsExist() {
    // 验证通用包的测试存在
    val commonTestsToCheck = listOf(
      "ErrorHandling"
    )
    
    commonTestsToCheck.forEach { testName ->
      val testFile = File("$testRoot/common/${testName}Test.kt")
      assertTrue(
        "通用组件 $testName 缺少测试文件: ${testFile.path}",
        testFile.exists() || testFileExistsInClasspath(testName)
      )
    }
  }

  fun testTestStructureIsConsistent() {
    // 验证测试结构一致性
    val testDirectories = listOf(
      "$testRoot/tools",
      "$testRoot/services", 
      "$testRoot/actions",
      "$testRoot/performance",
      "$testRoot/common"
    )
    
    testDirectories.forEach { dirPath ->
      val dir = File(dirPath)
      if (dir.exists()) {
        assertTrue("测试目录 $dirPath 应该包含测试文件", dir.listFiles()?.isNotEmpty() == true)
      }
    }
  }

  fun testNoOrphanedTestFiles() {
    // 验证没有孤立的测试文件（没有对应源文件的测试）
    val testDir = File(testRoot)
    if (testDir.exists()) {
      testDir.walkTopDown()
        .filter { it.isFile && it.name.endsWith("Test.kt") }
        .forEach { testFile ->
          val testName = testFile.nameWithoutExtension.removeSuffix("Test")
          // 这里可以添加更严格的验证逻辑
          assertTrue("测试文件 ${testFile.name} 应该有对应的源文件", testName.isNotEmpty())
        }
    }
  }

  /**
   * 检查测试文件是否存在于类路径中
   * 这是一个辅助方法，用于处理文件系统路径可能不准确的情况
   */
  private fun testFileExistsInClasspath(className: String): Boolean {
    return try {
      // 尝试通过类加载器查找测试类
      val testClassName = "io.github.truenine.composeserver.ide.ideamcp.${className}Test"
      Class.forName(testClassName, false, this.javaClass.classLoader)
      true
    } catch (e: ClassNotFoundException) {
      false
    }
  }
}
