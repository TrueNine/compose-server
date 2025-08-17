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
 * LibCodeService 使用示例
 *
 * 展示如何使用创建的测试工具和模拟数据来测试 LibCodeService 功能
 */
class LibCodeServiceUsageExample {

  @Test
  fun `example - basic usage with mock data`() = runBlocking {
    // 创建服务实例
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    // 使用模拟数据生成器创建测试数据
    val testClassName = "com.example.TestService"
    val mockResult =
      MockDataGenerator.generateMockLibCodeResult(
        className = testClassName,
        language = "java",
        isDecompiled = false,
        sourceType = SourceType.SOURCE_JAR,
        includeMembers = listOf("processData", "validate", "cleanup"),
      )

    println("=== LibCodeService 使用示例 ===")
    println("测试类名: $testClassName")
    println("生成的源码长度: ${mockResult.sourceCode.length} 字符")
    println("库名: ${mockResult.metadata.libraryName}")
    println("版本: ${mockResult.metadata.version}")
    println()

    // 执行实际的服务调用
    val result = libCodeService.getLibraryCode(project = mockProject, fullyQualifiedName = testClassName, memberName = null)

    // 验证结果
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    println("实际服务返回的源码长度: ${result.sourceCode.length} 字符")
    println("是否反编译: ${result.isDecompiled}")
    println("语言: ${result.language}")
    println("源码类型: ${result.metadata.sourceType}")
  }

  @Test
  fun `example - testing with generated jar file`() = runBlocking {
    // 创建临时JAR文件用于测试
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

    println("=== 使用生成的JAR文件测试 ===")
    println("临时JAR文件: ${tempJarFile.absolutePath}")
    println("文件大小: ${tempJarFile.length()} 字节")
    println("包含的类:")
    testClasses.keys.forEach { className -> println("  - $className") }

    // 使用真实的JAR文件路径进行测试
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    val result = libCodeService.getLibraryCode(project = mockProject, fullyQualifiedName = "com.example.TestClass", memberName = "execute")

    assertNotNull(result)
    println("提取结果:")
    println("  源码长度: ${result.sourceCode.length}")
    println("  语言: ${result.language}")
    println("  库名: ${result.metadata.libraryName}")
  }

  @Test
  fun `example - performance testing with large source`() = runBlocking {
    // 生成大型源码用于性能测试
    val largeSource = MockDataGenerator.generateLargeSourceCode(className = "LargePerformanceTestClass", methodCount = 50, linesPerMethod = 15)

    println("=== 性能测试示例 ===")
    println("大型源码统计:")
    println("  总字符数: ${largeSource.length}")
    println("  总行数: ${largeSource.lines().size}")
    println("  包含方法数: 50")

    // 创建包含大型类的JAR文件
    val largeClassJar = MockDataGenerator.createTempJarFile(mapOf("com/example/large/LargePerformanceTestClass.java" to largeSource), "large-test", ".jar")

    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    // 测量处理时间
    val startTime = System.currentTimeMillis()
    val result = libCodeService.getLibraryCode(project = mockProject, fullyQualifiedName = "com.example.large.LargePerformanceTestClass", memberName = null)
    val endTime = System.currentTimeMillis()

    println("性能测试结果:")
    println("  处理时间: ${endTime - startTime}ms")
    println("  返回源码长度: ${result.sourceCode.length}")
    println("  内存使用合理: ${result.sourceCode.length < 1024 * 1024}") // 小于1MB
  }

  @Test
  fun `example - testing multiple class names`() = runBlocking {
    // 生成多个测试类名
    val testClassNames = MockDataGenerator.generateTestClassNames(5)

    println("=== 批量测试示例 ===")
    println("生成的测试类名:")
    testClassNames.forEach { className -> println("  - $className") }

    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    // 批量测试
    val results = mutableListOf<Pair<String, Long>>()

    testClassNames.forEach { className ->
      val startTime = System.currentTimeMillis()
      val result = libCodeService.getLibraryCode(project = mockProject, fullyQualifiedName = className, memberName = null)
      val endTime = System.currentTimeMillis()

      results.add(className to (endTime - startTime))
      assertNotNull(result)
    }

    println("批量测试结果:")
    results.forEach { (className, time) -> println("  $className: ${time}ms") }

    val averageTime = results.map { it.second }.average()
    println("平均处理时间: ${averageTime.toInt()}ms")
  }

  @Test
  fun `example - testing different source types`() = runBlocking {
    println("=== 不同源码类型测试示例 ===")

    val sourceTypes = listOf(SourceType.SOURCE_JAR to "来自源码JAR", SourceType.DECOMPILED to "反编译代码", SourceType.NOT_FOUND to "未找到源码")

    sourceTypes.forEach { (sourceType, description) ->
      val mockResult =
        MockDataGenerator.generateMockLibCodeResult(
          className = "com.example.${sourceType.name}Class",
          language = "java",
          isDecompiled = sourceType == SourceType.DECOMPILED,
          sourceType = sourceType,
        )

      println("$description:")
      println("  类名: ${mockResult.metadata.libraryName}")
      println("  版本: ${mockResult.metadata.version ?: "无"}")
      println("  是否反编译: ${mockResult.isDecompiled}")
      println("  源码长度: ${mockResult.sourceCode.length}")
      println()
    }
  }

  @Test
  fun `example - kotlin source generation`() = runBlocking {
    // 生成Kotlin源码示例
    val kotlinSource =
      MockDataGenerator.generateMockKotlinSource(
        packageName = "com.example.kotlin",
        className = "KotlinTestClass",
        includeMembers = listOf("processData", "validate", "transform"),
      )

    println("=== Kotlin源码生成示例 ===")
    println("生成的Kotlin源码:")
    println(kotlinSource)
    println()
    println("源码统计:")
    println("  总字符数: ${kotlinSource.length}")
    println("  总行数: ${kotlinSource.lines().size}")
    println("  包含data class: ${kotlinSource.contains("data class")}")
  }
}
