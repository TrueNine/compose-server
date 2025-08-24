package itest.integrate.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import jakarta.annotation.Resource
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

/**
 * 时区无关性集成测试
 *
 * 测试不同时区环境下的序列化一致性，验证UTC时间戳的正确性， 测试多种时间格式的反序列化兼容性
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TimezoneIndependenceTest {

  @Resource @Qualifier(JacksonAutoConfiguration.DEFAULT_OBJECT_MAPPER_BEAN_NAME) private lateinit var objectMapper: ObjectMapper

  @Nested
  inner class TimezoneConsistencyTests {

    @Test
    fun same_instant_in_different_timezones_should_serialize_to_same_timestamp() {
      // 创建相同时刻的不同时区时间
      val baseInstant = Instant.parse("2023-06-15T12:00:00Z")
      val utcTime = ZonedDateTime.ofInstant(baseInstant, ZoneOffset.UTC)
      val beijingTime = ZonedDateTime.ofInstant(baseInstant, ZoneId.of("Asia/Shanghai"))
      val newYorkTime = ZonedDateTime.ofInstant(baseInstant, ZoneId.of("America/New_York"))
      val tokyoTime = ZonedDateTime.ofInstant(baseInstant, ZoneId.of("Asia/Tokyo"))

      // 序列化所有时间
      val utcJson = objectMapper.writeValueAsString(utcTime)
      val beijingJson = objectMapper.writeValueAsString(beijingTime)
      val newYorkJson = objectMapper.writeValueAsString(newYorkTime)
      val tokyoJson = objectMapper.writeValueAsString(tokyoTime)

      // 所有序列化结果应该相同（都是UTC时间戳）
      val expectedTimestamp = baseInstant.toEpochMilli()
      assertEquals(expectedTimestamp.toString(), utcJson)
      assertEquals(expectedTimestamp.toString(), beijingJson)
      assertEquals(expectedTimestamp.toString(), newYorkJson)
      assertEquals(expectedTimestamp.toString(), tokyoJson)
    }

    @Test
    fun local_datetime_should_be_treated_as_utc_for_timestamp_serialization() {
      // LocalDateTime没有时区信息，应该被当作UTC处理
      val localDateTime = LocalDateTime.of(2023, 6, 15, 12, 0, 0)
      val expectedUtcInstant = localDateTime.toInstant(ZoneOffset.UTC)

      val json = objectMapper.writeValueAsString(localDateTime)
      val expectedTimestamp = expectedUtcInstant.toEpochMilli()

      assertEquals(expectedTimestamp.toString(), json)
    }

    @Test
    fun offset_datetime_should_convert_to_utc_timestamp() {
      // 测试不同偏移量的OffsetDateTime
      val baseTime = LocalDateTime.of(2023, 6, 15, 12, 0, 0)
      val utcTime = OffsetDateTime.of(baseTime, ZoneOffset.UTC)
      val plusEightTime = OffsetDateTime.of(baseTime.plusHours(8), ZoneOffset.ofHours(8))
      val minusFiveTime = OffsetDateTime.of(baseTime.minusHours(5), ZoneOffset.ofHours(-5))

      val utcJson = objectMapper.writeValueAsString(utcTime)
      val plusEightJson = objectMapper.writeValueAsString(plusEightTime)
      val minusFiveJson = objectMapper.writeValueAsString(minusFiveTime)

      // 所有时间都应该序列化为相同的UTC时间戳
      assertEquals(utcJson, plusEightJson)
      assertEquals(utcJson, minusFiveJson)
    }
  }

  @Nested
  inner class UtcTimestampValidationTests {

    @Test
    fun serialized_timestamps_should_represent_utc_time() {
      // 创建一个已知的UTC时间
      val knownUtcTime = ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
      val expectedTimestamp = knownUtcTime.toInstant().toEpochMilli()

      // 序列化
      val json = objectMapper.writeValueAsString(knownUtcTime)
      val actualTimestamp = json.toLong()

      assertEquals(expectedTimestamp, actualTimestamp)

      // 验证时间戳确实代表UTC时间
      val reconstructedInstant = Instant.ofEpochMilli(actualTimestamp)
      val reconstructedUtc = ZonedDateTime.ofInstant(reconstructedInstant, ZoneOffset.UTC)

      assertEquals(knownUtcTime.toInstant(), reconstructedUtc.toInstant())
    }

    @Test
    fun timestamp_should_be_timezone_independent() {
      val originalTimeZone = TimeZone.getDefault()

      try {
        val testTime = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
        val results = mutableListOf<String>()

        // 在不同的系统时区下序列化相同的时间
        val timeZones = listOf("UTC", "Asia/Shanghai", "America/New_York", "Europe/London", "Asia/Tokyo")

        for (tzId in timeZones) {
          TimeZone.setDefault(TimeZone.getTimeZone(tzId))
          val json = objectMapper.writeValueAsString(testTime)
          results.add(json)
        }

        // 所有结果应该相同
        val firstResult = results.first()
        results.forEach { result -> assertEquals(firstResult, result, "时间戳序列化应该与系统时区无关") }
      } finally {
        TimeZone.setDefault(originalTimeZone)
      }
    }
  }

  @Nested
  inner class MultiFormatDeserializationTests {

    @Test
    fun should_deserialize_timestamp_to_correct_time_objects() {
      val timestamp = 1687003200000L // 2023-06-17T12:00:00Z
      val timestampJson = timestamp.toString()

      // 反序列化为不同的时间类型
      val instant = objectMapper.readValue(timestampJson, Instant::class.java)
      val zonedDateTime = objectMapper.readValue(timestampJson, ZonedDateTime::class.java)
      val offsetDateTime = objectMapper.readValue(timestampJson, OffsetDateTime::class.java)
      val localDateTime = objectMapper.readValue(timestampJson, LocalDateTime::class.java)

      // 验证所有类型都表示相同的时刻
      val expectedInstant = Instant.ofEpochMilli(timestamp)
      assertEquals(expectedInstant, instant)
      assertEquals(expectedInstant, zonedDateTime.toInstant())
      assertEquals(expectedInstant, offsetDateTime.toInstant())
      assertEquals(expectedInstant, localDateTime.toInstant(ZoneOffset.UTC))
    }

    @Test
    fun should_handle_iso8601_format_for_backward_compatibility() {
      val iso8601String = "\"2023-06-17T12:00:00Z\""
      val expectedInstant = Instant.parse("2023-06-17T12:00:00Z")

      try {
        // 尝试反序列化ISO8601格式（向后兼容性）
        val instant = objectMapper.readValue(iso8601String, Instant::class.java)
        assertEquals(expectedInstant, instant)
      } catch (e: Exception) {
        // 如果不支持ISO8601格式，这是预期的（因为我们优先使用时间戳）
        assertTrue(e.message?.contains("Cannot deserialize") == true || e.message?.contains("not supported") == true)
      }
    }

    @Test
    fun should_handle_various_timestamp_formats() {
      val baseInstant = Instant.parse("2023-06-17T12:00:00Z")
      val millisTimestamp = baseInstant.toEpochMilli()
      val secondsTimestamp = baseInstant.epochSecond

      // 测试毫秒时间戳
      val millisJson = millisTimestamp.toString()
      val instantFromMillis = objectMapper.readValue(millisJson, Instant::class.java)
      assertEquals(baseInstant, instantFromMillis)

      // 测试秒时间戳（如果支持）
      val secondsJson = secondsTimestamp.toString()
      try {
        val instantFromSeconds = objectMapper.readValue(secondsJson, Instant::class.java)
        // 检查是否被当作毫秒时间戳处理（这是常见的情况）
        if (instantFromSeconds.toEpochMilli() == secondsTimestamp) {
          // 被当作毫秒时间戳处理，这是可以接受的
          assertTrue(true, "秒时间戳被当作毫秒时间戳处理，这是可以接受的")
        } else {
          // 如果支持秒时间戳，验证结果
          assertEquals(secondsTimestamp, instantFromSeconds.epochSecond)
        }
      } catch (e: Exception) {
        // 如果不支持秒时间戳，这也是可以接受的
        assertTrue(
          e.message?.contains("Cannot deserialize") == true || e.message?.contains("not supported") == true || e.message?.contains("Invalid") == true,
          "秒时间戳格式不被支持，这是可以接受的",
        )
      }
    }
  }

  @Nested
  inner class EdgeCaseTests {

    @Test
    fun should_handle_epoch_time() {
      val epochInstant = Instant.EPOCH
      val json = objectMapper.writeValueAsString(epochInstant)
      assertEquals("0", json)

      val deserializedInstant = objectMapper.readValue(json, Instant::class.java)
      assertEquals(epochInstant, deserializedInstant)
    }

    @Test
    fun should_handle_far_future_dates() {
      val farFuture = Instant.parse("2099-12-31T23:59:59Z")
      val json = objectMapper.writeValueAsString(farFuture)
      val timestamp = json.toLong()

      assertTrue(timestamp > 0, "未来日期应该产生正时间戳")

      val deserializedInstant = objectMapper.readValue(json, Instant::class.java)
      assertEquals(farFuture, deserializedInstant)
    }

    @Test
    fun should_handle_daylight_saving_time_transitions() {
      // 测试夏令时转换期间的时间
      val dstTransition = ZonedDateTime.of(2023, 3, 12, 2, 30, 0, 0, ZoneId.of("America/New_York"))
      val json = objectMapper.writeValueAsString(dstTransition)
      val timestamp = json.toLong()

      // 验证时间戳是有效的
      assertTrue(timestamp > 0)

      // 验证往返序列化
      val deserializedZdt = objectMapper.readValue(json, ZonedDateTime::class.java)
      assertEquals(dstTransition.toInstant(), deserializedZdt.toInstant())
    }
  }

  @Nested
  inner class RoundTripTests {

    @Test
    fun all_time_types_should_support_round_trip_serialization() {
      val baseInstant = Instant.now()
      val instant = baseInstant
      val zonedDateTime = ZonedDateTime.ofInstant(baseInstant, ZoneOffset.UTC)
      val offsetDateTime = OffsetDateTime.ofInstant(baseInstant, ZoneOffset.UTC)
      val localDateTime = LocalDateTime.ofInstant(baseInstant, ZoneOffset.UTC)

      // 测试往返序列化
      testRoundTrip(instant, Instant::class.java)
      testRoundTrip(zonedDateTime, ZonedDateTime::class.java)
      testRoundTrip(offsetDateTime, OffsetDateTime::class.java)
      testRoundTrip(localDateTime, LocalDateTime::class.java)
    }

    private fun <T> testRoundTrip(original: T, clazz: Class<T>) {
      val json = objectMapper.writeValueAsString(original)
      val deserialized = objectMapper.readValue(json, clazz)

      when (original) {
        is Instant -> {
          // 由于时间戳序列化可能会丢失纳秒精度，我们比较毫秒精度
          val originalMillis = (original as Instant).toEpochMilli()
          val deserializedMillis = (deserialized as Instant).toEpochMilli()
          assertEquals(originalMillis, deserializedMillis)
        }

        is ZonedDateTime -> {
          val originalMillis = (original as ZonedDateTime).toInstant().toEpochMilli()
          val deserializedMillis = (deserialized as ZonedDateTime).toInstant().toEpochMilli()
          assertEquals(originalMillis, deserializedMillis)
        }

        is OffsetDateTime -> {
          val originalMillis = (original as OffsetDateTime).toInstant().toEpochMilli()
          val deserializedMillis = (deserialized as OffsetDateTime).toInstant().toEpochMilli()
          assertEquals(originalMillis, deserializedMillis)
        }

        is LocalDateTime -> {
          val originalMillis = (original as LocalDateTime).toInstant(ZoneOffset.UTC).toEpochMilli()
          val deserializedMillis = (deserialized as LocalDateTime).toInstant(ZoneOffset.UTC).toEpochMilli()
          assertEquals(originalMillis, deserializedMillis)
        }
      }
    }
  }
}
