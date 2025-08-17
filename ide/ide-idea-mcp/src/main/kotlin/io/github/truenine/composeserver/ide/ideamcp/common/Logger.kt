package io.github.truenine.composeserver.ide.ideamcp.common

import org.slf4j.LoggerFactory

/** 简化的日志工具类，用于快速迁移 McpLogManager */
object Logger {
  private val logger = LoggerFactory.getLogger("ComposeServerMcp")

  fun debug(message: String, vararg args: Any?) {
    logger.debug(message, *args)
  }

  fun info(message: String, vararg args: Any?) {
    logger.info(message, *args)
  }

  fun warn(message: String, vararg args: Any?) {
    logger.warn(message, *args)
  }

  fun error(message: String, vararg args: Any?) {
    logger.error(message, *args)
  }

  fun error(message: String, throwable: Throwable) {
    logger.error(message, throwable)
  }
}
