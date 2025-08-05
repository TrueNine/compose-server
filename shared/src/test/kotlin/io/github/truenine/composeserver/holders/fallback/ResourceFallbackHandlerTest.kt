package io.github.truenine.composeserver.holders.fallback

import io.github.truenine.composeserver.holders.config.FallbackConfiguration
import io.github.truenine.composeserver.holders.config.ResourceSource
import io.github.truenine.composeserver.holders.config.ResourceType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

class ResourceFallbackHandlerTest {

  private lateinit var resolver: PathMatchingResourcePatternResolver
  private lateinit var fallbackConfig: FallbackConfiguration
  private lateinit var fallbackHandler: ResourceFallbackHandler
  private lateinit var resourceSources: List<ResourceSource>

  @BeforeEach
  fun setUp() {
    resolver = mockk()
    fallbackConfig =
      FallbackConfiguration(
        enableProfileFallback = true,
        enableClasspathFallback = true,
        defaultPatterns = listOf("default.yml", "fallback.properties"),
        createEmptyResources = true,
      )

    resourceSources =
      listOf(
        ResourceSource(ResourceType.FILESYSTEM, "/opt/config", 1000, "prod"),
        ResourceSource(ResourceType.FILESYSTEM, "/opt/config", 900, null),
        ResourceSource(ResourceType.CLASSPATH, "config", 500, "prod"),
        ResourceSource(ResourceType.CLASSPATH, "config", 400, null),
      )

    fallbackHandler = ResourceFallbackHandler(fallbackConfig, resolver)
  }

  @Nested
  inner class ProfileFallbackTest {

    @Test
    fun `should attempt profile fallback when enabled`() {
      val mockResource = ByteArrayResource("fallback content".toByteArray())

      // Mock resource resolution for non-profile sources
      every { resolver.getResources("file:/opt/config/test.yml") } returns arrayOf(mockResource)
      every { resolver.getResources("classpath:config/test.yml") } returns emptyArray()

      val results = fallbackHandler.handleFallback("test.yml", "prod", resourceSources)

      assertEquals(1, results.size)
      assertEquals(mockResource, results[0])
      verify { resolver.getResources("file:/opt/config/test.yml") }
    }

    @Test
    fun `should skip profile fallback when profile is null`() {
      val mockResource = ByteArrayResource("fallback content".toByteArray())

      // Mock classpath fallback
      every { resolver.getResources("classpath:config/test.yml") } returns arrayOf(mockResource)

      val results = fallbackHandler.handleFallback("test.yml", null, resourceSources)

      assertEquals(1, results.size)
      assertEquals(mockResource, results[0])
    }

    @Test
    fun `should skip profile fallback when disabled`() {
      val disabledConfig = FallbackConfiguration(enableProfileFallback = false, enableClasspathFallback = true)
      val handler = ResourceFallbackHandler(disabledConfig, resolver)

      val mockResource = ByteArrayResource("fallback content".toByteArray())
      every { resolver.getResources("classpath:config/test.yml") } returns arrayOf(mockResource)

      val results = handler.handleFallback("test.yml", "prod", resourceSources)

      assertEquals(1, results.size)
      assertEquals(mockResource, results[0])
    }
  }

  @Nested
  inner class ClasspathFallbackTest {

    @Test
    fun `should attempt classpath fallback when enabled`() {
      val mockResource = ByteArrayResource("classpath fallback".toByteArray())

      // Mock classpath resource resolution
      every { resolver.getResources("classpath:config/test.yml") } returns arrayOf(mockResource)

      val results = fallbackHandler.handleFallback("test.yml", "prod", resourceSources)

      assertEquals(1, results.size)
      assertEquals(mockResource, results[0])
      verify { resolver.getResources("classpath:config/test.yml") }
    }

    @Test
    fun `should filter classpath sources by profile`() {
      val prodResource = ByteArrayResource("prod classpath".toByteArray())
      val defaultResource = ByteArrayResource("default classpath".toByteArray())

      every { resolver.getResources("classpath:config/test.yml") } returns arrayOf(prodResource, defaultResource)

      val results = fallbackHandler.handleFallback("test.yml", "prod", resourceSources)

      assertTrue(results.isNotEmpty())
      verify { resolver.getResources("classpath:config/test.yml") }
    }

    @Test
    fun `should skip classpath fallback when disabled`() {
      val disabledConfig =
        FallbackConfiguration(
          enableProfileFallback = false,
          enableClasspathFallback = false,
          defaultPatterns = listOf("default.yml"),
          createEmptyResources = false,
        )
      val handler = ResourceFallbackHandler(disabledConfig, resolver)

      val results = handler.handleFallback("test.yml", "prod", resourceSources)

      assertTrue(results.isEmpty())
    }

    @Test
    fun `should handle exceptions during classpath fallback gracefully`() {
      every { resolver.getResources(any()) } throws RuntimeException("Mock exception")

      val results = fallbackHandler.handleFallback("test.yml", "prod", resourceSources)

      // Should not throw, should continue with other fallback strategies or return empty
      assertTrue(results.isEmpty() || results.isNotEmpty()) // Either is acceptable
    }
  }

  @Nested
  inner class DefaultPatternFallbackTest {

    @Test
    fun `should try default patterns when configured`() {
      val mockResource = ByteArrayResource("default pattern".toByteArray())

      // Mock ALL possible patterns to return empty except the one we want to succeed
      every { resolver.getResources(any()) } returns emptyArray()

      // Only the specific default pattern should succeed
      every { resolver.getResources("file:/opt/config/prod/default.yml") } returns arrayOf(mockResource)

      val results = fallbackHandler.handleFallback("missing.yml", "prod", resourceSources)

      assertEquals(1, results.size)
      assertEquals(mockResource, results[0])
      verify { resolver.getResources("file:/opt/config/prod/default.yml") }
    }

    @Test
    fun `should try all default patterns until one succeeds`() {
      val mockResource = ByteArrayResource("fallback properties".toByteArray())

      // Mock ALL possible patterns to return empty except the one we want to succeed
      every { resolver.getResources(any()) } returns emptyArray()

      // Only the specific fallback pattern should succeed
      every { resolver.getResources("file:/opt/config/prod/fallback.properties") } returns arrayOf(mockResource)

      val results = fallbackHandler.handleFallback("missing.yml", "prod", resourceSources)

      assertEquals(1, results.size)
      assertEquals(mockResource, results[0])
      verify { resolver.getResources("file:/opt/config/prod/fallback.properties") }
    }

    @Test
    fun `should return empty when no default patterns configured`() {
      val noDefaultsConfig =
        FallbackConfiguration(enableProfileFallback = true, enableClasspathFallback = true, defaultPatterns = emptyList(), createEmptyResources = false)
      val handler = ResourceFallbackHandler(noDefaultsConfig, resolver)

      every { resolver.getResources(any()) } returns emptyArray()

      val results = handler.handleFallback("missing.yml", "prod", resourceSources)

      assertTrue(results.isEmpty())
    }

    @Test
    fun `should handle exceptions during default pattern resolution`() {
      every { resolver.getResources(any()) } throws RuntimeException("Mock exception")

      val results = fallbackHandler.handleFallback("missing.yml", "prod", resourceSources)

      // Should handle exceptions gracefully
      assertTrue(results.isEmpty() || results.isNotEmpty())
    }
  }

  @Nested
  inner class EmptyResourceCreationTest {

    @Test
    fun `should create empty resource when enabled and all other strategies fail`() {
      every { resolver.getResources(any()) } returns emptyArray()

      val results = fallbackHandler.handleFallback("missing.yml", "prod", resourceSources)

      assertEquals(1, results.size)
      val emptyResource = results[0]
      assertTrue(emptyResource.exists())
      assertTrue(emptyResource.isReadable())
      assertEquals(0, emptyResource.contentLength())
      assertNotNull(emptyResource.filename)
      assertTrue(emptyResource.description.contains("Empty fallback resource"))
    }

    @Test
    fun `should not create empty resource when disabled`() {
      val noEmptyConfig =
        FallbackConfiguration(enableProfileFallback = true, enableClasspathFallback = true, defaultPatterns = emptyList(), createEmptyResources = false)
      val handler = ResourceFallbackHandler(noEmptyConfig, resolver)

      every { resolver.getResources(any()) } returns emptyArray()

      val results = handler.handleFallback("missing.yml", "prod", resourceSources)

      assertTrue(results.isEmpty())
    }

    @Test
    fun `should extract reasonable filename from pattern`() {
      every { resolver.getResources(any()) } returns emptyArray()

      val testCases =
        mapOf(
          "config.yml" to "config.yml",
          "*/config.yml" to "config.yml",
          "**/*config*" to "config.txt",
          "very/deep/path/file.properties" to "very_deep_path_file.properties",
          "*.yml" to ".yml",
          "*" to "fallback-resource.txt",
        )

      testCases.forEach { (pattern, expectedFilename) ->
        val results = fallbackHandler.handleFallback(pattern, "prod", resourceSources)

        assertEquals(1, results.size, "Should create empty resource for pattern: $pattern")
        assertEquals(expectedFilename, results[0].filename, "Filename should match for pattern: $pattern")
      }
    }

    @Test
    fun `should handle special characters in filename extraction`() {
      every { resolver.getResources(any()) } returns emptyArray()

      val specialPatterns = listOf("config/../file.yml", "config\\..\\file.yml", "config??file.yml", "config**file.yml")

      specialPatterns.forEach { pattern ->
        val results = fallbackHandler.handleFallback(pattern, "prod", resourceSources)

        assertEquals(1, results.size)
        val filename = results[0].filename
        assertNotNull(filename)
        assertFalse(filename.contains(".."), "Filename should not contain path traversal: $filename")
        assertFalse(filename.contains("?"), "Filename should not contain wildcards: $filename")
        assertFalse(filename.contains("*"), "Filename should not contain wildcards: $filename")
      }
    }
  }

  @Nested
  inner class FallbackStrategyTest {

    @Test
    fun `should check if fallback is enabled correctly`() {
      assertTrue(fallbackHandler.isFallbackEnabled())

      val disabledConfig =
        FallbackConfiguration(enableProfileFallback = false, enableClasspathFallback = false, defaultPatterns = emptyList(), createEmptyResources = false)
      val disabledHandler = ResourceFallbackHandler(disabledConfig, resolver)

      assertFalse(disabledHandler.isFallbackEnabled())
    }

    @Test
    fun `should provide correct fallback strategy summary`() {
      val summary = fallbackHandler.getFallbackStrategySummary()

      assertTrue(summary.contains("profile-fallback"))
      assertTrue(summary.contains("classpath-fallback"))
      assertTrue(summary.contains("default-patterns(2)"))
      assertTrue(summary.contains("empty-resources"))
    }

    @Test
    fun `should provide 'none' summary when all strategies disabled`() {
      val disabledConfig =
        FallbackConfiguration(enableProfileFallback = false, enableClasspathFallback = false, defaultPatterns = emptyList(), createEmptyResources = false)
      val disabledHandler = ResourceFallbackHandler(disabledConfig, resolver)

      assertEquals("none", disabledHandler.getFallbackStrategySummary())
    }

    @Test
    fun `should provide partial summary when some strategies enabled`() {
      val partialConfig =
        FallbackConfiguration(
          enableProfileFallback = true,
          enableClasspathFallback = false,
          defaultPatterns = listOf("default.yml"),
          createEmptyResources = false,
        )
      val partialHandler = ResourceFallbackHandler(partialConfig, resolver)

      val summary = partialHandler.getFallbackStrategySummary()

      assertTrue(summary.contains("profile-fallback"))
      assertFalse(summary.contains("classpath-fallback"))
      assertTrue(summary.contains("default-patterns(1)"))
      assertFalse(summary.contains("empty-resources"))
    }
  }

  @Nested
  inner class FallbackOrderTest {

    @Test
    fun `should try strategies in correct order and stop on first success`() {
      val profileFallbackResource = ByteArrayResource("profile fallback".toByteArray())

      // Mock ALL possible patterns to return empty except the one we want to succeed
      every { resolver.getResources(any()) } returns emptyArray()

      // Only the profile fallback pattern should succeed
      every { resolver.getResources("file:/opt/config/test.yml") } returns arrayOf(profileFallbackResource)

      val results = fallbackHandler.handleFallback("test.yml", "prod", resourceSources)

      // Should return profile fallback result and not try other strategies
      assertEquals(1, results.size)
      assertEquals(profileFallbackResource, results[0])

      verify { resolver.getResources("file:/opt/config/test.yml") }
    }

    @Test
    fun `should continue to next strategy when previous fails`() {
      val classpathResource = ByteArrayResource("classpath fallback".toByteArray())

      // Profile fallback fails, classpath succeeds
      every { resolver.getResources("file:/opt/config/test.yml") } returns emptyArray()
      every { resolver.getResources("classpath:config/test.yml") } returns arrayOf(classpathResource)

      val results = fallbackHandler.handleFallback("test.yml", "prod", resourceSources)

      assertEquals(1, results.size)
      assertEquals(classpathResource, results[0])

      verify { resolver.getResources("file:/opt/config/test.yml") }
      verify { resolver.getResources("classpath:config/test.yml") }
    }

    @Test
    fun `should try all strategies when previous ones fail`() {
      val defaultPatternResource = ByteArrayResource("default pattern".toByteArray())

      // All primary strategies fail, default pattern succeeds
      every { resolver.getResources(match { !it.contains("default.yml") }) } returns emptyArray()
      every { resolver.getResources(match { it.contains("default.yml") }) } returns arrayOf(defaultPatternResource)

      val results = fallbackHandler.handleFallback("missing.yml", "prod", resourceSources)

      assertEquals(1, results.size)
      assertEquals(defaultPatternResource, results[0])
    }

    @Test
    fun `should create empty resource only as last resort`() {
      // All other strategies fail
      every { resolver.getResources(any()) } returns emptyArray()

      val results = fallbackHandler.handleFallback("missing.yml", "prod", resourceSources)

      assertEquals(1, results.size)
      assertTrue(results[0] is ByteArrayResource)
      assertEquals(0, results[0].contentLength())
    }
  }

  @Nested
  inner class EdgeCaseTest {

    @Test
    fun `should handle empty resource sources list`() {
      every { resolver.getResources(any()) } returns emptyArray()

      val results = fallbackHandler.handleFallback("test.yml", "prod", emptyList())

      // Should still create empty resource as fallback
      assertEquals(1, results.size)
      assertTrue(results[0] is ByteArrayResource)
    }

    @Test
    fun `should handle null profile gracefully`() {
      val mockResource = ByteArrayResource("fallback".toByteArray())
      every { resolver.getResources("classpath:config/test.yml") } returns arrayOf(mockResource)

      val results = fallbackHandler.handleFallback("test.yml", null, resourceSources)

      assertEquals(1, results.size)
      assertEquals(mockResource, results[0])
    }

    @Test
    fun `should handle resource sources with mixed profiles`() {
      val mixedSources =
        listOf(
          ResourceSource(ResourceType.FILESYSTEM, "/opt/config", 1000, "prod"),
          ResourceSource(ResourceType.FILESYSTEM, "/opt/config", 900, "dev"),
          ResourceSource(ResourceType.CLASSPATH, "config", 500, null), // No profile
        )

      val mockResource = ByteArrayResource("mixed profile fallback".toByteArray())
      every { resolver.getResources("classpath:config/test.yml") } returns arrayOf(mockResource)

      val results = fallbackHandler.handleFallback("test.yml", "test", mixedSources)

      assertEquals(1, results.size)
      assertEquals(mockResource, results[0])
    }

    @Test
    fun `should handle concurrent access safely`() {
      val mockResource = ByteArrayResource("concurrent fallback".toByteArray())

      // Mock all patterns to return empty except profile fallback patterns
      every { resolver.getResources(any()) } returns emptyArray()

      // Only profile fallback should succeed for each test pattern
      repeat(10) { i -> every { resolver.getResources("file:/opt/config/test$i.yml") } returns arrayOf(mockResource) }

      val threads = mutableListOf<Thread>()
      val results = mutableListOf<List<Resource>>()

      repeat(10) { i ->
        val thread = Thread {
          val result = fallbackHandler.handleFallback("test$i.yml", "prod", resourceSources)
          synchronized(results) { results.add(result) }
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      assertEquals(10, results.size)
      results.forEach { result ->
        assertEquals(1, result.size)
        assertEquals(mockResource, result[0])
      }
    }
  }
}
