package io.github.truenine.composeserver.ide.ideamcp

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue

/** MCP 插件日志管理器 提供线程安全的日志收集和分发功能 */
object McpLogManager {
  private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
  val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

  private val logQueue = ConcurrentLinkedQueue<LogEntry>()
  private val maxLogEntries = 1000

  /** 记录调试日志 */
  fun debug(message: String, source: String = "MCP") {
    addLog(LogLevel.DEBUG, message, source)
  }

  /** 记录信息日志 */
  fun info(message: String, source: String = "MCP") {
    addLog(LogLevel.INFO, message, source)
  }

  /** 记录警告日志 */
  fun warn(message: String, source: String = "MCP") {
    addLog(LogLevel.WARN, message, source)
  }

  /** 记录错误日志 */
  fun error(message: String, source: String = "MCP", throwable: Throwable? = null) {
    val finalMessage =
      if (throwable != null) {
        "$message - ${throwable.message}\n${throwable.stackTraceToString()}"
      } else {
        message
      }
    addLog(LogLevel.ERROR, finalMessage, source)
  }

  /** 清空所有日志 */
  fun clearLogs() {
    logQueue.clear()
    _logs.value = emptyList()
  }

  /** 获取指定级别的日志 */
  fun getLogsByLevel(level: LogLevel): List<LogEntry> {
    return _logs.value.filter { it.level == level }
  }

  /** 获取包含指定关键词的日志 */
  fun searchLogs(keyword: String): List<LogEntry> {
    return _logs.value.filter { it.message.contains(keyword, ignoreCase = true) || it.source.contains(keyword, ignoreCase = true) }
  }

  private fun addLog(level: LogLevel, message: String, source: String) {
    val logEntry = LogEntry(timestamp = LocalDateTime.now(), level = level, message = message, source = source)

    logQueue.offer(logEntry)

    // 维护最大日志条数
    while (logQueue.size > maxLogEntries) {
      logQueue.poll()
    }

    // 更新状态
    _logs.value = logQueue.toList()
  }
}

/** 日志条目数据类 */
data class LogEntry(val timestamp: LocalDateTime, val level: LogLevel, val message: String, val source: String) {
  val formattedTime: String
    get() = timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
}

/** 日志级别枚举 */
enum class LogLevel(val displayName: String, val color: String) {
  DEBUG("DEBUG", "#6C7B7F"), // 灰色
  INFO("INFO", "#4A90E2"), // 蓝色
  WARN("WARN", "#F5A623"), // 橙色
  ERROR("ERROR", "#D0021B"), // 红色
}
