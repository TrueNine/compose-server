package io.github.truenine.composeserver.ide.ideamcp.performance

import io.github.truenine.composeserver.ide.ideamcp.LogLevel
import io.github.truenine.composeserver.ide.ideamcp.McpLogManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 日志管理器性能测试
 * 测试大量日志处理和内存管理效率
 */
class LogManagerPerformanceTest : PerformanceTestBase() {

  @Test
  fun `测试大量日志添加性能`() = runBlocking {
    McpLogManager.clearLogs()
    
    val logCount = 10000
    val maxTimeMs = 5000L // 5秒内完成
    
    val actualTime = measureAndAssertTime(maxTimeMs) {
      repeat(logCount) { index ->
        when (index % 4) {
          0 -> McpLogManager.debug("调试消息 $index", "性能测试")
          1 -> McpLogManager.info("信息消息 $index", "性能测试")
          2 -> McpLogManager.warn("警告消息 $index", "性能测试")
          3 -> McpLogManager.error("错误消息 $index", "性能测试")
        }
      }
    }
    
    val logs = McpLogManager.logs.first()
    assertEquals(logCount, logs.size, "日志数量应该匹配")
    
    println("添加 $logCount 条日志耗时: ${actualTime}ms")
  }

  @Test
  fun `测试日志搜索性能`() = runBlocking {
    McpLogManager.clearLogs()
    
    // 准备测试数据
    val logCount = 5000
    val searchKeyword = "特殊关键词"
    var expectedMatches = 0
    
    repeat(logCount) { index ->
      val message = if (index % 100 == 0) {
        expectedMatches++
        "包含${searchKeyword}的消息 $index"
      } else {
        "普通消息 $index"
      }
      McpLogManager.info(message, "搜索测试")
    }
    
    val maxSearchTimeMs = 1000L // 1秒内完成搜索
    
    val actualTime = measureAndAssertTime(maxSearchTimeMs) {
      val searchResults = McpLogManager.searchLogs(searchKeyword)
      assertEquals(expectedMatches, searchResults.size, "搜索结果数量应该匹配")
    }
    
    println("在 $logCount 条日志中搜索耗时: ${actualTime}ms")
  }

  @Test
  fun `测试日志过滤性能`() = runBlocking {
    McpLogManager.clearLogs()
    
    val logCount = 8000
    var errorCount = 0
    
    // 添加不同级别的日志
    repeat(logCount) { index ->
      when (index % 4) {
        0 -> McpLogManager.debug("调试消息 $index", "过滤测试")
        1 -> McpLogManager.info("信息消息 $index", "过滤测试")
        2 -> McpLogManager.warn("警告消息 $index", "过滤测试")
        3 -> {
          McpLogManager.error("错误消息 $index", "过滤测试")
          errorCount++
        }
      }
    }
    
    val maxFilterTimeMs = 500L // 500毫秒内完成过滤
    
    val actualTime = measureAndAssertTime(maxFilterTimeMs) {
      val errorLogs = McpLogManager.getLogsByLevel(LogLevel.ERROR)
      assertEquals(errorCount, errorLogs.size, "错误日志数量应该匹配")
    }
    
    println("过滤 $logCount 条日志耗时: ${actualTime}ms")
  }

  @Test
  fun `测试日志内存使用效率`() = runBlocking {
    McpLogManager.clearLogs()
    
    val logCount = 20000
    val maxMemoryMB = 50L // 最大50MB内存使用
    
    val memoryUsed = measureMemoryUsage {
      repeat(logCount) { index ->
        val largeMessage = createLargeTestData(10) // 每条日志包含较多内容
        McpLogManager.info("消息 $index: $largeMessage", "内存测试")
      }
    }
    
    assertMemoryUsage(memoryUsed, maxMemoryMB)
    
    val logs = McpLogManager.logs.first()
    assertEquals(logCount, logs.size, "日志数量应该匹配")
    
    println("添加 $logCount 条大日志使用内存: ${memoryUsed / (1024 * 1024)}MB")
  }

  @Test
  fun `测试并发日志添加性能`() = runBlocking {
    McpLogManager.clearLogs()
    
    val concurrentTasks = 10
    val logsPerTask = 1000
    val totalLogs = concurrentTasks * logsPerTask
    val maxTimeMs = 3000L // 3秒内完成
    
    val actualTime = measureAndAssertTime(maxTimeMs) {
      runBlocking {
        val tasks = (1..concurrentTasks).map { taskId ->
          async {
            repeat(logsPerTask) { index ->
              McpLogManager.info("并发任务 $taskId 消息 $index", "并发测试")
            }
          }
        }
        tasks.awaitAll()
      }
    }
    
    val logs = McpLogManager.logs.first()
    assertEquals(totalLogs, logs.size, "总日志数量应该匹配")
    
    println("$concurrentTasks 个并发任务添加 $totalLogs 条日志耗时: ${actualTime}ms")
  }

  @Test
  fun `测试日志清空性能`() = runBlocking {
    // 先添加大量日志
    val logCount = 15000
    repeat(logCount) { index ->
      McpLogManager.info("待清空的消息 $index", "清空测试")
    }
    
    val logsBeforeClear = McpLogManager.logs.first()
    assertEquals(logCount, logsBeforeClear.size, "清空前日志数量应该匹配")
    
    val maxClearTimeMs = 1000L // 1秒内完成清空
    
    val actualTime = measureAndAssertTime(maxClearTimeMs) {
      McpLogManager.clearLogs()
    }
    
    val logsAfterClear = McpLogManager.logs.first()
    assertTrue(logsAfterClear.isEmpty(), "清空后应该没有日志")
    
    println("清空 $logCount 条日志耗时: ${actualTime}ms")
  }
}
