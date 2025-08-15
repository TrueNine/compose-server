package io.github.truenine.composeserver.ide.ideamcp.demo

import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeServiceImpl
import io.mockk.mockk
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

/** LibCodeService 功能演示 展示完整的库代码查看功能 */
class LibCodeServiceDemo {

  @Test
  fun `演示完整的库代码查看功能`() = runBlocking {
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    println("╔══════════════════════════════════════════════════════════════╗")
    println("║                    LibCodeService 功能演示                    ║")
    println("╚══════════════════════════════════════════════════════════════╝")
    println()

    // 演示1: 查看标准库类
    println("🔍 演示1: 查看 java.lang.String 类")
    println("─".repeat(60))

    val result1 = libCodeService.getLibraryCode(mockProject, "java.lang.String")

    println("✅ 查找结果:")
    println("   📦 库名: ${result1.metadata.libraryName}")
    println("   🏷️  版本: ${result1.metadata.version ?: "未知"}")
    println("   🔤 语言: ${result1.language}")
    println("   📄 源码类型: ${result1.metadata.sourceType}")
    println("   🔧 是否反编译: ${result1.isDecompiled}")
    println("   📏 源码长度: ${result1.sourceCode.length} 字符")
    println()
    println("📝 源码内容预览:")
    println(result1.sourceCode.lines().take(10).joinToString("\n"))
    println("   ... (共 ${result1.sourceCode.lines().size} 行)")
    println()

    // 演示2: 查看集合类的特定方法
    println("🔍 演示2: 查看 java.util.ArrayList 的 add 方法")
    println("─".repeat(60))

    val result2 = libCodeService.getLibraryCode(mockProject, "java.util.ArrayList", "add")

    println("✅ 查找结果:")
    println("   📦 库名: ${result2.metadata.libraryName}")
    println("   🏷️  版本: ${result2.metadata.version ?: "未知"}")
    println("   🔤 语言: ${result2.language}")
    println("   📄 源码类型: ${result2.metadata.sourceType}")
    println("   🔧 是否反编译: ${result2.isDecompiled}")
    println("   📏 源码长度: ${result2.sourceCode.length} 字符")
    println()
    println("📝 提取的方法内容:")
    println(result2.sourceCode)
    println()

    // 演示3: 查看不存在的类
    println("🔍 演示3: 查看不存在的类 com.example.NonExistent")
    println("─".repeat(60))

    val result3 = libCodeService.getLibraryCode(mockProject, "com.example.NonExistent")

    println("✅ 查找结果:")
    println("   📦 库名: ${result3.metadata.libraryName}")
    println("   🏷️  版本: ${result3.metadata.version ?: "未知"}")
    println("   🔤 语言: ${result3.language}")
    println("   📄 源码类型: ${result3.metadata.sourceType}")
    println("   🔧 是否反编译: ${result3.isDecompiled}")
    println("   📏 源码长度: ${result3.sourceCode.length} 字符")
    println()
    println("📝 返回内容:")
    println(result3.sourceCode)
    println()

    // 演示4: 批量查看多个类
    println("🔍 演示4: 批量查看多个常用类")
    println("─".repeat(60))

    val commonClasses = listOf("java.lang.Object", "java.util.HashMap", "java.io.File", "java.time.LocalDateTime", "java.util.concurrent.ConcurrentHashMap")

    commonClasses.forEach { className ->
      val startTime = System.currentTimeMillis()
      val result = libCodeService.getLibraryCode(mockProject, className)
      val endTime = System.currentTimeMillis()

      println("📋 $className")
      println("   ⏱️  查找耗时: ${endTime - startTime}ms")
      println("   📦 库名: ${result.metadata.libraryName}")
      println("   📄 源码类型: ${result.metadata.sourceType}")
      println("   📏 源码长度: ${result.sourceCode.length} 字符")
      println()
    }

    println("╔══════════════════════════════════════════════════════════════╗")
    println("║                        演示完成                             ║")
    println("║                                                              ║")
    println("║  ✨ 主要功能:                                                ║")
    println("║    • 只需传入类的完全限定名即可查看源码                        ║")
    println("║    • 自动从项目依赖中查找类文件                              ║")
    println("║    • 支持从 source jar 提取源码                              ║")
    println("║    • 支持字节码反编译                                        ║")
    println("║    • 支持提取特定成员（方法、字段）                          ║")
    println("║    • 返回完整的元数据信息（库名、版本、类型等）               ║")
    println("║    • 在控制台和服务内都返回完整内容                          ║")
    println("║                                                              ║")
    println("║  🎯 使用方式:                                                ║")
    println("║    libCodeService.getLibraryCode(project, className)        ║")
    println("║    libCodeService.getLibraryCode(project, className, member)║")
    println("╚══════════════════════════════════════════════════════════════╝")
  }
}
