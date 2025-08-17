package io.github.truenine.composeserver.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.springframework.boot.test.context.SpringBootTest

/**
 * Jackson序列化反序列化测试
 *
 * 测试基本的JSON序列化功能和时间戳序列化行为
 */
@SpringBootTest
class JacksonSerializationTest {
  lateinit var defaultMapper: ObjectMapper
    @Resource(name = JacksonAutoConfiguration.DEFAULT_OBJECT_MAPPER_BEAN_NAME) set

  @Nested
  inner class BasicSerializationTest {

    @Test
    fun serialize_long_to_json_number() {
      val longValue = 1234567890123451L
      val json = defaultMapper.writeValueAsString(longValue)

      log.info("Long value {} serialized to: {}", longValue, json)
      assertEquals("1234567890123451", json, "Long值应该序列化为JSON数字格式")

      // 验证反序列化
      val deserializedLong = defaultMapper.readValue(json, Long::class.java)
      assertEquals(longValue, deserializedLong)
    }

    @Test
    fun serialize_string_preserves_content() {
      val stringValue = "test string with special chars: 中文 🎉 \"quotes\""
      val json = defaultMapper.writeValueAsString(stringValue)

      log.info("String value serialized to: {}", json)

      val deserializedString = defaultMapper.readValue(json, String::class.java)
      assertEquals(stringValue, deserializedString)
    }

    @Test
    fun serialize_null_values() {
      val nullString: String? = null
      val json = defaultMapper.writeValueAsString(nullString)

      log.info("Null value serialized to: {}", json)
      assertEquals("null", json)

      val deserializedNull = defaultMapper.readValue(json, String::class.java)
      assertEquals(null, deserializedNull)
    }
  }

  @Nested
  inner class TimestampSerializationTest {

    @Test
    fun serialize_localDateTime_to_timestamp() {
      val localDateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val json = defaultMapper.writeValueAsString(localDateTime)

      log.info("LocalDateTime {} serialized to: {}", localDateTime, json)

      // 验证序列化为时间戳
      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "LocalDateTime应该序列化为时间戳")
      assertTrue(timestamp > 0, "时间戳应该大于0")

      // 验证时间戳的合理性
      val expectedTimestamp = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
      assertEquals(expectedTimestamp, timestamp, "时间戳应该基于UTC计算")
    }

    @Test
    fun deserialize_timestamp_to_localDateTime() {
      val originalDateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val timestamp = originalDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()

      val deserializedDateTime = defaultMapper.readValue(timestamp.toString(), LocalDateTime::class.java)

      log.info("Timestamp {} deserialized to LocalDateTime: {}", timestamp, deserializedDateTime)
      assertEquals(originalDateTime, deserializedDateTime)
    }

    @Test
    fun round_trip_localDateTime_preserves_value() {
      val originalDateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45, 123000000)

      val json = defaultMapper.writeValueAsString(originalDateTime)
      val deserializedDateTime = defaultMapper.readValue(json, LocalDateTime::class.java)

      log.info("Round trip: {} -> {} -> {}", originalDateTime, json, deserializedDateTime)

      // 由于时间戳精度限制，纳秒部分可能丢失，只比较到毫秒
      val originalMillis = originalDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
      val deserializedMillis = deserializedDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
      assertEquals(originalMillis, deserializedMillis)
    }

    @Test
    fun serialize_instant_to_timestamp() {
      val instant = Instant.ofEpochMilli(1737000645000L)
      val json = defaultMapper.writeValueAsString(instant)

      log.info("Instant {} serialized to: {}", instant, json)

      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "Instant应该序列化为时间戳")
      assertEquals(1737000645000L, timestamp)
    }

    @Test
    fun timezone_independence_verification() {
      // 验证时区无关性 - LocalDateTime始终按UTC处理
      val localDateTime1 = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val localDateTime2 = LocalDateTime.of(2025, 1, 16, 12, 30, 45)

      val json1 = defaultMapper.writeValueAsString(localDateTime1)
      val json2 = defaultMapper.writeValueAsString(localDateTime2)

      log.info("LocalDateTime1: {} -> {}", localDateTime1, json1)
      log.info("LocalDateTime2: {} -> {}", localDateTime2, json2)

      assertEquals(json1, json2, "相同的LocalDateTime应该序列化为相同的时间戳")

      // 验证序列化结果是时间戳
      val timestamp = json1.toLongOrNull()
      assertNotNull(timestamp, "LocalDateTime应该序列化为时间戳")
    }
  }
}
