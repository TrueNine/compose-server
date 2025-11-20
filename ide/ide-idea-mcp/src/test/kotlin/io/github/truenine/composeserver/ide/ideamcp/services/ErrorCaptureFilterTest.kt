package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * 错误捕获过滤器测试类
 *
 * 测试 ErrorCaptureFilter 的错误捕获和过滤功能
 */
class ErrorCaptureFilterTest : BasePlatformTestCase() {

  private lateinit var errorFilter: ErrorCaptureFilter

  override fun setUp() {
    super.setUp()
    errorFilter = ErrorCaptureFilter()
  }

  override fun tearDown() {
    errorFilter.clearAllCapturedErrors()
    super.tearDown()
  }

  fun testShouldHighlightErrorElementReturnsTrueByDefault() {
    // 创建模拟的 PsiErrorElement
    val mockErrorElement = mockk<PsiErrorElement>()
    every { mockErrorElement.errorDescription } returns "Test syntax error"
    every { mockErrorElement.text } returns "invalid_syntax"
    every { mockErrorElement.containingFile } returns null

    // 测试默认行为
    val result = errorFilter.shouldHighlightErrorElement(mockErrorElement)

    assertTrue(result, "Should highlight error element by default")
  }

  fun testErrorCaptureFunctionality() {
    // 创建简单的模拟对象，测试基本功能
    val mockErrorElement = mockk<PsiErrorElement>()

    every { mockErrorElement.errorDescription } returns "Expecting ';'"
    every { mockErrorElement.text } returns "invalid"
    every { mockErrorElement.containingFile } returns null // 简化测试

    // 调用过滤器方法，即使没有完整的上下文，方法也应该能正常执行
    val result = errorFilter.shouldHighlightErrorElement(mockErrorElement)

    // 验证方法正常执行并返回预期的默认值
    assertTrue(result, "Should return true by default for error highlighting")

    // 测试获取捕获错误的基本功能
    val capturedErrors = errorFilter.getCapturedErrors("/test/path")
    assertTrue(capturedErrors.isEmpty(), "Should return empty list for non-existent path")
  }

  fun testErrorFilteringInTestContext() {
    // 创建测试文件
    val testFile =
      myFixture.tempDirFixture.createFile(
        "TestClass.kt",
        """
        class TestClass {
          // incomplete code for testing
          fun testMethod(
        }
        """
          .trimIndent(),
      )

    val mockErrorElement = mockk<PsiErrorElement>()
    val mockPsiFile = mockk<PsiFile>()

    every { mockErrorElement.errorDescription } returns "incomplete code"
    every { mockErrorElement.text } returns "testMethod("
    every { mockErrorElement.textOffset } returns 50
    every { mockErrorElement.containingFile } returns mockPsiFile
    every { mockPsiFile.virtualFile } returns testFile
    every { mockPsiFile.name } returns "TestClass.kt"
    every { mockPsiFile.project } returns project

    // 调用过滤器方法
    val shouldHighlight = errorFilter.shouldHighlightErrorElement(mockErrorElement)

    // 在测试上下文中，不完整的代码错误应该被抑制
    assertFalse(shouldHighlight, "Should suppress incomplete code errors in test context")
  }

  fun testErrorFilteringInMarkdownFiles() {
    // 创建 Markdown 文件
    val markdownFile =
      myFixture.tempDirFixture.createFile(
        "README.md",
        """
        # Test Document

        ```kotlin
        // This code might have syntax errors
        fun incomplete(
        ```
        """
          .trimIndent(),
      )

    val mockErrorElement = mockk<PsiErrorElement>()
    val mockPsiFile = mockk<PsiFile>()

    every { mockErrorElement.errorDescription } returns "Expecting ')'"
    every { mockErrorElement.text } returns "incomplete("
    every { mockErrorElement.textOffset } returns 80
    every { mockErrorElement.containingFile } returns mockPsiFile
    every { mockPsiFile.virtualFile } returns markdownFile
    every { mockPsiFile.name } returns "README.md"
    every { mockPsiFile.project } returns project

    // 调用过滤器方法
    val shouldHighlight = errorFilter.shouldHighlightErrorElement(mockErrorElement)

    // Markdown 文件中的语法错误应该正常显示（除非在代码块中）
    assertTrue(shouldHighlight, "Should highlight errors in markdown files by default")
  }

  fun testClearCapturedErrorsFunctionality() {
    val testFile = myFixture.tempDirFixture.createFile("test.kt", "test content")
    val filePath = testFile.path

    // 手动添加一些错误信息
    val errorInfo =
      CapturedErrorInfo(
        filePath = filePath,
        line = 1,
        column = 1,
        errorDescription = "Test error",
        elementText = "test",
        timestamp = System.currentTimeMillis(),
      )

    // 通过反射访问私有字段来添加测试数据
    val capturedErrorsField = ErrorCaptureFilter::class.java.getDeclaredField("capturedErrors")
    capturedErrorsField.isAccessible = true
    @Suppress("UNCHECKED_CAST") val capturedErrors = capturedErrorsField.get(errorFilter) as MutableMap<String, MutableList<CapturedErrorInfo>>
    capturedErrors[filePath] = mutableListOf(errorInfo)

    // 验证错误存在
    assertFalse(errorFilter.getCapturedErrors(filePath).isEmpty())

    // 清除特定文件的错误
    errorFilter.clearCapturedErrors(filePath)
    assertTrue(errorFilter.getCapturedErrors(filePath).isEmpty())

    // 添加多个文件的错误
    capturedErrors["file1.kt"] = mutableListOf(errorInfo)
    capturedErrors["file2.kt"] = mutableListOf(errorInfo)

    // 清除所有错误
    errorFilter.clearAllCapturedErrors()
    assertTrue(errorFilter.getAllCapturedErrors().isEmpty())
  }

  fun testErrorCaptureFilterManagerSingleton() {
    val instance1 = ErrorCaptureFilterManager.getInstance()
    val instance2 = ErrorCaptureFilterManager.getInstance()

    assertSame(instance1, instance2, "Should return the same instance")

    // 设置新实例
    val newFilter = ErrorCaptureFilter()
    ErrorCaptureFilterManager.setInstance(newFilter)
    val instance3 = ErrorCaptureFilterManager.getInstance()

    assertSame(newFilter, instance3, "Should return the set instance")
    assertNotSame(instance1, instance3, "Should be different from original instance")
  }
}
