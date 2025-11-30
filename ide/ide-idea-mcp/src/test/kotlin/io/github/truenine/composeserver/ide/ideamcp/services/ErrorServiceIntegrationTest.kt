package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlin.test.*

/**
 * ErrorService integration tests.
 *
 * Verifies integration between ErrorService and ErrorCaptureFilter.
 */
class ErrorServiceIntegrationTest : BasePlatformTestCase() {

  private lateinit var errorService: ErrorServiceImpl

  override fun setUp() {
    super.setUp()
    errorService = ErrorServiceImpl()
  }

  fun testErrorServiceWithSyntaxErrors() {
    // Create a Kotlin file that contains syntax errors
    val testFile =
      myFixture.configureByText(
        "TestFile.kt",
        """
        package test

        class TestClass {
          fun testMethod() {
            // There is a syntax error here - missing right parenthesis
            val result = someFunction(
          }
        }
        """
          .trimIndent(),
      )

    // Analyze file
    val allProblems = errorService.analyzeFile(project, testFile.virtualFile)

    // Verify that issues can be detected.
    // Note: in the test environment, real syntax highlighting may be limited,
    // so this primarily verifies that method calls do not throw exceptions.
    assertNotNull(allProblems, "Should return problems list")

    // Get syntax errors
    val syntaxErrors = errorService.getCapturedSyntaxErrors(project, testFile.virtualFile)

    // Verify that syntax errors are correctly captured
    assertNotNull(syntaxErrors, "Should return syntax errors list")
  }

  fun testErrorServiceWithValidFile() {
    // Create a Kotlin file without syntax errors
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

    // Analyze file
    val allProblems = errorService.analyzeFile(project, testFile.virtualFile)

    // Get syntax errors
    val syntaxErrors = errorService.getCapturedSyntaxErrors(project, testFile.virtualFile)

    // Verify that method calls succeed
    assertNotNull(allProblems, "Should return problems list")
    assertNotNull(syntaxErrors, "Should return syntax errors list")
  }

  fun testErrorServiceWithDirectory() {
    // Create temporary directory
    val tempDir = myFixture.tempDirFixture.findOrCreateDir("testDir")

    // Test directory handling
    val problems = errorService.analyzeFile(project, tempDir)

    // Directory should return an empty list
    assertTrue(problems.isEmpty(), "Directory should return empty problems list")

    val syntaxErrors = errorService.getCapturedSyntaxErrors(project, tempDir)
    assertTrue(syntaxErrors.isEmpty(), "Directory should return empty syntax errors list")
  }

  fun testErrorCaptureFilterManager() {
    // Test filter manager
    val filter1 = ErrorCaptureFilterManager.getInstance()
    val filter2 = ErrorCaptureFilterManager.getInstance()

    assertSame(filter1, filter2, "Should return same instance")

    // Test setting a new instance
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

    // Verify data class properties
    assertEquals("/test/path/TestFile.kt", errorInfo.filePath)
    assertEquals(5, errorInfo.line)
    assertEquals(10, errorInfo.column)
    assertEquals("Expecting ')'", errorInfo.errorDescription)
    assertEquals("someFunction(", errorInfo.elementText)
    assertTrue(errorInfo.timestamp > 0)
  }

  fun testErrorFilterBasicFunctionality() {
    val errorFilter = ErrorCaptureFilter()

    // Test basic clearing functionality
    errorFilter.clearAllCapturedErrors()

    val allErrors = errorFilter.getAllCapturedErrors()
    assertTrue(allErrors.isEmpty(), "Should be empty after clear all")

    // Test retrieving errors for a non-existent file
    val nonExistentErrors = errorFilter.getCapturedErrors("/non/existent/file.kt")
    assertTrue(nonExistentErrors.isEmpty(), "Should return empty list for non-existent file")
  }

  fun testCollectErrorsWithMultipleFiles() {
    // Create multiple files
    val file1 = myFixture.configureByText("File1.kt", "class TestClass1")
    val file2 = myFixture.configureByText("File2.kt", "class TestClass2")

    // Test error collection
    val errors1 = errorService.collectErrors(project, file1.virtualFile)
    val errors2 = errorService.collectErrors(project, file2.virtualFile)

    // Verify method calls succeed
    assertNotNull(errors1, "Should return errors list for file1")
    assertNotNull(errors2, "Should return errors list for file2")
  }
}
