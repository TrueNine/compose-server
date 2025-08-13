package io.github.truenine.composeserver.ide.ideamcp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class McpLogManagerTest {

  @Test
  fun `日志管理器初始状态为空`() = runBlocking {
    McpLogManager.clearLogs()
    val logs = McpLogManager.logs.first()
    assertTrue(logs.isEmpty())
  }

  @Test
  fun `可以添加不同级别的日志`() = runBlocking {
    McpLogManager.clearLogs()

    McpLogManager.debug("调试消息", "测试")
    McpLogManager.info("信息消息", "测试")
    McpLogManager.warn("警告消息", "测试")
    McpLogManager.error("错误消息", "测试")

    val logs = McpLogManager.logs.first()
    assertEquals(4, logs.size)
    assertEquals(LogLevel.DEBUG, logs[0].level)
    assertEquals(LogLevel.INFO, logs[1].level)
    assertEquals(LogLevel.WARN, logs[2].level)
    assertEquals(LogLevel.ERROR, logs[3].level)
  }

  @Test
  fun `可以按级别过滤日志`() = runBlocking {
    McpLogManager.clearLogs()

    McpLogManager.debug("调试消息", "测试")
    McpLogManager.info("信息消息", "测试")
    McpLogManager.error("错误消息", "测试")

    val errorLogs = McpLogManager.getLogsByLevel(LogLevel.ERROR)
    assertEquals(1, errorLogs.size)
    assertEquals("错误消息", errorLogs[0].message)
  }

  @Test
  fun `可以搜索日志内容`() = runBlocking {
    McpLogManager.clearLogs()

    McpLogManager.info("用户登录成功", "认证")
    McpLogManager.info("数据处理完成", "处理器")
    McpLogManager.error("用户认证失败", "认证")

    val searchResults = McpLogManager.searchLogs("用户")
    assertEquals(2, searchResults.size)
    assertTrue(searchResults.all { it.message.contains("用户") })
  }

  @Test
  fun `可以清空所有日志`() = runBlocking {
    McpLogManager.info("测试消息", "测试")
    assertTrue(McpLogManager.logs.first().isNotEmpty())

    McpLogManager.clearLogs()
    val logs = McpLogManager.logs.first()
    assertTrue(logs.isEmpty())
  }

  @Test
  fun `错误日志可以包含异常信息`() = runBlocking {
    McpLogManager.clearLogs()

    val exception = RuntimeException("测试异常")
    McpLogManager.error("处理失败", "测试", exception)

    val logs = McpLogManager.logs.first()
    assertEquals(1, logs.size)
    assertTrue(logs[0].message.contains("测试异常"))
    assertTrue(logs[0].message.contains("RuntimeException"))
  }
}
