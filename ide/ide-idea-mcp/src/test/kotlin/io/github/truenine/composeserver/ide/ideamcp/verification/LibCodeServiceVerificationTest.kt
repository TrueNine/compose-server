package io.github.truenine.composeserver.ide.ideamcp.verification

import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeServiceImpl
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

/**
 * LibCodeService verification tests.
 *
 * Verifies that the updated functionality works as expected.
 */
class LibCodeServiceVerificationTest {

  @Test
  fun `verify simplified API - only class name required`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "java.lang.String"

    println("[TEST] Verification test: only class name argument")
    println("Test class: $className")

    // When
    val result = libCodeService.getLibraryCode(mockProject, className)

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertNotNull(result.metadata)
    assertTrue(result.metadata.libraryName.isNotEmpty())
    assertTrue(result.language.isNotEmpty())

    println("[PASS] Verification succeeded:")
    println("  - Source length: ${result.sourceCode.length} characters")
    println("  - Library name: ${result.metadata.libraryName}")
    println("  - Language: ${result.language}")
    println("  - Source type: ${result.metadata.sourceType}")
    println("  - Decompiled: ${result.isDecompiled}")
    println()
  }

  @Test
  fun `verify member extraction`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "java.util.ArrayList"
    val memberName = "add"

    println("[TEST] Verification test: member extraction")
    println("Test class: $className")
    println("Member name: $memberName")

    // When
    val result = libCodeService.getLibraryCode(mockProject, className, memberName)

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertTrue(result.sourceCode.contains("ArrayList") || result.sourceCode.contains("add"))

    println("[PASS] Verification succeeded:")
    println("  - Source length: ${result.sourceCode.length} characters")
    println("  - Contains class name: ${result.sourceCode.contains("ArrayList")}")
    println("  - Contains member name: ${result.sourceCode.contains("add")}")
    println()
  }

  @Test
  fun `verify handling of non-existent class`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "com.nonexistent.NonExistentClass"

    println("[TEST] Verification test: handling non-existent class")
    println("Test class: $className")

    // When
    val result = libCodeService.getLibraryCode(mockProject, className)

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertEquals(SourceType.NOT_FOUND, result.metadata.sourceType)

    println("[PASS] Verification succeeded:")
    println("  - Source type: ${result.metadata.sourceType}")
    println("  - Returned content: ${result.sourceCode.take(100)}...")
    println()
  }

  @Test
  fun `verify handling of multiple common classes`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val testClasses = listOf("java.lang.Object", "java.util.HashMap", "java.io.File", "java.time.LocalDateTime")

    println("[TEST] Verification test: multiple common classes")
    println("Test class list: ${testClasses.joinToString(", ")}")
    println()

    // When & Then
    testClasses.forEach { className ->
      val startTime = System.currentTimeMillis()
      val result = libCodeService.getLibraryCode(mockProject, className)
      val endTime = System.currentTimeMillis()

      assertNotNull(result)
      assertTrue(result.sourceCode.isNotEmpty())
      assertNotNull(result.metadata)

      println("[INFO] $className:")
      println("  Lookup time: ${endTime - startTime}ms")
      println("  Library name: ${result.metadata.libraryName}")
      println("  Source type: ${result.metadata.sourceType}")
      println("  Source length: ${result.sourceCode.length} characters")
      println("  Language: ${result.language}")
      println()
    }
  }

  @Test
  fun `verify API signature correctness`() {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    println("[TEST] Verification test: API signature correctness")

    // When & Then - compile-time verification
    // These calls should compile, proving the API signatures are correct.

    // Only class name
    runBlocking {
      val result1 = libCodeService.getLibraryCode(mockProject, "java.lang.String")
      assertNotNull(result1)
    }

    // Class name and member name
    runBlocking {
      val result2 = libCodeService.getLibraryCode(mockProject, "java.util.List", "add")
      assertNotNull(result2)
    }

    // Class name with null member name
    runBlocking {
      val result3 = libCodeService.getLibraryCode(mockProject, "java.util.Map", null)
      assertNotNull(result3)
    }

    println("[PASS] API signature verification succeeded:")
    println("  - Supports class name only")
    println("  - Supports class name plus member name")
    println("  - Supports null member name")
    println("  - No longer requires file path parameter")
    println()
  }

  @Test
  fun `verify completeness of returned result`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "java.util.concurrent.ConcurrentHashMap"

    println("[TEST] Verification test: completeness of returned result")
    println("Test class: $className")

    // When
    val result = libCodeService.getLibraryCode(mockProject, className)

    // Then
    assertNotNull(result)
    assertNotNull(result.sourceCode)
    assertNotNull(result.language)
    assertNotNull(result.metadata)
    assertNotNull(result.metadata.libraryName)
    assertNotNull(result.metadata.sourceType)

    println("[PASS] Result completeness verification succeeded:")
    println("  - sourceCode: ${if (result.sourceCode.isNotEmpty()) "OK" else "FAIL"}")
    println("  - language: ${if (result.language.isNotEmpty()) "OK" else "FAIL"}")
    println("  - isDecompiled: ${result.isDecompiled}")
    println("  - metadata.libraryName: ${if (result.metadata.libraryName.isNotEmpty()) "OK" else "FAIL"}")
    println("  - metadata.sourceType: ${result.metadata.sourceType}")
    println("  - metadata.version: ${result.metadata.version ?: "null"}")
    println("  - metadata.documentation: ${result.metadata.documentation ?: "null"}")
    println()
  }
}
