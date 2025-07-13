package io.github.truenine.composeserver.testtoolkit.utils

import java.time.Duration
import java.time.Instant
import org.slf4j.LoggerFactory

/**
 * # 测试重试工具类
 *
 * 提供统一的重试机制，使用 Java 内置的等待和重试策略。 支持指数退避、自定义超时时间和重试条件。
 *
 * ## 特性
 * - 指数退避重试策略
 * - 可配置的超时时间和重试间隔
 * - 支持自定义重试条件
 * - 统一的异常处理
 * - 详细的日志记录
 *
 * ## 使用示例
 *
 * ```kotlin
 * // 简单重试
 * val result = TestRetryUtils.retryUntilSuccess {
 *   someOperationThatMightFail()
 * }
 *
 * // 自定义配置的重试
 * val result = TestRetryUtils.retryWithConfig(
 *   timeout = Duration.ofSeconds(30),
 *   pollInterval = Duration.ofMillis(500)
 * ) {
 *   someOperationThatMightFail()
 * }
 *
 * // 等待条件满足
 * TestRetryUtils.waitUntil(timeout = Duration.ofSeconds(10)) {
 *   container.isRunning
 * }
 * ```
 *
 * @author TrueNine
 * @since 2025-07-12
 */
object TestRetryUtils {
  private val log = LoggerFactory.getLogger(TestRetryUtils::class.java)

  /** 默认超时时间 */
  val DEFAULT_TIMEOUT: Duration = Duration.ofSeconds(30)

  /** 默认轮询间隔 */
  val DEFAULT_POLL_INTERVAL: Duration = Duration.ofMillis(200)

  /** 默认初始延迟 */
  val DEFAULT_INITIAL_DELAY: Duration = Duration.ofMillis(100)

  /**
   * 内部等待实现
   *
   * @param timeout 超时时间
   * @param pollInterval 轮询间隔
   * @param initialDelay 初始延迟
   * @param condition 条件检查函数
   * @throws RuntimeException 如果超时
   */
  private fun waitForCondition(timeout: Duration, pollInterval: Duration, initialDelay: Duration, condition: () -> Boolean) {
    val startTime = Instant.now()
    val endTime = startTime.plus(timeout)

    // 初始延迟
    if (!initialDelay.isZero) {
      Thread.sleep(initialDelay.toMillis())
    }

    while (Instant.now().isBefore(endTime)) {
      try {
        if (condition()) {
          return
        }
      } catch (e: Exception) {
        log.debug("条件检查时发生异常，将忽略: {}", e.message)
      }

      Thread.sleep(pollInterval.toMillis())
    }

    throw RuntimeException("等待条件超时，超时时间: $timeout")
  }

  /**
   * 重试执行操作直到成功
   *
   * @param T 返回类型
   * @param timeout 超时时间
   * @param pollInterval 轮询间隔
   * @param initialDelay 初始延迟
   * @param operation 要执行的操作
   * @return 操作结果
   * @throws Exception 如果在超时时间内操作仍然失败
   */
  fun <T> retryUntilSuccess(
    timeout: Duration = DEFAULT_TIMEOUT,
    pollInterval: Duration = DEFAULT_POLL_INTERVAL,
    initialDelay: Duration = DEFAULT_INITIAL_DELAY,
    operation: () -> T,
  ): T {
    log.debug("开始重试操作，超时时间: {}, 轮询间隔: {}", timeout, pollInterval)

    val startTime = Instant.now()
    val endTime = startTime.plus(timeout)
    var lastException: Exception? = null

    // 初始延迟
    if (!initialDelay.isZero) {
      Thread.sleep(initialDelay.toMillis())
    }

    while (Instant.now().isBefore(endTime)) {
      try {
        val result = operation()
        log.debug("操作执行成功")
        return result
      } catch (e: Exception) {
        lastException = e
        log.debug("操作执行失败，将重试: {}", e.message)
        Thread.sleep(pollInterval.toMillis())
      }
    }

    throw (lastException ?: RuntimeException("操作失败，未知原因"))
  }

  /**
   * 等待条件满足
   *
   * @param timeout 超时时间
   * @param pollInterval 轮询间隔
   * @param initialDelay 初始延迟
   * @param condition 要检查的条件
   * @throws Exception 如果在超时时间内条件仍不满足
   */
  fun waitUntil(
    timeout: Duration = DEFAULT_TIMEOUT,
    pollInterval: Duration = DEFAULT_POLL_INTERVAL,
    initialDelay: Duration = DEFAULT_INITIAL_DELAY,
    condition: () -> Boolean,
  ) {
    log.debug("开始等待条件满足，超时时间: {}, 轮询间隔: {}", timeout, pollInterval)

    waitForCondition(timeout, pollInterval, initialDelay) {
      val result = condition()
      if (result) {
        log.debug("条件已满足")
      } else {
        log.debug("条件未满足，继续等待")
      }
      result
    }
  }

  /**
   * 等待条件满足并返回结果
   *
   * @param T 返回类型
   * @param timeout 超时时间
   * @param pollInterval 轮询间隔
   * @param initialDelay 初始延迟
   * @param supplier 提供结果的函数
   * @param condition 检查结果的条件
   * @return 满足条件的结果
   */
  fun <T> waitUntilResult(
    timeout: Duration = DEFAULT_TIMEOUT,
    pollInterval: Duration = DEFAULT_POLL_INTERVAL,
    initialDelay: Duration = DEFAULT_INITIAL_DELAY,
    supplier: () -> T,
    condition: (T) -> Boolean,
  ): T {
    log.debug("开始等待结果满足条件，超时时间: {}, 轮询间隔: {}", timeout, pollInterval)

    val startTime = Instant.now()
    val endTime = startTime.plus(timeout)

    // 初始延迟
    if (!initialDelay.isZero) {
      Thread.sleep(initialDelay.toMillis())
    }

    while (Instant.now().isBefore(endTime)) {
      try {
        val result = supplier()
        val satisfied = condition(result)
        if (satisfied) {
          log.debug("结果满足条件")
          return result
        } else {
          log.debug("结果不满足条件，继续等待")
        }
      } catch (e: Exception) {
        log.debug("获取结果时发生异常，将忽略: {}", e.message)
      }

      Thread.sleep(pollInterval.toMillis())
    }

    throw RuntimeException("等待结果满足条件超时，超时时间: $timeout")
  }

  /**
   * 使用指数退避策略重试操作
   *
   * @param T 返回类型
   * @param maxAttempts 最大重试次数
   * @param initialDelay 初始延迟
   * @param maxDelay 最大延迟
   * @param multiplier 延迟倍数
   * @param operation 要执行的操作
   * @return 操作结果
   * @throws Exception 如果所有重试都失败
   */
  fun <T> retryWithExponentialBackoff(
    maxAttempts: Int = 5,
    initialDelay: Duration = Duration.ofMillis(100),
    maxDelay: Duration = Duration.ofSeconds(5),
    multiplier: Double = 2.0,
    operation: () -> T,
  ): T {
    log.debug("开始指数退避重试，最大尝试次数: {}, 初始延迟: {}", maxAttempts, initialDelay)

    var currentDelay = initialDelay
    var lastException: Exception? = null

    repeat(maxAttempts) { attempt ->
      try {
        log.debug("第 {} 次尝试", attempt + 1)
        return operation()
      } catch (e: Exception) {
        lastException = e
        log.debug("第 {} 次尝试失败: {}", attempt + 1, e.message)

        if (attempt < maxAttempts - 1) {
          log.debug("等待 {} 后重试", currentDelay)
          Thread.sleep(currentDelay.toMillis())
          currentDelay = Duration.ofMillis(minOf((currentDelay.toMillis() * multiplier).toLong(), maxDelay.toMillis()))
        }
      }
    }

    throw (lastException ?: RuntimeException("所有重试都失败"))
  }

  /**
   * 创建自定义配置的等待操作
   *
   * @param timeout 超时时间
   * @param pollInterval 轮询间隔
   * @param initialDelay 初始延迟
   * @param ignoreExceptions 是否忽略异常
   * @param condition 条件检查函数
   */
  fun waitWithCustomConfig(
    timeout: Duration,
    pollInterval: Duration = DEFAULT_POLL_INTERVAL,
    initialDelay: Duration = DEFAULT_INITIAL_DELAY,
    ignoreExceptions: Boolean = true,
    condition: () -> Boolean,
  ) {
    if (ignoreExceptions) {
      waitForCondition(timeout, pollInterval, initialDelay, condition)
    } else {
      // 不忽略异常的版本
      val startTime = Instant.now()
      val endTime = startTime.plus(timeout)

      // 初始延迟
      if (!initialDelay.isZero) {
        Thread.sleep(initialDelay.toMillis())
      }

      while (Instant.now().isBefore(endTime)) {
        if (condition()) {
          return
        }
        Thread.sleep(pollInterval.toMillis())
      }

      throw RuntimeException("等待条件超时，超时时间: $timeout")
    }
  }
}
