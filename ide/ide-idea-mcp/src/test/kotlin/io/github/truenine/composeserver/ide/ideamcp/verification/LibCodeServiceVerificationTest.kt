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

/** LibCodeService 验证测试 验证修改后的功能是否正常工作 */
class LibCodeServiceVerificationTest {

  @Test
  fun `验证接口简化 - 只需要传入类名`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "java.lang.String"

    println("🔍 验证测试: 只传入类名参数")
    println("测试类名: $className")

    // When
    val result = libCodeService.getLibraryCode(mockProject, className)

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertNotNull(result.metadata)
    assertTrue(result.metadata.libraryName.isNotEmpty())
    assertTrue(result.language.isNotEmpty())

    println("✅ 验证成功:")
    println("  - 源码长度: ${result.sourceCode.length} 字符")
    println("  - 库名: ${result.metadata.libraryName}")
    println("  - 语言: ${result.language}")
    println("  - 源码类型: ${result.metadata.sourceType}")
    println("  - 是否反编译: ${result.isDecompiled}")
    println()
  }

  @Test
  fun `验证成员提取功能`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "java.util.ArrayList"
    val memberName = "add"

    println("🔍 验证测试: 成员提取功能")
    println("测试类名: $className")
    println("成员名: $memberName")

    // When
    val result = libCodeService.getLibraryCode(mockProject, className, memberName)

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertTrue(result.sourceCode.contains("ArrayList") || result.sourceCode.contains("add"))

    println("✅ 验证成功:")
    println("  - 源码长度: ${result.sourceCode.length} 字符")
    println("  - 包含类名: ${result.sourceCode.contains("ArrayList")}")
    println("  - 包含成员名: ${result.sourceCode.contains("add")}")
    println()
  }

  @Test
  fun `验证不存在类的处理`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "com.nonexistent.NonExistentClass"

    println("🔍 验证测试: 不存在类的处理")
    println("测试类名: $className")

    // When
    val result = libCodeService.getLibraryCode(mockProject, className)

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertEquals(SourceType.NOT_FOUND, result.metadata.sourceType)

    println("✅ 验证成功:")
    println("  - 源码类型: ${result.metadata.sourceType}")
    println("  - 返回内容: ${result.sourceCode.take(100)}...")
    println()
  }

  @Test
  fun `验证多个常用类的处理`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val testClasses = listOf("java.lang.Object", "java.util.HashMap", "java.io.File", "java.time.LocalDateTime")

    println("🔍 验证测试: 多个常用类的处理")
    println("测试类列表: ${testClasses.joinToString(", ")}")
    println()

    // When & Then
    testClasses.forEach { className ->
      val startTime = System.currentTimeMillis()
      val result = libCodeService.getLibraryCode(mockProject, className)
      val endTime = System.currentTimeMillis()

      assertNotNull(result)
      assertTrue(result.sourceCode.isNotEmpty())
      assertNotNull(result.metadata)

      println("📋 $className:")
      println("  ⏱️  查找耗时: ${endTime - startTime}ms")
      println("  📦 库名: ${result.metadata.libraryName}")
      println("  📄 源码类型: ${result.metadata.sourceType}")
      println("  📏 源码长度: ${result.sourceCode.length} 字符")
      println("  🔤 语言: ${result.language}")
      println()
    }
  }

  @Test
  fun `验证接口签名正确性`() {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    println("🔍 验证测试: 接口签名正确性")

    // When & Then - 编译时验证
    // 这些调用应该能够编译通过，证明接口签名正确

    // 只传入类名
    runBlocking {
      val result1 = libCodeService.getLibraryCode(mockProject, "java.lang.String")
      assertNotNull(result1)
    }

    // 传入类名和成员名
    runBlocking {
      val result2 = libCodeService.getLibraryCode(mockProject, "java.util.List", "add")
      assertNotNull(result2)
    }

    // 传入类名，成员名为null
    runBlocking {
      val result3 = libCodeService.getLibraryCode(mockProject, "java.util.Map", null)
      assertNotNull(result3)
    }

    println("✅ 接口签名验证成功:")
    println("  - 支持只传入类名")
    println("  - 支持传入类名和成员名")
    println("  - 支持成员名为null")
    println("  - 不再需要文件路径参数")
    println()
  }

  @Test
  fun `验证返回结果的完整性`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "java.util.concurrent.ConcurrentHashMap"

    println("🔍 验证测试: 返回结果的完整性")
    println("测试类名: $className")

    // When
    val result = libCodeService.getLibraryCode(mockProject, className)

    // Then
    assertNotNull(result)
    assertNotNull(result.sourceCode)
    assertNotNull(result.language)
    assertNotNull(result.metadata)
    assertNotNull(result.metadata.libraryName)
    assertNotNull(result.metadata.sourceType)

    println("✅ 结果完整性验证成功:")
    println("  - sourceCode: ${if (result.sourceCode.isNotEmpty()) "✓" else "✗"}")
    println("  - language: ${if (result.language.isNotEmpty()) "✓" else "✗"}")
    println("  - isDecompiled: ${result.isDecompiled}")
    println("  - metadata.libraryName: ${if (result.metadata.libraryName.isNotEmpty()) "✓" else "✗"}")
    println("  - metadata.sourceType: ${result.metadata.sourceType}")
    println("  - metadata.version: ${result.metadata.version ?: "null"}")
    println("  - metadata.documentation: ${result.metadata.documentation ?: "null"}")
    println()
  }
}
