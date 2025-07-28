package io.github.truenine.composeserver.testtoolkit.testcontainers

import io.github.truenine.composeserver.testtoolkit.properties.TestcontainersProperties
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationContext

/** # TestcontainersConfigurationHolder 测试类 */
class TestcontainersConfigurationHolderTest {

  @Nested
  inner class GetTestcontainersProperties {

    @Test
    fun should_return_default_properties_when_no_context() {
      val holder = TestcontainersConfigurationHolder()

      val properties = TestcontainersConfigurationHolder.getTestcontainersProperties()

      // 验证属性对象本身
      assertNotNull(properties, "属性对象不应为 null")
      assertNotNull(properties.postgres, "PostgreSQL 配置不应为 null")
      assertNotNull(properties.redis, "Redis 配置不应为 null")
      assertNotNull(properties.minio, "MinIO 配置不应为 null")

      // 验证默认配置值
      assertEquals("postgres:17-alpine", properties.postgres.image, "PostgreSQL 默认镜像应该正确")
      assertEquals("redis:7-alpine", properties.redis.image, "Redis 默认镜像应该正确")
      assertEquals("minio/minio:RELEASE.2025-07-23T15-54-02Z", properties.minio.image, "MinIO 默认镜像应该正确")

      // 验证配置的完整性
      assertTrue(properties.postgres.image.isNotEmpty(), "PostgreSQL 镜像名不应为空")
      assertTrue(properties.redis.image.isNotEmpty(), "Redis 镜像名不应为空")
      assertTrue(properties.minio.image.isNotEmpty(), "MinIO 镜像名不应为空")

      // 验证镜像名格式
      assertTrue(properties.postgres.image.contains(":"), "PostgreSQL 镜像应包含版本标签")
      assertTrue(properties.redis.image.contains(":"), "Redis 镜像应包含版本标签")
      assertTrue(properties.minio.image.contains(":"), "MinIO 镜像应包含版本标签")
    }

    @Test
    fun should_return_default_properties_when_bean_not_found() {
      val mockContext = mockk<ApplicationContext>()
      every { mockContext.getBean(TestcontainersProperties::class.java) } throws RuntimeException("Bean not found")

      val holder = TestcontainersConfigurationHolder()
      holder.setApplicationContext(mockContext)

      val properties = TestcontainersConfigurationHolder.getTestcontainersProperties()

      // 验证在Bean不存在时返回默认配置
      assertNotNull(properties, "属性对象不应为 null")
      assertEquals("postgres:17-alpine", properties.postgres.image, "PostgreSQL 默认镜像应该正确")

      // 验证所有默认配置都可用
      assertNotNull(properties.postgres, "PostgreSQL 配置不应为 null")
      assertNotNull(properties.redis, "Redis 配置不应为 null")
      assertNotNull(properties.minio, "MinIO 配置不应为 null")
    }

    @Test
    fun should_return_configured_properties_when_available() {
      val customProperties = TestcontainersProperties()
      val mockContext = mockk<ApplicationContext>()
      every { mockContext.getBean(TestcontainersProperties::class.java) } returns customProperties

      val holder = TestcontainersConfigurationHolder()
      holder.setApplicationContext(mockContext)

      val properties = TestcontainersConfigurationHolder.getTestcontainersProperties()

      // 验证返回的是自定义配置
      assertEquals(customProperties, properties, "应该返回自定义配置对象")
      assertNotNull(properties, "自定义属性对象不应为 null")

      // 验证自定义配置的完整性
      assertNotNull(customProperties.postgres, "自定义 PostgreSQL 配置不应为 null")
      assertNotNull(customProperties.redis, "自定义 Redis 配置不应为 null")
      assertNotNull(customProperties.minio, "自定义 MinIO 配置不应为 null")
    }
  }

  @Nested
  inner class SetApplicationContext {

    @Test
    fun should_set_application_context_successfully() {
      val mockContext = mockk<ApplicationContext>()
      val holder = TestcontainersConfigurationHolder()

      // 不应该抛出异常
      holder.setApplicationContext(mockContext)

      // 验证ApplicationContext设置生效（通过调用 getTestcontainersProperties 来间接验证）
      // 由于 applicationContext 是私有的，我们通过功能验证来确认设置成功
      val properties = TestcontainersConfigurationHolder.getTestcontainersProperties()
      assertNotNull(properties, "设置ApplicationContext后应能获取属性")
    }
  }
}
