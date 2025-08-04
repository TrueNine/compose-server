package io.github.truenine.composeserver.oss.properties

import io.github.truenine.composeserver.testtoolkit.log
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BaseOssPropertiesTest {

  private class TestOssProperties : BaseOssProperties()

  @Nested
  inner class `默认值测试` {

    @Test
    fun `测试默认配置值`() {
      val properties = TestOssProperties()

      assertEquals(null, properties.endpoint)
      assertEquals(null, properties.region)
      assertEquals(null, properties.accessKey)
      assertEquals(null, properties.secretKey)
      assertEquals(null, properties.exposedBaseUrl)
      assertTrue(properties.enableSsl)
      assertEquals(Duration.ofSeconds(30), properties.connectionTimeout)
      assertEquals(Duration.ofMinutes(5), properties.readTimeout)
      assertEquals(Duration.ofMinutes(5), properties.writeTimeout)
      assertEquals(100, properties.maxConnections)
      assertEquals(null, properties.defaultBucket)
      assertFalse(properties.autoCreateBucket)
      assertFalse(properties.enableVersioning)
      assertFalse(properties.enableLogging)
    }
  }

  @Nested
  inner class `配置验证测试` {

    @Test
    fun `测试有效配置验证通过`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "localhost:9000"
          accessKey = "minioadmin"
          secretKey = "minioadmin"
        }

      // 应该不抛出异常
      properties.validate()
    }

    @Test
    fun `测试缺少endpoint时验证失败`() {
      val properties =
        TestOssProperties().apply {
          accessKey = "minioadmin"
          secretKey = "minioadmin"
        }

      val exception = assertThrows<IllegalArgumentException> { properties.validate() }
      assertTrue(exception.message!!.contains("Endpoint cannot be null or blank"))
    }

    @Test
    fun `测试空endpoint时验证失败`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "   "
          accessKey = "minioadmin"
          secretKey = "minioadmin"
        }

      val exception = assertThrows<IllegalArgumentException> { properties.validate() }
      assertTrue(exception.message!!.contains("Endpoint cannot be null or blank"))
    }

    @Test
    fun `测试缺少accessKey时验证失败`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "localhost:9000"
          secretKey = "minioadmin"
        }

      val exception = assertThrows<IllegalArgumentException> { properties.validate() }
      assertTrue(exception.message!!.contains("Access key cannot be null or blank"))
    }

    @Test
    fun `测试缺少secretKey时验证失败`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "localhost:9000"
          accessKey = "minioadmin"
        }

      val exception = assertThrows<IllegalArgumentException> { properties.validate() }
      assertTrue(exception.message!!.contains("Secret key cannot be null or blank"))
    }

    @Test
    fun `测试无效maxConnections时验证失败`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "localhost:9000"
          accessKey = "minioadmin"
          secretKey = "minioadmin"
          maxConnections = 0
        }

      val exception = assertThrows<IllegalArgumentException> { properties.validate() }
      assertTrue(exception.message!!.contains("Max connections must be positive"))
    }

    @Test
    fun `测试负数超时时验证失败`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "localhost:9000"
          accessKey = "minioadmin"
          secretKey = "minioadmin"
          connectionTimeout = Duration.ofSeconds(-1)
        }

      val exception = assertThrows<IllegalArgumentException> { properties.validate() }
      assertTrue(exception.message!!.contains("Connection timeout cannot be negative"))
    }
  }

  @Nested
  inner class `URL构建测试` {

    @Test
    fun `测试获取有效endpoint - 不带协议`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "localhost:9000"
          enableSsl = false
        }

      assertEquals("http://localhost:9000", properties.getEffectiveEndpoint())
    }

    @Test
    fun `测试获取有效endpoint - 启用SSL`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "localhost:9000"
          enableSsl = true
        }

      assertEquals("https://localhost:9000", properties.getEffectiveEndpoint())
    }

    @Test
    fun `测试获取有效endpoint - 已包含协议`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "https://s3.amazonaws.com"
          enableSsl = false // 应该被忽略
        }

      assertEquals("https://s3.amazonaws.com", properties.getEffectiveEndpoint())
    }

    @Test
    fun `测试获取有效endpoint - HTTP协议`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "http://localhost:9000"
          enableSsl = true // 应该被忽略
        }

      assertEquals("http://localhost:9000", properties.getEffectiveEndpoint())
    }

    @Test
    fun `测试获取有效endpoint - 未配置时抛出异常`() {
      val properties = TestOssProperties()

      val exception = assertThrows<IllegalStateException> { properties.getEffectiveEndpoint() }
      assertTrue(exception.message!!.contains("Endpoint is not configured"))
    }

    @Test
    fun `测试获取有效exposedBaseUrl - 使用自定义URL`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "localhost:9000"
          exposedBaseUrl = "https://cdn.example.com"
        }

      assertEquals("https://cdn.example.com", properties.getEffectiveExposedBaseUrl())
    }

    @Test
    fun `测试获取有效exposedBaseUrl - 使用默认endpoint`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "localhost:9000"
          enableSsl = false
        }

      assertEquals("http://localhost:9000", properties.getEffectiveExposedBaseUrl())
    }
  }

  @Nested
  inner class `字符串表示测试` {

    @Test
    fun `测试toString方法隐藏敏感信息`() {
      val properties =
        TestOssProperties().apply {
          endpoint = "localhost:9000"
          region = "us-east-1"
          accessKey = "minioadmin"
          secretKey = "minioadmin"
          defaultBucket = "test-bucket"
        }

      val toString = properties.toString()
      log.info("Properties toString: {}", toString)

      assertTrue(toString.contains("endpoint='localhost:9000'"))
      assertTrue(toString.contains("region='us-east-1'"))
      assertTrue(toString.contains("accessKey='mini***'")) // 敏感信息被隐藏
      assertFalse(toString.contains("minioadmin")) // 完整的密钥不应该出现
      assertTrue(toString.contains("defaultBucket='test-bucket'"))
    }

    @Test
    fun `测试toString方法处理null值`() {
      val properties = TestOssProperties()

      val toString = properties.toString()
      log.info("Properties toString with nulls: {}", toString)

      assertTrue(toString.contains("endpoint='null'"))
      assertTrue(toString.contains("region='null'"))
      assertTrue(toString.contains("accessKey='null***'"))
    }
  }
}
