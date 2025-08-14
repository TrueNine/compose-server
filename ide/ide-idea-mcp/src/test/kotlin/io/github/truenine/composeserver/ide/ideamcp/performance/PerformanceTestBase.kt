package io.github.truenine.composeserver.ide.ideamcp.performance

import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

/**
 * 性能测试基类
 * 提供性能测试的通用功能和断言
 */
abstract class PerformanceTestBase {

  /**
   * 测量执行时间并验证性能要求
   * @param expectedMaxTimeMs 期望的最大执行时间（毫秒）
   * @param operation 要测试的操作
   * @return 实际执行时间
   */
  protected fun measureAndAssertTime(expectedMaxTimeMs: Long, operation: () -> Unit): Long {
    val actualTime = measureTimeMillis(operation)
    assertTrue(
      actualTime <= expectedMaxTimeMs,
      "操作执行时间 ${actualTime}ms 超过了期望的最大时间 ${expectedMaxTimeMs}ms"
    )
    return actualTime
  }

  /**
   * 测量内存使用情况
   * @param operation 要测试的操作
   * @return 内存使用情况（字节）
   */
  protected fun measureMemoryUsage(operation: () -> Unit): Long {
    System.gc() // 强制垃圾回收
    Thread.sleep(100) // 等待垃圾回收完成
    
    val runtime = Runtime.getRuntime()
    val beforeMemory = runtime.totalMemory() - runtime.freeMemory()
    
    operation()
    
    val afterMemory = runtime.totalMemory() - runtime.freeMemory()
    return afterMemory - beforeMemory
  }

  /**
   * 创建大量测试数据
   * @param size 数据大小
   * @return 测试数据字符串
   */
  protected fun createLargeTestData(size: Int): String {
    return buildString {
      repeat(size) {
        append("这是测试数据行 $it\n")
      }
    }
  }

  /**
   * 创建模拟的大文件内容
   * @param lines 行数
   * @return 文件内容
   */
  protected fun createMockFileContent(lines: Int): String {
    return buildString {
      appendLine("package com.example.test")
      appendLine("")
      appendLine("class TestClass {")
      repeat(lines - 10) { lineNum ->
        when (lineNum % 5) {
          0 -> appendLine("  // 注释行 $lineNum")
          1 -> appendLine("  private val property$lineNum = \"value$lineNum\"")
          2 -> appendLine("  fun method$lineNum(): String {")
          3 -> appendLine("    return \"result$lineNum\"")
          4 -> appendLine("  }")
        }
      }
      appendLine("}")
    }
  }

  /**
   * 验证内存使用是否在合理范围内
   * @param memoryUsed 使用的内存（字节）
   * @param maxExpectedMB 期望的最大内存使用（MB）
   */
  protected fun assertMemoryUsage(memoryUsed: Long, maxExpectedMB: Long) {
    val memoryUsedMB = memoryUsed / (1024 * 1024)
    assertTrue(
      memoryUsedMB <= maxExpectedMB,
      "内存使用 ${memoryUsedMB}MB 超过了期望的最大值 ${maxExpectedMB}MB"
    )
  }
}
