package io.github.truenine.composeserver.testtoolkit.utils

import java.time.Duration
import org.testcontainers.containers.Container
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

/**
 * # 容器扩展函数
 *
 * 为 Testcontainers 的 GenericContainer 提供便捷的扩展方法， 简化容器操作和提高测试稳定性。
 *
 * @author TrueNine
 * @since 2025-07-12
 */

/**
 * 为容器创建命令执行器
 *
 * @return ContainerCommandExecutor 实例
 */
fun GenericContainer<*>.commandExecutor(): ContainerCommandExecutor {
  return ContainerCommandExecutor(this)
}

/**
 * 安全执行容器命令，带重试机制
 *
 * @param timeout 超时时间
 * @param maxRetries 最大重试次数
 * @param commands 要执行的命令
 * @return 命令执行结果
 */
fun GenericContainer<*>.safeExecInContainer(
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
  vararg commands: String,
): Container.ExecResult {
  return commandExecutor().executeCommand(timeout, maxRetries, *commands)
}

/**
 * 执行命令并验证退出码
 *
 * @param expectedExitCode 期望的退出码
 * @param timeout 超时时间
 * @param maxRetries 最大重试次数
 * @param commands 要执行的命令
 * @return 命令执行结果
 */
fun GenericContainer<*>.execWithExpectedExitCode(
  expectedExitCode: Int,
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
  vararg commands: String,
): Container.ExecResult {
  return commandExecutor().executeCommandWithExpectedExitCode(expectedExitCode, timeout, maxRetries, *commands)
}

/**
 * 执行命令并获取输出
 *
 * @param timeout 超时时间
 * @param maxRetries 最大重试次数
 * @param commands 要执行的命令
 * @return 标准输出内容
 */
fun GenericContainer<*>.execAndGetOutput(
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
  vararg commands: String,
): String {
  return commandExecutor().executeCommandAndGetOutput(timeout, maxRetries, *commands)
}

/**
 * 执行命令并检查输出是否包含指定内容
 *
 * @param expectedContent 期望包含的内容
 * @param timeout 超时时间
 * @param maxRetries 最大重试次数
 * @param commands 要执行的命令
 * @return 命令执行结果
 */
fun GenericContainer<*>.execAndCheckOutput(
  expectedContent: String,
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
  vararg commands: String,
): Container.ExecResult {
  return commandExecutor().executeCommandAndCheckOutput(expectedContent, timeout, maxRetries, *commands)
}

/**
 * 等待容器完全就绪
 *
 * @param timeout 超时时间
 * @param pollInterval 轮询间隔
 */
fun GenericContainer<*>.waitForReady(timeout: Duration = Duration.ofSeconds(30), pollInterval: Duration = Duration.ofMillis(500)) {
  commandExecutor().waitForContainerReady(timeout, pollInterval)
}

/**
 * 检查文件是否存在
 *
 * @param filePath 文件路径
 * @param timeout 超时时间
 * @param maxRetries 最大重试次数
 * @return 文件是否存在
 */
fun GenericContainer<*>.fileExists(
  filePath: String,
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
): Boolean {
  return commandExecutor().fileExists(filePath, timeout, maxRetries)
}

/**
 * 等待文件出现
 *
 * @param filePath 文件路径
 * @param timeout 超时时间
 * @param pollInterval 轮询间隔
 */
fun GenericContainer<*>.waitForFile(filePath: String, timeout: Duration = Duration.ofSeconds(30), pollInterval: Duration = Duration.ofMillis(500)) {
  commandExecutor().waitForFile(filePath, timeout, pollInterval)
}

/**
 * 读取文件内容
 *
 * @param filePath 文件路径
 * @param timeout 超时时间
 * @param maxRetries 最大重试次数
 * @return 文件内容
 */
fun GenericContainer<*>.readFile(
  filePath: String,
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
): String {
  return commandExecutor().readFileContent(filePath, timeout, maxRetries)
}

/**
 * 配置容器使用更稳定的等待策略
 *
 * @param logMessage 要等待的日志消息
 * @param times 消息出现次数
 * @param timeout 超时时间
 * @return 配置后的容器
 */
fun <T : GenericContainer<T>> T.withStableWaitStrategy(logMessage: String, times: Int = 1, timeout: Duration = Duration.ofSeconds(60)): T {
  return this.waitingFor(Wait.forLogMessage(logMessage, times).withStartupTimeout(timeout))
}

/**
 * 配置容器使用健康检查等待策略
 *
 * @param healthCheckCommand 健康检查命令
 * @param timeout 超时时间
 * @param interval 检查间隔
 * @return 配置后的容器
 */
fun <T : GenericContainer<T>> T.withHealthCheck(
  healthCheckCommand: Array<String>,
  timeout: Duration = Duration.ofSeconds(60),
  interval: Duration = Duration.ofSeconds(5),
): T {
  return this.waitingFor(Wait.forHealthcheck().withStartupTimeout(timeout)).withCommand(*healthCheckCommand)
}

/**
 * 启动容器并等待完全就绪
 *
 * @param readyTimeout 就绪检查超时时间
 * @param readyPollInterval 就绪检查轮询间隔
 */
fun GenericContainer<*>.startAndWaitForReady(readyTimeout: Duration = Duration.ofSeconds(30), readyPollInterval: Duration = Duration.ofMillis(500)) {
  start()
  waitForReady(readyTimeout, readyPollInterval)
}
