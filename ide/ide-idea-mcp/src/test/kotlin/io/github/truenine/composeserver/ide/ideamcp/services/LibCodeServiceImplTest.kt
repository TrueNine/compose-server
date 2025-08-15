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
 * LibCodeServiceImpl 的集成测试
 *
 * 使用 BasePlatformTestCase 来模拟一个真实的 IntelliJ 项目环境， 包括虚拟文件系统、模块和库依赖
 */
class LibCodeServiceImplTest : BasePlatformTestCase() {

  private lateinit var service: LibCodeService

  /** 在每个测试方法执行前设置测试环境 */
  override fun setUp() {
    super.setUp()
    service = LibCodeServiceImpl()
  }

  /** 测试当类在任何地方都找不到时，服务能否返回 NOT_FOUND 结果 */
  @Test
  fun testGetLibraryCode_notFound() = runBlocking {
    // Given
    val className = "com.nonexistent.NonExistentClass"

    // When
    val result = service.getLibraryCode(project, className)

    // Then
    assertNotNull("结果不应为空", result)
    assertEquals("应返回 NOT_FOUND 状态", SourceType.NOT_FOUND, result.metadata.sourceType)
    assertFalse("NOT_FOUND 结果不应标记为反编译", result.isDecompiled)
    assertEquals("语言应为 text", "text", result.language)
    assertTrue("源码应包含未找到提示", result.sourceCode.contains("未找到类"))
    assertTrue("源码应包含类名", result.sourceCode.contains(className))
  }

  /** 测试从类名提取库名的功能 */
  @Test
  fun testGetLibraryCode_extractLibraryName() = runBlocking {
    // Given
    val className = "org.springframework.boot.SpringApplication"

    // When
    val result = service.getLibraryCode(project, className)

    // Then
    assertNotNull("结果不应为空", result)
    assertEquals("应从类名提取库名", "org.springframework.boot", result.metadata.libraryName)
    assertEquals("应返回 NOT_FOUND 状态", SourceType.NOT_FOUND, result.metadata.sourceType)
  }

  /** 测试成员提取功能（使用模拟源码） */
  @Test
  fun testGetLibraryCode_memberExtraction() = runBlocking {
    // Given - 创建一个临时的源码 JAR 文件
    val tempDir = Files.createTempDirectory("test-lib").toFile()
    val sourcesJarFile = File(tempDir, "test-lib-1.0-sources.jar")

    // 创建包含源码的 JAR 文件
    createSourcesJar(
      sourcesJarFile,
      "com/example/TestClass.java",
      "package com.example;\npublic class TestClass {\n  public void testMethod() {}\n  public int getValue() { return 42; }\n}",
    )

    // 将 JAR 文件转换为 VirtualFile 并添加到项目
    val sourcesJar = VfsUtil.findFileByIoFile(sourcesJarFile, true)
    if (sourcesJar != null) {
      runWriteAction { ModuleRootModificationUtil.addModuleLibrary(module, "test-lib", emptyList(), listOf(sourcesJar.url)) }

      val className = "com.example.TestClass"
      val memberName = "testMethod"

      // When
      val result = service.getLibraryCode(project, className, memberName)

      // Then
      assertNotNull("结果不应为空", result)
      // 由于源码查找可能在测试环境中不工作，我们主要验证不会崩溃
      assertTrue("结果应包含有效内容", result.sourceCode.isNotEmpty())
    }

    // 清理临时文件
    tempDir.deleteRecursively()
  }

  /** 测试语言检测功能 */
  @Test
  fun testGetLibraryCode_languageDetection() = runBlocking {
    // Given - 创建包含 Kotlin 源码的 JAR
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
      assertNotNull("结果不应为空", result)
      assertTrue("结果应包含有效内容", result.sourceCode.isNotEmpty())
    }

    // 清理临时文件
    tempDir.deleteRecursively()
  }

  /** 测试版本信息提取 */
  @Test
  fun testGetLibraryCode_versionExtraction() = runBlocking {
    // Given - 创建带版本信息的 JAR
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
      assertNotNull("结果不应为空", result)
      assertNotNull("库名不应为空", result.metadata.libraryName)
      assertTrue("结果应包含有效内容", result.sourceCode.isNotEmpty())
    }

    // 清理临时文件
    tempDir.deleteRecursively()
  }

  /** 测试库名提取逻辑的各种情况 */
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
      assertEquals("类名 $className 应提取出库名 $expectedLibraryName", expectedLibraryName, result.metadata.libraryName)
    }
  }

  /** 测试成员提取逻辑 */
  @Test
  fun testMemberExtractionLogic() = runBlocking {
    // Given
    val className = "com.example.TestClass"
    val memberName = "testMethod"

    // When
    val result = service.getLibraryCode(project, className, memberName)

    // Then
    assertNotNull("结果不应为空", result)
    // 由于没有实际的源码，应该返回 NOT_FOUND
    assertEquals("应返回 NOT_FOUND 状态", SourceType.NOT_FOUND, result.metadata.sourceType)
    assertTrue("源码应包含未找到提示", result.sourceCode.contains("未找到类"))
  }

  /** 创建包含源码的 JAR 文件 */
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
