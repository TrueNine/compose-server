package io.github.truenine.composeserver.testtoolkit.utils

import io.github.truenine.composeserver.testtoolkit.log
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * # 测试重试工具类测试
 *
 * 测试 TestRetryUtils 的所有功能，确保100%覆盖率
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class TestRetryUtilsTest {

  @Test
  fun `测试 retryUntilSuccess 方法的成功情况`() {
    log.info("开始测试 retryUntilSuccess 方法的成功情况")

    val result = TestRetryUtils.retryUntilSuccess { "Success" }

    assertEquals("Success", result, "应该返回成功的结果")

    log.info("retryUntilSuccess 方法成功情况测试完成")
  }

  @Test
  fun `测试 retryUntilSuccess 方法的重试机制`() {
    log.info("开始测试 retryUntilSuccess 方法的重试机制")

    val attemptCounter = AtomicInteger(0)

    val result =
      TestRetryUtils.retryUntilSuccess(timeout = Duration.ofSeconds(5), pollInterval = Duration.ofMillis(100)) {
        val attempt = attemptCounter.incrementAndGet()
        if (attempt < 3) {
          throw RuntimeException("Attempt $attempt failed")
        }
        "Success after $attempt attempts"
      }

    assertTrue(result.contains("Success"), "应该在重试后成功")
    assertTrue(attemptCounter.get() >= 3, "应该至少重试3次")

    log.info("retryUntilSuccess 方法重试机制测试完成")
  }

  @Test
  fun `测试 retryUntilSuccess 方法的超时情况`() {
    log.info("开始测试 retryUntilSuccess 方法的超时情况")

    assertFailsWith<RuntimeException> {
      TestRetryUtils.retryUntilSuccess(timeout = Duration.ofMillis(500), pollInterval = Duration.ofMillis(100)) { throw RuntimeException("Always fails") }
    }

    log.info("retryUntilSuccess 方法超时情况测试完成")
  }

  @Test
  fun `测试 retryUntilSuccess 方法的自定义配置成功情况`() {
    log.info("开始测试 retryUntilSuccess 方法的自定义配置成功情况")

    val result = TestRetryUtils.retryUntilSuccess(timeout = Duration.ofSeconds(5), pollInterval = Duration.ofMillis(100)) { "Config Success" }

    assertEquals("Config Success", result, "应该返回配置的成功结果")

    log.info("retryUntilSuccess 方法自定义配置成功情况测试完成")
  }

  @Test
  fun `测试 retryUntilSuccess 方法的自定义配置重试机制`() {
    log.info("开始测试 retryUntilSuccess 方法的自定义配置重试机制")

    val attemptCounter = AtomicInteger(0)

    val result =
      TestRetryUtils.retryUntilSuccess(timeout = Duration.ofSeconds(5), pollInterval = Duration.ofMillis(100)) {
        val attempt = attemptCounter.incrementAndGet()
        if (attempt < 2) {
          throw RuntimeException("Config attempt $attempt failed")
        }
        "Config success after $attempt attempts"
      }

    assertTrue(result.contains("success"), "应该在重试后成功")
    assertTrue(attemptCounter.get() >= 2, "应该至少重试2次")

    log.info("retryUntilSuccess 方法自定义配置重试机制测试完成")
  }

  @Test
  fun `测试 retryWithExponentialBackoff 方法的成功情况`() {
    log.info("开始测试 retryWithExponentialBackoff 方法的成功情况")

    val result =
      TestRetryUtils.retryWithExponentialBackoff(maxAttempts = 3, initialDelay = Duration.ofMillis(50), maxDelay = Duration.ofSeconds(1)) {
        "Exponential Success"
      }

    assertEquals("Exponential Success", result, "应该返回指数退避的成功结果")

    log.info("retryWithExponentialBackoff 方法成功情况测试完成")
  }

  @Test
  fun `测试 retryWithExponentialBackoff 方法的重试机制`() {
    log.info("开始测试 retryWithExponentialBackoff 方法的重试机制")

    val attemptCounter = AtomicInteger(0)

    val result =
      TestRetryUtils.retryWithExponentialBackoff(maxAttempts = 4, initialDelay = Duration.ofMillis(10), maxDelay = Duration.ofMillis(100)) {
        val attempt = attemptCounter.incrementAndGet()
        if (attempt < 3) {
          throw RuntimeException("Exponential attempt $attempt failed")
        }
        "Exponential success after $attempt attempts"
      }

    assertTrue(result.contains("success"), "应该在指数退避重试后成功")
    assertTrue(attemptCounter.get() >= 3, "应该至少重试3次")

    log.info("retryWithExponentialBackoff 方法重试机制测试完成")
  }

  @Test
  fun `测试 retryWithExponentialBackoff 方法的最大尝试次数限制`() {
    log.info("开始测试 retryWithExponentialBackoff 方法的最大尝试次数限制")

    val attemptCounter = AtomicInteger(0)

    assertFailsWith<RuntimeException> {
      TestRetryUtils.retryWithExponentialBackoff(maxAttempts = 2, initialDelay = Duration.ofMillis(10), maxDelay = Duration.ofMillis(50)) {
        attemptCounter.incrementAndGet()
        throw RuntimeException("Always fails")
      }
    }

    assertEquals(2, attemptCounter.get(), "应该只尝试最大次数")

    log.info("retryWithExponentialBackoff 方法最大尝试次数限制测试完成")
  }

  @Test
  fun `测试 waitUntil 方法的成功情况`() {
    log.info("开始测试 waitUntil 方法的成功情况")

    TestRetryUtils.waitUntil(timeout = Duration.ofSeconds(5), pollInterval = Duration.ofMillis(100)) {
      true // 立即满足条件
    }

    log.info("waitUntil 方法成功情况测试完成")
  }

  @Test
  fun `测试 waitUntil 方法的等待机制`() {
    log.info("开始测试 waitUntil 方法的等待机制")

    val attemptCounter = AtomicInteger(0)

    TestRetryUtils.waitUntil(timeout = Duration.ofSeconds(5), pollInterval = Duration.ofMillis(100)) {
      val attempt = attemptCounter.incrementAndGet()
      attempt >= 3 // 第3次尝试时满足条件
    }

    assertTrue(attemptCounter.get() >= 3, "应该至少尝试3次")

    log.info("waitUntil 方法等待机制测试完成")
  }

  @Test
  fun `测试 waitUntil 方法的超时情况`() {
    log.info("开始测试 waitUntil 方法的超时情况")

    assertFailsWith<RuntimeException> {
      TestRetryUtils.waitUntil(timeout = Duration.ofMillis(300), pollInterval = Duration.ofMillis(100)) {
        false // 永远不满足条件
      }
    }

    log.info("waitUntil 方法超时情况测试完成")
  }

  @Test
  fun `测试 waitUntil 方法的默认参数`() {
    log.info("开始测试 waitUntil 方法的默认参数")

    TestRetryUtils.waitUntil {
      true // 使用默认参数，立即满足条件
    }

    log.info("waitUntil 方法默认参数测试完成")
  }

  @Test
  fun `测试重试方法的异常处理`() {
    log.info("开始测试重试方法的异常处理")

    // 测试特定异常类型的处理
    assertFailsWith<IllegalArgumentException> {
      TestRetryUtils.retryUntilSuccess(timeout = Duration.ofMillis(500), pollInterval = Duration.ofMillis(100)) {
        throw IllegalArgumentException("Specific exception")
      }
    }

    log.info("重试方法异常处理测试完成")
  }

  @Test
  fun `测试重试方法的边界条件`() {
    log.info("开始测试重试方法的边界条件")

    // 测试极短的超时时间
    assertFailsWith<RuntimeException> {
      TestRetryUtils.retryUntilSuccess(timeout = Duration.ofMillis(1), pollInterval = Duration.ofMillis(10)) { throw RuntimeException("Quick fail") }
    }

    // 测试零次重试
    assertFailsWith<RuntimeException> {
      TestRetryUtils.retryWithExponentialBackoff(maxAttempts = 1, initialDelay = Duration.ofMillis(10), maxDelay = Duration.ofMillis(100)) {
        throw RuntimeException("Single attempt fail")
      }
    }

    log.info("重试方法边界条件测试完成")
  }
}
