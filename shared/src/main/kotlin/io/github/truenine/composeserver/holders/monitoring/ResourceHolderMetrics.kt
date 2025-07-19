package io.github.truenine.composeserver.holders.monitoring

import io.github.truenine.composeserver.logger
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Metrics collector for ResourceHolder performance monitoring.
 *
 * This class tracks various performance metrics:
 * - Resolution times and counts
 * - Cache hit/miss rates
 * - Error rates and types
 * - Resource source performance
 * - Memory usage patterns
 *
 * @author TrueNine
 * @since 2024-07-18
 */
class ResourceHolderMetrics {

  companion object {
    private val log = logger<ResourceHolderMetrics>()
  }

  // Resolution metrics
  private val totalResolutions = AtomicLong(0)
  private val successfulResolutions = AtomicLong(0)
  private val failedResolutions = AtomicLong(0)
  private val fallbackResolutions = AtomicLong(0)

  // Timing metrics
  private val totalResolutionTime = AtomicLong(0) // in nanoseconds
  private val maxResolutionTime = AtomicLong(0)
  private val minResolutionTime = AtomicLong(Long.MAX_VALUE)

  // Cache metrics (updated from ResourceCache)
  private val cacheHits = AtomicLong(0)
  private val cacheMisses = AtomicLong(0)
  private val cacheEvictions = AtomicLong(0)

  // Error metrics
  private val validationErrors = AtomicLong(0)
  private val ioErrors = AtomicLong(0)
  private val configurationErrors = AtomicLong(0)

  // Resource source metrics
  private val sourcePerformance = mutableMapOf<String, SourceMetrics>()

  // Startup metrics
  private val initializationTime = AtomicReference<Long>()
  private val startupTime = AtomicReference<Instant>()

  init {
    startupTime.set(Instant.now())
  }

  /** Records a resource resolution attempt. */
  fun recordResolution(success: Boolean, durationNanos: Long, usedFallback: Boolean = false, sourceName: String? = null) {
    totalResolutions.incrementAndGet()

    if (success) {
      successfulResolutions.incrementAndGet()
    } else {
      failedResolutions.incrementAndGet()
    }

    if (usedFallback) {
      fallbackResolutions.incrementAndGet()
    }

    // Update timing metrics
    totalResolutionTime.addAndGet(durationNanos)
    updateMaxTime(durationNanos)
    updateMinTime(durationNanos)

    // Update source-specific metrics
    sourceName?.let { name ->
      synchronized(sourcePerformance) {
        val metrics = sourcePerformance.getOrPut(name) { SourceMetrics() }
        metrics.recordResolution(success, durationNanos)
      }
    }
  }

  /** Records cache statistics. */
  fun updateCacheMetrics(hits: Long, misses: Long, evictions: Long) {
    cacheHits.set(hits)
    cacheMisses.set(misses)
    cacheEvictions.set(evictions)
  }

  /** Records an error by type. */
  fun recordError(errorType: ErrorType) {
    when (errorType) {
      ErrorType.VALIDATION -> validationErrors.incrementAndGet()
      ErrorType.IO -> ioErrors.incrementAndGet()
      ErrorType.CONFIGURATION -> configurationErrors.incrementAndGet()
    }
  }

  /** Records initialization completion time. */
  fun recordInitializationComplete() {
    val startTime = startupTime.get()
    if (startTime != null && initializationTime.get() == null) {
      val duration = java.time.Duration.between(startTime, Instant.now()).toMillis()
      initializationTime.set(duration)
      log.info("ResourceHolder initialization completed in {}ms", duration)
    }
  }

  /** Gets comprehensive performance statistics. */
  fun getPerformanceStats(): PerformanceStats {
    val totalRes = totalResolutions.get()
    val avgResolutionTime =
      if (totalRes > 0) {
        totalResolutionTime.get() / totalRes / 1_000_000.0 // Convert to milliseconds
      } else {
        0.0
      }

    val cacheHitRate = run {
      val hits = cacheHits.get()
      val misses = cacheMisses.get()

      // Handle potential overflow when adding very large numbers
      if (hits == 0L && misses == 0L) {
        0.0
      } else if (hits == Long.MAX_VALUE && misses == Long.MAX_VALUE) {
        // Special case for very large numbers to avoid overflow
        0.5
      } else {
        // Use BigInteger for very large numbers to avoid overflow
        val totalOperations = hits + misses
        if (totalOperations > 0) {
          hits.toDouble() / totalOperations
        } else {
          // Handle overflow case - use BigInteger arithmetic
          val bigHits = hits.toBigInteger()
          val bigMisses = misses.toBigInteger()
          val bigTotal = bigHits + bigMisses
          if (bigTotal > 0.toBigInteger()) {
            bigHits.toDouble() / bigTotal.toDouble()
          } else {
            0.0
          }
        }
      }
    }

    val successRate =
      if (totalRes > 0) {
        successfulResolutions.get().toDouble() / totalRes
      } else {
        0.0
      }

    val fallbackRate =
      if (totalRes > 0) {
        fallbackResolutions.get().toDouble() / totalRes
      } else {
        0.0
      }

    return PerformanceStats(
      totalResolutions = totalRes,
      successfulResolutions = successfulResolutions.get(),
      failedResolutions = failedResolutions.get(),
      fallbackResolutions = fallbackResolutions.get(),
      averageResolutionTimeMs = avgResolutionTime,
      maxResolutionTimeMs = maxResolutionTime.get() / 1_000_000.0,
      minResolutionTimeMs = if (minResolutionTime.get() == Long.MAX_VALUE) 0.0 else minResolutionTime.get() / 1_000_000.0,
      cacheHitRate = cacheHitRate,
      successRate = successRate,
      fallbackRate = fallbackRate,
      validationErrors = validationErrors.get(),
      ioErrors = ioErrors.get(),
      configurationErrors = configurationErrors.get(),
      initializationTimeMs = initializationTime.get() ?: 0L,
      sourceMetrics = sourcePerformance.toMap(),
    )
  }

  /** Gets a human-readable performance summary. */
  fun getPerformanceSummary(): String {
    val stats = getPerformanceStats()
    return buildString {
      appendLine("ResourceHolder Performance Summary:")
      appendLine("  Total Resolutions: ${stats.totalResolutions}")
      appendLine("  Success Rate: ${String.format("%.2f%%", stats.successRate * 100)}")
      appendLine("  Fallback Rate: ${String.format("%.2f%%", stats.fallbackRate * 100)}")
      appendLine("  Average Resolution Time: ${String.format("%.2fms", stats.averageResolutionTimeMs)}")
      appendLine("  Cache Hit Rate: ${String.format("%.2f%%", stats.cacheHitRate * 100)}")
      appendLine("  Initialization Time: ${stats.initializationTimeMs}ms")
      if (stats.validationErrors + stats.ioErrors + stats.configurationErrors > 0) {
        appendLine("  Errors: ${stats.validationErrors} validation, ${stats.ioErrors} I/O, ${stats.configurationErrors} configuration")
      }
    }
  }

  /** Resets all metrics (useful for testing). */
  fun reset() {
    totalResolutions.set(0)
    successfulResolutions.set(0)
    failedResolutions.set(0)
    fallbackResolutions.set(0)
    totalResolutionTime.set(0)
    maxResolutionTime.set(0)
    minResolutionTime.set(Long.MAX_VALUE)
    cacheHits.set(0)
    cacheMisses.set(0)
    cacheEvictions.set(0)
    validationErrors.set(0)
    ioErrors.set(0)
    configurationErrors.set(0)
    synchronized(sourcePerformance) { sourcePerformance.clear() }
    startupTime.set(Instant.now())
    initializationTime.set(null)
  }

  private fun updateMaxTime(durationNanos: Long) {
    var currentMax = maxResolutionTime.get()
    while (durationNanos > currentMax) {
      if (maxResolutionTime.compareAndSet(currentMax, durationNanos)) {
        break
      }
      currentMax = maxResolutionTime.get()
    }
  }

  private fun updateMinTime(durationNanos: Long) {
    var currentMin = minResolutionTime.get()
    while (durationNanos < currentMin) {
      if (minResolutionTime.compareAndSet(currentMin, durationNanos)) {
        break
      }
      currentMin = minResolutionTime.get()
    }
  }

  /** Metrics for individual resource sources. */
  data class SourceMetrics(
    var resolutions: Long = 0,
    var successes: Long = 0,
    var totalTime: Long = 0,
    var maxTime: Long = 0,
    var minTime: Long = Long.MAX_VALUE,
  ) {
    fun recordResolution(success: Boolean, durationNanos: Long) {
      resolutions++
      if (success) successes++
      totalTime += durationNanos
      if (durationNanos > maxTime) maxTime = durationNanos
      if (durationNanos < minTime) minTime = durationNanos
    }

    val successRate: Double
      get() = if (resolutions > 0) successes.toDouble() / resolutions else 0.0

    val averageTimeMs: Double
      get() = if (resolutions > 0) totalTime.toDouble() / resolutions / 1_000_000.0 else 0.0
  }

  /** Comprehensive performance statistics. */
  data class PerformanceStats(
    val totalResolutions: Long,
    val successfulResolutions: Long,
    val failedResolutions: Long,
    val fallbackResolutions: Long,
    val averageResolutionTimeMs: Double,
    val maxResolutionTimeMs: Double,
    val minResolutionTimeMs: Double,
    val cacheHitRate: Double,
    val successRate: Double,
    val fallbackRate: Double,
    val validationErrors: Long,
    val ioErrors: Long,
    val configurationErrors: Long,
    val initializationTimeMs: Long,
    val sourceMetrics: Map<String, SourceMetrics>,
  )

  /** Error types for categorization. */
  enum class ErrorType {
    VALIDATION,
    IO,
    CONFIGURATION,
  }
}
