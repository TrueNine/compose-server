package io.github.truenine.composeserver

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Extension functions for Result type to provide unified exception handling patterns
 *
 * These utilities help standardize error handling across the codebase, particularly useful for OSS operations and other I/O intensive tasks.
 *
 * @author TrueNine
 * @since 2025-08-04
 */

/**
 * Safely execute a synchronous operation and wrap the result
 *
 * @param action The operation to execute
 * @return Result wrapping the operation result or exception
 */
inline fun <T> safeCall(action: () -> T): Result<T> {
  return try {
    Result.success(action())
  } catch (e: Exception) {
    Result.failure(e)
  }
}

/**
 * Safely execute an asynchronous operation with IO context and wrap the result
 *
 * @param action The suspending operation to execute
 * @return Result wrapping the operation result or exception
 */
suspend inline fun <T> safeCallAsync(crossinline action: suspend () -> T): Result<T> {
  return try {
    withContext(Dispatchers.IO) { Result.success(action()) }
  } catch (e: Exception) {
    Result.failure(e)
  }
}

/**
 * Safely execute an operation with custom context and wrap the result
 *
 * @param context The coroutine context to use
 * @param action The suspending operation to execute
 * @return Result wrapping the operation result or exception
 */
suspend inline fun <T> safeCallWithContext(context: kotlin.coroutines.CoroutineContext, crossinline action: suspend () -> T): Result<T> {
  return try {
    withContext(context) { Result.success(action()) }
  } catch (e: Exception) {
    Result.failure(e)
  }
}

/**
 * Transform a Result's success value while preserving failure
 *
 * @param transform The transformation function
 * @return New Result with transformed success value
 */
inline fun <T, R> Result<T>.mapSuccess(transform: (T) -> R): Result<R> {
  return fold(onSuccess = { Result.success(transform(it)) }, onFailure = { Result.failure(it) })
}

/**
 * Transform a Result's failure while preserving success
 *
 * @param transform The transformation function for the exception
 * @return New Result with transformed failure
 */
inline fun <T> Result<T>.mapFailure(transform: (Throwable) -> Throwable): Result<T> {
  return fold(onSuccess = { Result.success(it) }, onFailure = { Result.failure(transform(it)) })
}

/**
 * Execute an action if the Result is successful, returning the original Result
 *
 * @param action The action to execute with the success value
 * @return The original Result
 */
inline fun <T> Result<T>.onSuccessDo(action: (T) -> Unit): Result<T> {
  if (isSuccess) {
    action(getOrThrow())
  }
  return this
}

/**
 * Execute an action if the Result is a failure, returning the original Result
 *
 * @param action The action to execute with the exception
 * @return The original Result
 */
inline fun <T> Result<T>.onFailureDo(action: (Throwable) -> Unit): Result<T> {
  if (isFailure) {
    action(exceptionOrNull()!!)
  }
  return this
}

/**
 * Convert a nullable value to a Result
 *
 * @param errorMessage The error message if value is null
 * @return Result.success if value is not null, Result.failure otherwise
 */
fun <T : Any> T?.toResult(errorMessage: String = "Value is null"): Result<T> {
  return if (this != null) {
    Result.success(this)
  } else {
    Result.failure(IllegalArgumentException(errorMessage))
  }
}

/**
 * Combine multiple Results into a single Result containing a list
 *
 * @param results The Results to combine
 * @return Result.success with list of all success values, or Result.failure with first failure
 */
fun <T> combineResults(results: List<Result<T>>): Result<List<T>> {
  val values = mutableListOf<T>()
  for (result in results) {
    result.fold(
      onSuccess = { values.add(it) },
      onFailure = {
        return Result.failure(it)
      },
    )
  }
  return Result.success(values)
}

/**
 * Retry a Result-returning operation with exponential backoff
 *
 * @param maxRetries Maximum number of retry attempts
 * @param initialDelayMs Initial delay in milliseconds
 * @param maxDelayMs Maximum delay in milliseconds
 * @param backoffMultiplier Multiplier for exponential backoff
 * @param operation The operation to retry
 * @return Result of the operation after retries
 */
suspend fun <T> retryWithBackoff(
  maxRetries: Int = 3,
  initialDelayMs: Long = 1000,
  maxDelayMs: Long = 10000,
  backoffMultiplier: Double = 2.0,
  operation: suspend () -> Result<T>,
): Result<T> {
  var currentDelay = initialDelayMs
  repeat(maxRetries) { attempt ->
    val result = operation()
    if (result.isSuccess) {
      return result
    }

    if (attempt < maxRetries - 1) {
      kotlinx.coroutines.delay(currentDelay)
      currentDelay = (currentDelay * backoffMultiplier).toLong().coerceAtMost(maxDelayMs)
    }
  }
  return operation() // Final attempt
}
