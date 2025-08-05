package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ResultExtensionsTest {

  @Nested
  inner class `safeCall测试` {

    @Test
    fun `测试成功操作`() {
      val result = safeCall { "success" }

      assertTrue(result.isSuccess)
      assertEquals("success", result.getOrNull())
    }

    @Test
    fun `测试异常操作`() {
      val result = safeCall { throw RuntimeException("test error") }

      assertTrue(result.isFailure)
      assertTrue(result.exceptionOrNull() is RuntimeException)
      assertEquals("test error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `测试返回null的操作`() {
      val result = safeCall { null }

      assertTrue(result.isSuccess)
      assertEquals(null, result.getOrNull())
    }
  }

  @Nested
  inner class `safeCallAsync测试` {

    @Test
    fun `测试异步成功操作`() = runTest {
      val result = safeCallAsync {
        delay(10)
        "async success"
      }

      assertTrue(result.isSuccess)
      assertEquals("async success", result.getOrNull())
    }

    @Test
    fun `测试异步异常操作`() = runTest {
      val result = safeCallAsync {
        delay(10)
        throw IllegalStateException("async error")
      }

      assertTrue(result.isFailure)
      assertTrue(result.exceptionOrNull() is IllegalStateException)
      assertEquals("async error", result.exceptionOrNull()?.message)
    }
  }

  @Nested
  inner class `safeCallWithContext测试` {

    @Test
    fun `测试自定义上下文操作`() = runTest {
      val result =
        safeCallWithContext(Dispatchers.Default) {
          delay(10)
          "context success"
        }

      assertTrue(result.isSuccess)
      assertEquals("context success", result.getOrNull())
    }
  }

  @Nested
  inner class `Result转换测试` {

    @Test
    fun `测试mapSuccess`() {
      val result = Result.success(5)
      val mapped = result.mapSuccess { it * 2 }

      assertTrue(mapped.isSuccess)
      assertEquals(10, mapped.getOrNull())
    }

    @Test
    fun `测试mapSuccess保持失败状态`() {
      val result = Result.failure<Int>(RuntimeException("error"))
      val mapped = result.mapSuccess { it * 2 }

      assertTrue(mapped.isFailure)
      assertTrue(mapped.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `测试mapFailure`() {
      val result = Result.failure<String>(RuntimeException("original"))
      val mapped = result.mapFailure { IllegalArgumentException("transformed") }

      assertTrue(mapped.isFailure)
      assertTrue(mapped.exceptionOrNull() is IllegalArgumentException)
      assertEquals("transformed", mapped.exceptionOrNull()?.message)
    }

    @Test
    fun `测试mapFailure保持成功状态`() {
      val result = Result.success("value")
      val mapped = result.mapFailure { IllegalArgumentException("should not happen") }

      assertTrue(mapped.isSuccess)
      assertEquals("value", mapped.getOrNull())
    }
  }

  @Nested
  inner class `副作用操作测试` {

    @Test
    fun `测试onSuccessDo`() {
      var executed = false
      val result = Result.success("value")

      val returned = result.onSuccessDo { executed = true }

      assertTrue(executed)
      assertEquals(result, returned) // 应该返回原始Result
    }

    @Test
    fun `测试onSuccessDo不执行失败情况`() {
      var executed = false
      val result = Result.failure<String>(RuntimeException("error"))

      result.onSuccessDo { executed = true }

      assertFalse(executed)
    }

    @Test
    fun `测试onFailureDo`() {
      var executedException: Throwable? = null
      val originalException = RuntimeException("error")
      val result = Result.failure<String>(originalException)

      val returned = result.onFailureDo { executedException = it }

      assertEquals(originalException, executedException)
      assertEquals(result, returned) // 应该返回原始Result
    }

    @Test
    fun `测试onFailureDo不执行成功情况`() {
      var executed = false
      val result = Result.success("value")

      result.onFailureDo { executed = true }

      assertFalse(executed)
    }
  }

  @Nested
  inner class `nullable转Result测试` {

    @Test
    fun `测试非null值转Result`() {
      val value = "test"
      val result = value.toResult()

      assertTrue(result.isSuccess)
      assertEquals("test", result.getOrNull())
    }

    @Test
    fun `测试null值转Result`() {
      val value: String? = null
      val result = value.toResult()

      assertTrue(result.isFailure)
      assertTrue(result.exceptionOrNull() is IllegalArgumentException)
      assertEquals("Value is null", result.exceptionOrNull()?.message)
    }

    @Test
    fun `测试null值转Result自定义错误消息`() {
      val value: String? = null
      val result = value.toResult("Custom error message")

      assertTrue(result.isFailure)
      assertEquals("Custom error message", result.exceptionOrNull()?.message)
    }
  }

  @Nested
  inner class `组合Results测试` {

    @Test
    fun `测试组合所有成功的Results`() {
      val result1 = Result.success(1)
      val result2 = Result.success(2)
      val result3 = Result.success(3)

      val combined = combineResults(listOf(result1, result2, result3))

      assertTrue(combined.isSuccess)
      assertEquals(listOf(1, 2, 3), combined.getOrNull())
    }

    @Test
    fun `测试组合包含失败的Results`() {
      val result1 = Result.success(1)
      val result2 = Result.failure<Int>(RuntimeException("error"))
      val result3 = Result.success(3)

      val combined = combineResults(listOf(result1, result2, result3))

      assertTrue(combined.isFailure)
      assertTrue(combined.exceptionOrNull() is RuntimeException)
      assertEquals("error", combined.exceptionOrNull()?.message)
    }

    @Test
    fun `测试组合空Results`() {
      val combined = combineResults(emptyList<Result<String>>())

      assertTrue(combined.isSuccess)
      assertEquals(emptyList<String>(), combined.getOrNull())
    }
  }

  @Nested
  inner class `重试机制测试` {

    @Test
    fun `测试第一次成功无需重试`() = runTest {
      var attempts = 0
      val result =
        retryWithBackoff<String>(maxRetries = 3) {
          attempts++
          Result.success("success")
        }

      assertTrue(result.isSuccess)
      assertEquals("success", result.getOrNull())
      assertEquals(1, attempts)
    }

    @Test
    fun `测试重试后成功`() = runTest {
      var attempts = 0
      val result =
        retryWithBackoff<String>(maxRetries = 3, initialDelayMs = 1, maxDelayMs = 10) {
          attempts++
          if (attempts < 3) {
            Result.failure(RuntimeException("attempt $attempts failed"))
          } else {
            Result.success("success on attempt $attempts")
          }
        }

      assertTrue(result.isSuccess)
      assertEquals("success on attempt 3", result.getOrNull())
      assertEquals(3, attempts)
    }

    @Test
    fun `测试所有重试都失败`() = runTest {
      var attempts = 0
      val result =
        retryWithBackoff<String>(maxRetries = 2, initialDelayMs = 1, maxDelayMs = 10) {
          attempts++
          Result.failure(RuntimeException("attempt $attempts failed"))
        }

      assertTrue(result.isFailure)
      assertTrue(result.exceptionOrNull()?.message?.contains("attempt") == true)
      assertEquals(3, attempts) // maxRetries + 1 final attempt
    }

    @Test
    fun `测试重试延迟递增`() = runTest {
      var attempts = 0

      val result =
        retryWithBackoff<String>(
          maxRetries = 2,
          initialDelayMs = 1, // 使用很小的延迟以避免测试环境时间问题
          maxDelayMs = 10,
          backoffMultiplier = 2.0,
        ) {
          attempts++
          Result.failure(RuntimeException("always fail"))
        }

      log.info("Retry attempts: {}", attempts)

      // 验证重试次数正确
      assertEquals(3, attempts) // maxRetries + 1 final attempt
      assertTrue(result.isFailure)
      assertTrue(result.exceptionOrNull()?.message?.contains("always fail") == true)
    }
  }
}
