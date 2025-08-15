package io.github.truenine.composeserver.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.truenine.composeserver.depend.jackson.serializers.InstantTimestampDeserializer
import io.github.truenine.composeserver.depend.jackson.serializers.InstantTimestampSerializer
import io.github.truenine.composeserver.depend.jackson.serializers.LocalDateTimeTimestampDeserializer
import io.github.truenine.composeserver.depend.jackson.serializers.LocalDateTimeTimestampSerializer
import io.github.truenine.composeserver.depend.jackson.serializers.OffsetDateTimeTimestampDeserializer
import io.github.truenine.composeserver.depend.jackson.serializers.OffsetDateTimeTimestampSerializer
import io.github.truenine.composeserver.depend.jackson.serializers.ZonedDateTimeTimestampDeserializer
import io.github.truenine.composeserver.depend.jackson.serializers.ZonedDateTimeTimestampSerializer
import io.github.truenine.composeserver.testtoolkit.log
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested

/**
 * 时间戳序列化器测试
 *
 * 测试所有时间类型的时间戳序列化和反序列化功能
 */
class TimestampSerializersTest {

  private lateinit var mapper: ObjectMapper

  @BeforeEach
  fun setup() {
    mapper = ObjectMapper()
    val module =
      SimpleModule().apply {
        addSerializer(LocalDateTime::class.java, LocalDateTimeTimestampSerializer())
        addDeserializer(LocalDateTime::class.java, LocalDateTimeTimestampDeserializer())
        addSerializer(Instant::class.java, InstantTimestampSerializer())
        addDeserializer(Instant::class.java, InstantTimestampDeserializer())
        addSerializer(ZonedDateTime::class.java, ZonedDateTimeTimestampSerializer())
        addDeserializer(ZonedDateTime::class.java, ZonedDateTimeTimestampDeserializer())
        addSerializer(OffsetDateTime::class.java, OffsetDateTimeTimestampSerializer())
        addDeserializer(OffsetDateTime::class.java, OffsetDateTimeTimestampDeserializer())
      }
    mapper.registerModule(module)
  }

  @Nested
  inner class LocalDateTimeTimestampTest {

    @Test
    fun `serialize LocalDateTime to timestamp`() {
      val dateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val json = mapper.writeValueAsString(dateTime)

      log.info("LocalDateTime serialized to: {}", json)

      // 验证序列化为数字时间戳
      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "序列化结果应该是数字时间戳")
      assertTrue(timestamp > 0, "时间戳应该大于0")
    }

    @Test
    fun `deserialize timestamp to LocalDateTime`() {
      val originalDateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val timestamp = originalDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()

      val deserializedDateTime = mapper.readValue(timestamp.toString(), LocalDateTime::class.java)

      log.info("Timestamp {} deserialized to LocalDateTime: {}", timestamp, deserializedDateTime)

      assertEquals(originalDateTime, deserializedDateTime)
    }

    @Test
    fun `round trip LocalDateTime serialization`() {
      val originalDateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45)

      val json = mapper.writeValueAsString(originalDateTime)
      val deserializedDateTime = mapper.readValue(json, LocalDateTime::class.java)

      log.info("Round trip: {} -> {} -> {}", originalDateTime, json, deserializedDateTime)

      assertEquals(originalDateTime, deserializedDateTime)
    }
  }

  @Nested
  inner class InstantTimestampTest {

    @Test
    fun `serialize Instant to timestamp`() {
      val instant = Instant.ofEpochMilli(1737000645000L) // 2025-01-16T04:30:45Z
      val json = mapper.writeValueAsString(instant)

      log.info("Instant serialized to: {}", json)

      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "序列化结果应该是数字时间戳")
      assertEquals(1737000645000L, timestamp)
    }

    @Test
    fun `deserialize timestamp to Instant`() {
      val originalTimestamp = 1737000645000L
      val originalInstant = Instant.ofEpochMilli(originalTimestamp)

      val deserializedInstant = mapper.readValue(originalTimestamp.toString(), Instant::class.java)

      log.info("Timestamp {} deserialized to Instant: {}", originalTimestamp, deserializedInstant)

      assertEquals(originalInstant, deserializedInstant)
    }

    @Test
    fun `round trip Instant serialization`() {
      // 使用毫秒精度的Instant避免精度丢失问题
      val originalInstant = Instant.ofEpochMilli(System.currentTimeMillis())

      val json = mapper.writeValueAsString(originalInstant)
      val deserializedInstant = mapper.readValue(json, Instant::class.java)

      log.info("Round trip: {} -> {} -> {}", originalInstant, json, deserializedInstant)

      assertEquals(originalInstant, deserializedInstant)
    }
  }

  @Nested
  inner class ZonedDateTimeTimestampTest {

    @Test
    fun `serialize ZonedDateTime to timestamp`() {
      val zonedDateTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Asia/Shanghai"))
      val json = mapper.writeValueAsString(zonedDateTime)

      log.info("ZonedDateTime serialized to: {}", json)

      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "序列化结果应该是数字时间戳")
      assertTrue(timestamp > 0, "时间戳应该大于0")
    }

    @Test
    fun `deserialize timestamp to ZonedDateTime`() {
      val originalZonedDateTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Asia/Shanghai"))
      val timestamp = originalZonedDateTime.toInstant().toEpochMilli()

      val deserializedZonedDateTime = mapper.readValue(timestamp.toString(), ZonedDateTime::class.java)

      log.info("Timestamp {} deserialized to ZonedDateTime: {}", timestamp, deserializedZonedDateTime)

      // 由于反序列化时使用UTC时区，需要比较Instant
      assertEquals(originalZonedDateTime.toInstant(), deserializedZonedDateTime.toInstant())
    }

    @Test
    fun `round trip ZonedDateTime serialization preserves instant`() {
      val originalZonedDateTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Asia/Shanghai"))

      val json = mapper.writeValueAsString(originalZonedDateTime)
      val deserializedZonedDateTime = mapper.readValue(json, ZonedDateTime::class.java)

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
      val json = mapper.writeValueAsString(offsetDateTime)

      log.info("OffsetDateTime serialized to: {}", json)

      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "序列化结果应该是数字时间戳")
      assertTrue(timestamp > 0, "时间戳应该大于0")
    }

    @Test
    fun `deserialize timestamp to OffsetDateTime`() {
      val originalOffsetDateTime = OffsetDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.ofHours(8))
      val timestamp = originalOffsetDateTime.toInstant().toEpochMilli()

      val deserializedOffsetDateTime = mapper.readValue(timestamp.toString(), OffsetDateTime::class.java)

      log.info("Timestamp {} deserialized to OffsetDateTime: {}", timestamp, deserializedOffsetDateTime)

      // 由于反序列化时使用UTC偏移，需要比较Instant
      assertEquals(originalOffsetDateTime.toInstant(), deserializedOffsetDateTime.toInstant())
    }

    @Test
    fun `round trip OffsetDateTime serialization preserves instant`() {
      val originalOffsetDateTime = OffsetDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.ofHours(8))

      val json = mapper.writeValueAsString(originalOffsetDateTime)
      val deserializedOffsetDateTime = mapper.readValue(json, OffsetDateTime::class.java)

      log.info("Round trip: {} -> {} -> {}", originalOffsetDateTime, json, deserializedOffsetDateTime)

      // 偏移量可能不同，但时间点应该相同
      assertEquals(originalOffsetDateTime.toInstant(), deserializedOffsetDateTime.toInstant())
    }
  }

  @Nested
  inner class TimezoneIndependenceTest {

    @Test
    fun `different timezones serialize to same timestamp`() {
      val utcTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.UTC)
      val shanghaiTime = ZonedDateTime.of(2025, 1, 16, 20, 30, 45, 0, ZoneId.of("Asia/Shanghai"))
      val newYorkTime = ZonedDateTime.of(2025, 1, 16, 7, 30, 45, 0, ZoneId.of("America/New_York"))

      val utcJson = mapper.writeValueAsString(utcTime)
      val shanghaiJson = mapper.writeValueAsString(shanghaiTime)
      val newYorkJson = mapper.writeValueAsString(newYorkTime)

      log.info("UTC: {} -> {}", utcTime, utcJson)
      log.info("Shanghai: {} -> {}", shanghaiTime, shanghaiJson)
      log.info("New York: {} -> {}", newYorkTime, newYorkJson)

      // 相同的时间点应该序列化为相同的时间戳
      assertEquals(utcJson, shanghaiJson, "相同时间点的不同时区应该序列化为相同时间戳")
      assertEquals(utcJson, newYorkJson, "相同时间点的不同时区应该序列化为相同时间戳")
    }
  }
}
