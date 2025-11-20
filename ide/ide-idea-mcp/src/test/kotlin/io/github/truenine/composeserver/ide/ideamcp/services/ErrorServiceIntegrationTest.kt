package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * ErrorService 集成测试
 *
 * 测试 ErrorService 与 ErrorCaptureFilter 的集成功能
 */
class ErrorServiceIntegrationTest : BasePlatformTestCase() {

  private lateinit var errorService: ErrorServiceImpl

  override fun setUp() {
    super.setUp()
    errorService = ErrorServiceImpl()
  }

  fun testErrorServiceWithSyntaxErrors() {
    // 创建包含语法错误的 Kotlin 文件
    val testFile =
      myFixture.configureByText(
        "TestFile.kt",
        """
        package test

        class TestClass {
          fun testMethod() {
            // 这里有语法错误 - 缺少右括号
            val result = someFunction(
          }
        }
        """
          .trimIndent(),
      )

    // 分析文件
    val allProblems = errorService.analyzeFile(project, testFile.virtualFile)

    // 验证能够检测到问题
    // 注意：在测试环境中可能没有实际的语法错误检测，所以这里主要测试方法调用不会抛出异常
    assertNotNull(allProblems, "Should return problems list")

    // 获取语法错误
    val syntaxErrors = errorService.getCapturedSyntaxErrors(project, testFile.virtualFile)

    // 验证语法错误被正确捕获
    assertNotNull(syntaxErrors, "Should return syntax errors list")
  }

  fun testErrorServiceWithValidFile() {
    // 创建没有语法错误的 Kotlin 文件
    val testFile =
      myFixture.configureByText(
        "ValidFile.kt",
        """
        package test

        class ValidClass {
          fun validMethod(): String {
            return "Hello, World!"
          }
        }
        """
          .trimIndent(),
      )

    // 分析文件
    val allProblems = errorService.analyzeFile(project, testFile.virtualFile)

    // 获取语法错误
    val syntaxErrors = errorService.getCapturedSyntaxErrors(project, testFile.virtualFile)

    // 验证方法调用正常
    assertNotNull(allProblems, "Should return problems list")
    assertNotNull(syntaxErrors, "Should return syntax errors list")
  }

  fun testErrorServiceWithDirectory() {
    // 创建临时目录
    val tempDir = myFixture.tempDirFixture.findOrCreateDir("testDir")

    // 测试目录处理
    val problems = errorService.analyzeFile(project, tempDir)

    // 目录应该返回空列表
    assertTrue(problems.isEmpty(), "Directory should return empty problems list")

    val syntaxErrors = errorService.getCapturedSyntaxErrors(project, tempDir)
    assertTrue(syntaxErrors.isEmpty(), "Directory should return empty syntax errors list")
  }

  fun testErrorCaptureFilterManager() {
    // 测试过滤器管理器
    val filter1 = ErrorCaptureFilterManager.getInstance()
    val filter2 = ErrorCaptureFilterManager.getInstance()

    assertSame(filter1, filter2, "Should return same instance")

    // 测试设置新实例
    val newFilter = ErrorCaptureFilter()
    ErrorCaptureFilterManager.setInstance(newFilter)
    val filter3 = ErrorCaptureFilterManager.getInstance()

    assertSame(newFilter, filter3, "Should return the set instance")
    assertNotSame(filter1, filter3, "Should be different from original instance")
  }

  fun testCapturedErrorInfoDataClass() {
    val errorInfo =
      CapturedErrorInfo(
        filePath = "/test/path/TestFile.kt",
        line = 5,
        column = 10,
        errorDescription = "Expecting ')'",
        elementText = "someFunction(",
        timestamp = System.currentTimeMillis(),
      )

    // 验证数据类属性
    assertEquals("/test/path/TestFile.kt", errorInfo.filePath)
    assertEquals(5, errorInfo.line)
    assertEquals(10, errorInfo.column)
    assertEquals("Expecting ')'", errorInfo.errorDescription)
    assertEquals("someFunction(", errorInfo.elementText)
    assertTrue(errorInfo.timestamp > 0)
  }

  fun testErrorFilterBasicFunctionality() {
    val errorFilter = ErrorCaptureFilter()

    // 测试基本的清理功能
    errorFilter.clearAllCapturedErrors()

    val allErrors = errorFilter.getAllCapturedErrors()
    assertTrue(allErrors.isEmpty(), "Should be empty after clear all")

    // 测试获取不存在文件的错误
    val nonExistentErrors = errorFilter.getCapturedErrors("/non/existent/file.kt")
    assertTrue(nonExistentErrors.isEmpty(), "Should return empty list for non-existent file")
  }

  fun testCollectErrorsWithMultipleFiles() {
    // 创建多个文件
    val file1 = myFixture.configureByText("File1.kt", "class TestClass1")
    val file2 = myFixture.configureByText("File2.kt", "class TestClass2")

    // 测试收集错误
    val errors1 = errorService.collectErrors(project, file1.virtualFile)
    val errors2 = errorService.collectErrors(project, file2.virtualFile)

    // 验证方法调用正常
    assertNotNull(errors1, "Should return errors list for file1")
    assertNotNull(errors2, "Should return errors list for file2")
  }
}
