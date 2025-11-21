package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Functional tests verifying refactored error-capture behavior. */
class ErrorServiceFunctionalTest : BasePlatformTestCase() {

  private lateinit var errorService: ErrorService

  override fun setUp() {
    super.setUp()
    errorService = ErrorServiceImpl()
  }

  fun testErrorServiceBasicFunctionality() {
    // Create a simple test file
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

    // Test analyzeFile method
    val errors = errorService.analyzeFile(project, testFile.virtualFile)
    assertNotNull(errors, "analyzeFile should return a list")

    // Test collectErrors method
    val collectedErrors = errorService.collectErrors(project, testFile.virtualFile)
    assertNotNull(collectedErrors, "collectErrors should return a list")

    // Test getCapturedSyntaxErrors method
    val syntaxErrors = errorService.getCapturedSyntaxErrors(project, testFile.virtualFile)
    assertNotNull(syntaxErrors, "getCapturedSyntaxErrors should return a list")

    println("[PASS] All basic ErrorService methods work without throwing exceptions")
  }

  fun testErrorCaptureFilterManager() {
    // Test singleton behavior
    val instance1 = ErrorCaptureFilterManager.getInstance()
    val instance2 = ErrorCaptureFilterManager.getInstance()

    assertEquals(instance1, instance2, "Should return the same instance")

    // Test setting a new instance
    val newFilter = ErrorCaptureFilter()
    ErrorCaptureFilterManager.setInstance(newFilter)
    val instance3 = ErrorCaptureFilterManager.getInstance()

    assertEquals(newFilter, instance3, "Should return the set instance")

    println("[PASS] ErrorCaptureFilterManager singleton functionality works")
  }

  fun testErrorCaptureFilterBasicOperations() {
    val filter = ErrorCaptureFilter()

    // Test clear operations
    filter.clearAllCapturedErrors()
    val allErrors = filter.getAllCapturedErrors()
    assertTrue(allErrors.isEmpty(), "Should be empty after clearAll")

    // Test retrieving errors for a non-existent file
    val errors = filter.getCapturedErrors("/non/existent/file")
    assertTrue(errors.isEmpty(), "Should return empty list for non-existent file")

    println("[PASS] ErrorCaptureFilter basic operations work")
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

    println("[PASS] CapturedErrorInfo data class works correctly")
  }

  fun testErrorSeverityEnumValues() {
    // Verify error severity enum values
    val severities = ErrorSeverity.values()

    assertTrue(severities.contains(ErrorSeverity.ERROR))
    assertTrue(severities.contains(ErrorSeverity.WARNING))
    assertTrue(severities.contains(ErrorSeverity.WEAK_WARNING))
    assertTrue(severities.contains(ErrorSeverity.INFO))

    println("[PASS] ErrorSeverity enum contains all expected values")
  }

  fun testErrorServiceWithDirectoryInput() {
    // Create temporary directory
    val tempDir = myFixture.tempDirFixture.findOrCreateDir("testDir")

    // Test directory input
    val errors = errorService.analyzeFile(project, tempDir)
    assertTrue(errors.isEmpty(), "Directory should return empty list")

    val collectedErrors = errorService.collectErrors(project, tempDir)
    assertNotNull(collectedErrors, "collectErrors should handle directories")

    println("[PASS] ErrorService handles directory input correctly")
  }

  fun testIntegrationWithApplicationManager() {
    // Test behavior within ApplicationManager context
    val testFile = myFixture.configureByText("IntegrationTest.kt", "class Test")

    com.intellij.openapi.application.ApplicationManager.getApplication().invokeAndWait {
      val errors = errorService.analyzeFile(project, testFile.virtualFile)
      assertNotNull(errors, "Should work in ApplicationManager context")

      val collected = errorService.collectErrors(project, testFile.virtualFile)
      assertNotNull(collected, "collectErrors should work in ApplicationManager context")
    }

    println("[PASS] ErrorService works correctly in ApplicationManager context")
  }
}
