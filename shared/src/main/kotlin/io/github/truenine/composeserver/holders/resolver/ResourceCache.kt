package io.github.truenine.composeserver.holders.resolver

import io.github.truenine.composeserver.logger
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import org.springframework.core.io.Resource

/**
 * Thread-safe LRU cache for resolved resources with TTL support.
 *
 * This cache improves performance by storing previously resolved resources and avoiding repeated file system operations. It supports:
 * - LRU eviction policy
 * - Time-based expiration (TTL)
 * - Thread-safe operations
 * - Cache statistics
 * - Pattern-based invalidation
 *
 * @property maxSize Maximum number of entries to cache
 * @property ttlMillis Time-to-live for cache entries in milliseconds
 * @author TrueNine
 * @since 2024-07-18
 */
class ResourceCache(
  private val maxSize: Int = 1000,
  private val ttlMillis: Long = 300_000, // 5 minutes default
) {

  companion object {
    private val log = logger<ResourceCache>()
  }

  private val cache = ConcurrentHashMap<String, CacheEntry>()
  private val accessOrder = LinkedHashMap<String, Long>()
  private val lock = ReentrantReadWriteLock()

  // Cache statistics
  @Volatile private var hitCount = 0L
  @Volatile private var missCount = 0L
  @Volatile private var evictionCount = 0L

  /** Represents a cached resource entry with metadata. */
  private data class CacheEntry(val resources: List<Resource>, val timestamp: Instant, val profile: String?) {
    fun isExpired(ttlMillis: Long): Boolean {
      return Instant.now().toEpochMilli() - timestamp.toEpochMilli() > ttlMillis
    }
  }

  /**
   * Retrieves resources from cache if available and not expired.
   *
   * @param pattern The resource pattern
   * @param profile The profile (can be null)
   * @return Cached resources or null if not found/expired
   */
  fun get(pattern: String, profile: String?): List<Resource>? {
    val key = createKey(pattern, profile)

    lock.read {
      val entry = cache[key]

      if (entry == null) {
        missCount++
        log.debug("Cache miss for pattern: {} (profile: {})", pattern, profile)
        return null
      }

      if (entry.isExpired(ttlMillis)) {
        // Need to upgrade to write lock to remove expired entry
        return lock.write {
          // Double-check under write lock
          val entryAgain = cache[key]
          if (entryAgain != null && entryAgain.isExpired(ttlMillis)) {
            cache.remove(key)
            accessOrder.remove(key)
            missCount++
            log.debug("Cache entry expired for pattern: {} (profile: {})", pattern, profile)
            null
          } else {
            // Entry was refreshed by another thread
            entryAgain?.resources
          }
        }
      }

      // Update access order with write lock
      lock.write { accessOrder[key] = System.currentTimeMillis() }

      hitCount++
      log.debug("Cache hit for pattern: {} (profile: {}), {} resources", pattern, profile, entry.resources.size)
      return entry.resources
    }
  }

  /**
   * Stores resources in cache.
   *
   * @param pattern The resource pattern
   * @param profile The profile (can be null)
   * @param resources The resources to cache
   */
  fun put(pattern: String, profile: String?, resources: List<Resource>) {
    val key = createKey(pattern, profile)
    val entry = CacheEntry(resources, Instant.now(), profile)

    lock.write {
      // Add/update entry
      cache[key] = entry
      accessOrder[key] = System.currentTimeMillis()

      // Evict if necessary
      evictIfNecessary()

      log.debug("Cached {} resources for pattern: {} (profile: {})", resources.size, pattern, profile)
    }
  }

  /**
   * Invalidates cache entries matching the given pattern.
   *
   * @param pattern Pattern to match for invalidation (null = invalidate all)
   */
  fun invalidate(pattern: String? = null) {
    lock.write {
      if (pattern == null) {
        val size = cache.size
        cache.clear()
        accessOrder.clear()
        log.info("Invalidated entire cache ({} entries)", size)
      } else {
        val keysToRemove = cache.keys.filter { it.contains(pattern) }
        keysToRemove.forEach { key ->
          cache.remove(key)
          accessOrder.remove(key)
        }
        log.info("Invalidated {} cache entries matching pattern: {}", keysToRemove.size, pattern)
      }
    }
  }

  /**
   * Invalidates cache entries for a specific profile.
   *
   * @param profile The profile to invalidate
   */
  fun invalidateProfile(profile: String) {
    lock.write {
      val keysToRemove = cache.entries.filter { it.value.profile == profile }.map { it.key }

      keysToRemove.forEach { key ->
        cache.remove(key)
        accessOrder.remove(key)
      }

      log.info("Invalidated {} cache entries for profile: {}", keysToRemove.size, profile)
    }
  }

  /** Removes expired entries from cache. */
  fun cleanupExpired() {
    lock.write {
      val expiredKeys = cache.entries.filter { it.value.isExpired(ttlMillis) }.map { it.key }

      expiredKeys.forEach { key ->
        cache.remove(key)
        accessOrder.remove(key)
      }

      if (expiredKeys.isNotEmpty()) {
        log.debug("Cleaned up {} expired cache entries", expiredKeys.size)
      }
    }
  }

  /**
   * Gets cache statistics.
   *
   * @return Cache statistics
   */
  fun getStats(): CacheStats {
    return lock.read {
      CacheStats(
        size = cache.size,
        maxSize = maxSize,
        hitCount = hitCount,
        missCount = missCount,
        evictionCount = evictionCount,
        hitRate = if (hitCount + missCount > 0) hitCount.toDouble() / (hitCount + missCount) else 0.0,
      )
    }
  }

  /** Creates a cache key from pattern and profile. */
  private fun createKey(pattern: String, profile: String?): String {
    return if (profile != null) "$pattern#$profile" else pattern
  }

  /** Evicts least recently used entries if cache is full. */
  private fun evictIfNecessary() {
    // This method should only be called under write lock
    while (cache.size > maxSize) {
      val lruEntry = accessOrder.minByOrNull { it.value }
      if (lruEntry != null) {
        val lruKey = lruEntry.key
        cache.remove(lruKey)
        accessOrder.remove(lruKey)
        evictionCount++
        log.debug("Evicted LRU cache entry: {}", lruKey)
      } else {
        break
      }
    }
  }

  /** Cache statistics data class. */
  data class CacheStats(val size: Int, val maxSize: Int, val hitCount: Long, val missCount: Long, val evictionCount: Long, val hitRate: Double) {
    override fun toString(): String {
      return "CacheStats(size=$size/$maxSize, hits=$hitCount, misses=$missCount, " +
        "evictions=$evictionCount, hitRate=${String.format("%.2f%%", hitRate * 100)})"
    }
  }
}
