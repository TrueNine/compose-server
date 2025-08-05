package io.github.truenine.composeserver.holders.monitoring

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ResourceHolderMetricsTest {

  private lateinit var metrics: ResourceHolderMetrics

  @BeforeEach
  fun setUp() {
    metrics = ResourceHolderMetrics()
  }

  @Nested
  inner class ResolutionMetricsTest {

    @Test
    fun `should record successful resolution`() {
      metrics.recordResolution(success = true, durationNanos = 1_000_000L)

      val stats = metrics.getPerformanceStats()
      assertEquals(1, stats.totalResolutions)
      assertEquals(1, stats.successfulResolutions)
      assertEquals(0, stats.failedResolutions)
      assertEquals(1.0, stats.successRate)
      assertTrue(stats.averageResolutionTimeMs > 0.0)
    }

    @Test
    fun `should record failed resolution`() {
      metrics.recordResolution(success = false, durationNanos = 2_000_000L)

      val stats = metrics.getPerformanceStats()
      assertEquals(1, stats.totalResolutions)
      assertEquals(0, stats.successfulResolutions)
      assertEquals(1, stats.failedResolutions)
      assertEquals(0.0, stats.successRate)
    }

    @Test
    fun `should record fallback resolution`() {
      metrics.recordResolution(success = true, durationNanos = 1_000_000L, usedFallback = true)

      val stats = metrics.getPerformanceStats()
      assertEquals(1, stats.totalResolutions)
      assertEquals(1, stats.successfulResolutions)
      assertEquals(1, stats.fallbackResolutions)
      assertEquals(1.0, stats.fallbackRate)
    }

    @Test
    fun `should calculate correct averages for multiple resolutions`() {
      metrics.recordResolution(success = true, durationNanos = 1_000_000L) // 1ms
      metrics.recordResolution(success = false, durationNanos = 3_000_000L) // 3ms
      metrics.recordResolution(success = true, durationNanos = 5_000_000L) // 5ms

      val stats = metrics.getPerformanceStats()
      assertEquals(3, stats.totalResolutions)
      assertEquals(2, stats.successfulResolutions)
      assertEquals(1, stats.failedResolutions)
      assertEquals(2.0 / 3.0, stats.successRate, 0.001)
      assertEquals(3.0, stats.averageResolutionTimeMs, 0.001) // (1+3+5)/3 = 3ms
    }

    @Test
    fun `should track timing statistics correctly`() {
      metrics.recordResolution(success = true, durationNanos = 1_000_000L) // 1ms
      metrics.recordResolution(success = true, durationNanos = 5_000_000L) // 5ms
      metrics.recordResolution(success = true, durationNanos = 3_000_000L) // 3ms

      val stats = metrics.getPerformanceStats()
      assertEquals(3.0, stats.averageResolutionTimeMs, 0.001)
      assertEquals(5.0, stats.maxResolutionTimeMs, 0.001)
      assertEquals(1.0, stats.minResolutionTimeMs, 0.001)
    }

    @Test
    fun `should handle zero resolutions gracefully`() {
      val stats = metrics.getPerformanceStats()

      assertEquals(0, stats.totalResolutions)
      assertEquals(0, stats.successfulResolutions)
      assertEquals(0, stats.failedResolutions)
      assertEquals(0.0, stats.successRate)
      assertEquals(0.0, stats.averageResolutionTimeMs)
      assertEquals(0.0, stats.minResolutionTimeMs)
    }

    @Test
    fun `should record source-specific metrics when provided`() {
      metrics.recordResolution(success = true, durationNanos = 2_000_000L, sourceName = "filesystem")
      metrics.recordResolution(success = false, durationNanos = 4_000_000L, sourceName = "filesystem")
      metrics.recordResolution(success = true, durationNanos = 1_000_000L, sourceName = "classpath")

      val stats = metrics.getPerformanceStats()

      assertEquals(2, stats.sourceMetrics.size)

      val filesystemMetrics = stats.sourceMetrics["filesystem"]
      assertNotNull(filesystemMetrics)
      assertEquals(2, filesystemMetrics.resolutions)
      assertEquals(1, filesystemMetrics.successes)
      assertEquals(0.5, filesystemMetrics.successRate)
      assertEquals(3.0, filesystemMetrics.averageTimeMs, 0.001) // (2+4)/2 = 3ms

      val classpathMetrics = stats.sourceMetrics["classpath"]
      assertNotNull(classpathMetrics)
      assertEquals(1, classpathMetrics.resolutions)
      assertEquals(1, classpathMetrics.successes)
      assertEquals(1.0, classpathMetrics.successRate)
      assertEquals(1.0, classpathMetrics.averageTimeMs, 0.001)
    }
  }

  @Nested
  inner class CacheMetricsTest {

    @Test
    fun `should update cache metrics`() {
      metrics.updateCacheMetrics(hits = 100, misses = 25, evictions = 5)

      val stats = metrics.getPerformanceStats()
      assertEquals(0.8, stats.cacheHitRate, 0.001) // 100/(100+25) = 0.8
    }

    @Test
    fun `should handle zero cache operations`() {
      metrics.updateCacheMetrics(hits = 0, misses = 0, evictions = 0)

      val stats = metrics.getPerformanceStats()
      assertEquals(0.0, stats.cacheHitRate)
    }

    @Test
    fun `should calculate hit rate correctly for various scenarios`() {
      val testCases =
        listOf(
          Triple(50L, 50L, 0.5), // 50% hit rate
          Triple(75L, 25L, 0.75), // 75% hit rate
          Triple(100L, 0L, 1.0), // 100% hit rate
          Triple(0L, 100L, 0.0), // 0% hit rate
        )

      testCases.forEach { (hits, misses, expectedRate) ->
        val testMetrics = ResourceHolderMetrics()
        testMetrics.updateCacheMetrics(hits, misses, 0)

        val stats = testMetrics.getPerformanceStats()
        assertEquals(expectedRate, stats.cacheHitRate, 0.001, "Failed for hits=$hits, misses=$misses")
      }
    }
  }

  @Nested
  inner class ErrorMetricsTest {

    @Test
    fun `should record validation errors`() {
      metrics.recordError(ResourceHolderMetrics.ErrorType.VALIDATION)
      metrics.recordError(ResourceHolderMetrics.ErrorType.VALIDATION)

      val stats = metrics.getPerformanceStats()
      assertEquals(2, stats.validationErrors)
      assertEquals(0, stats.ioErrors)
      assertEquals(0, stats.configurationErrors)
    }

    @Test
    fun `should record IO errors`() {
      metrics.recordError(ResourceHolderMetrics.ErrorType.IO)

      val stats = metrics.getPerformanceStats()
      assertEquals(0, stats.validationErrors)
      assertEquals(1, stats.ioErrors)
      assertEquals(0, stats.configurationErrors)
    }

    @Test
    fun `should record configuration errors`() {
      metrics.recordError(ResourceHolderMetrics.ErrorType.CONFIGURATION)

      val stats = metrics.getPerformanceStats()
      assertEquals(0, stats.validationErrors)
      assertEquals(0, stats.ioErrors)
      assertEquals(1, stats.configurationErrors)
    }

    @Test
    fun `should record mixed error types`() {
      metrics.recordError(ResourceHolderMetrics.ErrorType.VALIDATION)
      metrics.recordError(ResourceHolderMetrics.ErrorType.IO)
      metrics.recordError(ResourceHolderMetrics.ErrorType.CONFIGURATION)
      metrics.recordError(ResourceHolderMetrics.ErrorType.VALIDATION)

      val stats = metrics.getPerformanceStats()
      assertEquals(2, stats.validationErrors)
      assertEquals(1, stats.ioErrors)
      assertEquals(1, stats.configurationErrors)
    }
  }

  @Nested
  inner class InitializationMetricsTest {

    @Test
    fun `should record initialization time`() {
      // Wait a bit then record completion
      Thread.sleep(10)
      metrics.recordInitializationComplete()

      val stats = metrics.getPerformanceStats()
      assertTrue(stats.initializationTimeMs > 0)
      assertTrue(stats.initializationTimeMs < 1000) // Should be reasonably quick
    }

    @Test
    fun `should handle multiple initialization completion calls`() {
      metrics.recordInitializationComplete()
      val firstTime = metrics.getPerformanceStats().initializationTimeMs

      Thread.sleep(10)
      metrics.recordInitializationComplete()
      val secondTime = metrics.getPerformanceStats().initializationTimeMs

      // Should keep the first recorded time
      assertEquals(firstTime, secondTime)
    }
  }

  @Nested
  inner class SourceMetricsTest {

    @Test
    fun `should calculate source metrics correctly`() {
      val sourceMetrics = ResourceHolderMetrics.SourceMetrics()

      sourceMetrics.recordResolution(success = true, durationNanos = 2_000_000L) // 2ms
      sourceMetrics.recordResolution(success = false, durationNanos = 4_000_000L) // 4ms
      sourceMetrics.recordResolution(success = true, durationNanos = 6_000_000L) // 6ms

      assertEquals(3, sourceMetrics.resolutions)
      assertEquals(2, sourceMetrics.successes)
      assertEquals(2.0 / 3.0, sourceMetrics.successRate, 0.001)
      assertEquals(4.0, sourceMetrics.averageTimeMs, 0.001) // (2+4+6)/3 = 4ms
      assertEquals(6_000_000L, sourceMetrics.maxTime)
      assertEquals(2_000_000L, sourceMetrics.minTime)
    }

    @Test
    fun `should handle zero resolutions in source metrics`() {
      val sourceMetrics = ResourceHolderMetrics.SourceMetrics()

      assertEquals(0, sourceMetrics.resolutions)
      assertEquals(0, sourceMetrics.successes)
      assertEquals(0.0, sourceMetrics.successRate)
      assertEquals(0.0, sourceMetrics.averageTimeMs)
      assertEquals(0L, sourceMetrics.maxTime)
      assertEquals(Long.MAX_VALUE, sourceMetrics.minTime)
    }

    @Test
    fun `should track min and max times correctly`() {
      val sourceMetrics = ResourceHolderMetrics.SourceMetrics()

      sourceMetrics.recordResolution(success = true, durationNanos = 5_000_000L)
      sourceMetrics.recordResolution(success = true, durationNanos = 1_000_000L)
      sourceMetrics.recordResolution(success = true, durationNanos = 8_000_000L)
      sourceMetrics.recordResolution(success = true, durationNanos = 3_000_000L)

      assertEquals(8_000_000L, sourceMetrics.maxTime)
      assertEquals(1_000_000L, sourceMetrics.minTime)
    }
  }

  @Nested
  inner class PerformanceSummaryTest {

    @Test
    fun `should generate readable performance summary`() {
      // Add some test data
      metrics.recordResolution(success = true, durationNanos = 2_000_000L)
      metrics.recordResolution(success = false, durationNanos = 3_000_000L)
      metrics.recordResolution(success = true, durationNanos = 1_000_000L, usedFallback = true)
      metrics.updateCacheMetrics(80, 20, 5)
      metrics.recordError(ResourceHolderMetrics.ErrorType.VALIDATION)
      metrics.recordInitializationComplete()

      val summary = metrics.getPerformanceSummary()

      assertTrue(summary.contains("ResourceHolder Performance Summary"))
      assertTrue(summary.contains("Total Resolutions: 3"))
      assertTrue(summary.contains("Success Rate: 66.67%"))
      assertTrue(summary.contains("Fallback Rate: 33.33%"))
      assertTrue(summary.contains("Cache Hit Rate: 80.00%"))
      assertTrue(summary.contains("Average Resolution Time:"))
      assertTrue(summary.contains("Initialization Time:"))
      assertTrue(summary.contains("Errors: 1 validation"))
    }

    @Test
    fun `should handle summary with no errors`() {
      metrics.recordResolution(success = true, durationNanos = 1_000_000L)
      metrics.updateCacheMetrics(100, 0, 0)

      val summary = metrics.getPerformanceSummary()

      assertTrue(summary.contains("Success Rate: 100.00%"))
      assertTrue(summary.contains("Cache Hit Rate: 100.00%"))
      assertFalse(summary.contains("Errors:"))
    }

    @Test
    fun `should format percentages correctly`() {
      metrics.recordResolution(success = true, durationNanos = 1_000_000L)
      metrics.recordResolution(success = true, durationNanos = 1_000_000L)
      metrics.recordResolution(success = false, durationNanos = 1_000_000L)
      // 2/3 success rate = 66.67%

      val summary = metrics.getPerformanceSummary()
      assertTrue(summary.contains("66.67%"))
    }
  }

  @Nested
  inner class ResetFunctionalityTest {

    @Test
    fun `should reset all metrics to initial state`() {
      // Add various metrics
      metrics.recordResolution(success = true, durationNanos = 1_000_000L, sourceName = "test")
      metrics.recordResolution(success = false, durationNanos = 2_000_000L)
      metrics.updateCacheMetrics(50, 10, 2)
      metrics.recordError(ResourceHolderMetrics.ErrorType.VALIDATION)
      metrics.recordError(ResourceHolderMetrics.ErrorType.IO)
      metrics.recordInitializationComplete()

      // Verify metrics exist
      val statsBefore = metrics.getPerformanceStats()
      assertTrue(statsBefore.totalResolutions > 0)
      assertTrue(statsBefore.sourceMetrics.isNotEmpty())

      // Reset
      metrics.reset()

      // Verify everything is reset
      val statsAfter = metrics.getPerformanceStats()
      assertEquals(0, statsAfter.totalResolutions)
      assertEquals(0, statsAfter.successfulResolutions)
      assertEquals(0, statsAfter.failedResolutions)
      assertEquals(0, statsAfter.fallbackResolutions)
      assertEquals(0.0, statsAfter.averageResolutionTimeMs)
      assertEquals(0.0, statsAfter.maxResolutionTimeMs)
      assertEquals(0.0, statsAfter.minResolutionTimeMs)
      assertEquals(0.0, statsAfter.cacheHitRate)
      assertEquals(0.0, statsAfter.successRate)
      assertEquals(0.0, statsAfter.fallbackRate)
      assertEquals(0, statsAfter.validationErrors)
      assertEquals(0, statsAfter.ioErrors)
      assertEquals(0, statsAfter.configurationErrors)
      assertEquals(0, statsAfter.initializationTimeMs)
      assertTrue(statsAfter.sourceMetrics.isEmpty())
    }

    @Test
    fun `should allow recording new metrics after reset`() {
      // Record, reset, then record again
      metrics.recordResolution(success = true, durationNanos = 1_000_000L)
      metrics.reset()
      metrics.recordResolution(success = false, durationNanos = 2_000_000L)

      val stats = metrics.getPerformanceStats()
      assertEquals(1, stats.totalResolutions)
      assertEquals(0, stats.successfulResolutions)
      assertEquals(1, stats.failedResolutions)
    }
  }

  @Nested
  inner class ConcurrencyTest {

    @Test
    fun `should handle concurrent resolution recording safely`() {
      val threads = mutableListOf<Thread>()

      repeat(100) { i ->
        val thread = Thread {
          metrics.recordResolution(success = i % 2 == 0, durationNanos = (i + 1) * 1_000_000L, usedFallback = i % 3 == 0, sourceName = "source${i % 3}")
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      val stats = metrics.getPerformanceStats()
      assertEquals(100, stats.totalResolutions)
      assertEquals(50, stats.successfulResolutions) // Every other one succeeds
      assertEquals(50, stats.failedResolutions)
      assertEquals(0.5, stats.successRate)
      assertEquals(34, stats.fallbackResolutions) // Every third one uses fallback (0,3,6...99 = 34 items)
      assertTrue(stats.sourceMetrics.isNotEmpty())
    }

    @Test
    fun `should handle concurrent cache metric updates safely`() {
      val threads = mutableListOf<Thread>()

      repeat(50) {
        val thread = Thread { metrics.updateCacheMetrics(10, 2, 1) }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      // Should complete without exceptions
      val stats = metrics.getPerformanceStats()
      assertNotNull(stats)
    }

    @Test
    fun `should handle concurrent error recording safely`() {
      val threads = mutableListOf<Thread>()

      repeat(30) { i ->
        val thread = Thread {
          val errorType =
            when (i % 3) {
              0 -> ResourceHolderMetrics.ErrorType.VALIDATION
              1 -> ResourceHolderMetrics.ErrorType.IO
              else -> ResourceHolderMetrics.ErrorType.CONFIGURATION
            }
          metrics.recordError(errorType)
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      val stats = metrics.getPerformanceStats()
      assertEquals(10, stats.validationErrors)
      assertEquals(10, stats.ioErrors)
      assertEquals(10, stats.configurationErrors)
    }

    @Test
    fun `should handle mixed concurrent operations safely`() {
      val threads = mutableListOf<Thread>()

      repeat(100) { i ->
        val thread = Thread {
          when (i % 4) {
            0 -> metrics.recordResolution(success = true, durationNanos = 1_000_000L)
            1 -> metrics.updateCacheMetrics(1, 0, 0)
            2 -> metrics.recordError(ResourceHolderMetrics.ErrorType.VALIDATION)
            3 -> metrics.getPerformanceStats()
          }
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      // Should complete without exceptions and have meaningful data
      val stats = metrics.getPerformanceStats()
      assertTrue(stats.totalResolutions > 0)
      assertTrue(stats.validationErrors > 0)
    }
  }

  @Nested
  inner class EdgeCaseTest {

    @Test
    fun `should handle very large duration values`() {
      metrics.recordResolution(success = true, durationNanos = Long.MAX_VALUE)

      val stats = metrics.getPerformanceStats()
      assertTrue(stats.averageResolutionTimeMs > 0)
      assertTrue(stats.maxResolutionTimeMs > 0)
    }

    @Test
    fun `should handle very small duration values`() {
      metrics.recordResolution(success = true, durationNanos = 1L)

      val stats = metrics.getPerformanceStats()
      assertTrue(stats.averageResolutionTimeMs >= 0)
      assertTrue(stats.minResolutionTimeMs >= 0)
    }

    @Test
    fun `should handle null source names gracefully`() {
      metrics.recordResolution(success = true, durationNanos = 1_000_000L, sourceName = null)

      val stats = metrics.getPerformanceStats()
      assertEquals(1, stats.totalResolutions)
      // Source metrics should be empty since no source name was provided
      assertTrue(stats.sourceMetrics.isEmpty())
    }

    @Test
    fun `should handle empty source names`() {
      metrics.recordResolution(success = true, durationNanos = 1_000_000L, sourceName = "")

      val stats = metrics.getPerformanceStats()
      assertEquals(1, stats.totalResolutions)
      // Should create entry for empty string
      assertTrue(stats.sourceMetrics.containsKey(""))
    }

    @Test
    fun `should handle very large cache numbers`() {
      metrics.updateCacheMetrics(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE)

      val stats = metrics.getPerformanceStats()
      assertEquals(0.5, stats.cacheHitRate, 0.001) // MAX_VALUE / (MAX_VALUE + MAX_VALUE) = 0.5
    }
  }
}
