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
      val properties = VolcengineTosProperties(
        accessKey = "test-access-key",
        secretKey = "test-secret-key",
        sessionToken = "test-session-token",
        endpoint = "https://tos-s3-cn-beijing.volces.com",
        region = "cn-beijing",
        enableSsl = false,
        connectionTimeout = Duration.ofSeconds(60),
        socketTimeout = Duration.ofSeconds(60),
        maxConnections = 200,
        maxRetries = 5,
        enableCrc = false,
        enableLogging = true
      )

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
      val properties = VolcengineTosProperties(
        accessKey = "",
        secretKey = "",
        endpoint = "",
        region = ""
      )

      assertEquals("", properties.accessKey)
      assertEquals("", properties.secretKey)
      assertEquals("", properties.endpoint)
      assertEquals("", properties.region)
    }

    @Test
    fun `测试零值和极大值`() {
      val properties = VolcengineTosProperties(
        connectionTimeout = Duration.ZERO,
        socketTimeout = Duration.ofHours(24),
        maxConnections = 0,
        maxRetries = 0
      )

      assertEquals(Duration.ZERO, properties.connectionTimeout)
      assertEquals(Duration.ofHours(24), properties.socketTimeout)
      assertEquals(0, properties.maxConnections)
      assertEquals(0, properties.maxRetries)
    }

    @Test
    fun `测试极大值`() {
      val properties = VolcengineTosProperties(
        maxConnections = Int.MAX_VALUE,
        maxRetries = Int.MAX_VALUE
      )

      assertEquals(Int.MAX_VALUE, properties.maxConnections)
      assertEquals(Int.MAX_VALUE, properties.maxRetries)
    }
  }

  @Nested
  inner class PropertyModification {

    @Test
    fun `测试通过构造函数设置属性`() {
      val properties = VolcengineTosProperties(
        accessKey = "new-access-key",
        enableSsl = false
      )

      assertEquals("new-access-key", properties.accessKey)
      assertFalse(properties.enableSsl)
    }

    @Test
    fun `测试copy方法修改属性`() {
      val originalProperties = VolcengineTosProperties()

      val modifiedProperties = originalProperties.copy(
        accessKey = "another-access-key",
        enableSsl = false
      )

      assertEquals("another-access-key", modifiedProperties.accessKey)
      assertFalse(modifiedProperties.enableSsl)

      // 原对象不变
      assertNull(originalProperties.accessKey)
      assertTrue(originalProperties.enableSsl)
    }
  }

  @Nested
  inner class BooleanProperties {

    @Test
    fun `测试布尔属性默认值`() {
      val properties = VolcengineTosProperties()

      // 测试默认值
      assertTrue(properties.enableSsl)
      assertTrue(properties.enableCrc)
      assertFalse(properties.enableLogging)
      assertTrue(properties.enableVirtualHostedStyle)
    }

    @Test
    fun `测试布尔属性设置`() {
      val properties = VolcengineTosProperties(
        enableSsl = false,
        enableCrc = false,
        enableLogging = true,
        enableVirtualHostedStyle = false
      )

      assertFalse(properties.enableSsl)
      assertFalse(properties.enableCrc)
      assertTrue(properties.enableLogging)
      assertFalse(properties.enableVirtualHostedStyle)
    }
  }

  @Nested
  inner class DurationProperties {

    @Test
    fun `测试持续时间属性`() {
      // 测试各种持续时间值
      val durations = listOf(Duration.ofMillis(100), Duration.ofSeconds(1), Duration.ofMinutes(1), Duration.ofHours(1), Duration.ofDays(1))

      durations.forEach { duration ->
        val properties = VolcengineTosProperties(
          connectionTimeout = duration,
          socketTimeout = duration
        )

        assertEquals(duration, properties.connectionTimeout)
        assertEquals(duration, properties.socketTimeout)
      }
    }
  }

  @Nested
  inner class IntegerProperties {

    @Test
    fun `测试整数属性边界值`() {
      // 测试 maxConnections
      val connectionValues = listOf(0, 1, 50, 100, 500, 1000, Int.MAX_VALUE)
      connectionValues.forEach { value ->
        val properties = VolcengineTosProperties(maxConnections = value)
        assertEquals(value, properties.maxConnections)
      }

      // 测试 maxRetries
      val retryValues = listOf(0, 1, 3, 5, 10, 100, Int.MAX_VALUE)
      retryValues.forEach { value ->
        val properties = VolcengineTosProperties(maxRetries = value)
        assertEquals(value, properties.maxRetries)
      }
    }
  }

  @Nested
  inner class StringProperties {

    @Test
    fun `测试字符串属性各种值`() {
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
        val properties = VolcengineTosProperties(
          accessKey = value,
          secretKey = value,
          sessionToken = value,
          endpoint = value,
          region = value
        )

        assertEquals(value, properties.accessKey)
        assertEquals(value, properties.secretKey)
        assertEquals(value, properties.sessionToken)
        assertEquals(value, properties.endpoint)
        assertEquals(value, properties.region)
      }
    }
  }
}
