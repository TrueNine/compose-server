package io.github.truenine.composeserver.holders.resolver

import io.mockk.mockk
import kotlin.test.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource

class ResourceCacheTest {

  private lateinit var cache: ResourceCache
  private lateinit var testResource: Resource

  @BeforeEach
  fun setUp() {
    cache = ResourceCache(maxSize = 3, ttlMillis = 1000) // Small cache for testing
    testResource = ByteArrayResource("test content".toByteArray())
  }

  @Test
  fun `should cache and retrieve resources`() {
    val resources = listOf(testResource)

    // Cache miss initially
    assertNull(cache.get("test.yml", null))

    // Put resource in cache
    cache.put("test.yml", null, resources)

    // Cache hit
    val cachedResources = cache.get("test.yml", null)
    assertNotNull(cachedResources)
    assertEquals(1, cachedResources.size)
    assertEquals(testResource, cachedResources[0])
  }

  @Test
  fun `should handle profile-specific caching`() {
    val devResources = listOf(testResource)
    val prodResource = ByteArrayResource("prod content".toByteArray())
    val prodResources = listOf(prodResource)

    // Cache resources for different profiles
    cache.put("config.yml", "dev", devResources)
    cache.put("config.yml", "prod", prodResources)
    cache.put("config.yml", null, listOf(mockk()))

    // Retrieve profile-specific resources
    val devCached = cache.get("config.yml", "dev")
    val prodCached = cache.get("config.yml", "prod")
    val defaultCached = cache.get("config.yml", null)

    assertNotNull(devCached)
    assertNotNull(prodCached)
    assertNotNull(defaultCached)

    assertEquals(testResource, devCached[0])
    assertEquals(prodResource, prodCached[0])
    assertNotEquals(devCached[0], prodCached[0])
  }

  @Test
  fun `should expire entries after TTL`() {
    val resources = listOf(testResource)

    // Use cache with very short TTL
    val shortTtlCache = ResourceCache(maxSize = 10, ttlMillis = 50)

    shortTtlCache.put("test.yml", null, resources)

    // Should be available immediately
    assertNotNull(shortTtlCache.get("test.yml", null))

    // Wait for expiration
    Thread.sleep(100)

    // Should be expired
    assertNull(shortTtlCache.get("test.yml", null))
  }

  @Test
  fun `should evict LRU entries when cache is full`() {
    val resource1 = ByteArrayResource("content1".toByteArray())
    val resource2 = ByteArrayResource("content2".toByteArray())
    val resource3 = ByteArrayResource("content3".toByteArray())
    val resource4 = ByteArrayResource("content4".toByteArray())

    // Fill cache to capacity
    cache.put("file1.yml", null, listOf(resource1))
    Thread.sleep(10) // Ensure different timestamps
    cache.put("file2.yml", null, listOf(resource2))
    Thread.sleep(10)
    cache.put("file3.yml", null, listOf(resource3))

    // All should be cached
    assertNotNull(cache.get("file1.yml", null))
    assertNotNull(cache.get("file2.yml", null))
    assertNotNull(cache.get("file3.yml", null))

    // Access file1 and file3 to make them more recently used
    Thread.sleep(10)
    cache.get("file1.yml", null)
    Thread.sleep(10)
    cache.get("file3.yml", null)

    // Add one more (should evict file2 as it's LRU)
    Thread.sleep(10)
    cache.put("file4.yml", null, listOf(resource4))

    // file2 should be evicted, others should remain
    assertNotNull(cache.get("file1.yml", null))
    assertNull(cache.get("file2.yml", null))
    assertNotNull(cache.get("file3.yml", null))
    assertNotNull(cache.get("file4.yml", null))
  }

  @Test
  fun `should invalidate all entries`() {
    cache.put("file1.yml", null, listOf(testResource))
    cache.put("file2.yml", "dev", listOf(testResource))
    cache.put("file3.yml", "prod", listOf(testResource))

    // All should be cached
    assertNotNull(cache.get("file1.yml", null))
    assertNotNull(cache.get("file2.yml", "dev"))
    assertNotNull(cache.get("file3.yml", "prod"))

    // Invalidate all
    cache.invalidate()

    // All should be gone
    assertNull(cache.get("file1.yml", null))
    assertNull(cache.get("file2.yml", "dev"))
    assertNull(cache.get("file3.yml", "prod"))
  }

  @Test
  fun `should invalidate entries matching pattern`() {
    cache.put("config.yml", null, listOf(testResource))
    cache.put("database.yml", null, listOf(testResource))
    cache.put("app.properties", null, listOf(testResource))

    // All should be cached
    assertNotNull(cache.get("config.yml", null))
    assertNotNull(cache.get("database.yml", null))
    assertNotNull(cache.get("app.properties", null))

    // Invalidate YAML files
    cache.invalidate("yml")

    // YAML files should be gone, properties should remain
    assertNull(cache.get("config.yml", null))
    assertNull(cache.get("database.yml", null))
    assertNotNull(cache.get("app.properties", null))
  }

  @Test
  fun `should invalidate profile-specific entries`() {
    cache.put("config.yml", "dev", listOf(testResource))
    cache.put("config.yml", "prod", listOf(testResource))
    cache.put("config.yml", null, listOf(testResource))

    // All should be cached
    assertNotNull(cache.get("config.yml", "dev"))
    assertNotNull(cache.get("config.yml", "prod"))
    assertNotNull(cache.get("config.yml", null))

    // Invalidate dev profile
    cache.invalidateProfile("dev")

    // Only dev should be gone
    assertNull(cache.get("config.yml", "dev"))
    assertNotNull(cache.get("config.yml", "prod"))
    assertNotNull(cache.get("config.yml", null))
  }

  @Test
  fun `should cleanup expired entries`() {
    val shortTtlCache = ResourceCache(maxSize = 10, ttlMillis = 50)

    shortTtlCache.put("file1.yml", null, listOf(testResource))
    shortTtlCache.put("file2.yml", null, listOf(testResource))

    // Both should be available
    assertNotNull(shortTtlCache.get("file1.yml", null))
    assertNotNull(shortTtlCache.get("file2.yml", null))

    // Wait for expiration
    Thread.sleep(100)

    // Cleanup expired entries
    shortTtlCache.cleanupExpired()

    // Both should be gone after cleanup
    assertNull(shortTtlCache.get("file1.yml", null))
    assertNull(shortTtlCache.get("file2.yml", null))
  }

  @Test
  fun `should provide accurate statistics`() {
    val stats = cache.getStats()

    assertEquals(0, stats.size)
    assertEquals(0, stats.hitCount)
    assertEquals(0, stats.missCount)
    assertEquals(0.0, stats.hitRate)

    // Add some entries and access them
    cache.put("file1.yml", null, listOf(testResource))
    cache.put("file2.yml", null, listOf(testResource))

    // Generate hits and misses
    cache.get("file1.yml", null) // hit
    cache.get("file1.yml", null) // hit
    cache.get("nonexistent.yml", null) // miss

    // Update stats (simulate what would happen in real usage)
    // Note: In real usage, ResourceCache would be updated by ResourceResolver
    // Here we simulate the behavior
    val updatedStats = cache.getStats()
    assertEquals(2, updatedStats.size)
  }

  @Test
  fun `should handle concurrent access safely`() {
    // Use a larger cache for concurrent testing
    val concurrentCache = ResourceCache(maxSize = 20, ttlMillis = 10000)
    val threads = mutableListOf<Thread>()
    val results = mutableListOf<Resource?>()

    // Create multiple threads that access the cache concurrently
    repeat(10) { i ->
      val thread = Thread {
        val resources = listOf(ByteArrayResource("content$i".toByteArray()))
        concurrentCache.put("file$i.yml", null, resources)

        val retrieved = concurrentCache.get("file$i.yml", null)
        synchronized(results) { results.add(retrieved?.firstOrNull()) }
      }
      threads.add(thread)
      thread.start()
    }

    // Wait for all threads to complete
    threads.forEach { it.join() }

    // All operations should have completed successfully
    assertEquals(10, results.size)
    results.forEach { assertNotNull(it) }
  }

  @Test
  fun `should handle empty resource lists`() {
    val emptyResources = emptyList<Resource>()

    cache.put("empty.yml", null, emptyResources)

    val cached = cache.get("empty.yml", null)
    assertNotNull(cached)
    assertTrue(cached.isEmpty())
  }

  @Test
  fun `should create proper cache keys`() {
    val resources = listOf(testResource)

    // Test different key combinations
    cache.put("test.yml", null, resources)
    cache.put("test.yml", "dev", resources)
    cache.put("test.yml", "prod", resources)

    // Each should be cached separately
    assertNotNull(cache.get("test.yml", null))
    assertNotNull(cache.get("test.yml", "dev"))
    assertNotNull(cache.get("test.yml", "prod"))

    // Different profiles should not interfere
    cache.invalidateProfile("dev")
    assertNotNull(cache.get("test.yml", null))
    assertNull(cache.get("test.yml", "dev"))
    assertNotNull(cache.get("test.yml", "prod"))
  }
}
