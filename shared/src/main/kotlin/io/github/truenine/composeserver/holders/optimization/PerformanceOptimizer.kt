package io.github.truenine.composeserver.holders.optimization

import io.github.truenine.composeserver.logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Performance optimization utilities for ResourceHolder operations.
 *
 * This class provides various optimization techniques:
 * - String interning for common patterns
 * - Path normalization caching
 * - Resource metadata caching
 * - Memory-efficient operations
 *
 * @author TrueNine
 * @since 2024-07-18
 */
object PerformanceOptimizer {

  private val log = logger<PerformanceOptimizer>()

  // String interning for common patterns and paths
  private val internedStrings = ConcurrentHashMap<String, String>()
  private val pathNormalizationCache = ConcurrentHashMap<String, String>()
  private val lock = ReentrantReadWriteLock()

  // Performance thresholds
  private const val MAX_INTERNED_STRINGS = 10000
  private const val MAX_PATH_CACHE_SIZE = 5000

  /**
   * Interns a string to reduce memory usage for frequently used strings.
   *
   * This is particularly useful for resource patterns and paths that are used repeatedly throughout the application lifecycle.
   *
   * @param str The string to intern
   * @return The interned string
   */
  fun internString(str: String): String {
    if (str.length > 500) {
      // Don't intern very long strings
      return str
    }

    return lock.read { internedStrings[str] }
      ?: lock.write {
        // Double-check pattern
        internedStrings[str]
          ?: run {
            if (internedStrings.size >= MAX_INTERNED_STRINGS) {
              // Don't grow the cache indefinitely
              str
            } else {
              val interned = str.intern()
              internedStrings[str] = interned
              interned
            }
          }
      }
  }

  /**
   * Normalizes a path with caching for performance.
   *
   * Path normalization is expensive, so we cache the results for frequently accessed paths.
   *
   * @param path The path to normalize
   * @return The normalized path
   */
  fun normalizePath(path: String): String {
    if (path.isEmpty()) return path

    return lock.read { pathNormalizationCache[path] }
      ?: lock.write {
        // Double-check pattern
        pathNormalizationCache[path]
          ?: run {
            val normalized = doNormalizePath(path)
            if (pathNormalizationCache.size < MAX_PATH_CACHE_SIZE) {
              pathNormalizationCache[path] = normalized
            }
            normalized
          }
      }
  }

  /** Performs the actual path normalization. */
  private fun doNormalizePath(path: String): String {
    return path
      .replace('\\', '/') // Normalize separators
      .replace(Regex("/+"), "/") // Remove duplicate separators
      .removeSuffix("/") // Remove trailing separator
      .let { if (it.isEmpty()) "/" else it } // Ensure root path is "/"
  }

  /**
   * Creates an optimized string builder with appropriate initial capacity.
   *
   * This helps reduce memory allocations during string building operations.
   *
   * @param estimatedLength Estimated final length of the string
   * @return StringBuilder with optimized capacity
   */
  fun createOptimizedStringBuilder(estimatedLength: Int = 64): StringBuilder {
    // Round up to next power of 2 for better memory allocation
    val capacity =
      if (estimatedLength <= 16) 16
      else if (estimatedLength <= 32) 32
      else if (estimatedLength <= 64) 64 else if (estimatedLength <= 128) 128 else if (estimatedLength <= 256) 256 else estimatedLength

    return StringBuilder(capacity)
  }

  /**
   * Efficiently joins path components without creating intermediate strings.
   *
   * @param components The path components to join
   * @param separator The separator to use (default: "/")
   * @return The joined path
   */
  fun joinPaths(components: List<String>, separator: String = "/"): String {
    if (components.isEmpty()) return ""
    if (components.size == 1) return components[0]

    val totalLength = components.sumOf { it.length } + (components.size - 1) * separator.length
    val builder = createOptimizedStringBuilder(totalLength)

    components.forEachIndexed { index, component ->
      if (index > 0) builder.append(separator)
      builder.append(component)
    }

    return builder.toString()
  }

  /**
   * Efficiently checks if a string contains any of the given substrings.
   *
   * This is optimized for checking multiple patterns against a single string.
   *
   * @param text The text to search in
   * @param patterns The patterns to search for
   * @return true if any pattern is found
   */
  fun containsAny(text: String, patterns: Collection<String>): Boolean {
    if (patterns.isEmpty()) return false

    // Sort patterns by length (longer first) for potential early termination
    val sortedPatterns = patterns.sortedByDescending { it.length }

    return sortedPatterns.any { pattern -> text.contains(pattern, ignoreCase = false) }
  }

  /**
   * Efficiently removes a prefix from a string if present.
   *
   * @param text The text to process
   * @param prefix The prefix to remove
   * @return The text without the prefix
   */
  fun removePrefix(text: String, prefix: String): String {
    return if (text.startsWith(prefix)) {
      text.substring(prefix.length)
    } else {
      text
    }
  }

  /**
   * Efficiently removes a suffix from a string if present.
   *
   * @param text The text to process
   * @param suffix The suffix to remove
   * @return The text without the suffix
   */
  fun removeSuffix(text: String, suffix: String): String {
    return if (text.endsWith(suffix)) {
      text.substring(0, text.length - suffix.length)
    } else {
      text
    }
  }

  /**
   * Creates a hash code for a resource pattern that's optimized for caching.
   *
   * This creates consistent hash codes for patterns that should be considered equivalent (e.g., different path separators).
   *
   * @param pattern The pattern to hash
   * @param profile Optional profile to include in hash
   * @return Optimized hash code
   */
  fun createPatternHash(pattern: String, profile: String? = null): Int {
    val normalizedPattern = normalizePath(pattern)
    return if (profile != null) {
      (normalizedPattern.hashCode() * 31) + profile.hashCode()
    } else {
      normalizedPattern.hashCode()
    }
  }

  /**
   * Clears all optimization caches.
   *
   * This should be called periodically or when memory pressure is detected.
   */
  fun clearCaches() {
    lock.write {
      val internedCount = internedStrings.size
      val pathCacheCount = pathNormalizationCache.size

      internedStrings.clear()
      pathNormalizationCache.clear()

      log.info("Cleared optimization caches: {} interned strings, {} cached paths", internedCount, pathCacheCount)
    }
  }

  /** Gets cache statistics for monitoring. */
  fun getCacheStats(): CacheStats {
    return lock.read {
      CacheStats(
        internedStringsCount = internedStrings.size,
        pathCacheCount = pathNormalizationCache.size,
        maxInternedStrings = MAX_INTERNED_STRINGS,
        maxPathCacheSize = MAX_PATH_CACHE_SIZE,
      )
    }
  }

  /**
   * Estimates memory usage of the optimization caches.
   *
   * @return Estimated memory usage in bytes
   */
  fun estimateMemoryUsage(): Long {
    return lock.read {
      var totalBytes = 0L

      // Estimate interned strings memory
      internedStrings.keys.forEach { key ->
        totalBytes += key.length * 2L // Assuming 2 bytes per char
        totalBytes += 32L // Object overhead
      }

      // Estimate path cache memory
      pathNormalizationCache.forEach { (key, value) ->
        totalBytes += key.length * 2L
        totalBytes += value.length * 2L
        totalBytes += 64L // Object overhead
      }

      totalBytes
    }
  }

  /** Cache statistics data class. */
  data class CacheStats(val internedStringsCount: Int, val pathCacheCount: Int, val maxInternedStrings: Int, val maxPathCacheSize: Int) {
    val internedStringsUtilization: Double
      get() = internedStringsCount.toDouble() / maxInternedStrings

    val pathCacheUtilization: Double
      get() = pathCacheCount.toDouble() / maxPathCacheSize

    override fun toString(): String {
      return "CacheStats(interned: $internedStringsCount/$maxInternedStrings " +
        "(${String.format("%.1f%%", internedStringsUtilization * 100)}), " +
        "paths: $pathCacheCount/$maxPathCacheSize " +
        "(${String.format("%.1f%%", pathCacheUtilization * 100)}))"
    }
  }
}
