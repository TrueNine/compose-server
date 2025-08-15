package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.project.Project
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

/** LibCodeService 控制台输出测试 验证服务返回完整内容并在控制台输出详细信息 */
class LibCodeServiceConsoleTest {

  @Test
  fun `应该返回完整的源码内容并输出到控制台`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "java.lang.String"

    println("=== LibCodeService 控制台测试开始 ===")
    println("测试类名: $className")
    println()

    // When
    val startTime = System.currentTimeMillis()
    val result = libCodeService.getLibraryCode(mockProject, className, null)
    val endTime = System.currentTimeMillis()

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertTrue(result.metadata.libraryName.isNotEmpty())

    // 输出完整结果到控制台
    println("=== 查找结果 ===")
    println("类名: ${result.metadata.libraryName}")
    println("版本: ${result.metadata.version ?: "未知"}")
    println("语言: ${result.language}")
    println("源码类型: ${result.metadata.sourceType}")
    println("是否反编译: ${result.isDecompiled}")
    println("源码长度: ${result.sourceCode.length} 字符")
    println("执行时间: ${endTime - startTime}ms")
    println()

    println("=== 源码内容 ===")
    println(result.sourceCode)
    println()

    println("=== 测试完成 ===")
  }

  @Test
  fun `应该返回特定成员的源码内容`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "java.util.ArrayList"
    val memberName = "add"

    println("=== 成员提取测试开始 ===")
    println("测试类名: $className")
    println("成员名: $memberName")
    println()

    // When
    val startTime = System.currentTimeMillis()
    val result = libCodeService.getLibraryCode(mockProject, className, memberName)
    val endTime = System.currentTimeMillis()

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertContains(result.sourceCode, "ArrayList")

    // 输出完整结果到控制台
    println("=== 成员提取结果 ===")
    println("类名: ${result.metadata.libraryName}")
    println("版本: ${result.metadata.version ?: "未知"}")
    println("语言: ${result.language}")
    println("源码类型: ${result.metadata.sourceType}")
    println("是否反编译: ${result.isDecompiled}")
    println("源码长度: ${result.sourceCode.length} 字符")
    println("执行时间: ${endTime - startTime}ms")
    println()

    println("=== 提取的成员源码 ===")
    println(result.sourceCode)
    println()

    println("=== 成员提取测试完成 ===")
  }

  @Test
  fun `应该处理不存在的类并返回适当的信息`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "com.nonexistent.NonExistentClass"

    println("=== 不存在类测试开始 ===")
    println("测试类名: $className")
    println()

    // When
    val startTime = System.currentTimeMillis()
    val result = libCodeService.getLibraryCode(mockProject, className, null)
    val endTime = System.currentTimeMillis()

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())

    // 输出完整结果到控制台
    println("=== 不存在类处理结果 ===")
    println("类名: ${result.metadata.libraryName}")
    println("版本: ${result.metadata.version ?: "未知"}")
    println("语言: ${result.language}")
    println("源码类型: ${result.metadata.sourceType}")
    println("是否反编译: ${result.isDecompiled}")
    println("源码长度: ${result.sourceCode.length} 字符")
    println("执行时间: ${endTime - startTime}ms")
    println()

    println("=== 返回的内容 ===")
    println(result.sourceCode)
    println()

    println("=== 不存在类测试完成 ===")
  }

  @Test
  fun `应该测试多个不同类型的类`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val testClasses = listOf("java.lang.Object", "java.util.HashMap", "java.io.File", "java.time.LocalDateTime")

    println("=== 多类型测试开始 ===")
    println("测试类列表: ${testClasses.joinToString(", ")}")
    println()

    // When & Then
    testClasses.forEach { className ->
      println("--- 测试类: $className ---")

      val startTime = System.currentTimeMillis()
      val result = libCodeService.getLibraryCode(mockProject, className, null)
      val endTime = System.currentTimeMillis()

      assertNotNull(result)
      assertTrue(result.sourceCode.isNotEmpty())

      println("库名: ${result.metadata.libraryName}")
      println("版本: ${result.metadata.version ?: "未知"}")
      println("语言: ${result.language}")
      println("源码类型: ${result.metadata.sourceType}")
      println("是否反编译: ${result.isDecompiled}")
      println("源码长度: ${result.sourceCode.length} 字符")
      println("执行时间: ${endTime - startTime}ms")
      println("源码预览: ${result.sourceCode.take(200)}...")
      println()
    }

    println("=== 多类型测试完成 ===")
  }
}
