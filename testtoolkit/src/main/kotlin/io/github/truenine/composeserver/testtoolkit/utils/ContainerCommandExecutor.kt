package io.github.truenine.composeserver.testtoolkit.utils

import java.time.Duration
import org.slf4j.LoggerFactory
import org.testcontainers.containers.Container
import org.testcontainers.containers.GenericContainer

/**
 * # 容器命令执行工具类
 *
 * 提供稳定的容器命令执行功能，集成重试机制和错误处理。 解决 Testcontainers 中 execInContainer 可能返回 null exitCode 的问题。
 *
 * ## 特性
 * - 自动重试机制处理 null exitCode 问题
 * - 统一的错误处理和日志记录
 * - 支持自定义超时和重试配置
 * - 提供便捷的命令执行方法
 *
 * ## 使用示例
 *
 * ```kotlin
 * val executor = ContainerCommandExecutor(container)
 *
 * // 执行简单命令
 * val result = executor.executeCommand("echo", "Hello World")
 * println("输出: ${result.stdout}")
 *
 * // 执行命令并验证退出码
 * executor.executeCommandWithExpectedExitCode(0, "ls", "/tmp")
 *
 * // 执行命令并获取输出
 * val output = executor.executeCommandAndGetOutput("cat", "/etc/hostname")
 * ```
 *
 * @param container 要执行命令的容器
 * @author TrueNine
 * @since 2025-07-12
 */
class ContainerCommandExecutor(private val container: GenericContainer<*>) {

  companion object {
    private val log = LoggerFactory.getLogger(ContainerCommandExecutor::class.java)

    /** 默认命令执行超时时间 */
    val DEFAULT_COMMAND_TIMEOUT: Duration = Duration.ofSeconds(30)

    /** 默认重试次数 */
    const val DEFAULT_MAX_RETRIES = 3
  }

  /**
   * 执行容器命令，带重试机制
   *
   * @param timeout 超时时间
   * @param maxRetries 最大重试次数
   * @param commands 要执行的命令
   * @return 命令执行结果
   * @throws RuntimeException 如果命令执行失败
   */
  fun executeCommand(timeout: Duration = DEFAULT_COMMAND_TIMEOUT, maxRetries: Int = DEFAULT_MAX_RETRIES, vararg commands: String): Container.ExecResult {
    require(commands.isNotEmpty()) { "命令不能为空" }

    log.debug("执行容器命令: {}", commands.joinToString(" "))

    return TestRetryUtils.retryWithExponentialBackoff(maxAttempts = maxRetries, initialDelay = Duration.ofMillis(100), maxDelay = Duration.ofSeconds(2)) {
      try {
        val result = container.execInContainer(*commands)

        log.debug("命令执行完成，退出码: {}, 输出长度: {}", result.exitCode, result.stdout.length)
        result
      } catch (e: Exception) {
        log.warn("命令执行失败: {}, 错误: {}", commands.joinToString(" "), e.message)
        throw e
      }
    }
  }

  /**
   * 执行命令并验证退出码
   *
   * @param expectedExitCode 期望的退出码
   * @param timeout 超时时间
   * @param maxRetries 最大重试次数
   * @param commands 要执行的命令
   * @return 命令执行结果
   * @throws AssertionError 如果退出码不匹配
   */
  fun executeCommandWithExpectedExitCode(
    expectedExitCode: Int,
    timeout: Duration = DEFAULT_COMMAND_TIMEOUT,
    maxRetries: Int = DEFAULT_MAX_RETRIES,
    vararg commands: String,
  ): Container.ExecResult {
    val result = executeCommand(timeout, maxRetries, *commands)

    if (result.exitCode != expectedExitCode) {
      val errorMsg =
        "命令执行退出码不匹配。期望: $expectedExitCode, 实际: ${result.exitCode}, " + "命令: ${commands.joinToString(" ")}, 输出: ${result.stdout}, 错误: ${result.stderr}"
      log.error(errorMsg)
      throw AssertionError(errorMsg)
    }

    return result
  }

  /**
   * 执行命令并获取标准输出
   *
   * @param timeout 超时时间
   * @param maxRetries 最大重试次数
   * @param commands 要执行的命令
   * @return 标准输出内容（已去除首尾空白）
   */
  fun executeCommandAndGetOutput(timeout: Duration = DEFAULT_COMMAND_TIMEOUT, maxRetries: Int = DEFAULT_MAX_RETRIES, vararg commands: String): String {
    val result = executeCommandWithExpectedExitCode(0, timeout, maxRetries, *commands)
    return result.stdout.trim()
  }

  /**
   * 执行命令并检查输出是否包含指定内容
   *
   * @param expectedContent 期望包含的内容
   * @param timeout 超时时间
   * @param maxRetries 最大重试次数
   * @param commands 要执行的命令
   * @return 命令执行结果
   * @throws AssertionError 如果输出不包含期望内容
   */
  fun executeCommandAndCheckOutput(
    expectedContent: String,
    timeout: Duration = DEFAULT_COMMAND_TIMEOUT,
    maxRetries: Int = DEFAULT_MAX_RETRIES,
    vararg commands: String,
  ): Container.ExecResult {
    val result = executeCommandWithExpectedExitCode(0, timeout, maxRetries, *commands)

    if (!result.stdout.contains(expectedContent)) {
      val errorMsg = "命令输出不包含期望内容。期望包含: '$expectedContent', " + "实际输出: '${result.stdout}', 命令: ${commands.joinToString(" ")}"
      log.error(errorMsg)
      throw AssertionError(errorMsg)
    }

    return result
  }

  /**
   * 等待容器就绪（通过执行简单命令检查）
   *
   * @param timeout 超时时间
   * @param pollInterval 轮询间隔
   */
  fun waitForContainerReady(timeout: Duration = Duration.ofSeconds(30), pollInterval: Duration = Duration.ofMillis(500)) {
    log.debug("等待容器就绪")

    TestRetryUtils.waitUntil(timeout = timeout, pollInterval = pollInterval) {
      try {
        // 尝试执行一个简单的命令来检查容器是否就绪
        val result = container.execInContainer("echo", "ready")
        result.exitCode == 0
      } catch (e: Exception) {
        log.debug("容器尚未就绪: {}", e.message)
        false
      }
    }

    log.debug("容器已就绪")
  }

  /**
   * 检查文件是否存在
   *
   * @param filePath 文件路径
   * @param timeout 超时时间
   * @param maxRetries 最大重试次数
   * @return 文件是否存在
   */
  fun fileExists(filePath: String, timeout: Duration = DEFAULT_COMMAND_TIMEOUT, maxRetries: Int = DEFAULT_MAX_RETRIES): Boolean {
    return try {
      val result = executeCommand(timeout, maxRetries, "test", "-f", filePath)
      result.exitCode == 0
    } catch (e: Exception) {
      log.debug("检查文件存在性时出错: {}", e.message)
      false
    }
  }

  /**
   * 等待文件出现
   *
   * @param filePath 文件路径
   * @param timeout 超时时间
   * @param pollInterval 轮询间隔
   */
  fun waitForFile(filePath: String, timeout: Duration = Duration.ofSeconds(30), pollInterval: Duration = Duration.ofMillis(500)) {
    log.debug("等待文件出现: {}", filePath)

    TestRetryUtils.waitUntil(timeout = timeout, pollInterval = pollInterval) { fileExists(filePath) }

    log.debug("文件已出现: {}", filePath)
  }

  /**
   * 读取文件内容
   *
   * @param filePath 文件路径
   * @param timeout 超时时间
   * @param maxRetries 最大重试次数
   * @return 文件内容
   */
  fun readFileContent(filePath: String, timeout: Duration = DEFAULT_COMMAND_TIMEOUT, maxRetries: Int = DEFAULT_MAX_RETRIES): String {
    return executeCommandAndGetOutput(timeout, maxRetries, "cat", filePath)
  }
}
