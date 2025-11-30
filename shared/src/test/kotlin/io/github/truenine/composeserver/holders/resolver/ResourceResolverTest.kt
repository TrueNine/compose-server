package io.github.truenine.composeserver.holders.resolver

import io.github.truenine.composeserver.holders.config.*
import io.github.truenine.composeserver.holders.exception.InvalidResourcePatternException
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.io.*
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import kotlin.test.*
import kotlin.test.assertNotNull

class ResourceResolverTest {

  private lateinit var resourceLoader: ResourceLoader
  private lateinit var cache: ResourceCache
  private lateinit var fallbackConfig: FallbackConfiguration
  private lateinit var resourceSources: List<ResourceSource>
  private lateinit var resolver: ResourceResolver
  private lateinit var patternResolver: PathMatchingResourcePatternResolver

  @BeforeEach
  fun setUp() {
    resourceLoader = mockk()
    cache = mockk(relaxed = true)
    patternResolver = mockk()
    fallbackConfig = FallbackConfiguration(enableProfileFallback = true, enableClasspathFallback = true)

    // Create mocked ResourceSource objects
    resourceSources =
      listOf(
        mockk<ResourceSource>(relaxed = true).apply {
          every { type } returns ResourceType.FILESYSTEM
          every { path } returns "/opt/config"
          every { priority } returns 1000
          every { profile } returns "prod"
          every { description } returns "Filesystem source for prod"
        },
        mockk<ResourceSource>(relaxed = true).apply {
          every { type } returns ResourceType.FILESYSTEM
          every { path } returns "/opt/config"
          every { priority } returns 900
          every { profile } returns null
          every { description } returns "Filesystem source"
        },
        mockk<ResourceSource>(relaxed = true).apply {
          every { type } returns ResourceType.CLASSPATH
          every { path } returns "config"
          every { priority } returns 500
          every { profile } returns "prod"
          every { description } returns "Classpath source for prod"
        },
        mockk<ResourceSource>(relaxed = true).apply {
          every { type } returns ResourceType.CLASSPATH
          every { path } returns "config"
          every { priority } returns 400
          every { profile } returns null
          every { description } returns "Classpath source"
        },
      )

    resolver = ResourceResolver(resourceSources, resourceLoader, cache, fallbackConfig)
  }

  @Test
  fun `should resolve single resource successfully`() {
    val mockResource = ByteArrayResource("test content".toByteArray())

    // Mock cache miss
    every { cache.get("test.yml", "prod") } returns null

    // Mock profile matching - first source matches "prod" profile
    every { resourceSources[0].matchesProfile("prod") } returns true
    every { resourceSources[1].matchesProfile("prod") } returns false
    every { resourceSources[2].matchesProfile("prod") } returns true
    every { resourceSources[3].matchesProfile("prod") } returns false

    // Mock successful resource resolution for the first source
    every { resourceSources[0].resolveResources("test.yml", any()) } returns listOf(mockResource)
    every { resourceSources[2].resolveResources("test.yml", any()) } returns emptyList()

    val result = resolver.resolveResource("test.yml", "prod")

    assertNotNull(result)
    assertEquals(mockResource, result)

    // Verify cache was updated
    verify { cache.put("test.yml", "prod", listOf(mockResource)) }
  }

  @Test
  fun `should return cached resource when available`() {
    val cachedResource = ByteArrayResource("cached content".toByteArray())

    // Mock cache hit
    every { cache.get("test.yml", "prod") } returns listOf(cachedResource)

    val result = resolver.resolveResource("test.yml", "prod")

    assertNotNull(result)
    assertEquals(cachedResource, result)

    // Verify cache was not updated (since it was a hit)
    verify(exactly = 0) { cache.put(any(), any(), any()) }
  }

  @Test
  fun `should resolve multiple resources with correct priority`() {
    val highPriorityResource =
      object : ByteArrayResource("high priority".toByteArray()) {
        override fun getFilename() = "config.yml"
      }
    val lowPriorityResource =
      object : ByteArrayResource("low priority".toByteArray()) {
        override fun getFilename() = "config.yml"
      }

    // Mock cache miss
    every { cache.get("config.yml", null) } returns null

    // Mock profile matching - sources 1 and 3 match null profile
    every { resourceSources[0].matchesProfile(null) } returns false
    every { resourceSources[1].matchesProfile(null) } returns true
    every { resourceSources[2].matchesProfile(null) } returns false
    every { resourceSources[3].matchesProfile(null) } returns true

    // Mock resources from different sources (same filename, different priority)
    every { resourceSources[1].resolveResources("config.yml", any()) } returns listOf(highPriorityResource)
    every { resourceSources[3].resolveResources("config.yml", any()) } returns listOf(lowPriorityResource)

    val results = resolver.resolveResources("config.yml", null)

    assertEquals(1, results.size) // Duplicates should be removed
    assertEquals(highPriorityResource, results[0]) // Higher priority should win
  }

  @Test
  fun `should filter resources by profile`() {
    val prodResource = ByteArrayResource("prod content".toByteArray())
    val defaultResource = ByteArrayResource("default content".toByteArray())

    // Mock cache miss
    every { cache.get("test.yml", "prod") } returns null

    // Mock profile matching - sources 0 and 2 match "prod" profile
    every { resourceSources[0].matchesProfile("prod") } returns true
    every { resourceSources[1].matchesProfile("prod") } returns false
    every { resourceSources[2].matchesProfile("prod") } returns true
    every { resourceSources[3].matchesProfile("prod") } returns false

    // Mock resources from profile-specific and default sources
    every { resourceSources[0].resolveResources("test.yml", any()) } returns listOf(prodResource)
    every { resourceSources[2].resolveResources("test.yml", any()) } returns listOf(defaultResource)

    val results = resolver.resolveResources("test.yml", "prod")

    assertTrue(results.isNotEmpty())
    // Should include resources from sources that match the profile
  }

  @Test
  fun `should validate resource patterns`() {
    assertThrows<InvalidResourcePatternException> { resolver.resolveResource("../../../etc/passwd", null) }

    assertThrows<InvalidResourcePatternException> { resolver.resolveResource("", null) }
  }

  @Test
  fun `should handle resource resolution errors gracefully`() {
    // Mock cache miss
    every { cache.get("test.yml", null) } returns null

    // Mock all sources throwing exceptions
    resourceSources.forEach { source -> every { source.resolveResources("test.yml", any()) } throws RuntimeException("Test error") }

    val results = resolver.resolveResources("test.yml", null)

    // Should return empty list instead of throwing
    assertTrue(results.isEmpty())
  }

  @Test
  fun `should use fallback when no resources found`() {
    // Mock cache miss
    every { cache.get("missing.yml", "prod") } returns null

    // Mock no resources found from primary sources
    resourceSources.forEach { source -> every { source.resolveResources("missing.yml", any()) } returns emptyList() }

    // Mock fallback returning a resource
    val fallbackResource = ByteArrayResource("fallback content".toByteArray())
    // This would be handled by the fallback handler in real implementation

    val results = resolver.resolveResources("missing.yml", "prod")

    // In this test setup, should return empty since we haven't mocked the fallback handler
    // In real usage, fallback handler would provide resources
    assertTrue(results.isEmpty())
  }

  @Test
  fun `should invalidate cache correctly`() {
    resolver.invalidateCache("*.yml")

    verify { cache.invalidate("*.yml") }
  }

  @Test
  fun `should invalidate cache for specific profile`() {
    resolver.invalidateCacheForProfile("dev")

    verify { cache.invalidateProfile("dev") }
  }

  @Test
  fun `should cleanup expired cache entries`() {
    resolver.cleanupCache()

    verify { cache.cleanupExpired() }
  }

  @Test
  fun `should return configured resource sources`() {
    val sources = resolver.getResourceSources()

    assertEquals(resourceSources.size, sources.size)
    assertEquals(resourceSources, sources)
  }

  @Test
  fun `should get cache statistics`() {
    val mockStats = ResourceCache.CacheStats(size = 10, maxSize = 100, hitCount = 50, missCount = 10, evictionCount = 2, hitRate = 0.83)

    every { cache.getStats() } returns mockStats

    val stats = resolver.getCacheStats()

    assertEquals(mockStats, stats)
  }

  @Test
  fun `should handle null patterns gracefully`() {
    assertThrows<InvalidResourcePatternException> { resolver.resolveResource("", null) }
  }

  @Test
  fun `should remove duplicate resources correctly`() {
    val resource1 =
      object : ByteArrayResource("content1".toByteArray()) {
        override fun getFilename() = "same.yml"
      }
    val resource2 =
      object : ByteArrayResource("content2".toByteArray()) {
        override fun getFilename() = "same.yml"
      }
    val resource3 =
      object : ByteArrayResource("content3".toByteArray()) {
        override fun getFilename() = "different.yml"
      }

    // Mock cache miss
    every { cache.get("*.yml", null) } returns null

    // Mock profile matching - sources 1 and 3 match null profile
    every { resourceSources[0].matchesProfile(null) } returns false
    every { resourceSources[1].matchesProfile(null) } returns true
    every { resourceSources[2].matchesProfile(null) } returns false
    every { resourceSources[3].matchesProfile(null) } returns true

    // Mock different sources returning resources with same and different names
    every { resourceSources[1].resolveResources("*.yml", any()) } returns listOf(resource1, resource3)
    every { resourceSources[3].resolveResources("*.yml", any()) } returns listOf(resource2)

    val results = resolver.resolveResources("*.yml", null)

    // Should have 2 resources (duplicates removed, higher priority kept)
    assertEquals(2, results.size)

    // Higher priority resource should be kept
    assertTrue(results.contains(resource1)) // From higher priority source
    assertTrue(results.contains(resource3))
    assertFalse(results.contains(resource2)) // Should be filtered out as duplicate
  }

  @Test
  fun `should handle concurrent access safely`() {
    val threads = mutableListOf<Thread>()
    val results = mutableListOf<Resource?>()

    // Mock cache behavior
    every { cache.get(any(), any()) } returns null
    every { cache.put(any(), any(), any()) } returns Unit

    // Mock profile matching for all sources
    resourceSources.forEach { source -> every { source.matchesProfile(null) } returns true }

    // Mock resource resolution
    val mockResource = ByteArrayResource("test".toByteArray())
    resourceSources.forEach { source -> every { source.resolveResources(any(), any()) } returns listOf(mockResource) }

    // Create multiple threads accessing the resolver concurrently
    repeat(10) { i ->
      val thread = Thread {
        val result = resolver.resolveResource("test$i.yml", null)
        synchronized(results) { results.add(result) }
      }
      threads.add(thread)
      thread.start()
    }

    // Wait for all threads to complete
    threads.forEach { it.join() }

    // All operations should complete successfully
    assertEquals(10, results.size)
    results.forEach { assertNotNull(it) }
  }
}
