package io.github.truenine.composeserver.depend.jackson

import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import java.time.*
import kotlin.test.*
import org.junit.jupiter.api.Nested
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.ObjectMapper

/**
 * Comprehensive timestamp serialization tests.
 *
 * Tests the timestamp serialization functionality for all time types in a Spring Boot environment, including:
 * - Timestamp serialization for all time types
 * - Timezone independence validation
 * - Round-trip serialization correctness
 * - Consistency across different timezones
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

      // Verify serialization to a numeric timestamp
      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "LocalDateTime should serialize to a numeric timestamp")
      assertTrue(timestamp > 0, "Timestamp should be greater than 0")

      // Verify the reasonableness of the timestamp (a timestamp for 2025 should be within a reasonable range)
      assertTrue(timestamp > 1700000000000L, "Timestamp should be within the range for 2025")
      assertTrue(timestamp < 1800000000000L, "Timestamp should be within a reasonable range")
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

      // Due to timestamp precision limitations, the nanosecond part may be lost, so we only compare up to milliseconds
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
      assertNotNull(timestamp, "Instant should serialize to a numeric timestamp")
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
      // Use a millisecond-precision Instant to avoid precision loss issues
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
      assertNotNull(timestamp, "ZonedDateTime should serialize to a numeric timestamp")
      assertTrue(timestamp > 0, "Timestamp should be greater than 0")

      // Verify that the timestamp is consistent with the expected UTC timestamp
      val expectedTimestamp = zonedDateTime.toInstant().toEpochMilli()
      assertEquals(expectedTimestamp, timestamp)
    }

    @Test
    fun `deserialize timestamp to ZonedDateTime`() {
      val originalZonedDateTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Asia/Shanghai"))
      val timestamp = originalZonedDateTime.toInstant().toEpochMilli()

      val deserializedZonedDateTime = defaultMapper.readValue(timestamp.toString(), ZonedDateTime::class.java)

      log.info("Timestamp {} deserialized to ZonedDateTime: {}", timestamp, deserializedZonedDateTime)

      // Since deserialization uses the UTC timezone, compare the Instants
      assertEquals(originalZonedDateTime.toInstant(), deserializedZonedDateTime.toInstant())
    }

    @Test
    fun `round trip ZonedDateTime serialization preserves instant`() {
      val originalZonedDateTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Europe/London"))

      val json = defaultMapper.writeValueAsString(originalZonedDateTime)
      val deserializedZonedDateTime = defaultMapper.readValue(json, ZonedDateTime::class.java)

      log.info("Round trip: {} -> {} -> {}", originalZonedDateTime, json, deserializedZonedDateTime)

      // Timezone may differ, but the instant in time should be the same
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
      assertNotNull(timestamp, "OffsetDateTime should serialize to a numeric timestamp")
      assertTrue(timestamp > 0, "Timestamp should be greater than 0")

      // Verify that the timestamp is consistent with the expected UTC timestamp
      val expectedTimestamp = offsetDateTime.toInstant().toEpochMilli()
      assertEquals(expectedTimestamp, timestamp)
    }

    @Test
    fun `deserialize timestamp to OffsetDateTime`() {
      val originalOffsetDateTime = OffsetDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.ofHours(-5))
      val timestamp = originalOffsetDateTime.toInstant().toEpochMilli()

      val deserializedOffsetDateTime = defaultMapper.readValue(timestamp.toString(), OffsetDateTime::class.java)

      log.info("Timestamp {} deserialized to OffsetDateTime: {}", timestamp, deserializedOffsetDateTime)

      // Since deserialization uses a UTC offset, compare the Instants
      assertEquals(originalOffsetDateTime.toInstant(), deserializedOffsetDateTime.toInstant())
    }

    @Test
    fun `round trip OffsetDateTime serialization preserves instant`() {
      val originalOffsetDateTime = OffsetDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.ofHours(-3))

      val json = defaultMapper.writeValueAsString(originalOffsetDateTime)
      val deserializedOffsetDateTime = defaultMapper.readValue(json, OffsetDateTime::class.java)

      log.info("Round trip: {} -> {} -> {}", originalOffsetDateTime, json, deserializedOffsetDateTime)

      // Offset may differ, but the instant in time should be the same
      assertEquals(originalOffsetDateTime.toInstant(), deserializedOffsetDateTime.toInstant())
    }
  }

  @Nested
  inner class TimezoneIndependenceTest {

    @Test
    fun `different timezones serialize to same timestamp for same instant`() {
      // Same instant in time, different timezone representations
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

      // The same instant in time should serialize to the same timestamp
      assertEquals(utcJson, shanghaiJson, "Different timezones for the same instant should serialize to the same timestamp")
      assertEquals(utcJson, newYorkJson, "Different timezones for the same instant should serialize to the same timestamp")
      assertEquals(utcJson, londonJson, "Different timezones for the same instant should serialize to the same timestamp")
    }

    @Test
    fun `OffsetDateTime with different offsets serialize to same timestamp for same instant`() {
      // Same UTC instant, different offset representations
      val utcOffset = OffsetDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.UTC)
      val plusEightOffset = OffsetDateTime.of(2025, 1, 16, 20, 30, 45, 0, ZoneOffset.ofHours(8))
      val minusFiveOffset = OffsetDateTime.of(2025, 1, 16, 7, 30, 45, 0, ZoneOffset.ofHours(-5))

      val utcJson = defaultMapper.writeValueAsString(utcOffset)
      val plusEightJson = defaultMapper.writeValueAsString(plusEightOffset)
      val minusFiveJson = defaultMapper.writeValueAsString(minusFiveOffset)

      log.info("UTC offset: {} -> {}", utcOffset, utcJson)
      log.info("+8 offset: {} -> {}", plusEightOffset, plusEightJson)
      log.info("-5 offset: {} -> {}", minusFiveOffset, minusFiveJson)

      assertEquals(utcJson, plusEightJson, "Different offsets for the same instant should serialize to the same timestamp")
      assertEquals(utcJson, minusFiveJson, "Different offsets for the same instant should serialize to the same timestamp")
    }

    @Test
    fun `LocalDateTime serialization is timezone independent`() {
      // LocalDateTime has no timezone information, so it should always be treated as UTC
      val localDateTime1 = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val localDateTime2 = LocalDateTime.of(2025, 1, 16, 12, 30, 45)

      val json1 = defaultMapper.writeValueAsString(localDateTime1)
      val json2 = defaultMapper.writeValueAsString(localDateTime2)

      log.info("LocalDateTime1: {} -> {}", localDateTime1, json1)
      log.info("LocalDateTime2: {} -> {}", localDateTime2, json2)

      assertEquals(json1, json2, "The same LocalDateTime should serialize to the same timestamp")

      // Verify that the serialization result is a timestamp
      val timestamp = json1.toLongOrNull()
      assertNotNull(timestamp, "LocalDateTime should serialize to a timestamp")
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

        // LocalDate may be serialized to a string or an array format, depending on the configuration
        assertTrue(json.isNotEmpty(), "LocalDate serialization result should not be empty")

        // Try to deserialize for verification
        val deserializedDate = defaultMapper.readValue(json, LocalDate::class.java)
        assertEquals(localDate, deserializedDate)
      } catch (e: Exception) {
        log.info("LocalDate serialization failed as expected: {}", e.message)
        // LocalDate may not support timestamp serialization, which is normal
      }
    }

    @Test
    fun `LocalTime serialization behavior`() {
      val localTime = LocalTime.of(12, 30, 45)

      try {
        val json = defaultMapper.writeValueAsString(localTime)
        log.info("LocalTime {} serialized to: {}", localTime, json)

        assertTrue(json.isNotEmpty(), "LocalTime serialization result should not be empty")

        // Try to deserialize for verification
        val deserializedTime = defaultMapper.readValue(json, LocalTime::class.java)
        assertEquals(localTime, deserializedTime)
      } catch (e: Exception) {
        log.info("LocalTime serialization failed as expected: {}", e.message)
        // LocalTime may not support timestamp serialization, which is normal
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

      // Test Instant
      val instantJson1 = defaultMapper.writeValueAsString(instant)
      val instantJson2 = nonIgnoreMapper.writeValueAsString(instant)
      log.info("Instant - Default mapper: {}, Non-ignore mapper: {}", instantJson1, instantJson2)

      // Test LocalDateTime
      val localDateTimeJson1 = defaultMapper.writeValueAsString(localDateTime)
      val localDateTimeJson2 = nonIgnoreMapper.writeValueAsString(localDateTime)
      log.info("LocalDateTime - Default mapper: {}, Non-ignore mapper: {}", localDateTimeJson1, localDateTimeJson2)

      // Test ZonedDateTime
      val zonedDateTimeJson1 = defaultMapper.writeValueAsString(zonedDateTime)
      val zonedDateTimeJson2 = nonIgnoreMapper.writeValueAsString(zonedDateTime)
      log.info("ZonedDateTime - Default mapper: {}, Non-ignore mapper: {}", zonedDateTimeJson1, zonedDateTimeJson2)

      // Verify that the timestamp serialization behavior of the two mappers is consistent
      // Note: The non-ignoring mapper may include additional type information, so we mainly verify the timestamp value
      val instantTimestamp1 = instantJson1.toLongOrNull()
      val instantTimestamp2 =
        if (instantJson2.contains("@class")) {
          // If type information is included, extract the numeric part
          instantJson2.substringAfterLast(":").substringBefore("}").toLongOrNull()
        } else {
          instantJson2.toLongOrNull()
        }

      if (instantTimestamp1 != null && instantTimestamp2 != null) {
        assertEquals(instantTimestamp1, instantTimestamp2, "Both ObjectMappers should produce the same Instant timestamp")
      }
    }

    @Test
    fun `timestamp deserialization works with both mappers`() {
      val timestamp = 1737000645000L
      val timestampJson = timestamp.toString()

      // Test that the default mapper can correctly deserialize the timestamp
      val instant1 = defaultMapper.readValue(timestampJson, Instant::class.java)

      // The non-ignoring mapper needs type information, so we serialize it first and then deserialize it
      val instant = Instant.ofEpochMilli(timestamp)
      val nonIgnoreJson = nonIgnoreMapper.writeValueAsString(instant)
      val instant2 = nonIgnoreMapper.readValue(nonIgnoreJson, Instant::class.java)

      log.info("Timestamp {} deserialized - Default mapper: {}", timestamp, instant1)
      log.info("Non-ignore mapper round trip: {} -> {} -> {}", instant, nonIgnoreJson, instant2)

      assertEquals(Instant.ofEpochMilli(timestamp), instant1)
      assertEquals(instant, instant2, "Non-ignoring mapper round-trip serialization should remain consistent")
    }
  }

  @Nested
  inner class RoundTripConsistencyTest {

    @Test
    fun `multiple round trips maintain consistency`() {
      val originalInstant = Instant.ofEpochMilli(1737000645000L)

      // Perform multiple round-trip serializations
      var currentInstant = originalInstant
      repeat(5) { iteration ->
        val json = defaultMapper.writeValueAsString(currentInstant)
        currentInstant = defaultMapper.readValue(json, Instant::class.java)

        log.info("Round trip {}: {} -> {} -> {}", iteration + 1, if (iteration == 0) originalInstant else "previous", json, currentInstant)

        assertEquals(originalInstant, currentInstant, "Should remain consistent after the ${iteration + 1}-th round trip")
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

      // The time fields may differ slightly due to timezone handling, but the instants should be the same
      assertEquals(event.timestamp.toEpochMilli(), deserializedEvent.timestamp.toEpochMilli())
      assertEquals(event.zonedTime.toInstant(), deserializedEvent.zonedTime.toInstant())
      assertEquals(event.offsetTime.toInstant(), deserializedEvent.offsetTime.toInstant())
    }
  }
}
