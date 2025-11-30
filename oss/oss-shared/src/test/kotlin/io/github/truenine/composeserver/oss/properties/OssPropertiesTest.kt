package io.github.truenine.composeserver.oss.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.StandardEnvironment
import java.time.Duration
import kotlin.test.*

/**
 * Test for OSS configuration properties functionality and backward compatibility
 *
 * @author TrueNine
 * @since 2025-08-11
 */
class OssPropertiesTest {

  @Nested
  inner class `Basic Configuration Binding` {

    @Test
    fun `should bind all configuration properties correctly`() {
      // Prepare configuration data
      val properties =
        mapOf(
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.provider" to "minio",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.endpoint" to "http://localhost:9000",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.region" to "us-east-1",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.access-key" to "minioadmin",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.secret-key" to "minioadmin",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.exposed-base-url" to "http://localhost:9000",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.enable-ssl" to "false",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.connection-timeout" to "PT30S",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.read-timeout" to "PT5M",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.write-timeout" to "PT5M",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.max-connections" to "100",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.default-bucket" to "test-bucket",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.auto-create-bucket" to "true",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.versioning" to "true",
          "${SpringBootConfigurationPropertiesPrefixes.OSS}.logging" to "true",
        )

      // Create environment and binder
      val environment = StandardEnvironment()
      environment.propertySources.addFirst(MapPropertySource("test", properties))
      val binder = Binder.get(environment)

      // Bind configuration
      val ossProperties = binder.bind(SpringBootConfigurationPropertiesPrefixes.OSS, OssProperties::class.java).get()

      // Verify binding results
      assertEquals("minio", ossProperties.provider)
      assertEquals("http://localhost:9000", ossProperties.endpoint)
      assertEquals("us-east-1", ossProperties.region)
      assertEquals("minioadmin", ossProperties.accessKey)
      assertEquals("minioadmin", ossProperties.secretKey)
      assertEquals("http://localhost:9000", ossProperties.exposedBaseUrl)
      assertFalse(ossProperties.enableSsl)
      assertEquals(Duration.ofSeconds(30), ossProperties.connectionTimeout)
      assertEquals(Duration.ofMinutes(5), ossProperties.readTimeout)
      assertEquals(Duration.ofMinutes(5), ossProperties.writeTimeout)
      assertEquals(100, ossProperties.maxConnections)
      assertEquals("test-bucket", ossProperties.defaultBucket)
      assertTrue(ossProperties.autoCreateBucket)
      assertTrue(ossProperties.versioning)
      assertTrue(ossProperties.logging)
    }

    @Test
    fun `should use default values when configuration is not provided`() {
      // Create empty configuration
      val properties = emptyMap<String, Any>()
      val environment = StandardEnvironment()
      environment.propertySources.addFirst(MapPropertySource("test", properties))
      val binder = Binder.get(environment)

      // Bind configuration
      val ossProperties = binder.bind(SpringBootConfigurationPropertiesPrefixes.OSS, OssProperties::class.java).orElse(OssProperties())

      // Verify default values
      assertNull(ossProperties?.provider)
      assertNull(ossProperties?.endpoint)
      assertNull(ossProperties?.region)
      assertNull(ossProperties?.accessKey)
      assertNull(ossProperties?.secretKey)
      assertNull(ossProperties?.exposedBaseUrl)
      assertEquals(ossProperties?.enableSsl, false)
      assertEquals(Duration.ofSeconds(5), ossProperties?.connectionTimeout)
      assertEquals(Duration.ofSeconds(3), ossProperties?.readTimeout)
      assertEquals(Duration.ofSeconds(3), ossProperties?.writeTimeout)
      assertEquals(127, ossProperties?.maxConnections)
      assertEquals("attachments", ossProperties?.defaultBucket)
      assertEquals(ossProperties?.autoCreateBucket, false)
      assertEquals(ossProperties?.versioning, false)
      assertEquals(ossProperties?.logging, false)
    }
  }

  @Nested
  inner class `Backward Compatibility` {

    @Test
    fun `provider property should still be settable and readable`() {
      val ossProperties = OssProperties()

      // Set the provider property (although deprecated)
      ossProperties.provider = "minio"

      // Verify it can be read
      assertEquals("minio", ossProperties.provider)
    }

    @Test
    fun `provider property should work correctly in configuration binding`() {
      val properties = mapOf("${SpringBootConfigurationPropertiesPrefixes.OSS}.provider" to "volcengine-tos")

      val environment = StandardEnvironment()
      environment.propertySources.addFirst(MapPropertySource("test", properties))
      val binder = Binder.get(environment)

      val ossProperties = binder.bind(SpringBootConfigurationPropertiesPrefixes.OSS, OssProperties::class.java).get()

      assertEquals("volcengine-tos", ossProperties.provider)
    }
  }

  @Nested
  inner class `Configuration Validation` {

    @Test
    fun `validate method should throw an exception when required properties are missing`() {
      val ossProperties = OssProperties()

      // Test missing endpoint
      try {
        ossProperties.validate()
        throw AssertionError("Should have thrown an exception")
      } catch (e: IllegalArgumentException) {
        assertEquals("Endpoint cannot be null or blank", e.message)
      }

      // Set endpoint, test missing accessKey
      ossProperties.endpoint = "http://localhost:9000"
      try {
        ossProperties.validate()
        throw AssertionError("Should have thrown an exception")
      } catch (e: IllegalArgumentException) {
        assertEquals("Access key cannot be null or blank", e.message)
      }

      // Set accessKey, test missing secretKey
      ossProperties.accessKey = "test"
      try {
        ossProperties.validate()
        throw AssertionError("Should have thrown an exception")
      } catch (e: IllegalArgumentException) {
        assertEquals("Secret key cannot be null or blank", e.message)
      }
    }

    @Test
    fun `validate method should pass when all required properties are present`() {
      val ossProperties = OssProperties(endpoint = "http://localhost:9000", accessKey = "test", secretKey = "test")

      // Should not throw an exception
      ossProperties.validate()
    }
  }

  @Nested
  inner class `URL Handling` {

    @Test
    fun `getEffectiveEndpoint should correctly handle different endpoint formats`() {
      // Test endpoint with protocol
      val ossProperties1 = OssProperties(endpoint = "https://s3.amazonaws.com")
      assertEquals("https://s3.amazonaws.com", ossProperties1.getEffectiveEndpoint())

      // Test endpoint without protocol, with SSL enabled
      val ossProperties2 = OssProperties(endpoint = "s3.amazonaws.com", enableSsl = true)
      assertEquals("https://s3.amazonaws.com", ossProperties2.getEffectiveEndpoint())

      // Test endpoint without protocol, with SSL disabled
      val ossProperties3 = OssProperties(endpoint = "localhost:9000", enableSsl = false)
      assertEquals("http://localhost:9000", ossProperties3.getEffectiveEndpoint())
    }

    @Test
    fun `getEffectiveExposedBaseUrl should correctly handle exposedBaseUrl`() {
      // Test with exposedBaseUrl present
      val ossProperties1 = OssProperties(endpoint = "http://localhost:9000", exposedBaseUrl = "http://public.example.com")
      assertEquals("http://public.example.com", ossProperties1.getEffectiveExposedBaseUrl())

      // Test without exposedBaseUrl, should use endpoint
      val ossProperties2 = OssProperties(endpoint = "http://localhost:9000")
      assertEquals("http://localhost:9000", ossProperties2.getEffectiveExposedBaseUrl())
    }
  }

  @Nested
  inner class ToString {

    @Test
    fun `toString should hide sensitive information`() {
      val ossProperties = OssProperties(provider = "minio", endpoint = "http://localhost:9000", accessKey = "minioadmin", secretKey = "minioadmin")

      val toStringResult = ossProperties.toString()

      // Verify that non-sensitive information is included
      assertTrue(toStringResult.contains("provider='minio'"))
      assertTrue(toStringResult.contains("endpoint='http://localhost:9000'"))

      // Verify that sensitive information is hidden
      assertTrue(toStringResult.contains("accessKey='mini***'"))
      assertFalse(toStringResult.contains("minioadmin"))
    }
  }
}
