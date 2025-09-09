package io.github.truenine.composeserver.oss.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.StandardEnvironment

/**
 * 测试 OSS 配置属性的功能和向后兼容性
 *
 * @author TrueNine
 * @since 2025-08-11
 */
class OssPropertiesTest {

  @Nested
  inner class `基本配置绑定` {

    @Test
    fun `应该正确绑定所有配置属性`() {
      // 准备配置数据
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

      // 创建环境和绑定器
      val environment = StandardEnvironment()
      environment.propertySources.addFirst(MapPropertySource("test", properties))
      val binder = Binder.get(environment)

      // 绑定配置
      val ossProperties = binder.bind(SpringBootConfigurationPropertiesPrefixes.OSS, OssProperties::class.java).get()

      // 验证绑定结果
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
    fun `应该使用默认值当配置未提供时`() {
      // 创建空配置
      val properties = emptyMap<String, Any>()
      val environment = StandardEnvironment()
      environment.propertySources.addFirst(MapPropertySource("test", properties))
      val binder = Binder.get(environment)

      // 绑定配置
      val ossProperties = binder.bind(SpringBootConfigurationPropertiesPrefixes.OSS, OssProperties::class.java).orElse(OssProperties())

      // 验证默认值
      assertNull(ossProperties.provider)
      assertNull(ossProperties.endpoint)
      assertNull(ossProperties.region)
      assertNull(ossProperties.accessKey)
      assertNull(ossProperties.secretKey)
      assertNull(ossProperties.exposedBaseUrl)
      assertFalse(ossProperties.enableSsl)
      assertEquals(Duration.ofSeconds(5), ossProperties.connectionTimeout)
      assertEquals(Duration.ofSeconds(3), ossProperties.readTimeout)
      assertEquals(Duration.ofSeconds(3), ossProperties.writeTimeout)
      assertEquals(127, ossProperties.maxConnections)
      assertEquals("attachments", ossProperties.defaultBucket)
      assertFalse(ossProperties.autoCreateBucket)
      assertFalse(ossProperties.versioning)
      assertFalse(ossProperties.logging)
    }
  }

  @Nested
  inner class `向后兼容性` {

    @Test
    fun `provider 属性应该仍然可以设置和读取`() {
      val ossProperties = OssProperties()

      // 设置 provider 属性（尽管已废弃）
      ossProperties.provider = "minio"

      // 验证可以读取
      assertEquals("minio", ossProperties.provider)
    }

    @Test
    fun `provider 属性应该在配置绑定中正常工作`() {
      val properties = mapOf("${SpringBootConfigurationPropertiesPrefixes.OSS}.provider" to "volcengine-tos")

      val environment = StandardEnvironment()
      environment.propertySources.addFirst(MapPropertySource("test", properties))
      val binder = Binder.get(environment)

      val ossProperties = binder.bind(SpringBootConfigurationPropertiesPrefixes.OSS, OssProperties::class.java).get()

      assertEquals("volcengine-tos", ossProperties.provider)
    }
  }

  @Nested
  inner class `配置验证` {

    @Test
    fun `validate 方法应该在必需属性缺失时抛出异常`() {
      val ossProperties = OssProperties()

      // 测试缺少 endpoint
      try {
        ossProperties.validate()
        throw AssertionError("应该抛出异常")
      } catch (e: IllegalArgumentException) {
        assertEquals("Endpoint cannot be null or blank", e.message)
      }

      // 设置 endpoint，测试缺少 accessKey
      ossProperties.endpoint = "http://localhost:9000"
      try {
        ossProperties.validate()
        throw AssertionError("应该抛出异常")
      } catch (e: IllegalArgumentException) {
        assertEquals("Access key cannot be null or blank", e.message)
      }

      // 设置 accessKey，测试缺少 secretKey
      ossProperties.accessKey = "test"
      try {
        ossProperties.validate()
        throw AssertionError("应该抛出异常")
      } catch (e: IllegalArgumentException) {
        assertEquals("Secret key cannot be null or blank", e.message)
      }
    }

    @Test
    fun `validate 方法应该在所有必需属性存在时通过`() {
      val ossProperties = OssProperties(endpoint = "http://localhost:9000", accessKey = "test", secretKey = "test")

      // 不应该抛出异常
      ossProperties.validate()
    }
  }

  @Nested
  inner class `URL 处理` {

    @Test
    fun `getEffectiveEndpoint 应该正确处理不同的 endpoint 格式`() {
      // 测试带协议的 endpoint
      val ossProperties1 = OssProperties(endpoint = "https://s3.amazonaws.com")
      assertEquals("https://s3.amazonaws.com", ossProperties1.getEffectiveEndpoint())

      // 测试不带协议的 endpoint，启用 SSL
      val ossProperties2 = OssProperties(endpoint = "s3.amazonaws.com", enableSsl = true)
      assertEquals("https://s3.amazonaws.com", ossProperties2.getEffectiveEndpoint())

      // 测试不带协议的 endpoint，禁用 SSL
      val ossProperties3 = OssProperties(endpoint = "localhost:9000", enableSsl = false)
      assertEquals("http://localhost:9000", ossProperties3.getEffectiveEndpoint())
    }

    @Test
    fun `getEffectiveExposedBaseUrl 应该正确处理 exposedBaseUrl`() {
      // 测试有 exposedBaseUrl 的情况
      val ossProperties1 = OssProperties(endpoint = "http://localhost:9000", exposedBaseUrl = "http://public.example.com")
      assertEquals("http://public.example.com", ossProperties1.getEffectiveExposedBaseUrl())

      // 测试没有 exposedBaseUrl 的情况，应该使用 endpoint
      val ossProperties2 = OssProperties(endpoint = "http://localhost:9000")
      assertEquals("http://localhost:9000", ossProperties2.getEffectiveExposedBaseUrl())
    }
  }

  @Nested
  inner class ToString {

    @Test
    fun `toString 应该隐藏敏感信息`() {
      val ossProperties = OssProperties(provider = "minio", endpoint = "http://localhost:9000", accessKey = "minioadmin", secretKey = "minioadmin")

      val toStringResult = ossProperties.toString()

      // 验证包含非敏感信息
      assertTrue(toStringResult.contains("provider='minio'"))
      assertTrue(toStringResult.contains("endpoint='http://localhost:9000'"))

      // 验证敏感信息被隐藏
      assertTrue(toStringResult.contains("accessKey='mini***'"))
      assertFalse(toStringResult.contains("minioadmin"))
    }
  }
}
