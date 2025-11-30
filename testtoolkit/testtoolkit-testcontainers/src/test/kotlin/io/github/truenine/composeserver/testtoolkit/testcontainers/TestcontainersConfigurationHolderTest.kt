package io.github.truenine.composeserver.testtoolkit.testcontainers

import io.github.truenine.composeserver.testtoolkit.properties.TestcontainersProperties
import io.mockk.every
import io.mockk.mockk
import kotlin.test.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationContext

/** TestcontainersConfigurationHolder test class. */
class TestcontainersConfigurationHolderTest {

  @Nested
  inner class GetTestcontainersProperties {

    @Test
    fun should_return_default_properties_when_no_context() {
      val holder = TestcontainersConfigurationHolder()

      val properties = TestcontainersConfigurationHolder.getTestcontainersProperties()

      // Verify properties object and nested configs
      assertNotNull(properties, "Properties object should not be null")
      assertNotNull(properties.postgres, "PostgreSQL config should not be null")
      assertNotNull(properties.redis, "Redis config should not be null")
      assertNotNull(properties.minio, "MinIO config should not be null")

      // Verify default configuration values
      assertEquals("postgres:17.6-alpine3.22", properties.postgres.image, "PostgreSQL default image should be correct")
      assertEquals("redis/redis-stack:7.2.0-v18", properties.redis.image, "Redis default image should be correct")
      assertEquals("minio/minio:RELEASE.2025-09-07T16-13-09Z-cpuv1", properties.minio.image, "MinIO default image should be correct")

      // Verify configuration completeness
      assertTrue(properties.postgres.image.isNotEmpty(), "PostgreSQL image name should not be empty")
      assertTrue(properties.redis.image.isNotEmpty(), "Redis image name should not be empty")
      assertTrue(properties.minio.image.isNotEmpty(), "MinIO image name should not be empty")

      // Verify image name format
      assertTrue(properties.postgres.image.contains(":"), "PostgreSQL image should contain a tag")
      assertTrue(properties.redis.image.contains(":"), "Redis image should contain a tag")
      assertTrue(properties.minio.image.contains(":"), "MinIO image should contain a tag")
    }

    @Test
    fun should_return_default_properties_when_bean_not_found() {
      val mockContext = mockk<ApplicationContext>()
      every { mockContext.getBean(TestcontainersProperties::class.java) } throws RuntimeException("Bean not found")

      val holder = TestcontainersConfigurationHolder()
      holder.setApplicationContext(mockContext)

      val properties = TestcontainersConfigurationHolder.getTestcontainersProperties()

      // Verify that default configuration is returned when bean is not found
      assertNotNull(properties, "Properties object should not be null")
      assertEquals("postgres:17.6-alpine3.22", properties.postgres.image, "PostgreSQL default image should be correct")

      // Verify all default configurations are available
      assertNotNull(properties.postgres, "PostgreSQL config should not be null")
      assertNotNull(properties.redis, "Redis config should not be null")
      assertNotNull(properties.minio, "MinIO config should not be null")
    }

    @Test
    fun should_return_configured_properties_when_available() {
      val customProperties = TestcontainersProperties()
      val mockContext = mockk<ApplicationContext>()
      every { mockContext.getBean(TestcontainersProperties::class.java) } returns customProperties

      val holder = TestcontainersConfigurationHolder()
      holder.setApplicationContext(mockContext)

      val properties = TestcontainersConfigurationHolder.getTestcontainersProperties()

      // Verify that the returned configuration is the custom one
      assertEquals(customProperties, properties, "Custom configuration object should be returned")
      assertNotNull(properties, "Custom properties object should not be null")

      // Verify completeness of custom configuration
      assertNotNull(customProperties.postgres, "Custom PostgreSQL config should not be null")
      assertNotNull(customProperties.redis, "Custom Redis config should not be null")
      assertNotNull(customProperties.minio, "Custom MinIO config should not be null")
    }
  }

  @Nested
  inner class SetApplicationContext {

    @Test
    fun should_set_application_context_successfully() {
      val mockContext = mockk<ApplicationContext>()
      val holder = TestcontainersConfigurationHolder()

      // Should not throw any exception
      holder.setApplicationContext(mockContext)

      // Verify that ApplicationContext takes effect by indirectly calling getTestcontainersProperties
      // Since applicationContext is private, we confirm by behavior
      val properties = TestcontainersConfigurationHolder.getTestcontainersProperties()
      assertNotNull(properties, "Properties should be obtainable after setting ApplicationContext")
    }
  }
}
