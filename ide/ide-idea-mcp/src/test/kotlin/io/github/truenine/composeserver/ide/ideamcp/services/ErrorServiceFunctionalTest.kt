package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** 功能验证测试 - 验证重构后的错误捕获功能是否正常工作 */
class ErrorServiceFunctionalTest : BasePlatformTestCase() {

  private lateinit var errorService: ErrorService

  override fun setUp() {
    super.setUp()
    errorService = ErrorServiceImpl()
  }

  fun testErrorServiceBasicFunctionality() {
    // 创建一个简单的测试文件
    val testFile =
      myFixture.configureByText(
        "TestClass.kt",
        """
          package test
          
          class TestClass {
            fun sayHello(): String {
              return "Hello, World!"
            }
          }
        """
          .trimIndent(),
      )

    // 测试 analyzeFile 方法
    val errors = errorService.analyzeFile(project, testFile.virtualFile)
    assertNotNull(errors, "analyzeFile should return a list")

    // 测试 collectErrors 方法
    val collectedErrors = errorService.collectErrors(project, testFile.virtualFile)
    assertNotNull(collectedErrors, "collectErrors should return a list")

    // 测试 getCapturedSyntaxErrors 方法
    val syntaxErrors = errorService.getCapturedSyntaxErrors(project, testFile.virtualFile)
    assertNotNull(syntaxErrors, "getCapturedSyntaxErrors should return a list")

    println("✓ All basic ErrorService methods work without throwing exceptions")
  }

  fun testErrorCaptureFilterManager() {
    // 测试单例模式
    val instance1 = ErrorCaptureFilterManager.getInstance()
    val instance2 = ErrorCaptureFilterManager.getInstance()

    assertEquals(instance1, instance2, "Should return the same instance")

    // 测试设置新实例
    val newFilter = ErrorCaptureFilter()
    ErrorCaptureFilterManager.setInstance(newFilter)
    val instance3 = ErrorCaptureFilterManager.getInstance()

    assertEquals(newFilter, instance3, "Should return the set instance")

    println("✓ ErrorCaptureFilterManager singleton functionality works")
  }

  fun testErrorCaptureFilterBasicOperations() {
    val filter = ErrorCaptureFilter()

    // 测试清理操作
    filter.clearAllCapturedErrors()
    val allErrors = filter.getAllCapturedErrors()
    assertTrue(allErrors.isEmpty(), "Should be empty after clearAll")

    // 测试获取不存在文件的错误
    val errors = filter.getCapturedErrors("/non/existent/file")
    assertTrue(errors.isEmpty(), "Should return empty list for non-existent file")

    println("✓ ErrorCaptureFilter basic operations work")
  }

  fun testCapturedErrorInfoDataClass() {
    val errorInfo =
      CapturedErrorInfo(
        filePath = "/test/path.kt",
        line = 1,
        column = 5,
        errorDescription = "Test error",
        elementText = "test",
        timestamp = System.currentTimeMillis(),
      )

    assertEquals("/test/path.kt", errorInfo.filePath)
    assertEquals(1, errorInfo.line)
    assertEquals(5, errorInfo.column)
    assertEquals("Test error", errorInfo.errorDescription)
    assertEquals("test", errorInfo.elementText)
    assertTrue(errorInfo.timestamp > 0)

    println("✓ CapturedErrorInfo data class works correctly")
  }

  fun testErrorSeverityEnumValues() {
    // 验证错误严重程度枚举值
    val severities = ErrorSeverity.values()

    assertTrue(severities.contains(ErrorSeverity.ERROR))
    assertTrue(severities.contains(ErrorSeverity.WARNING))
    assertTrue(severities.contains(ErrorSeverity.WEAK_WARNING))
    assertTrue(severities.contains(ErrorSeverity.INFO))

    println("✓ ErrorSeverity enum contains all expected values")
  }

  fun testErrorServiceWithDirectoryInput() {
    // 创建临时目录
    val tempDir = myFixture.tempDirFixture.findOrCreateDir("testDir")

    // 测试目录输入
    val errors = errorService.analyzeFile(project, tempDir)
    assertTrue(errors.isEmpty(), "Directory should return empty list")

    val collectedErrors = errorService.collectErrors(project, tempDir)
    assertNotNull(collectedErrors, "collectErrors should handle directories")

    println("✓ ErrorService handles directory input correctly")
  }

  fun testIntegrationWithApplicationManager() {
    // 测试在应用程序管理器环境中的工作
    val testFile = myFixture.configureByText("IntegrationTest.kt", "class Test")

    com.intellij.openapi.application.ApplicationManager.getApplication().invokeAndWait {
      val errors = errorService.analyzeFile(project, testFile.virtualFile)
      assertNotNull(errors, "Should work in ApplicationManager context")

      val collected = errorService.collectErrors(project, testFile.virtualFile)
      assertNotNull(collected, "collectErrors should work in ApplicationManager context")
    }

    println("✓ ErrorService works correctly in ApplicationManager context")
  }
}
