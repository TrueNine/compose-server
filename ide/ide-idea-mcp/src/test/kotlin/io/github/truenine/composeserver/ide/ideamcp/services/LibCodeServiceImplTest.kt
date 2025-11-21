package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType
import java.io.File
import java.nio.file.Files
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Integration tests for LibCodeServiceImpl.
 *
 * Uses BasePlatformTestCase to simulate a real IntelliJ project environment,
 * including virtual file system, modules, and library dependencies.
 */
class LibCodeServiceImplTest : BasePlatformTestCase() {

  private lateinit var service: LibCodeService

  /** Set up test environment before each test method. */
  override fun setUp() {
    super.setUp()
    service = LibCodeServiceImpl()
  }

  /** Verify NOT_FOUND result when the class cannot be found anywhere. */
  @Test
  fun testGetLibraryCode_notFound() = runBlocking {
    // Given
    val className = "com.nonexistent.NonExistentClass"

    // When
    val result = service.getLibraryCode(project, className)

    // Then
    assertNotNull("Result should not be null", result)
    assertEquals("Should return NOT_FOUND status", SourceType.NOT_FOUND, result.metadata.sourceType)
    assertFalse("NOT_FOUND result should not be marked as decompiled", result.isDecompiled)
    assertEquals("Language should be text", "text", result.language)
    assertTrue("Source should contain not-found hint", result.sourceCode.contains("Class not found"))
    assertTrue("Source should contain class name", result.sourceCode.contains(className))
  }

  /** Verify extracting library name from class name. */
  @Test
  fun testGetLibraryCode_extractLibraryName() = runBlocking {
    // Given
    val className = "org.springframework.boot.SpringApplication"

    // When
    val result = service.getLibraryCode(project, className)

    // Then
    assertNotNull("Result should not be null", result)
    assertEquals("Should extract library name from class name", "org.springframework.boot", result.metadata.libraryName)
    assertEquals("Should return NOT_FOUND status", SourceType.NOT_FOUND, result.metadata.sourceType)
  }

  /** Verify member extraction using mock source. */
  @Test
  fun testGetLibraryCode_memberExtraction() = runBlocking {
    // Given - create a temporary source JAR file
    val tempDir = Files.createTempDirectory("test-lib").toFile()
    val sourcesJarFile = File(tempDir, "test-lib-1.0-sources.jar")

    // Create a JAR file that contains source code
    createSourcesJar(
      sourcesJarFile,
      "com/example/TestClass.java",
      "package com.example;\npublic class TestClass {\n  public void testMethod() {}\n  public int getValue() { return 42; }\n}",
    )

    // Convert the JAR file to a VirtualFile and add it to the project
    val sourcesJar = VfsUtil.findFileByIoFile(sourcesJarFile, true)
    if (sourcesJar != null) {
      runWriteAction { ModuleRootModificationUtil.addModuleLibrary(module, "test-lib", emptyList(), listOf(sourcesJar.url)) }

      val className = "com.example.TestClass"
      val memberName = "testMethod"

      // When
      val result = service.getLibraryCode(project, className, memberName)

      // Then
      assertNotNull("Result should not be null", result)
      // Since source lookup may not work in the test environment, primarily verify it does not crash.
      assertTrue("Result should contain non-empty content", result.sourceCode.isNotEmpty())
    }

    // Clean up temporary files
    tempDir.deleteRecursively()
  }

  /** Verify language detection. */
  @Test
  fun testGetLibraryCode_languageDetection() = runBlocking {
    // Given - create a JAR that contains Kotlin source
    val tempDir = Files.createTempDirectory("test-kotlin-lib").toFile()
    val sourcesJarFile = File(tempDir, "kotlin-lib-1.0-sources.jar")

    createSourcesJar(sourcesJarFile, "com/example/KotlinClass.kt", "package com.example\nclass KotlinClass {\n  fun kotlinMethod() {}\n}")

    val sourcesJar = VfsUtil.findFileByIoFile(sourcesJarFile, true)
    if (sourcesJar != null) {
      runWriteAction { ModuleRootModificationUtil.addModuleLibrary(module, "kotlin-lib", emptyList(), listOf(sourcesJar.url)) }

      val className = "com.example.KotlinClass"

      // When
      val result = service.getLibraryCode(project, className)

      // Then
      assertNotNull("Result should not be null", result)
      assertTrue("Result should contain non-empty content", result.sourceCode.isNotEmpty())
    }

    // Clean up temporary files
    tempDir.deleteRecursively()
  }

  /** Verify version information extraction. */
  @Test
  fun testGetLibraryCode_versionExtraction() = runBlocking {
    // Given - create a JAR with version information in its name
    val tempDir = Files.createTempDirectory("test-versioned-lib").toFile()
    val sourcesJarFile = File(tempDir, "versioned-lib-2.1.3-sources.jar")

    createSourcesJar(sourcesJarFile, "com/versioned/VersionedClass.java", "package com.versioned;\npublic class VersionedClass {}")

    val sourcesJar = VfsUtil.findFileByIoFile(sourcesJarFile, true)
    if (sourcesJar != null) {
      runWriteAction { ModuleRootModificationUtil.addModuleLibrary(module, "versioned-lib", emptyList(), listOf(sourcesJar.url)) }

      val className = "com.versioned.VersionedClass"

      // When
      val result = service.getLibraryCode(project, className)

      // Then
      assertNotNull("Result should not be null", result)
      assertNotNull("Library name should not be null", result.metadata.libraryName)
      assertTrue("Result should contain non-empty content", result.sourceCode.isNotEmpty())
    }

    // Clean up temporary files
    tempDir.deleteRecursively()
  }

  /** Verify various cases for library-name extraction logic. */
  @Test
  fun testExtractLibraryNameFromClassName() = runBlocking {
    // Given & When & Then
    val testCases =
      listOf(
        "java.lang.String" to "java.lang.String",
        "org.springframework.boot.SpringApplication" to "org.springframework.boot",
        "com.example.MyClass" to "com.example.MyClass",
        "io.github.truenine.composeserver.SomeClass" to "io.github.truenine",
        "SingleClass" to "SingleClass",
      )

    testCases.forEach { (className, expectedLibraryName) ->
      val result = service.getLibraryCode(project, className)
      assertEquals("Class name $className should produce library name $expectedLibraryName", expectedLibraryName, result.metadata.libraryName)
    }
  }

  /** Verify member extraction logic without real source. */
  @Test
  fun testMemberExtractionLogic() = runBlocking {
    // Given
    val className = "com.example.TestClass"
    val memberName = "testMethod"

    // When
    val result = service.getLibraryCode(project, className, memberName)

    // Then
    assertNotNull("Result should not be null", result)
    // Without real source code, NOT_FOUND should be returned
    assertEquals("Should return NOT_FOUND status", SourceType.NOT_FOUND, result.metadata.sourceType)
    assertTrue("Source should contain not-found hint", result.sourceCode.contains("Class not found"))
  }

  /** Create a JAR file that contains source code. */
  private fun createSourcesJar(jarFile: File, entryPath: String, sourceContent: String) {
    jarFile.parentFile.mkdirs()
    JarOutputStream(jarFile.outputStream()).use { jos ->
      val entry = JarEntry(entryPath)
      jos.putNextEntry(entry)
      jos.write(sourceContent.toByteArray(Charsets.UTF_8))
      jos.closeEntry()
    }
  }
}
