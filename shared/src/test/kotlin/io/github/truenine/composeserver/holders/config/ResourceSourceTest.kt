package io.github.truenine.composeserver.holders.config

import io.github.truenine.composeserver.holders.exception.InvalidResourceSourceException
import io.mockk.every
import io.mockk.mockk
import kotlin.test.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

class ResourceSourceTest {

  @Test
  fun `should create filesystem resource source`() {
    val source = ResourceSource.filesystem("/opt/config", 1000, "prod")

    assertEquals(ResourceType.FILESYSTEM, source.type)
    assertEquals("/opt/config", source.path)
    assertEquals(1000, source.priority)
    assertEquals("prod", source.profile)
    assertTrue(source.description.contains("Filesystem"))
  }

  @Test
  fun `should create classpath resource source`() {
    val source = ResourceSource.classpath("config/data", 500)

    assertEquals(ResourceType.CLASSPATH, source.type)
    assertEquals("config/data", source.path)
    assertEquals(500, source.priority)
    assertNull(source.profile)
    assertTrue(source.description.contains("Classpath"))
  }

  @Test
  fun `should create URL resource source`() {
    val source = ResourceSource.url("https://config.example.com", 2000, "prod")

    assertEquals(ResourceType.URL, source.type)
    assertEquals("https://config.example.com", source.path)
    assertEquals(2000, source.priority)
    assertEquals("prod", source.profile)
    assertTrue(source.description.contains("URL"))
  }

  @Test
  fun `should validate resource source parameters`() {
    assertThrows<InvalidResourceSourceException> { ResourceSource(ResourceType.FILESYSTEM, "", 100) }

    assertThrows<InvalidResourceSourceException> { ResourceSource(ResourceType.FILESYSTEM, "/valid/path", -1) }
  }

  @Test
  fun `should match profile correctly`() {
    val profileSource = ResourceSource.filesystem("/config", 100, "dev")
    val defaultSource = ResourceSource.filesystem("/config", 100, null)

    assertTrue(profileSource.matchesProfile("dev"))
    assertFalse(profileSource.matchesProfile("prod"))
    assertFalse(profileSource.matchesProfile(null))

    assertTrue(defaultSource.matchesProfile("dev"))
    assertTrue(defaultSource.matchesProfile("prod"))
    assertTrue(defaultSource.matchesProfile(null))
  }

  @Test
  fun `should resolve resources with mock resolver`() {
    val source = ResourceSource.classpath("config", 100)
    val resolver = mockk<PathMatchingResourcePatternResolver>()
    val mockResource = mockk<Resource>()

    every { mockResource.exists() } returns true
    every { resolver.getResources("classpath:config/test.yml") } returns arrayOf(mockResource)

    val resources = source.resolveResources("test.yml", resolver)

    assertEquals(1, resources.size)
    assertEquals(mockResource, resources[0])
  }

  @Test
  fun `should handle resolver exceptions gracefully`() {
    val source = ResourceSource.classpath("config", 100)
    val resolver = mockk<PathMatchingResourcePatternResolver>()

    every { resolver.getResources(any()) } throws RuntimeException("Test exception")

    val resources = source.resolveResources("test.yml", resolver)

    assertTrue(resources.isEmpty())
  }

  @Test
  fun `should filter non-existing resources`() {
    val source = ResourceSource.classpath("config", 100)
    val resolver = mockk<PathMatchingResourcePatternResolver>()
    val existingResource = mockk<Resource>()
    val nonExistingResource = mockk<Resource>()

    every { existingResource.exists() } returns true
    every { nonExistingResource.exists() } returns false
    every { resolver.getResources(any()) } returns arrayOf(existingResource, nonExistingResource)

    val resources = source.resolveResources("*.yml", resolver)

    assertEquals(1, resources.size)
    assertEquals(existingResource, resources[0])
  }

  @Test
  fun `should create default sources with correct priorities`() {
    val sources = ResourceSource.createDefaultSources(configLocation = "config", dataLocation = "data", applicationHome = "/app", profile = "dev")

    assertTrue(sources.isNotEmpty())

    // Should be sorted by priority (highest first)
    val priorities = sources.map { it.priority }
    assertEquals(priorities.sortedDescending(), priorities)

    // Should include profile-specific sources
    val profileSources = sources.filter { it.profile == "dev" }
    assertTrue(profileSources.isNotEmpty())

    // Should include default sources
    val defaultSources = sources.filter { it.profile == null }
    assertTrue(defaultSources.isNotEmpty())

    // Highest priority should be external filesystem with profile
    val highestPriority = sources.first()
    assertEquals(ResourceType.FILESYSTEM, highestPriority.type)
    assertEquals("dev", highestPriority.profile)
    assertTrue(highestPriority.path.contains("/app"))
  }

  @Test
  fun `should create default sources without profile`() {
    val sources = ResourceSource.createDefaultSources(configLocation = "config", dataLocation = "data", applicationHome = "/app", profile = null)

    assertTrue(sources.isNotEmpty())

    // Should not include any profile-specific sources
    val profileSources = sources.filter { it.profile != null }
    assertTrue(profileSources.isEmpty())

    // All sources should be default (no profile)
    val defaultSources = sources.filter { it.profile == null }
    assertEquals(sources.size, defaultSources.size)
  }

  @Test
  fun `should normalize paths correctly`() {
    val source = ResourceSource.filesystem("config\\data", 100)
    val resolver = mockk<PathMatchingResourcePatternResolver>()
    val mockResource = ByteArrayResource("test".toByteArray())

    every { resolver.getResources("file:config/data/test.yml") } returns arrayOf(mockResource)

    val resources = source.resolveResources("test.yml", resolver)

    assertEquals(1, resources.size)
  }

  @Test
  fun `should handle empty pattern`() {
    val source = ResourceSource.classpath("config", 100)
    val resolver = mockk<PathMatchingResourcePatternResolver>()

    val resources = source.resolveResources("", resolver)

    assertTrue(resources.isEmpty())
  }

  @Test
  fun `should build correct patterns for different types`() {
    val filesystemSource = ResourceSource.filesystem("/opt/config", 100, "prod")
    val classpathSource = ResourceSource.classpath("config/data", 100, "dev")
    val urlSource = ResourceSource.url("https://config.example.com", 100)

    val resolver = mockk<PathMatchingResourcePatternResolver>()

    // Mock the resolver to capture the patterns being used
    val capturedPatterns = mutableListOf<String>()
    every { resolver.getResources(capture(capturedPatterns)) } returns emptyArray()

    filesystemSource.resolveResources("test.yml", resolver)
    classpathSource.resolveResources("test.yml", resolver)
    urlSource.resolveResources("test.yml", resolver)

    assertEquals(3, capturedPatterns.size)

    // Check filesystem pattern
    assertTrue(capturedPatterns[0].startsWith("file:"))
    assertTrue(capturedPatterns[0].contains("opt/config"))
    assertTrue(capturedPatterns[0].contains("prod"))
    assertTrue(capturedPatterns[0].endsWith("test.yml"))

    // Check classpath pattern
    assertTrue(capturedPatterns[1].startsWith("classpath:"))
    assertTrue(capturedPatterns[1].contains("config/data"))
    assertTrue(capturedPatterns[1].contains("dev"))
    assertTrue(capturedPatterns[1].endsWith("test.yml"))

    // Check URL pattern
    assertTrue(capturedPatterns[2].startsWith("https://"))
    assertTrue(capturedPatterns[2].contains("config.example.com"))
    assertTrue(capturedPatterns[2].endsWith("test.yml"))
  }
}
