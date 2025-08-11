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
 * 测试 MinIO 自动配置机制
 *
 * @author TrueNine
 * @since 2025-08-11
 */
class MinioAutoConfigurationTest {

  private val contextRunner = ApplicationContextRunner().withConfiguration(AutoConfigurations.of(MinioAutoConfiguration::class.java))

  @Nested
  inner class `自动配置启用条件` {

    @Test
    fun `当 MinioClient 类存在时应该启用自动配置`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.minio.endpoint=localhost",
          "compose.oss.minio.port=9000",
          "compose.oss.minio.access-key=minioadmin",
          "compose.oss.minio.secret-key=minioadmin",
        )
        .run { context ->
          // 验证配置属性被正确绑定
          val minioProperties = context.getBean(MinioProperties::class.java)
          kotlin.test.assertEquals("localhost", minioProperties.endpoint)
          kotlin.test.assertEquals(9000, minioProperties.port)
          kotlin.test.assertEquals("minioadmin", minioProperties.accessKey)
          kotlin.test.assertEquals("minioadmin", minioProperties.secretKey)

          // 验证 MinIO 客户端被创建
          assertTrue(context.containsBeanDefinition("minioClient"))
          assertTrue(context.containsBeanDefinition("minioObjectStorageService"))
        }
    }

    @Test
    fun `当缺少必需配置时应该不创建客户端`() {
      contextRunner.run { context ->
        // 验证上下文启动失败（因为缺少必需配置）
        assertTrue(context.startupFailure != null)

        // 验证失败原因是缺少必需配置
        val failure = context.startupFailure
        assertTrue(failure?.message?.contains("MinIO access key is required") == true || failure?.message?.contains("MinIO endpoint is required") == true)
      }
    }
  }

  @Nested
  inner class `配置属性绑定` {

    @Test
    fun `应该正确绑定 MinIO 特定配置`() {
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
          kotlin.test.assertTrue(minioProperties.enableSsl)
          kotlin.test.assertEquals("us-east-1", minioProperties.region)
        }
    }

    @Test
    fun `应该支持通用 OSS 配置作为后备`() {
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

          // 验证通用配置
          kotlin.test.assertEquals("oss.example.com", ossProperties.endpoint)
          kotlin.test.assertEquals("osskey", ossProperties.accessKey)
          kotlin.test.assertEquals("osssecret", ossProperties.secretKey)

          // 验证 MinIO 特定配置
          kotlin.test.assertEquals("minio.example.com", minioProperties.endpoint)
          kotlin.test.assertEquals("miniokey", minioProperties.accessKey)
          kotlin.test.assertEquals("miniosecret", minioProperties.secretKey)
        }
    }
  }

  @Nested
  inner class `向后兼容性` {

    @Test
    fun `应该忽略废弃的 provider 配置`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.provider=minio", // 废弃的配置
          "compose.oss.minio.endpoint=localhost",
          "compose.oss.minio.port=9000",
          "compose.oss.minio.access-key=minioadmin",
          "compose.oss.minio.secret-key=minioadmin",
        )
        .run { context ->
          // 验证 provider 属性仍然可以读取（向后兼容）
          val ossProperties = context.getBean(OssProperties::class.java)
          kotlin.test.assertEquals("minio", ossProperties.provider)

          // 验证 MinIO 配置正确绑定
          val minioProperties = context.getBean(MinioProperties::class.java)
          kotlin.test.assertEquals("localhost", minioProperties.endpoint)
          kotlin.test.assertEquals(9000, minioProperties.port)

          // 验证 MinIO 客户端被创建
          assertTrue(context.containsBeanDefinition("minioClient"))
          assertTrue(context.containsBeanDefinition("minioObjectStorageService"))
        }
    }

    @Test
    fun `应该在没有 provider 配置时正常工作`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.minio.endpoint=localhost",
          "compose.oss.minio.port=9000",
          "compose.oss.minio.access-key=minioadmin",
          "compose.oss.minio.secret-key=minioadmin",
        )
        .run { context ->
          // 验证 provider 属性为 null
          val ossProperties = context.getBean(OssProperties::class.java)
          kotlin.test.assertNull(ossProperties.provider)

          // 验证 MinIO 配置正确绑定
          val minioProperties = context.getBean(MinioProperties::class.java)
          kotlin.test.assertEquals("localhost", minioProperties.endpoint)
          kotlin.test.assertEquals(9000, minioProperties.port)

          // 验证 MinIO 客户端被创建
          assertTrue(context.containsBeanDefinition("minioClient"))
          assertTrue(context.containsBeanDefinition("minioObjectStorageService"))
        }
    }
  }

  @Nested
  inner class `Bean 定义验证` {

    @Test
    fun `应该定义 MinioClient Bean 工厂方法`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.minio.endpoint=localhost",
          "compose.oss.minio.port=9000",
          "compose.oss.minio.access-key=minioadmin",
          "compose.oss.minio.secret-key=minioadmin",
        )
        .run { context ->
          // 验证 Bean 定义存在
          assertTrue(context.containsBeanDefinition("minioClient"))
          assertTrue(context.containsBeanDefinition("minioObjectStorageService"))

          // 验证 Bean 名称存在
          val beanNames = context.beanDefinitionNames
          assertTrue(beanNames.contains("minioClient"))
          assertTrue(beanNames.contains("minioObjectStorageService"))
        }
    }

    @Test
    fun `应该正确配置 Bean 的条件注解`() {
      // 验证自动配置类的注解
      val autoConfigClass = MinioAutoConfiguration::class.java

      // 验证 @ConditionalOnClass 注解
      val conditionalOnClass = autoConfigClass.getAnnotation(org.springframework.boot.autoconfigure.condition.ConditionalOnClass::class.java)
      assertNotNull(conditionalOnClass)
      kotlin.test.assertTrue(conditionalOnClass.value.contains(MinioClient::class))

      // 验证 @Order 注解
      val order = autoConfigClass.getAnnotation(org.springframework.core.annotation.Order::class.java)
      assertNotNull(order)
      kotlin.test.assertEquals(100, order.value)
    }
  }
}
