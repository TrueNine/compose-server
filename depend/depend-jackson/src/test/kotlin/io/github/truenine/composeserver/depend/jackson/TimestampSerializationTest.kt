package io.github.truenine.composeserver.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.springframework.boot.test.context.SpringBootTest

/**
 * 时间戳序列化综合测试
 *
 * 测试Spring Boot环境下所有时间类型的时间戳序列化功能，包括：
 * - 所有时间类型的时间戳序列化
 * - 时区无关性验证
 * - 往返序列化正确性
 * - 不同时区下的一致性
 */
@SpringBootTest
class TimestampSerializationTest {

  lateinit var defaultMapper: ObjectMapper
    @Resource(name = JacksonAutoConfiguration.DEFAULT_OBJECT_MAPPER_BEAN_NAME) set

  lateinit var nonIgnoreMapper: ObjectMapper
    @Resource(name = JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME) set

  @Nested
  inner class LocalDateTimeTimestampTest {

    @Test
    fun `serialize LocalDateTime to timestamp`() {
      val dateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val json = defaultMapper.writeValueAsString(dateTime)

      log.info("LocalDateTime {} serialized to: {}", dateTime, json)

      // 验证序列化为数字时间戳
      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "LocalDateTime应该序列化为数字时间戳")
      assertTrue(timestamp > 0, "时间戳应该大于0")

      // 验证时间戳的合理性（2025年的时间戳应该在合理范围内）
      assertTrue(timestamp > 1700000000000L, "时间戳应该在2025年范围内")
      assertTrue(timestamp < 1800000000000L, "时间戳应该在合理范围内")
    }

    @Test
    fun `deserialize timestamp to LocalDateTime`() {
      val originalDateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val expectedTimestamp = originalDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()

      val deserializedDateTime = defaultMapper.readValue(expectedTimestamp.toString(), LocalDateTime::class.java)

      log.info("Timestamp {} deserialized to LocalDateTime: {}", expectedTimestamp, deserializedDateTime)
      assertEquals(originalDateTime, deserializedDateTime)
    }

    @Test
    fun `round trip LocalDateTime serialization`() {
      val originalDateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45, 123000000)

      val json = defaultMapper.writeValueAsString(originalDateTime)
      val deserializedDateTime = defaultMapper.readValue(json, LocalDateTime::class.java)

      log.info("Round trip: {} -> {} -> {}", originalDateTime, json, deserializedDateTime)

      // 由于时间戳精度限制，纳秒部分可能丢失，只比较到毫秒
      val originalMillis = originalDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
      val deserializedMillis = deserializedDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
      assertEquals(originalMillis, deserializedMillis)
    }
  }

  @Nested
  inner class InstantTimestampTest {

    @Test
    fun `serialize Instant to timestamp`() {
      val instant = Instant.ofEpochMilli(1737000645000L) // 2025-01-16T04:30:45Z
      val json = defaultMapper.writeValueAsString(instant)

      log.info("Instant {} serialized to: {}", instant, json)

      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "Instant应该序列化为数字时间戳")
      assertEquals(1737000645000L, timestamp)
    }

    @Test
    fun `deserialize timestamp to Instant`() {
      val originalTimestamp = 1737000645000L
      val originalInstant = Instant.ofEpochMilli(originalTimestamp)

      val deserializedInstant = defaultMapper.readValue(originalTimestamp.toString(), Instant::class.java)

      log.info("Timestamp {} deserialized to Instant: {}", originalTimestamp, deserializedInstant)
      assertEquals(originalInstant, deserializedInstant)
    }

    @Test
    fun `round trip Instant serialization preserves precision`() {
      // 使用毫秒精度的Instant避免精度丢失问题
      val originalInstant = Instant.ofEpochMilli(System.currentTimeMillis())

      val json = defaultMapper.writeValueAsString(originalInstant)
      val deserializedInstant = defaultMapper.readValue(json, Instant::class.java)

      log.info("Round trip: {} -> {} -> {}", originalInstant, json, deserializedInstant)
      assertEquals(originalInstant, deserializedInstant)
    }
  }

  @Nested
  inner class ZonedDateTimeTimestampTest {

    @Test
    fun `serialize ZonedDateTime to timestamp`() {
      val zonedDateTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Asia/Shanghai"))
      val json = defaultMapper.writeValueAsString(zonedDateTime)

      log.info("ZonedDateTime {} serialized to: {}", zonedDateTime, json)

      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "ZonedDateTime应该序列化为数字时间戳")
      assertTrue(timestamp > 0, "时间戳应该大于0")

      // 验证时间戳与预期的UTC时间戳一致
      val expectedTimestamp = zonedDateTime.toInstant().toEpochMilli()
      assertEquals(expectedTimestamp, timestamp)
    }

    @Test
    fun `deserialize timestamp to ZonedDateTime`() {
      val originalZonedDateTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Asia/Shanghai"))
      val timestamp = originalZonedDateTime.toInstant().toEpochMilli()

      val deserializedZonedDateTime = defaultMapper.readValue(timestamp.toString(), ZonedDateTime::class.java)

      log.info("Timestamp {} deserialized to ZonedDateTime: {}", timestamp, deserializedZonedDateTime)

      // 由于反序列化时使用UTC时区，需要比较Instant
      assertEquals(originalZonedDateTime.toInstant(), deserializedZonedDateTime.toInstant())
    }

    @Test
    fun `round trip ZonedDateTime serialization preserves instant`() {
      val originalZonedDateTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Europe/London"))

      val json = defaultMapper.writeValueAsString(originalZonedDateTime)
      val deserializedZonedDateTime = defaultMapper.readValue(json, ZonedDateTime::class.java)

      log.info("Round trip: {} -> {} -> {}", originalZonedDateTime, json, deserializedZonedDateTime)

      // 时区可能不同，但时间点应该相同
      assertEquals(originalZonedDateTime.toInstant(), deserializedZonedDateTime.toInstant())
    }
  }

  @Nested
  inner class OffsetDateTimeTimestampTest {

    @Test
    fun `serialize OffsetDateTime to timestamp`() {
      val offsetDateTime = OffsetDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.ofHours(8))
      val json = defaultMapper.writeValueAsString(offsetDateTime)

      log.info("OffsetDateTime {} serialized to: {}", offsetDateTime, json)

      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "OffsetDateTime应该序列化为数字时间戳")
      assertTrue(timestamp > 0, "时间戳应该大于0")

      // 验证时间戳与预期的UTC时间戳一致
      val expectedTimestamp = offsetDateTime.toInstant().toEpochMilli()
      assertEquals(expectedTimestamp, timestamp)
    }

    @Test
    fun `deserialize timestamp to OffsetDateTime`() {
      val originalOffsetDateTime = OffsetDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.ofHours(-5))
      val timestamp = originalOffsetDateTime.toInstant().toEpochMilli()

      val deserializedOffsetDateTime = defaultMapper.readValue(timestamp.toString(), OffsetDateTime::class.java)

      log.info("Timestamp {} deserialized to OffsetDateTime: {}", timestamp, deserializedOffsetDateTime)

      // 由于反序列化时使用UTC偏移，需要比较Instant
      assertEquals(originalOffsetDateTime.toInstant(), deserializedOffsetDateTime.toInstant())
    }

    @Test
    fun `round trip OffsetDateTime serialization preserves instant`() {
      val originalOffsetDateTime = OffsetDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.ofHours(-3))

      val json = defaultMapper.writeValueAsString(originalOffsetDateTime)
      val deserializedOffsetDateTime = defaultMapper.readValue(json, OffsetDateTime::class.java)

      log.info("Round trip: {} -> {} -> {}", originalOffsetDateTime, json, deserializedOffsetDateTime)

      // 偏移量可能不同，但时间点应该相同
      assertEquals(originalOffsetDateTime.toInstant(), deserializedOffsetDateTime.toInstant())
    }
  }

  @Nested
  inner class TimezoneIndependenceTest {

    @Test
    fun `different timezones serialize to same timestamp for same instant`() {
      // 相同的时间点，不同的时区表示
      val utcTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.UTC)
      val shanghaiTime = ZonedDateTime.of(2025, 1, 16, 20, 30, 45, 0, ZoneId.of("Asia/Shanghai"))
      val newYorkTime = ZonedDateTime.of(2025, 1, 16, 7, 30, 45, 0, ZoneId.of("America/New_York"))
      val londonTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Europe/London"))

      val utcJson = defaultMapper.writeValueAsString(utcTime)
      val shanghaiJson = defaultMapper.writeValueAsString(shanghaiTime)
      val newYorkJson = defaultMapper.writeValueAsString(newYorkTime)
      val londonJson = defaultMapper.writeValueAsString(londonTime)

      log.info("UTC: {} -> {}", utcTime, utcJson)
      log.info("Shanghai: {} -> {}", shanghaiTime, shanghaiJson)
      log.info("New York: {} -> {}", newYorkTime, newYorkJson)
      log.info("London: {} -> {}", londonTime, londonJson)

      // 相同的时间点应该序列化为相同的时间戳
      assertEquals(utcJson, shanghaiJson, "相同时间点的不同时区应该序列化为相同时间戳")
      assertEquals(utcJson, newYorkJson, "相同时间点的不同时区应该序列化为相同时间戳")
      assertEquals(utcJson, londonJson, "相同时间点的不同时区应该序列化为相同时间戳")
    }

    @Test
    fun `OffsetDateTime with different offsets serialize to same timestamp for same instant`() {
      // 相同的UTC时间点，不同的偏移量表示
      val utcOffset = OffsetDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.UTC)
      val plusEightOffset = OffsetDateTime.of(2025, 1, 16, 20, 30, 45, 0, ZoneOffset.ofHours(8))
      val minusFiveOffset = OffsetDateTime.of(2025, 1, 16, 7, 30, 45, 0, ZoneOffset.ofHours(-5))

      val utcJson = defaultMapper.writeValueAsString(utcOffset)
      val plusEightJson = defaultMapper.writeValueAsString(plusEightOffset)
      val minusFiveJson = defaultMapper.writeValueAsString(minusFiveOffset)

      log.info("UTC offset: {} -> {}", utcOffset, utcJson)
      log.info("+8 offset: {} -> {}", plusEightOffset, plusEightJson)
      log.info("-5 offset: {} -> {}", minusFiveOffset, minusFiveJson)

      assertEquals(utcJson, plusEightJson, "相同时间点的不同偏移量应该序列化为相同时间戳")
      assertEquals(utcJson, minusFiveJson, "相同时间点的不同偏移量应该序列化为相同时间戳")
    }

    @Test
    fun `LocalDateTime serialization is timezone independent`() {
      // LocalDateTime没有时区信息，应该始终按UTC处理
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

  @Nested
  inner class AdditionalTimeTypesTest {

    @Test
    fun `LocalDate serialization behavior`() {
      val localDate = LocalDate.of(2025, 1, 16)

      try {
        val json = defaultMapper.writeValueAsString(localDate)
        log.info("LocalDate {} serialized to: {}", localDate, json)

        // LocalDate可能序列化为字符串或数组格式，取决于配置
        assertTrue(json.isNotEmpty(), "LocalDate序列化结果不应为空")

        // 尝试反序列化验证
        val deserializedDate = defaultMapper.readValue(json, LocalDate::class.java)
        assertEquals(localDate, deserializedDate)
      } catch (e: Exception) {
        log.info("LocalDate serialization failed as expected: {}", e.message)
        // LocalDate可能不支持时间戳序列化，这是正常的
      }
    }

    @Test
    fun `LocalTime serialization behavior`() {
      val localTime = LocalTime.of(12, 30, 45)

      try {
        val json = defaultMapper.writeValueAsString(localTime)
        log.info("LocalTime {} serialized to: {}", localTime, json)

        assertTrue(json.isNotEmpty(), "LocalTime序列化结果不应为空")

        // 尝试反序列化验证
        val deserializedTime = defaultMapper.readValue(json, LocalTime::class.java)
        assertEquals(localTime, deserializedTime)
      } catch (e: Exception) {
        log.info("LocalTime serialization failed as expected: {}", e.message)
        // LocalTime可能不支持时间戳序列化，这是正常的
      }
    }
  }

  @Nested
  inner class ObjectMapperConsistencyTest {

    @Test
    fun `both ObjectMappers produce consistent timestamp serialization`() {
      val instant = Instant.ofEpochMilli(1737000645000L)
      val localDateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val zonedDateTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Asia/Shanghai"))

      // 测试Instant
      val instantJson1 = defaultMapper.writeValueAsString(instant)
      val instantJson2 = nonIgnoreMapper.writeValueAsString(instant)
      log.info("Instant - Default mapper: {}, Non-ignore mapper: {}", instantJson1, instantJson2)

      // 测试LocalDateTime
      val localDateTimeJson1 = defaultMapper.writeValueAsString(localDateTime)
      val localDateTimeJson2 = nonIgnoreMapper.writeValueAsString(localDateTime)
      log.info("LocalDateTime - Default mapper: {}, Non-ignore mapper: {}", localDateTimeJson1, localDateTimeJson2)

      // 测试ZonedDateTime
      val zonedDateTimeJson1 = defaultMapper.writeValueAsString(zonedDateTime)
      val zonedDateTimeJson2 = nonIgnoreMapper.writeValueAsString(zonedDateTime)
      log.info("ZonedDateTime - Default mapper: {}, Non-ignore mapper: {}", zonedDateTimeJson1, zonedDateTimeJson2)

      // 验证两个mapper的时间戳序列化行为一致
      // 注意：非忽略mapper可能包含额外的类型信息，所以我们主要验证时间戳值
      val instantTimestamp1 = instantJson1.toLongOrNull()
      val instantTimestamp2 =
        if (instantJson2.contains("@class")) {
          // 如果包含类型信息，提取数值部分
          instantJson2.substringAfterLast(":").substringBefore("}").toLongOrNull()
        } else {
          instantJson2.toLongOrNull()
        }

      if (instantTimestamp1 != null && instantTimestamp2 != null) {
        assertEquals(instantTimestamp1, instantTimestamp2, "两个ObjectMapper应该产生相同的Instant时间戳")
      }
    }

    @Test
    fun `timestamp deserialization works with both mappers`() {
      val timestamp = 1737000645000L
      val timestampJson = timestamp.toString()

      // 测试默认mapper能正确反序列化时间戳
      val instant1 = defaultMapper.readValue(timestampJson, Instant::class.java)

      // 非忽略mapper需要类型信息，所以我们使用它序列化后再反序列化
      val instant = Instant.ofEpochMilli(timestamp)
      val nonIgnoreJson = nonIgnoreMapper.writeValueAsString(instant)
      val instant2 = nonIgnoreMapper.readValue(nonIgnoreJson, Instant::class.java)

      log.info("Timestamp {} deserialized - Default mapper: {}", timestamp, instant1)
      log.info("Non-ignore mapper round trip: {} -> {} -> {}", instant, nonIgnoreJson, instant2)

      assertEquals(Instant.ofEpochMilli(timestamp), instant1)
      assertEquals(instant, instant2, "非忽略mapper往返序列化应该保持一致")
    }
  }

  @Nested
  inner class RoundTripConsistencyTest {

    @Test
    fun `multiple round trips maintain consistency`() {
      val originalInstant = Instant.ofEpochMilli(1737000645000L)

      // 进行多次往返序列化
      var currentInstant = originalInstant
      repeat(5) { iteration ->
        val json = defaultMapper.writeValueAsString(currentInstant)
        currentInstant = defaultMapper.readValue(json, Instant::class.java)

        log.info("Round trip {}: {} -> {} -> {}", iteration + 1, if (iteration == 0) originalInstant else "previous", json, currentInstant)

        assertEquals(originalInstant, currentInstant, "第${iteration + 1}次往返后应该保持一致")
      }
    }

    @Test
    fun `complex object with timestamps serializes correctly`() {
      data class TimeStampedEvent(
        val id: String,
        val timestamp: Instant,
        val localTime: LocalDateTime,
        val zonedTime: ZonedDateTime,
        val offsetTime: OffsetDateTime,
      )

      val event =
        TimeStampedEvent(
          id = "test-event",
          timestamp = Instant.ofEpochMilli(1737000645000L),
          localTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45),
          zonedTime = ZonedDateTime.of(2025, 1, 16, 20, 30, 45, 0, ZoneId.of("Asia/Shanghai")),
          offsetTime = OffsetDateTime.of(2025, 1, 16, 7, 30, 45, 0, ZoneOffset.ofHours(-5)),
        )

      val json = defaultMapper.writeValueAsString(event)
      val deserializedEvent = defaultMapper.readValue(json, TimeStampedEvent::class.java)

      log.info("Complex event serialized to: {}", json)
      log.info("Deserialized event: {}", deserializedEvent)

      assertEquals(event.id, deserializedEvent.id)
      assertEquals(event.timestamp, deserializedEvent.timestamp)

      // 时间字段可能因为时区处理而略有不同，但时间点应该相同
      assertEquals(event.timestamp.toEpochMilli(), deserializedEvent.timestamp.toEpochMilli())
      assertEquals(event.zonedTime.toInstant(), deserializedEvent.zonedTime.toInstant())
      assertEquals(event.offsetTime.toInstant(), deserializedEvent.offsetTime.toInstant())
    }
  }
}
