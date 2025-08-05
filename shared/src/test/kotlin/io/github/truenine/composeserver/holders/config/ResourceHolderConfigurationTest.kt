package io.github.truenine.composeserver.holders.config

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ResourceHolderConfigurationTest {

  @Test
  fun `should create default configuration`() {
    val config = ResourceHolderConfiguration()

    assertEquals("config", config.configLocation)
    assertEquals("data", config.location)
    assertTrue(config.matchFiles.isEmpty())
    assertTrue(config.activeProfiles.isEmpty())
    assertTrue(config.customSources.isEmpty())
    assertTrue(config.cache.enabled)
    assertFalse(config.strictMode)
    assertFalse(config.enableChangeDetection)
    assertEquals(30000, config.resolutionTimeoutMs)
    assertFalse(config.enableDebugLogging)
  }

  @Test
  fun `should create configuration with custom values`() {
    val customSources = listOf(CustomResourceSource("filesystem", "/opt/config", 1000, "prod", "External config"))

    val config =
      ResourceHolderConfiguration(
        configLocation = "custom-config",
        location = "custom-data",
        matchFiles = mutableListOf("*.yml", "*.properties"),
        activeProfiles = listOf("dev", "test"),
        customSources = customSources,
        strictMode = true,
        enableChangeDetection = true,
        enableDebugLogging = true,
      )

    assertEquals("custom-config", config.configLocation)
    assertEquals("custom-data", config.location)
    assertEquals(2, config.matchFiles.size)
    assertTrue(config.matchFiles.contains("*.yml"))
    assertTrue(config.matchFiles.contains("*.properties"))
    assertEquals(listOf("dev", "test"), config.activeProfiles)
    assertEquals(customSources, config.customSources)
    assertTrue(config.strictMode)
    assertTrue(config.enableChangeDetection)
    assertTrue(config.enableDebugLogging)
  }

  @Test
  fun `should get primary profile`() {
    val configWithoutProfiles = ResourceHolderConfiguration()
    assertNull(configWithoutProfiles.primaryProfile)

    val configWithProfiles = ResourceHolderConfiguration(activeProfiles = listOf("dev", "prod"))
    assertEquals("dev", configWithProfiles.primaryProfile)
  }

  @Test
  fun `should check if profile is active`() {
    val config = ResourceHolderConfiguration(activeProfiles = listOf("dev", "test"))

    assertTrue(config.isProfileActive("dev"))
    assertTrue(config.isProfileActive("test"))
    assertFalse(config.isProfileActive("prod"))
    assertFalse(config.isProfileActive(""))
  }

  @Test
  fun `should get all resource locations`() {
    val customSources = listOf(CustomResourceSource("filesystem", "/opt/config", 1000), CustomResourceSource("classpath", "config/external", 500))

    val config = ResourceHolderConfiguration(location = "data", customSources = customSources)

    val locations = config.getAllResourceLocations()
    assertEquals(3, locations.size)
    assertTrue(locations.contains("data"))
    assertTrue(locations.contains("/opt/config"))
    assertTrue(locations.contains("config/external"))
  }

  @Test
  fun `should handle duplicate resource locations`() {
    val customSources =
      listOf(
        CustomResourceSource("filesystem", "data", 1000), // Same as main location
        CustomResourceSource("classpath", "/opt/config", 500),
        CustomResourceSource("url", "/opt/config", 300), // Duplicate path
      )

    val config = ResourceHolderConfiguration(location = "data", customSources = customSources)

    val locations = config.getAllResourceLocations()
    assertEquals(2, locations.size) // Should be deduplicated
    assertTrue(locations.contains("data"))
    assertTrue(locations.contains("/opt/config"))
  }

  @Nested
  inner class CustomResourceSourceTest {

    @Test
    fun `should create filesystem resource source`() {
      val source = CustomResourceSource(type = "filesystem", path = "/opt/config", priority = 1000, profile = "prod", description = "Production config")

      assertEquals("filesystem", source.type)
      assertEquals("/opt/config", source.path)
      assertEquals(1000, source.priority)
      assertEquals("prod", source.profile)
      assertEquals("Production config", source.description)
    }

    @Test
    fun `should convert to ResourceSource with filesystem type`() {
      val customSource = CustomResourceSource("filesystem", "/opt/config", 1000, "prod")
      val resourceSource = customSource.toResourceSource()

      assertEquals(ResourceType.FILESYSTEM, resourceSource.type)
      assertEquals("/opt/config", resourceSource.path)
      assertEquals(1000, resourceSource.priority)
      assertEquals("prod", resourceSource.profile)
    }

    @Test
    fun `should convert to ResourceSource with classpath type`() {
      val customSource = CustomResourceSource("classpath", "config/data", 500)
      val resourceSource = customSource.toResourceSource()

      assertEquals(ResourceType.CLASSPATH, resourceSource.type)
      assertEquals("config/data", resourceSource.path)
      assertEquals(500, resourceSource.priority)
      assertNull(resourceSource.profile)
    }

    @Test
    fun `should handle different type aliases`() {
      val fileSource = CustomResourceSource("file", "/opt/config", 1000).toResourceSource()
      assertEquals(ResourceType.FILESYSTEM, fileSource.type)

      val cpSource = CustomResourceSource("cp", "config", 500).toResourceSource()
      assertEquals(ResourceType.CLASSPATH, cpSource.type)

      val httpSource = CustomResourceSource("http", "http://config.example.com", 200).toResourceSource()
      assertEquals(ResourceType.URL, httpSource.type)

      val httpsSource = CustomResourceSource("https", "https://config.example.com", 200).toResourceSource()
      assertEquals(ResourceType.URL, httpsSource.type)
    }

    @Test
    fun `should throw exception for unsupported type`() {
      val source = CustomResourceSource("unsupported", "/opt/config", 1000)

      assertThrows<IllegalArgumentException> { source.toResourceSource() }
    }

    @Test
    fun `should generate description when empty`() {
      val source = CustomResourceSource("filesystem", "/opt/config", 1000, description = "")
      val resourceSource = source.toResourceSource()

      assertEquals("filesystem: /opt/config", resourceSource.description)
    }
  }

  @Nested
  inner class CacheConfigurationTest {

    @Test
    fun `should create default cache configuration`() {
      val cacheConfig = CacheConfiguration()

      assertTrue(cacheConfig.enabled)
      assertEquals(1000, cacheConfig.maxSize)
      assertEquals(300_000, cacheConfig.ttlMs)
      assertTrue(cacheConfig.enableStats)
      assertEquals(60_000, cacheConfig.cleanupIntervalMs)
    }

    @Test
    fun `should create custom cache configuration`() {
      val cacheConfig = CacheConfiguration(enabled = false, maxSize = 500, ttlMs = 600_000, enableStats = false, cleanupIntervalMs = 120_000)

      assertFalse(cacheConfig.enabled)
      assertEquals(500, cacheConfig.maxSize)
      assertEquals(600_000, cacheConfig.ttlMs)
      assertFalse(cacheConfig.enableStats)
      assertEquals(120_000, cacheConfig.cleanupIntervalMs)
    }
  }

  @Nested
  inner class FallbackConfigurationTest {

    @Test
    fun `should create default fallback configuration`() {
      val fallbackConfig = FallbackConfiguration()

      assertTrue(fallbackConfig.enableProfileFallback)
      assertTrue(fallbackConfig.enableClasspathFallback)
      assertTrue(fallbackConfig.defaultPatterns.isEmpty())
      assertFalse(fallbackConfig.createEmptyResources)
    }

    @Test
    fun `should create custom fallback configuration`() {
      val fallbackConfig =
        FallbackConfiguration(
          enableProfileFallback = false,
          enableClasspathFallback = false,
          defaultPatterns = listOf("*.yml", "*.properties"),
          createEmptyResources = true,
        )

      assertFalse(fallbackConfig.enableProfileFallback)
      assertFalse(fallbackConfig.enableClasspathFallback)
      assertEquals(listOf("*.yml", "*.properties"), fallbackConfig.defaultPatterns)
      assertTrue(fallbackConfig.createEmptyResources)
    }
  }

  @Nested
  inner class ValidationConfigurationTest {

    @Test
    fun `should create default validation configuration`() {
      val validationConfig = ValidationConfiguration()

      assertTrue(validationConfig.enablePatternValidation)
      assertTrue(validationConfig.enableSourceValidation)
      assertEquals(listOf(".*"), validationConfig.allowedPatterns)
      assertEquals(listOf(".*\\.\\./.*"), validationConfig.forbiddenPatterns)
    }

    @Test
    fun `should create custom validation configuration`() {
      val validationConfig =
        ValidationConfiguration(
          enablePatternValidation = false,
          enableSourceValidation = false,
          allowedPatterns = listOf(".*\\.yml", ".*\\.properties"),
          forbiddenPatterns = listOf(".*\\.\\./.*", ".*etc/passwd.*"),
        )

      assertFalse(validationConfig.enablePatternValidation)
      assertFalse(validationConfig.enableSourceValidation)
      assertEquals(listOf(".*\\.yml", ".*\\.properties"), validationConfig.allowedPatterns)
      assertEquals(listOf(".*\\.\\./.*", ".*etc/passwd.*"), validationConfig.forbiddenPatterns)
    }
  }

  @Nested
  inner class EdgeCaseTest {

    @Test
    fun `should handle empty active profiles`() {
      val config = ResourceHolderConfiguration(activeProfiles = emptyList())

      assertNull(config.primaryProfile)
      assertFalse(config.isProfileActive("any"))
    }

    @Test
    fun `should handle single active profile`() {
      val config = ResourceHolderConfiguration(activeProfiles = listOf("prod"))

      assertEquals("prod", config.primaryProfile)
      assertTrue(config.isProfileActive("prod"))
      assertFalse(config.isProfileActive("dev"))
    }

    @Test
    fun `should handle empty custom sources`() {
      val config = ResourceHolderConfiguration(customSources = emptyList())

      val locations = config.getAllResourceLocations()
      assertEquals(1, locations.size)
      assertEquals("data", locations[0])
    }

    @Test
    fun `should handle null profile in custom source`() {
      val source = CustomResourceSource("filesystem", "/opt/config", 1000, profile = null)
      val resourceSource = source.toResourceSource()

      assertNull(resourceSource.profile)
    }
  }
}
