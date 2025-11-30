package io.github.truenine.composeserver.holders.optimization

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.*
import kotlin.test.assertNotNull

class PerformanceOptimizerTest {

  @BeforeEach
  fun setUp() {
    // Clear caches before each test
    PerformanceOptimizer.clearCaches()
  }

  @AfterEach
  fun tearDown() {
    // Clean up after each test
    PerformanceOptimizer.clearCaches()
  }

  @Nested
  inner class StringInterningTest {

    @Test
    fun `should intern strings for memory efficiency`() {
      val str1 = "config.yml"
      val str2 = "config.yml"

      val interned1 = PerformanceOptimizer.internString(str1)
      val interned2 = PerformanceOptimizer.internString(str2)

      // Should return the same interned instance
      assertTrue(interned1 === interned2)
      assertEquals(str1, interned1)
    }

    @Test
    fun `should not intern very long strings`() {
      val longString = "a".repeat(1000)
      val interned = PerformanceOptimizer.internString(longString)

      // Should return the original string, not interned
      assertTrue(interned === longString)
    }

    @Test
    fun `should handle different strings correctly`() {
      val str1 = "config.yml"
      val str2 = "data.properties"

      val interned1 = PerformanceOptimizer.internString(str1)
      val interned2 = PerformanceOptimizer.internString(str2)

      assertFalse(interned1 === interned2)
      assertEquals(str1, interned1)
      assertEquals(str2, interned2)
    }

    @Test
    fun `should respect cache size limit`() {
      // Fill cache beyond limit
      repeat(15000) { i -> PerformanceOptimizer.internString("string$i") }

      val stats = PerformanceOptimizer.getCacheStats()
      assertTrue(stats.internedStringsCount <= stats.maxInternedStrings)
    }

    @Test
    fun `should handle empty and null-like strings`() {
      val emptyStr = ""
      val spaces = "   "

      val internedEmpty = PerformanceOptimizer.internString(emptyStr)
      val internedSpaces = PerformanceOptimizer.internString(spaces)

      assertEquals(emptyStr, internedEmpty)
      assertEquals(spaces, internedSpaces)
    }

    @Test
    fun `should handle Unicode strings`() {
      val unicodeStr = "konfigurační-soubor.yml"
      val interned1 = PerformanceOptimizer.internString(unicodeStr)
      val interned2 = PerformanceOptimizer.internString(unicodeStr)

      assertTrue(interned1 === interned2)
      assertEquals(unicodeStr, interned1)
    }
  }

  @Nested
  inner class PathNormalizationTest {

    @Test
    fun `should normalize path separators`() {
      val windowsPath = "config\\data\\file.yml"
      val normalized = PerformanceOptimizer.normalizePath(windowsPath)

      assertEquals("config/data/file.yml", normalized)
    }

    @Test
    fun `should remove duplicate separators`() {
      val pathWithDuplicates = "config//data///file.yml"
      val normalized = PerformanceOptimizer.normalizePath(pathWithDuplicates)

      assertEquals("config/data/file.yml", normalized)
    }

    @Test
    fun `should remove trailing separator`() {
      val pathWithTrailing = "config/data/"
      val normalized = PerformanceOptimizer.normalizePath(pathWithTrailing)

      assertEquals("config/data", normalized)
    }

    @Test
    fun `should handle root path correctly`() {
      val rootPath = "/"
      val normalized = PerformanceOptimizer.normalizePath(rootPath)

      assertEquals("/", normalized)

      val emptyPath = ""
      val normalizedEmpty = PerformanceOptimizer.normalizePath(emptyPath)
      assertEquals("", normalizedEmpty)
    }

    @Test
    fun `should cache normalization results`() {
      val path = "config\\\\data//file.yml"

      val normalized1 = PerformanceOptimizer.normalizePath(path)
      val normalized2 = PerformanceOptimizer.normalizePath(path)

      // Should return the same cached result
      assertTrue(normalized1 === normalized2)
      assertEquals("config/data/file.yml", normalized1)
    }

    @Test
    fun `should handle complex path cases`() {
      val testCases =
        mapOf(
          "config/./data/../file.yml" to "config/./data/../file.yml", // Doesn't resolve . and ..
          "config\\data/mixed\\separators" to "config/data/mixed/separators",
          "///multiple/leading/slashes" to "/multiple/leading/slashes",
          "trailing/slash//" to "trailing/slash",
        )

      testCases.forEach { (input, expected) ->
        val normalized = PerformanceOptimizer.normalizePath(input)
        assertEquals(expected, normalized, "Failed for input: $input")
      }
    }

    @Test
    fun `should respect cache size limit for paths`() {
      // Fill cache beyond limit
      repeat(6000) { i -> PerformanceOptimizer.normalizePath("path$i\\\\data//file.yml") }

      val stats = PerformanceOptimizer.getCacheStats()
      assertTrue(stats.pathCacheCount <= stats.maxPathCacheSize)
    }
  }

  @Nested
  inner class StringBuilderOptimizationTest {

    @Test
    fun `should create string builder with appropriate capacity`() {
      val capacityTests = mapOf(10 to 16, 20 to 32, 50 to 64, 100 to 128, 200 to 256, 300 to 300)

      capacityTests.forEach { (estimated, expectedCapacity) ->
        val builder = PerformanceOptimizer.createOptimizedStringBuilder(estimated)
        // Note: We can't directly test capacity, but we can test that it works
        assertNotNull(builder)
        assertTrue(builder.capacity() >= estimated)
      }
    }

    @Test
    fun `should handle edge cases for string builder`() {
      val zeroCapacity = PerformanceOptimizer.createOptimizedStringBuilder(0)
      assertTrue(zeroCapacity.capacity() >= 16)

      val negativeCapacity = PerformanceOptimizer.createOptimizedStringBuilder(-1)
      assertTrue(negativeCapacity.capacity() >= 16)

      val largeCapacity = PerformanceOptimizer.createOptimizedStringBuilder(10000)
      assertTrue(largeCapacity.capacity() >= 10000)
    }
  }

  @Nested
  inner class PathJoiningTest {

    @Test
    fun `should join paths efficiently`() {
      val components = listOf("config", "data", "file.yml")
      val joined = PerformanceOptimizer.joinPaths(components)

      assertEquals("config/data/file.yml", joined)
    }

    @Test
    fun `should handle custom separator`() {
      val components = listOf("config", "data", "file.yml")
      val joined = PerformanceOptimizer.joinPaths(components, "\\")

      assertEquals("config\\data\\file.yml", joined)
    }

    @Test
    fun `should handle edge cases`() {
      assertEquals("", PerformanceOptimizer.joinPaths(emptyList()))
      assertEquals("single", PerformanceOptimizer.joinPaths(listOf("single")))
      assertEquals("first/second", PerformanceOptimizer.joinPaths(listOf("first", "second")))
    }

    @Test
    fun `should handle empty components`() {
      val components = listOf("config", "", "data", "file.yml")
      val joined = PerformanceOptimizer.joinPaths(components)

      assertEquals("config//data/file.yml", joined)
    }

    @Test
    fun `should be efficient for large component lists`() {
      val largeComponents = (1..1000).map { "component$it" }
      val joined = PerformanceOptimizer.joinPaths(largeComponents)

      assertTrue(joined.isNotEmpty())
      assertTrue(joined.contains("component1"))
      assertTrue(joined.contains("component1000"))
      assertEquals(999, joined.count { it == '/' }) // 999 separators for 1000 components
    }
  }

  @Nested
  inner class PatternMatchingTest {

    @Test
    fun `should check if text contains any patterns`() {
      val text = "config/data/application.yml"
      val patterns = listOf("yml", "properties", "xml")

      assertTrue(PerformanceOptimizer.containsAny(text, patterns))
    }

    @Test
    fun `should return false when no patterns match`() {
      val text = "config/data/application.json"
      val patterns = listOf("yml", "properties", "xml")

      assertFalse(PerformanceOptimizer.containsAny(text, patterns))
    }

    @Test
    fun `should handle empty pattern list`() {
      val text = "config/data/application.yml"
      val emptyPatterns = emptyList<String>()

      assertFalse(PerformanceOptimizer.containsAny(text, emptyPatterns))
    }

    @Test
    fun `should be case sensitive`() {
      val text = "Config/Data/Application.YML"
      val patterns = listOf("yml", "config")

      assertFalse(PerformanceOptimizer.containsAny(text, patterns))

      val upperPatterns = listOf("YML", "Config")
      assertTrue(PerformanceOptimizer.containsAny(text, upperPatterns))
    }

    @Test
    fun `should optimize by checking longer patterns first`() {
      val text = "application.properties.yml"
      val patterns = listOf("yml", "properties.yml", "application")

      // Should find the match regardless of optimization
      assertTrue(PerformanceOptimizer.containsAny(text, patterns))
    }
  }

  @Nested
  inner class StringManipulationTest {

    @Test
    fun `should remove prefix when present`() {
      val text = "classpath:config/data.yml"
      val prefix = "classpath:"

      val result = PerformanceOptimizer.removePrefix(text, prefix)
      assertEquals("config/data.yml", result)
    }

    @Test
    fun `should return original when prefix not present`() {
      val text = "config/data.yml"
      val prefix = "classpath:"

      val result = PerformanceOptimizer.removePrefix(text, prefix)
      assertEquals(text, result)
    }

    @Test
    fun `should remove suffix when present`() {
      val text = "config/data.yml"
      val suffix = ".yml"

      val result = PerformanceOptimizer.removeSuffix(text, suffix)
      assertEquals("config/data", result)
    }

    @Test
    fun `should return original when suffix not present`() {
      val text = "config/data.properties"
      val suffix = ".yml"

      val result = PerformanceOptimizer.removeSuffix(text, suffix)
      assertEquals(text, result)
    }

    @Test
    fun `should handle edge cases for prefix and suffix removal`() {
      assertEquals("", PerformanceOptimizer.removePrefix("prefix", "prefix"))
      assertEquals("", PerformanceOptimizer.removeSuffix("suffix", "suffix"))
      assertEquals("text", PerformanceOptimizer.removePrefix("text", ""))
      assertEquals("text", PerformanceOptimizer.removeSuffix("text", ""))
    }
  }

  @Nested
  inner class HashCodeOptimizationTest {

    @Test
    fun `should create consistent hash codes for normalized patterns`() {
      val pattern1 = "config\\data\\file.yml"
      val pattern2 = "config//data/file.yml"

      val hash1 = PerformanceOptimizer.createPatternHash(pattern1)
      val hash2 = PerformanceOptimizer.createPatternHash(pattern2)

      assertEquals(hash1, hash2, "Hash codes should be equal for equivalent patterns")
    }

    @Test
    fun `should include profile in hash code when provided`() {
      val pattern = "config/data.yml"

      val hashWithoutProfile = PerformanceOptimizer.createPatternHash(pattern)
      val hashWithProfile = PerformanceOptimizer.createPatternHash(pattern, "dev")

      assertNotEquals(hashWithoutProfile, hashWithProfile)
    }

    @Test
    fun `should create same hash for same pattern and profile`() {
      val pattern = "config/data.yml"
      val profile = "prod"

      val hash1 = PerformanceOptimizer.createPatternHash(pattern, profile)
      val hash2 = PerformanceOptimizer.createPatternHash(pattern, profile)

      assertEquals(hash1, hash2)
    }

    @Test
    fun `should create different hashes for different profiles`() {
      val pattern = "config/data.yml"

      val devHash = PerformanceOptimizer.createPatternHash(pattern, "dev")
      val prodHash = PerformanceOptimizer.createPatternHash(pattern, "prod")

      assertNotEquals(devHash, prodHash)
    }
  }

  @Nested
  inner class CacheManagementTest {

    @Test
    fun `should provide cache statistics`() {
      // Add some items to caches
      repeat(10) { i ->
        PerformanceOptimizer.internString("string$i")
        PerformanceOptimizer.normalizePath("path$i\\file.yml")
      }

      val stats = PerformanceOptimizer.getCacheStats()

      assertEquals(10, stats.internedStringsCount)
      assertEquals(10, stats.pathCacheCount)
      assertTrue(stats.maxInternedStrings > 0)
      assertTrue(stats.maxPathCacheSize > 0)
      assertTrue(stats.internedStringsUtilization > 0.0)
      assertTrue(stats.pathCacheUtilization > 0.0)
    }

    @Test
    fun `should clear caches correctly`() {
      // Add items to caches
      PerformanceOptimizer.internString("test")
      PerformanceOptimizer.normalizePath("test\\path")

      val statsBefore = PerformanceOptimizer.getCacheStats()
      assertTrue(statsBefore.internedStringsCount > 0)
      assertTrue(statsBefore.pathCacheCount > 0)

      PerformanceOptimizer.clearCaches()

      val statsAfter = PerformanceOptimizer.getCacheStats()
      assertEquals(0, statsAfter.internedStringsCount)
      assertEquals(0, statsAfter.pathCacheCount)
    }

    @Test
    fun `should estimate memory usage`() {
      // Add some items
      repeat(5) { i ->
        PerformanceOptimizer.internString("string$i")
        PerformanceOptimizer.normalizePath("path$i\\file.yml")
      }

      val memoryUsage = PerformanceOptimizer.estimateMemoryUsage()
      assertTrue(memoryUsage > 0, "Memory usage should be greater than 0")
    }

    @Test
    fun `should provide readable cache stats string`() {
      repeat(5) { i ->
        PerformanceOptimizer.internString("string$i")
        PerformanceOptimizer.normalizePath("path$i")
      }

      val stats = PerformanceOptimizer.getCacheStats()
      val statsString = stats.toString()

      assertTrue(statsString.contains("CacheStats"))
      assertTrue(statsString.contains("interned"))
      assertTrue(statsString.contains("paths"))
      assertTrue(statsString.contains("%"))
    }

    @Test
    fun `should handle utilization calculations correctly`() {
      val stats = PerformanceOptimizer.getCacheStats()

      assertTrue(stats.internedStringsUtilization >= 0.0)
      assertTrue(stats.internedStringsUtilization <= 1.0)
      assertTrue(stats.pathCacheUtilization >= 0.0)
      assertTrue(stats.pathCacheUtilization <= 1.0)
    }
  }

  @Nested
  inner class ConcurrencyTest {

    @Test
    fun `should handle concurrent string interning safely`() {
      val threads = mutableListOf<Thread>()
      val results = mutableListOf<String>()

      repeat(10) { i ->
        val thread = Thread {
          val interned = PerformanceOptimizer.internString("concurrent$i")
          synchronized(results) { results.add(interned) }
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      assertEquals(10, results.size)
      results.forEach { assertNotNull(it) }
    }

    @Test
    fun `should handle concurrent path normalization safely`() {
      val threads = mutableListOf<Thread>()
      val results = mutableListOf<String>()

      repeat(10) { i ->
        val thread = Thread {
          val normalized = PerformanceOptimizer.normalizePath("concurrent$i\\\\path//file.yml")
          synchronized(results) { results.add(normalized) }
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      assertEquals(10, results.size)
      results.forEach {
        assertNotNull(it)
        assertTrue(it.contains("/"))
        assertFalse(it.contains("\\"))
      }
    }

    @Test
    fun `should handle concurrent cache operations safely`() {
      val threads = mutableListOf<Thread>()

      // Mix of operations
      repeat(20) { i ->
        val thread = Thread {
          when (i % 4) {
            0 -> PerformanceOptimizer.internString("thread$i")
            1 -> PerformanceOptimizer.normalizePath("thread$i\\path")
            2 -> PerformanceOptimizer.getCacheStats()
            3 -> PerformanceOptimizer.estimateMemoryUsage()
          }
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      // Should complete without exceptions
      val finalStats = PerformanceOptimizer.getCacheStats()
      assertTrue(finalStats.internedStringsCount >= 0)
      assertTrue(finalStats.pathCacheCount >= 0)
    }
  }

  @Nested
  inner class PerformanceTest {

    @Test
    fun `should demonstrate performance benefit of caching`() {
      val path = "config\\\\data////file.yml"

      // First normalization (should cache)
      val start1 = System.nanoTime()
      val result1 = PerformanceOptimizer.normalizePath(path)
      val time1 = System.nanoTime() - start1

      // Second normalization (should use cache)
      val start2 = System.nanoTime()
      val result2 = PerformanceOptimizer.normalizePath(path)
      val time2 = System.nanoTime() - start2

      assertEquals(result1, result2)
      assertTrue(result1 === result2, "Should return cached instance")
      // Second call should be faster (usually, but not guaranteed in all environments)
    }

    @Test
    fun `should handle large volume of operations efficiently`() {
      val startTime = System.currentTimeMillis()

      // Perform many operations
      repeat(1000) { i ->
        PerformanceOptimizer.internString("pattern$i")
        PerformanceOptimizer.normalizePath("path$i\\data\\file.yml")
        PerformanceOptimizer.joinPaths(listOf("config", "data$i", "file.yml"))
        PerformanceOptimizer.createPatternHash("pattern$i", "profile$i")
      }

      val endTime = System.currentTimeMillis()
      val duration = endTime - startTime

      // Should complete within reasonable time (adjust as needed)
      assertTrue(duration < 5000, "Operations took too long: ${duration}ms")

      val stats = PerformanceOptimizer.getCacheStats()
      assertTrue(stats.internedStringsCount > 0)
      assertTrue(stats.pathCacheCount > 0)
    }
  }
}
