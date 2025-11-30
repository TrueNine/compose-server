package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.*

class ResultExtensionsTest {

  @Nested
  inner class SafeCallTests {

    @Test
    fun returnsSuccessWhenOperationSucceeds() {
      val result = safeCall { "success" }

      assertTrue(result.isSuccess)
      assertEquals("success", result.getOrNull())
    }

    @Test
    fun capturesExceptionWhenOperationFails() {
      val result = safeCall { throw RuntimeException("test error") }

      assertTrue(result.isFailure)
      assertTrue(result.exceptionOrNull() is RuntimeException)
      assertEquals("test error", result.exceptionOrNull()?.message)
    }

    @Test
    fun handlesNullReturnValue() {
      val result = safeCall { null }

      assertTrue(result.isSuccess)
      assertEquals(null, result.getOrNull())
    }
  }

  @Nested
  inner class SafeCallAsyncTests {

    @Test
    fun returnsSuccessForAsyncOperation() = runTest {
      val result = safeCallAsync {
        delay(10)
        "async success"
      }

      assertTrue(result.isSuccess)
      assertEquals("async success", result.getOrNull())
    }

    @Test
    fun capturesExceptionForAsyncOperation() = runTest {
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
  inner class SafeCallWithContextTests {

    @Test
    fun executesOperationWithCustomDispatcher() = runTest {
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
  inner class ResultTransformationTests {

    @Test
    fun mapsSuccessValue() {
      val result = Result.success(5)
      val mapped = result.mapSuccess { it * 2 }

      assertTrue(mapped.isSuccess)
      assertEquals(10, mapped.getOrNull())
    }

    @Test
    fun retainsFailureWhenMappingSuccess() {
      val result = Result.failure<Int>(RuntimeException("error"))
      val mapped = result.mapSuccess { it * 2 }

      assertTrue(mapped.isFailure)
      assertTrue(mapped.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun mapsFailureValue() {
      val result = Result.failure<String>(RuntimeException("original"))
      val mapped = result.mapFailure { IllegalArgumentException("transformed") }

      assertTrue(mapped.isFailure)
      assertTrue(mapped.exceptionOrNull() is IllegalArgumentException)
      assertEquals("transformed", mapped.exceptionOrNull()?.message)
    }

    @Test
    fun retainsSuccessWhenMappingFailure() {
      val result = Result.success("value")
      val mapped = result.mapFailure { IllegalArgumentException("should not happen") }

      assertTrue(mapped.isSuccess)
      assertEquals("value", mapped.getOrNull())
    }
  }

  @Nested
  inner class SideEffectTests {

    @Test
    fun executesOnSuccessCallback() {
      var executed = false
      val result = Result.success("value")

      val returned = result.onSuccessDo { executed = true }

      assertTrue(executed)
      assertEquals(result, returned) // Should return the original Result
    }

    @Test
    fun doesNotExecuteOnSuccessCallbackWhenFailure() {
      var executed = false
      val result = Result.failure<String>(RuntimeException("error"))

      result.onSuccessDo { executed = true }

      assertFalse(executed)
    }

    @Test
    fun executesOnFailureCallback() {
      var executedException: Throwable? = null
      val originalException = RuntimeException("error")
      val result = Result.failure<String>(originalException)

      val returned = result.onFailureDo { executedException = it }

      assertEquals(originalException, executedException)
      assertEquals(result, returned) // Should return the original Result
    }

    @Test
    fun doesNotExecuteOnFailureCallbackWhenSuccess() {
      var executed = false
      val result = Result.success("value")

      result.onFailureDo { executed = true }

      assertFalse(executed)
    }
  }

  @Nested
  inner class NullableConversionTests {

    @Test
    fun convertsNonNullValueToResult() {
      val value = "test"
      val result = value.toResult()

      assertTrue(result.isSuccess)
      assertEquals("test", result.getOrNull())
    }

    @Test
    fun convertsNullValueToFailure() {
      val value: String? = null
      val result = value.toResult()

      assertTrue(result.isFailure)
      assertTrue(result.exceptionOrNull() is IllegalArgumentException)
      assertEquals("Value is null", result.exceptionOrNull()?.message)
    }

    @Test
    fun convertsNullValueWithCustomMessage() {
      val value: String? = null
      val result = value.toResult("Custom error message")

      assertTrue(result.isFailure)
      assertEquals("Custom error message", result.exceptionOrNull()?.message)
    }
  }

  @Nested
  inner class CombineResultsTests {

    @Test
    fun combinesAllSuccessfulResults() {
      val result1 = Result.success(1)
      val result2 = Result.success(2)
      val result3 = Result.success(3)

      val combined = combineResults(listOf(result1, result2, result3))

      assertTrue(combined.isSuccess)
      assertEquals(listOf(1, 2, 3), combined.getOrNull())
    }

    @Test
    fun propagatesFailureWhenCombiningResults() {
      val result1 = Result.success(1)
      val result2 = Result.failure<Int>(RuntimeException("error"))
      val result3 = Result.success(3)

      val combined = combineResults(listOf(result1, result2, result3))

      assertTrue(combined.isFailure)
      assertTrue(combined.exceptionOrNull() is RuntimeException)
      assertEquals("error", combined.exceptionOrNull()?.message)
    }

    @Test
    fun combinesEmptyResults() {
      val combined = combineResults(emptyList<Result<String>>())

      assertTrue(combined.isSuccess)
      assertEquals(emptyList<String>(), combined.getOrNull())
    }
  }

  @Nested
  inner class RetryMechanismTests {

    @Test
    fun returnsImmediatelyWhenFirstAttemptSucceeds() = runTest {
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
    fun succeedsAfterRetries() = runTest {
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
    fun failsAfterExhaustingRetries() = runTest {
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
    fun increasesRetryDelayBetweenAttempts() = runTest {
      var attempts = 0

      val result =
        retryWithBackoff<String>(
          maxRetries = 2,
          initialDelayMs = 1, // Use a small delay to avoid timing issues during tests
          maxDelayMs = 10,
          backoffMultiplier = 2.0,
        ) {
          attempts++
          Result.failure(RuntimeException("always fail"))
        }

      log.info("Retry attempts: {}", attempts)

      // Verify the number of attempts is correct
      assertEquals(3, attempts) // maxRetries + 1 final attempt
      assertTrue(result.isFailure)
      assertTrue(result.exceptionOrNull()?.message?.contains("always fail") == true)
    }
  }
}
