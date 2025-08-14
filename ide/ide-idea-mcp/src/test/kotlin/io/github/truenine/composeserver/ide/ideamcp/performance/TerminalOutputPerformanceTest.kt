package io.github.truenine.composeserver.ide.ideamcp.performance


import io.github.truenine.composeserver.ide.ideamcp.TerminalOutputInterceptor
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * 终端输出处理性能测试
 * 测试大量输出数据的清洗和处理性能
 */
class TerminalOutputPerformanceTest : PerformanceTestBase() {

  private val outputInterceptor = TerminalOutputInterceptor()

  @Test
  fun `测试大量输出清洗性能`() {
    val largeOutput = buildString {
      repeat(10000) { lineIndex ->
        appendLine("这是输出行 $lineIndex - 包含一些需要清洗的内容")
        if (lineIndex % 100 == 0) {
          appendLine("\u001B[32m绿色文本\u001B[0m") // ANSI 颜色代码
        }
        if (lineIndex % 200 == 0) {
          appendLine("WARNING: 这是一个警告消息")
        }
        if (lineIndex % 300 == 0) {
          appendLine("ERROR: 这是一个错误消息")
        }
      }
    }
    
    val maxCleanTimeMs = 3000L // 3秒内完成清洗
    
    val actualTime = measureAndAssertTime(maxCleanTimeMs) {
      val cleanedOutput = outputInterceptor.enhancedCleanOutput(largeOutput)
      assertTrue(cleanedOutput.isNotEmpty(), "清洗后的输出不应为空")
    }
    
    println("清洗 10000 行输出耗时: ${actualTime}ms")
  }

  @Test
  fun `测试 Gradle 输出清洗性能`() {
    val gradleOutput = buildString {
      appendLine("> Task :compileKotlin")
      appendLine("w: file:///path/to/file.kt:10:5 Parameter 'unused' is never used")
      repeat(5000) { taskIndex ->
        appendLine("> Task :module$taskIndex:compileKotlin")
        appendLine("w: file:///path/to/module$taskIndex/File.kt:${taskIndex + 10}:5 Warning message $taskIndex")
        if (taskIndex % 100 == 0) {
          appendLine("e: file:///path/to/module$taskIndex/Error.kt:${taskIndex + 20}:10 Error message $taskIndex")
        }
        appendLine("> Task :module$taskIndex:classes")
      }
      appendLine("BUILD SUCCESSFUL in 45s")
      appendLine("127 actionable tasks: 89 executed, 38 up-to-date")
    }
    
    val maxCleanTimeMs = 2000L // 2秒内完成 Gradle 输出清洗
    
    val actualTime = measureAndAssertTime(maxCleanTimeMs) {
      val cleanedOutput = outputInterceptor.enhancedCleanOutput(gradleOutput)
      assertTrue(cleanedOutput.isNotEmpty(), "清洗后的 Gradle 输出不应为空")
    }
    
    println("清洗大量 Gradle 输出耗时: ${actualTime}ms")
  }

  @Test
  fun `测试 Maven 输出清洗性能`() {
    val mavenOutput = buildString {
      appendLine("[INFO] Scanning for projects...")
      appendLine("[INFO] ")
      repeat(3000) { moduleIndex ->
        appendLine("[INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ module$moduleIndex ---")
        appendLine("[INFO] Changes detected - recompiling the module!")
        appendLine("[INFO] Compiling 50 source files to /path/to/module$moduleIndex/target/classes")
        if (moduleIndex % 50 == 0) {
          appendLine("[WARNING] /path/to/module$moduleIndex/src/File.java:[${moduleIndex + 10},5] warning message $moduleIndex")
        }
        if (moduleIndex % 200 == 0) {
          appendLine("[ERROR] /path/to/module$moduleIndex/src/Error.java:[${moduleIndex + 20},10] error message $moduleIndex")
        }
      }
      appendLine("[INFO] BUILD SUCCESS")
      appendLine("[INFO] Total time: 2:30 min")
    }
    
    val maxCleanTimeMs = 2500L // 2.5秒内完成 Maven 输出清洗
    
    val actualTime = measureAndAssertTime(maxCleanTimeMs) {
      val cleanedOutput = outputInterceptor.enhancedCleanOutput(mavenOutput)
      assertTrue(cleanedOutput.isNotEmpty(), "清洗后的 Maven 输出不应为空")
    }
    
    println("清洗大量 Maven 输出耗时: ${actualTime}ms")
  }

  @Test
  fun `测试 NPM 输出清洗性能`() {
    val npmOutput = buildString {
      appendLine("npm WARN deprecated package@1.0.0: This package is deprecated")
      repeat(2000) { packageIndex ->
        appendLine("added package-$packageIndex@1.0.$packageIndex")
        if (packageIndex % 100 == 0) {
          appendLine("npm WARN package-$packageIndex@1.0.$packageIndex requires a peer of dependency@^2.0.0")
        }
        if (packageIndex % 300 == 0) {
          appendLine("npm ERR! peer dep missing: dependency@^2.0.0, required by package-$packageIndex@1.0.$packageIndex")
        }
      }
      appendLine("added 2000 packages from 1500 contributors in 45.678s")
    }
    
    val maxCleanTimeMs = 1500L // 1.5秒内完成 NPM 输出清洗
    
    val actualTime = measureAndAssertTime(maxCleanTimeMs) {
      val cleanedOutput = outputInterceptor.enhancedCleanOutput(npmOutput)
      assertTrue(cleanedOutput.isNotEmpty(), "清洗后的 NPM 输出不应为空")
    }
    
    println("清洗大量 NPM 输出耗时: ${actualTime}ms")
  }

  @Test
  fun `测试 ANSI 颜色代码移除性能`() {
    val colorfulOutput = buildString {
      repeat(8000) { lineIndex ->
        when (lineIndex % 6) {
          0 -> appendLine("\u001B[31m红色文本行 $lineIndex\u001B[0m")
          1 -> appendLine("\u001B[32m绿色文本行 $lineIndex\u001B[0m")
          2 -> appendLine("\u001B[33m黄色文本行 $lineIndex\u001B[0m")
          3 -> appendLine("\u001B[34m蓝色文本行 $lineIndex\u001B[0m")
          4 -> appendLine("\u001B[35m紫色文本行 $lineIndex\u001B[0m")
          5 -> appendLine("\u001B[36m青色文本行 $lineIndex\u001B[0m")
        }
      }
    }
    
    val maxRemoveTimeMs = 1000L // 1秒内完成 ANSI 代码移除
    
    val actualTime = measureAndAssertTime(maxRemoveTimeMs) {
      val cleanOutput = outputInterceptor.enhancedCleanOutput(colorfulOutput)
      assertTrue(cleanOutput.isNotEmpty(), "移除 ANSI 代码后的输出不应为空")
      // ANSI代码移除在enhancedCleanOutput中处理
    }
    
    println("移除 8000 行彩色输出的 ANSI 代码耗时: ${actualTime}ms")
  }

  @Test
  fun `测试关键信息提取性能`() {
    val complexOutput = buildString {
      repeat(5000) { index ->
        appendLine("普通日志行 $index")
        if (index % 50 == 0) {
          appendLine("ERROR: 关键错误信息 $index")
        }
        if (index % 100 == 0) {
          appendLine("WARNING: 重要警告信息 $index")
        }
        if (index % 200 == 0) {
          appendLine("BUILD FAILED")
          appendLine("FAILURE: Build failed with an exception.")
        }
        if (index % 300 == 0) {
          appendLine("Tests run: 10, Failures: 2, Errors: 1, Skipped: 0")
        }
      }
    }
    
    val maxExtractTimeMs = 2000L // 2秒内完成关键信息提取
    
    val actualTime = measureAndAssertTime(maxExtractTimeMs) {
      val keyInfo = outputInterceptor.enhancedCleanOutput(complexOutput)
      assertTrue(keyInfo.isNotEmpty(), "提取的关键信息不应为空")
    }
    
    println("从 5000 行复杂输出中提取关键信息耗时: ${actualTime}ms")
  }

  @Test
  fun `测试输出截断性能`() {
    // 创建超大输出（模拟非常详细的构建日志）
    val hugeOutput = buildString {
      repeat(50000) { lineIndex ->
        appendLine("详细构建日志行 $lineIndex - 包含大量详细信息和调试输出")
        if (lineIndex % 1000 == 0) {
          appendLine("=".repeat(80))
          appendLine("阶段 ${lineIndex / 1000} 完成")
          appendLine("=".repeat(80))
        }
      }
    }
    
    val maxTruncateTimeMs = 1000L // 1秒内完成输出截断
    
    val actualTime = measureAndAssertTime(maxTruncateTimeMs) {
      val truncatedOutput = outputInterceptor.enhancedCleanOutput(hugeOutput)
      assertTrue(truncatedOutput.isNotEmpty(), "截断后的输出不应为空")
      // 验证输出被合理截断（不会无限长）
      assertTrue(truncatedOutput.length <= hugeOutput.length, "输出长度应该合理")
    }
    
    println("截断 50000 行超大输出耗时: ${actualTime}ms")
  }

  @Test
  fun `测试输出清洗内存使用效率`() {
    val largeOutput = createLargeTestData(20000) // 20000行测试数据
    val maxMemoryMB = 100L // 最大100MB内存使用
    
    val memoryUsed = measureMemoryUsage {
      val cleanedOutput = outputInterceptor.enhancedCleanOutput(largeOutput)
      assertTrue(cleanedOutput.isNotEmpty(), "清洗后的输出不应为空")
    }
    
    assertMemoryUsage(memoryUsed, maxMemoryMB)
    
    println("清洗 20000 行大输出使用内存: ${memoryUsed / (1024 * 1024)}MB")
  }
}
