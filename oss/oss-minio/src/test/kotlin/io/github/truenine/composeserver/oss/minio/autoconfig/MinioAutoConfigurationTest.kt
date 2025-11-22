package io.github.truenine.composeserver.oss.minio.autoconfig

import io.github.truenine.composeserver.oss.minio.properties.MinioProperties
import io.github.truenine.composeserver.oss.properties.OssProperties
import io.minio.MinioClient
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

/**
 * Test for MinIO autoconfiguration mechanism
 *
 * @author TrueNine
 * @since 2025-08-11
 */
class MinioAutoConfigurationTest {

  private val contextRunner = ApplicationContextRunner().withConfiguration(AutoConfigurations.of(MinioAutoConfiguration::class.java))

  @Nested
  inner class `Autoconfiguration Enabling Conditions` {

    @Test
    fun `should enable autoconfiguration when MinioClient class is present`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.minio.endpoint=localhost",
          "compose.oss.minio.port=9000",
          "compose.oss.minio.access-key=minioadmin",
          "compose.oss.minio.secret-key=minioadmin",
        )
        .run { context ->
          // Verify that configuration properties are correctly bound
          val minioProperties = context.getBean(MinioProperties::class.java)
          kotlin.test.assertEquals("localhost", minioProperties.endpoint)
          kotlin.test.assertEquals(9000, minioProperties.port)
          kotlin.test.assertEquals("minioadmin", minioProperties.accessKey)
          kotlin.test.assertEquals("minioadmin", minioProperties.secretKey)

          // Verify that MinIO client is created
          assertTrue(context.containsBeanDefinition("minioClient"))
          assertTrue(context.containsBeanDefinition("minioObjectStorageService"))
        }
    }

    @Test
    fun `should not create client when required configuration is missing`() {
      contextRunner.run { context ->
        // Verify that context startup fails (due to missing required configuration)
        assertTrue(context.startupFailure != null)

        // Verify that the failure is due to missing required configuration
        val failure = context.startupFailure
        assertTrue(failure?.message?.contains("MinIO access key is required") == true || failure?.message?.contains("MinIO endpoint is required") == true)
      }
    }
  }

  @Nested
  inner class `Configuration Property Binding` {

    @Test
    fun `should bind MinIO specific configuration correctly`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.minio.endpoint=minio.example.com",
          "compose.oss.minio.port=9000",
          "compose.oss.minio.access-key=testkey",
          "compose.oss.minio.secret-key=testsecret",
          "compose.oss.minio.enable-ssl=true",
          "compose.oss.minio.region=us-east-1",
        )
        .run { context ->
          val minioProperties = context.getBean(MinioProperties::class.java)

          kotlin.test.assertEquals("minio.example.com", minioProperties.endpoint)
          kotlin.test.assertEquals(9000, minioProperties.port)
          kotlin.test.assertEquals("testkey", minioProperties.accessKey)
          kotlin.test.assertEquals("testsecret", minioProperties.secretKey)
          kotlin.test.assertEquals(minioProperties.enableSsl, true)
          kotlin.test.assertEquals("us-east-1", minioProperties.region)
        }
    }

    @Test
    fun `should support common OSS configuration as a fallback`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.endpoint=oss.example.com",
          "compose.oss.access-key=osskey",
          "compose.oss.secret-key=osssecret",
          "compose.oss.minio.endpoint=minio.example.com",
          "compose.oss.minio.access-key=miniokey",
          "compose.oss.minio.secret-key=miniosecret",
        )
        .run { context ->
          val ossProperties = context.getBean(OssProperties::class.java)
          val minioProperties = context.getBean(MinioProperties::class.java)

          // Verify common configuration
          kotlin.test.assertEquals("oss.example.com", ossProperties.endpoint)
          kotlin.test.assertEquals("osskey", ossProperties.accessKey)
          kotlin.test.assertEquals("osssecret", ossProperties.secretKey)

          // Verify MinIO specific configuration
          kotlin.test.assertEquals("minio.example.com", minioProperties.endpoint)
          kotlin.test.assertEquals("miniokey", minioProperties.accessKey)
          kotlin.test.assertEquals("miniosecret", minioProperties.secretKey)
        }
    }
  }

  @Nested
  inner class `Backward Compatibility` {

    @Test
    fun `should ignore deprecated provider configuration`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.provider=minio", // deprecated configuration
          "compose.oss.minio.endpoint=localhost",
          "compose.oss.minio.port=9000",
          "compose.oss.minio.access-key=minioadmin",
          "compose.oss.minio.secret-key=minioadmin",
        )
        .run { context ->
          // Verify that provider property can still be read (for backward compatibility)
          val ossProperties = context.getBean(OssProperties::class.java)
          kotlin.test.assertEquals("minio", ossProperties.provider)

          // Verify that MinIO configuration is correctly bound
          val minioProperties = context.getBean(MinioProperties::class.java)
          kotlin.test.assertEquals("localhost", minioProperties.endpoint)
          kotlin.test.assertEquals(9000, minioProperties.port)

          // Verify that MinIO client is created
          assertTrue(context.containsBeanDefinition("minioClient"))
          assertTrue(context.containsBeanDefinition("minioObjectStorageService"))
        }
    }

    @Test
    fun `should work correctly without provider configuration`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.minio.endpoint=localhost",
          "compose.oss.minio.port=9000",
          "compose.oss.minio.access-key=minioadmin",
          "compose.oss.minio.secret-key=minioadmin",
        )
        .run { context ->
          // Verify that provider property is null
          val ossProperties = context.getBean(OssProperties::class.java)
          kotlin.test.assertNull(ossProperties.provider)

          // Verify that MinIO configuration is correctly bound
          val minioProperties = context.getBean(MinioProperties::class.java)
          kotlin.test.assertEquals("localhost", minioProperties.endpoint)
          kotlin.test.assertEquals(9000, minioProperties.port)

          // Verify that MinIO client is created
          assertTrue(context.containsBeanDefinition("minioClient"))
          assertTrue(context.containsBeanDefinition("minioObjectStorageService"))
        }
    }
  }

  @Nested
  inner class `Bean Definition Verification` {

    @Test
    fun `should define MinioClient bean factory method`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.minio.endpoint=localhost",
          "compose.oss.minio.port=9000",
          "compose.oss.minio.access-key=minioadmin",
          "compose.oss.minio.secret-key=minioadmin",
        )
        .run { context ->
          // Verify that bean definitions exist
          assertTrue(context.containsBeanDefinition("minioClient"))
          assertTrue(context.containsBeanDefinition("minioObjectStorageService"))

          // Verify that bean names exist
          val beanNames = context.beanDefinitionNames
          assertTrue(beanNames.contains("minioClient"))
          assertTrue(beanNames.contains("minioObjectStorageService"))
        }
    }

    @Test
    fun `should configure bean's conditional annotations correctly`() {
      // Verify annotations of the autoconfiguration class
      val autoConfigClass = MinioAutoConfiguration::class.java

      // Verify @ConditionalOnClass annotation
      val conditionalOnClass = autoConfigClass.getAnnotation(org.springframework.boot.autoconfigure.condition.ConditionalOnClass::class.java)
      assertNotNull(conditionalOnClass)
      kotlin.test.assertTrue(conditionalOnClass.value.contains(MinioClient::class))

      // Verify @Order annotation
      val order = autoConfigClass.getAnnotation(org.springframework.core.annotation.Order::class.java)
      assertNotNull(order)
      kotlin.test.assertEquals(100, order.value)
    }
  }
}
