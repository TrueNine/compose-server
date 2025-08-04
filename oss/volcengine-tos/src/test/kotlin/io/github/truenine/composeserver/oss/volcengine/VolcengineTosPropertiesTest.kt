package io.github.truenine.composeserver.oss.volcengine

import io.github.truenine.composeserver.oss.volcengine.properties.VolcengineTosProperties
import java.time.Duration
import kotlin.test.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/** Volcengine TOS 配置属性测试 */
class VolcengineTosPropertiesTest {

  @Nested
  inner class DefaultValues {

    @Test
    fun `测试默认值设置`() {
      val properties = VolcengineTosProperties()

      assertNull(properties.accessKey)
      assertNull(properties.secretKey)
      assertNull(properties.sessionToken)
      assertNull(properties.endpoint)
      assertNull(properties.region)
      assertTrue(properties.enableSsl)
      assertEquals(Duration.ofSeconds(30), properties.connectionTimeout)
      assertEquals(Duration.ofMinutes(5), properties.socketTimeout)
      assertEquals(100, properties.maxConnections)
      assertEquals(3, properties.maxRetries)
      assertTrue(properties.enableCrc)
      assertFalse(properties.enableLogging)
    }
  }

  @Nested
  inner class PropertyAssignment {

    @Test
    fun `测试属性赋值`() {
      val properties =
        VolcengineTosProperties().apply {
          accessKey = "test-access-key"
          secretKey = "test-secret-key"
          sessionToken = "test-session-token"
          endpoint = "https://tos-s3-cn-beijing.volces.com"
          region = "cn-beijing"
          enableSsl = false
          connectionTimeout = Duration.ofSeconds(60)
          socketTimeout = Duration.ofSeconds(60)
          maxConnections = 200
          maxRetries = 5
          enableCrc = false
          enableLogging = true
        }

      assertEquals("test-access-key", properties.accessKey)
      assertEquals("test-secret-key", properties.secretKey)
      assertEquals("test-session-token", properties.sessionToken)
      assertEquals("https://tos-s3-cn-beijing.volces.com", properties.endpoint)
      assertEquals("cn-beijing", properties.region)
      assertFalse(properties.enableSsl)
      assertEquals(Duration.ofSeconds(60), properties.connectionTimeout)
      assertEquals(Duration.ofSeconds(60), properties.socketTimeout)
      assertEquals(200, properties.maxConnections)
      assertEquals(5, properties.maxRetries)
      assertFalse(properties.enableCrc)
      assertTrue(properties.enableLogging)
    }
  }

  @Nested
  inner class EdgeCases {

    @Test
    fun `测试空字符串值`() {
      val properties =
        VolcengineTosProperties().apply {
          accessKey = ""
          secretKey = ""
          endpoint = ""
          region = ""
        }

      assertEquals("", properties.accessKey)
      assertEquals("", properties.secretKey)
      assertEquals("", properties.endpoint)
      assertEquals("", properties.region)
    }

    @Test
    fun `测试零值和极大值`() {
      val properties =
        VolcengineTosProperties().apply {
          connectionTimeout = Duration.ZERO
          socketTimeout = Duration.ofHours(24)
          maxConnections = 0
          maxRetries = 0
        }

      assertEquals(Duration.ZERO, properties.connectionTimeout)
      assertEquals(Duration.ofHours(24), properties.socketTimeout)
      assertEquals(0, properties.maxConnections)
      assertEquals(0, properties.maxRetries)
    }

    @Test
    fun `测试极大值`() {
      val properties =
        VolcengineTosProperties().apply {
          maxConnections = Int.MAX_VALUE
          maxRetries = Int.MAX_VALUE
        }

      assertEquals(Int.MAX_VALUE, properties.maxConnections)
      assertEquals(Int.MAX_VALUE, properties.maxRetries)
    }
  }

  @Nested
  inner class PropertyModification {

    @Test
    fun `测试属性修改`() {
      val properties = VolcengineTosProperties()

      // 初始值
      assertNull(properties.accessKey)
      assertTrue(properties.enableSsl)

      // 修改值
      properties.accessKey = "new-access-key"
      properties.enableSsl = false

      // 验证修改
      assertEquals("new-access-key", properties.accessKey)
      assertFalse(properties.enableSsl)

      // 再次修改
      properties.accessKey = "another-access-key"
      properties.enableSsl = true

      // 验证再次修改
      assertEquals("another-access-key", properties.accessKey)
      assertTrue(properties.enableSsl)
    }
  }

  @Nested
  inner class BooleanProperties {

    @Test
    fun `测试布尔属性切换`() {
      val properties = VolcengineTosProperties()

      // 测试 enableSsl
      assertTrue(properties.enableSsl)
      properties.enableSsl = false
      assertFalse(properties.enableSsl)
      properties.enableSsl = true
      assertTrue(properties.enableSsl)

      // 测试 enableCrc
      assertTrue(properties.enableCrc)
      properties.enableCrc = false
      assertFalse(properties.enableCrc)
      properties.enableCrc = true
      assertTrue(properties.enableCrc)

      // 测试 enableLogging
      assertFalse(properties.enableLogging)
      properties.enableLogging = true
      assertTrue(properties.enableLogging)
      properties.enableLogging = false
      assertFalse(properties.enableLogging)
    }
  }

  @Nested
  inner class DurationProperties {

    @Test
    fun `测试持续时间属性`() {
      val properties = VolcengineTosProperties()

      // 测试各种持续时间值
      val durations = listOf(Duration.ofMillis(100), Duration.ofSeconds(1), Duration.ofMinutes(1), Duration.ofHours(1), Duration.ofDays(1))

      durations.forEach { duration ->
        properties.connectionTimeout = duration
        assertEquals(duration, properties.connectionTimeout)

        properties.socketTimeout = duration
        assertEquals(duration, properties.socketTimeout)
      }
    }
  }

  @Nested
  inner class IntegerProperties {

    @Test
    fun `测试整数属性边界值`() {
      val properties = VolcengineTosProperties()

      // 测试 maxConnections
      val connectionValues = listOf(0, 1, 50, 100, 500, 1000, Int.MAX_VALUE)
      connectionValues.forEach { value ->
        properties.maxConnections = value
        assertEquals(value, properties.maxConnections)
      }

      // 测试 maxRetries
      val retryValues = listOf(0, 1, 3, 5, 10, 100, Int.MAX_VALUE)
      retryValues.forEach { value ->
        properties.maxRetries = value
        assertEquals(value, properties.maxRetries)
      }
    }
  }

  @Nested
  inner class StringProperties {

    @Test
    fun `测试字符串属性各种值`() {
      val properties = VolcengineTosProperties()

      val testValues =
        listOf(
          null,
          "",
          "simple-value",
          "value-with-dashes",
          "value_with_underscores",
          "ValueWithCamelCase",
          "value.with.dots",
          "https://example.com/path?query=value",
          "very-long-value-that-might-be-used-in-real-scenarios-with-lots-of-characters",
        )

      testValues.forEach { value ->
        properties.accessKey = value
        assertEquals(value, properties.accessKey)

        properties.secretKey = value
        assertEquals(value, properties.secretKey)

        properties.sessionToken = value
        assertEquals(value, properties.sessionToken)

        properties.endpoint = value
        assertEquals(value, properties.endpoint)

        properties.region = value
        assertEquals(value, properties.region)
      }
    }
  }
}
