package io.github.truenine.composeserver.ide.ideamcp.demo

import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeServiceImpl
import io.mockk.mockk
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

/**
 * LibCodeService feature demo.
 *
 * Demonstrates the full library code viewing capabilities.
 */
class LibCodeServiceDemo {

  @Test
  fun `demo full library code viewing`() = runBlocking {
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    println("╔══════════════════════════════════════════════════════════════╗")
    println("║                    LibCodeService Feature Demo               ║")
    println("╚══════════════════════════════════════════════════════════════╝")
    println()

    // Demo 1: view a standard library class
    println("🔍 Demo 1: View class java.lang.String")
    println("─".repeat(60))

    val result1 = libCodeService.getLibraryCode(mockProject, "java.lang.String")

    println("✅ Lookup result:")
    println("   📦 Library: ${result1.metadata.libraryName}")
    println("   🏷️  Version: ${result1.metadata.version ?: "unknown"}")
    println("   🔤 Language: ${result1.language}")
    println("   📄 Source type: ${result1.metadata.sourceType}")
    println("   🔧 Decompiled: ${result1.isDecompiled}")
    println("   📏 Source length: ${result1.sourceCode.length} characters")
    println()
    println("📝 Source preview:")
    println(result1.sourceCode.lines().take(10).joinToString("\n"))
    println("   ... (total ${result1.sourceCode.lines().size} lines)")
    println()

    // Demo 2: view a specific method of a collection class
    println("🔍 Demo 2: View add method of java.util.ArrayList")
    println("─".repeat(60))

    val result2 = libCodeService.getLibraryCode(mockProject, "java.util.ArrayList", "add")

    println("✅ Lookup result:")
    println("   📦 Library: ${result2.metadata.libraryName}")
    println("   🏷️  Version: ${result2.metadata.version ?: "unknown"}")
    println("   🔤 Language: ${result2.language}")
    println("   📄 Source type: ${result2.metadata.sourceType}")
    println("   🔧 Decompiled: ${result2.isDecompiled}")
    println("   📏 Source length: ${result2.sourceCode.length} characters")
    println()
    println("📝 Extracted method content:")
    println(result2.sourceCode)
    println()

    // Demo 3: view a non-existent class
    println("🔍 Demo 3: View non-existent class com.example.NonExistent")
    println("─".repeat(60))

    val result3 = libCodeService.getLibraryCode(mockProject, "com.example.NonExistent")

    println("✅ Lookup result:")
    println("   📦 Library: ${result3.metadata.libraryName}")
    println("   🏷️  Version: ${result3.metadata.version ?: "unknown"}")
    println("   🔤 Language: ${result3.language}")
    println("   📄 Source type: ${result3.metadata.sourceType}")
    println("   🔧 Decompiled: ${result3.isDecompiled}")
    println("   📏 Source length: ${result3.sourceCode.length} characters")
    println()
    println("📝 Returned content:")
    println(result3.sourceCode)
    println()

    // Demo 4: view multiple commonly used classes
    println("🔍 Demo 4: View multiple common classes")
    println("─".repeat(60))

    val commonClasses = listOf("java.lang.Object", "java.util.HashMap", "java.io.File", "java.time.LocalDateTime", "java.util.concurrent.ConcurrentHashMap")

    commonClasses.forEach { className ->
      val startTime = System.currentTimeMillis()
      val result = libCodeService.getLibraryCode(mockProject, className)
      val endTime = System.currentTimeMillis()

      println("📋 $className")
      println("   ⏱️  Lookup time: ${endTime - startTime}ms")
      println("   📦 Library: ${result.metadata.libraryName}")
      println("   📄 Source type: ${result.metadata.sourceType}")
      println("   📏 Source length: ${result.sourceCode.length} characters")
      println()
    }

    println("╔══════════════════════════════════════════════════════════════╗")
    println("║                        Demo finished                        ║")
    println("║                                                              ║")
    println("║  ✨ Main features:                                           ║")
    println("║    • View source by providing fully-qualified class name     ║")
    println("║    • Automatically locate class files from project deps      ║")
    println("║    • Supports extracting source from source JAR              ║")
    println("║    • Supports bytecode decompilation                         ║")
    println("║    • Supports extracting specific members (methods, fields)  ║")
    println("║    • Returns full metadata (library name, version, type, ...)║")
    println("║    • Returns full content both in console and in service     ║")
    println("║                                                              ║")
    println("║  🎯 Usage:                                                   ║")
    println("║    libCodeService.getLibraryCode(project, className)        ║")
    println("║    libCodeService.getLibraryCode(project, className, member)║")
    println("╚══════════════════════════════════════════════════════════════╝")
  }
}
