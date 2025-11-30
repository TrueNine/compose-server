package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.project.Project
import io.mockk.mockk
import kotlin.test.*
import kotlinx.coroutines.runBlocking

/** LibCodeService console output tests. Verifies that the service returns complete content and prints detailed information to the console. */
class LibCodeServiceConsoleTest {

  @Test
  fun `should return full source code and print to console`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "java.lang.String"

    println("=== LibCodeService console test start ===")
    println("Test class: $className")
    println()

    // When
    val startTime = System.currentTimeMillis()
    val result = libCodeService.getLibraryCode(mockProject, className, null)
    val endTime = System.currentTimeMillis()

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertTrue(result.metadata.libraryName.isNotEmpty())

    // Print full result to console
    println("=== Lookup result ===")
    println("Library: ${result.metadata.libraryName}")
    println("Version: ${result.metadata.version ?: "unknown"}")
    println("Language: ${result.language}")
    println("Source type: ${result.metadata.sourceType}")
    println("Decompiled: ${result.isDecompiled}")
    println("Source length: ${result.sourceCode.length} characters")
    println("Execution time: ${endTime - startTime}ms")
    println()

    println("=== Source code ===")
    println(result.sourceCode)
    println()

    println("=== Test finished ===")
  }

  @Test
  fun `should return source code for specific member`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "java.util.ArrayList"
    val memberName = "add"

    println("=== Member extraction test start ===")
    println("Test class: $className")
    println("Member name: $memberName")
    println()

    // When
    val startTime = System.currentTimeMillis()
    val result = libCodeService.getLibraryCode(mockProject, className, memberName)
    val endTime = System.currentTimeMillis()

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertContains(result.sourceCode, "ArrayList")

    // Print full result to console
    println("=== Member extraction result ===")
    println("Library: ${result.metadata.libraryName}")
    println("Version: ${result.metadata.version ?: "unknown"}")
    println("Language: ${result.language}")
    println("Source type: ${result.metadata.sourceType}")
    println("Decompiled: ${result.isDecompiled}")
    println("Source length: ${result.sourceCode.length} characters")
    println("Execution time: ${endTime - startTime}ms")
    println()

    println("=== Extracted member source ===")
    println(result.sourceCode)
    println()

    println("=== Member extraction test finished ===")
  }

  @Test
  fun `should handle non-existent class and return appropriate info`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "com.nonexistent.NonExistentClass"

    println("=== Non-existent class test start ===")
    println("Test class: $className")
    println()

    // When
    val startTime = System.currentTimeMillis()
    val result = libCodeService.getLibraryCode(mockProject, className, null)
    val endTime = System.currentTimeMillis()

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())

    // Print full result to console
    println("=== Non-existent class result ===")
    println("Library: ${result.metadata.libraryName}")
    println("Version: ${result.metadata.version ?: "unknown"}")
    println("Language: ${result.language}")
    println("Source type: ${result.metadata.sourceType}")
    println("Decompiled: ${result.isDecompiled}")
    println("Source length: ${result.sourceCode.length} characters")
    println("Execution time: ${endTime - startTime}ms")
    println()

    println("=== Returned content ===")
    println(result.sourceCode)
    println()

    println("=== Non-existent class test finished ===")
  }

  @Test
  fun `should test multiple different class types`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val testClasses = listOf("java.lang.Object", "java.util.HashMap", "java.io.File", "java.time.LocalDateTime")

    println("=== Multi-type test start ===")
    println("Test class list: ${testClasses.joinToString(", ")}")
    println()

    // When & Then
    testClasses.forEach { className ->
      println("--- Testing class: $className ---")

      val startTime = System.currentTimeMillis()
      val result = libCodeService.getLibraryCode(mockProject, className, null)
      val endTime = System.currentTimeMillis()

      assertNotNull(result)
      assertTrue(result.sourceCode.isNotEmpty())

      println("Library: ${result.metadata.libraryName}")
      println("Version: ${result.metadata.version ?: "unknown"}")
      println("Language: ${result.language}")
      println("Source type: ${result.metadata.sourceType}")
      println("Decompiled: ${result.isDecompiled}")
      println("Source length: ${result.sourceCode.length} characters")
      println("Execution time: ${endTime - startTime}ms")
      println("Source preview: ${result.sourceCode.take(200)}...")
      println()
    }

    println("=== Multi-type test finished ===")
  }
}
