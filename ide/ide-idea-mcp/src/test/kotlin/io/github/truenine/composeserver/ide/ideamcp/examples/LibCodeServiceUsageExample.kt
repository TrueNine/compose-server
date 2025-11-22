package io.github.truenine.composeserver.ide.ideamcp.examples

import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeServiceImpl
import io.github.truenine.composeserver.ide.ideamcp.testutil.MockDataGenerator
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

/**
 * LibCodeService usage examples.
 *
 * Demonstrates how to use the testing utilities and mock data to verify LibCodeService functionality.
 */
class LibCodeServiceUsageExample {

  @Test
  fun `example - basic usage with mock data`() = runBlocking {
    // Create service instance
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    // Use mock data generator to create test data
    val testClassName = "com.example.TestService"
    val mockResult =
      MockDataGenerator.generateMockLibCodeResult(
        className = testClassName,
        language = "java",
        isDecompiled = false,
        sourceType = SourceType.SOURCE_JAR,
        includeMembers = listOf("processData", "validate", "cleanup"),
      )

    println("=== LibCodeService usage example ===")
    println("Test class: $testClassName")
    println("Generated source length: ${mockResult.sourceCode.length} characters")
    println("Library: ${mockResult.metadata.libraryName}")
    println("Version: ${mockResult.metadata.version}")
    println()

    // Execute actual service call
    val result = libCodeService.getLibraryCode(project = mockProject, fullyQualifiedName = testClassName, memberName = null)

    // Verify result
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    println("Actual service source length returned: ${result.sourceCode.length} characters")
    println("Decompiled: ${result.isDecompiled}")
    println("Language: ${result.language}")
    println("Source type: ${result.metadata.sourceType}")
  }

  @Test
  fun `example - testing with generated jar file`() = runBlocking {
    // Create temporary JAR file for testing
    val testClasses =
      mapOf(
        "com/example/TestClass.java" to
          MockDataGenerator.generateMockJavaSource(
            packageName = "com.example",
            className = "TestClass",
            includeMembers = listOf("execute", "validate", "cleanup"),
          ),
        "com/example/util/Helper.java" to
          MockDataGenerator.generateMockJavaSource(packageName = "com.example.util", className = "Helper", includeMembers = listOf("format", "parse")),
      )

    val tempJarFile = MockDataGenerator.createTempJarFile(testClasses, "example-lib", ".jar")

    println("=== Testing with generated JAR file ===")
    println("Temporary JAR file: ${tempJarFile.absolutePath}")
    println("File size: ${tempJarFile.length()} bytes")
    println("Contained classes:")
    testClasses.keys.forEach { className -> println("  - $className") }

    // Use real JAR file path for testing
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    val result = libCodeService.getLibraryCode(project = mockProject, fullyQualifiedName = "com.example.TestClass", memberName = "execute")

    assertNotNull(result)
    println("Extraction result:")
    println("  Source length: ${result.sourceCode.length}")
    println("  Language: ${result.language}")
    println("  Library: ${result.metadata.libraryName}")
  }

  @Test
  fun `example - performance testing with large source`() = runBlocking {
    // Generate large source for performance testing
    val largeSource = MockDataGenerator.generateLargeSourceCode(className = "LargePerformanceTestClass", methodCount = 50, linesPerMethod = 15)

    println("=== Performance test example ===")
    println("Large source statistics:")
    println("  Total characters: ${largeSource.length}")
    println("  Total lines: ${largeSource.lines().size}")
    println("  Method count: 50")

    // Create JAR file that contains the large class
    val largeClassJar = MockDataGenerator.createTempJarFile(mapOf("com/example/large/LargePerformanceTestClass.java" to largeSource), "large-test", ".jar")

    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    // Measure processing time
    val startTime = System.currentTimeMillis()
    val result = libCodeService.getLibraryCode(project = mockProject, fullyQualifiedName = "com.example.large.LargePerformanceTestClass", memberName = null)
    val endTime = System.currentTimeMillis()

    println("Performance test result:")
    println("  Processing time: ${endTime - startTime}ms")
    println("  Returned source length: ${result.sourceCode.length}")
    println("  Memory usage acceptable: ${result.sourceCode.length < 1024 * 1024}") // less than 1MB
  }

  @Test
  fun `example - testing multiple class names`() = runBlocking {
    // Generate multiple test class names
    val testClassNames = MockDataGenerator.generateTestClassNames(5)

    println("=== Batch test example ===")
    println("Generated test class names:")
    testClassNames.forEach { className -> println("  - $className") }

    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    // Batch testing
    val results = mutableListOf<Pair<String, Long>>()

    testClassNames.forEach { className ->
      val startTime = System.currentTimeMillis()
      val result = libCodeService.getLibraryCode(project = mockProject, fullyQualifiedName = className, memberName = null)
      val endTime = System.currentTimeMillis()

      results.add(className to (endTime - startTime))
      assertNotNull(result)
    }

    println("Batch test results:")
    results.forEach { (className, time) -> println("  $className: ${time}ms") }

    val averageTime = results.map { it.second }.average()
    println("Average processing time: ${averageTime.toInt()}ms")
  }

  @Test
  fun `example - testing different source types`() = runBlocking {
    println("=== Different source type test example ===")

    val sourceTypes = listOf(SourceType.SOURCE_JAR to "from source JAR", SourceType.DECOMPILED to "decompiled code", SourceType.NOT_FOUND to "source not found")

    sourceTypes.forEach { (sourceType, description) ->
      val mockResult =
        MockDataGenerator.generateMockLibCodeResult(
          className = "com.example.${sourceType.name}Class",
          language = "java",
          isDecompiled = sourceType == SourceType.DECOMPILED,
          sourceType = sourceType,
        )

      println("$description:")
      println("  Class name: ${mockResult.metadata.libraryName}")
      println("  Version: ${mockResult.metadata.version ?: "none"}")
      println("  Decompiled: ${mockResult.isDecompiled}")
      println("  Source length: ${mockResult.sourceCode.length}")
      println()
    }
  }

  @Test
  fun `example - kotlin source generation`() = runBlocking {
    // Generate Kotlin source example
    val kotlinSource =
      MockDataGenerator.generateMockKotlinSource(
        packageName = "com.example.kotlin",
        className = "KotlinTestClass",
        includeMembers = listOf("processData", "validate", "transform"),
      )

    println("=== Kotlin source generation example ===")
    println("Generated Kotlin source:")
    println(kotlinSource)
    println()
    println("Source statistics:")
    println("  Total characters: ${kotlinSource.length}")
    println("  Total lines: ${kotlinSource.lines().size}")
    println("  Contains data class: ${kotlinSource.contains("data class")}")
  }
}
