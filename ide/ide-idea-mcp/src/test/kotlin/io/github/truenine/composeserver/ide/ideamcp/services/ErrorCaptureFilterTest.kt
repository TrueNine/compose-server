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
 * Tests for ErrorCaptureFilter.
 *
 * Verifies error capturing and filtering behavior.
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
    // Create a mocked PsiErrorElement
    val mockErrorElement = mockk<PsiErrorElement>()
    every { mockErrorElement.errorDescription } returns "Test syntax error"
    every { mockErrorElement.text } returns "invalid_syntax"
    every { mockErrorElement.containingFile } returns null

    // Verify default behavior
    val result = errorFilter.shouldHighlightErrorElement(mockErrorElement)

    assertTrue(result, "Should highlight error element by default")
  }

  fun testErrorCaptureFunctionality() {
    // Create simple mocks to verify basic behavior
    val mockErrorElement = mockk<PsiErrorElement>()

    every { mockErrorElement.errorDescription } returns "Expecting ';'"
    every { mockErrorElement.text } returns "invalid"
    every { mockErrorElement.containingFile } returns null // Simplified test

    // Call the filter method; even without full context the method should execute
    val result = errorFilter.shouldHighlightErrorElement(mockErrorElement)

    // Verify the method executes and returns the expected default value
    assertTrue(result, "Should return true by default for error highlighting")

    // Verify basic functionality of retrieving captured errors
    val capturedErrors = errorFilter.getCapturedErrors("/test/path")
    assertTrue(capturedErrors.isEmpty(), "Should return empty list for non-existent path")
  }

  fun testErrorFilteringInTestContext() {
    // Create test file
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

    // Call filter method
    val shouldHighlight = errorFilter.shouldHighlightErrorElement(mockErrorElement)

    // In test context, incomplete code errors should be suppressed
    assertFalse(shouldHighlight, "Should suppress incomplete code errors in test context")
  }

  fun testErrorFilteringInMarkdownFiles() {
    // Create a Markdown file
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

    // Call filter method
    val shouldHighlight = errorFilter.shouldHighlightErrorElement(mockErrorElement)

    // Syntax errors in Markdown files should be highlighted by default (unless inside code blocks)
    assertTrue(shouldHighlight, "Should highlight errors in markdown files by default")
  }

  fun testClearCapturedErrorsFunctionality() {
    val testFile = myFixture.tempDirFixture.createFile("test.kt", "test content")
    val filePath = testFile.path

    // Manually add some error information
    val errorInfo =
      CapturedErrorInfo(
        filePath = filePath,
        line = 1,
        column = 1,
        errorDescription = "Test error",
        elementText = "test",
        timestamp = System.currentTimeMillis(),
      )

    // Use reflection to access the private field and inject test data
    val capturedErrorsField = ErrorCaptureFilter::class.java.getDeclaredField("capturedErrors")
    capturedErrorsField.isAccessible = true
    @Suppress("UNCHECKED_CAST") val capturedErrors = capturedErrorsField.get(errorFilter) as MutableMap<String, MutableList<CapturedErrorInfo>>
    capturedErrors[filePath] = mutableListOf(errorInfo)

    // Verify that errors are present
    assertFalse(errorFilter.getCapturedErrors(filePath).isEmpty())

    // Clear errors for a specific file
    errorFilter.clearCapturedErrors(filePath)
    assertTrue(errorFilter.getCapturedErrors(filePath).isEmpty())

    // Add errors for multiple files
    capturedErrors["file1.kt"] = mutableListOf(errorInfo)
    capturedErrors["file2.kt"] = mutableListOf(errorInfo)

    // Clear all errors
    errorFilter.clearAllCapturedErrors()
    assertTrue(errorFilter.getAllCapturedErrors().isEmpty())
  }

  fun testErrorCaptureFilterManagerSingleton() {
    val instance1 = ErrorCaptureFilterManager.getInstance()
    val instance2 = ErrorCaptureFilterManager.getInstance()

    assertSame(instance1, instance2, "Should return the same instance")

    // Set a new instance
    val newFilter = ErrorCaptureFilter()
    ErrorCaptureFilterManager.setInstance(newFilter)
    val instance3 = ErrorCaptureFilterManager.getInstance()

    assertSame(newFilter, instance3, "Should return the set instance")
    assertNotSame(instance1, instance3, "Should be different from original instance")
  }
}
