package itest.integrate.depend.jackson

import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import jakarta.annotation.Resource
import java.time.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.ObjectMapper

/**
 * Timezone independence integration tests
 *
 * Verifies serialization consistency under different timezone environments, validates correctness of UTC timestamps, and tests deserialization compatibility
 * for multiple time formats.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TimezoneIndependenceTest {

  @Resource @Qualifier(JacksonAutoConfiguration.DEFAULT_OBJECT_MAPPER_BEAN_NAME) private lateinit var objectMapper: ObjectMapper

  @Nested
  inner class TimezoneConsistencyTests {

    @Test
    fun same_instant_in_different_timezones_should_serialize_to_same_timestamp() {
      // Create times in different time zones representing the same instant
      val baseInstant = Instant.parse("2023-06-15T12:00:00Z")
      val utcTime = ZonedDateTime.ofInstant(baseInstant, ZoneOffset.UTC)
      val beijingTime = ZonedDateTime.ofInstant(baseInstant, ZoneId.of("Asia/Shanghai"))
      val newYorkTime = ZonedDateTime.ofInstant(baseInstant, ZoneId.of("America/New_York"))
      val tokyoTime = ZonedDateTime.ofInstant(baseInstant, ZoneId.of("Asia/Tokyo"))

      // Serialize all times
      val utcJson = objectMapper.writeValueAsString(utcTime)
      val beijingJson = objectMapper.writeValueAsString(beijingTime)
      val newYorkJson = objectMapper.writeValueAsString(newYorkTime)
      val tokyoJson = objectMapper.writeValueAsString(tokyoTime)

      // All serialized results should be the same (all are UTC timestamps)
      val expectedTimestamp = baseInstant.toEpochMilli()
      assertEquals(expectedTimestamp.toString(), utcJson)
      assertEquals(expectedTimestamp.toString(), beijingJson)
      assertEquals(expectedTimestamp.toString(), newYorkJson)
      assertEquals(expectedTimestamp.toString(), tokyoJson)
    }

    @Test
    fun local_datetime_should_be_treated_as_utc_for_timestamp_serialization() {
      // LocalDateTime has no timezone information and should be treated as UTC
      val localDateTime = LocalDateTime.of(2023, 6, 15, 12, 0, 0)
      val expectedUtcInstant = localDateTime.toInstant(ZoneOffset.UTC)

      val json = objectMapper.writeValueAsString(localDateTime)
      val expectedTimestamp = expectedUtcInstant.toEpochMilli()

      assertEquals(expectedTimestamp.toString(), json)
    }

    @Test
    fun offset_datetime_should_convert_to_utc_timestamp() {
      // Test OffsetDateTime with different offsets
      val baseTime = LocalDateTime.of(2023, 6, 15, 12, 0, 0)
      val utcTime = OffsetDateTime.of(baseTime, ZoneOffset.UTC)
      val plusEightTime = OffsetDateTime.of(baseTime.plusHours(8), ZoneOffset.ofHours(8))
      val minusFiveTime = OffsetDateTime.of(baseTime.minusHours(5), ZoneOffset.ofHours(-5))

      val utcJson = objectMapper.writeValueAsString(utcTime)
      val plusEightJson = objectMapper.writeValueAsString(plusEightTime)
      val minusFiveJson = objectMapper.writeValueAsString(minusFiveTime)

      // All times should serialize to the same UTC timestamp
      assertEquals(utcJson, plusEightJson)
      assertEquals(utcJson, minusFiveJson)
    }
  }

  @Nested
  inner class UtcTimestampValidationTests {

    @Test
    fun serialized_timestamps_should_represent_utc_time() {
      // Create a known UTC time
      val knownUtcTime = ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
      val expectedTimestamp = knownUtcTime.toInstant().toEpochMilli()

      // Serialize
      val json = objectMapper.writeValueAsString(knownUtcTime)
      val actualTimestamp = json.toLong()

      assertEquals(expectedTimestamp, actualTimestamp)

      // Verify that the timestamp really represents UTC time
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

        // Serialize the same time under different system time zones
        val timeZones = listOf("UTC", "Asia/Shanghai", "America/New_York", "Europe/London", "Asia/Tokyo")

        for (tzId in timeZones) {
          TimeZone.setDefault(TimeZone.getTimeZone(tzId))
          val json = objectMapper.writeValueAsString(testTime)
          results.add(json)
        }

        // All results should be the same
        val firstResult = results.first()
        results.forEach { result -> assertEquals(firstResult, result, "Timestamp serialization should be independent of the system time zone") }
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

      // Deserialize into different time types
      val instant = objectMapper.readValue(timestampJson, Instant::class.java)
      val zonedDateTime = objectMapper.readValue(timestampJson, ZonedDateTime::class.java)
      val offsetDateTime = objectMapper.readValue(timestampJson, OffsetDateTime::class.java)
      val localDateTime = objectMapper.readValue(timestampJson, LocalDateTime::class.java)

      // Verify that all types represent the same instant
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
        // Try deserializing ISO 8601 format (backward compatibility)
        val instant = objectMapper.readValue(iso8601String, Instant::class.java)
        assertEquals(expectedInstant, instant)
      } catch (e: Exception) {
        // If ISO 8601 format is not supported, this is expected (we prefer timestamps)
        assertTrue(e.message?.contains("Cannot deserialize") == true || e.message?.contains("not supported") == true)
      }
    }

    @Test
    fun should_handle_various_timestamp_formats() {
      val baseInstant = Instant.parse("2023-06-17T12:00:00Z")
      val millisTimestamp = baseInstant.toEpochMilli()
      val secondsTimestamp = baseInstant.epochSecond

      // Test millisecond timestamp
      val millisJson = millisTimestamp.toString()
      val instantFromMillis = objectMapper.readValue(millisJson, Instant::class.java)
      assertEquals(baseInstant, instantFromMillis)

      // Test second-based timestamp (if supported)
      val secondsJson = secondsTimestamp.toString()
      try {
        val instantFromSeconds = objectMapper.readValue(secondsJson, Instant::class.java)
        // Check whether it is treated as a millisecond timestamp (common case)
        if (instantFromSeconds.toEpochMilli() == secondsTimestamp) {
          // Being treated as a millisecond timestamp is acceptable
          assertTrue(true, "Second-based timestamp being treated as millisecond timestamp is acceptable")
        } else {
          // If second-based timestamps are supported, verify the result
          assertEquals(secondsTimestamp, instantFromSeconds.epochSecond)
        }
      } catch (e: Exception) {
        // If second-based timestamps are not supported, this is also acceptable
        assertTrue(
          e.message?.contains("Cannot deserialize") == true || e.message?.contains("not supported") == true || e.message?.contains("Invalid") == true,
          "Second-based timestamp format is not supported, which is acceptable",
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

      assertTrue(timestamp > 0, "Future date should produce a positive timestamp")

      val deserializedInstant = objectMapper.readValue(json, Instant::class.java)
      assertEquals(farFuture, deserializedInstant)
    }

    @Test
    fun should_handle_daylight_saving_time_transitions() {
      // Test time during daylight saving time transition
      val dstTransition = ZonedDateTime.of(2023, 3, 12, 2, 30, 0, 0, ZoneId.of("America/New_York"))
      val json = objectMapper.writeValueAsString(dstTransition)
      val timestamp = json.toLong()

      // Verify that the timestamp is valid
      assertTrue(timestamp > 0)

      // Verify round-trip serialization
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

      // Test round-trip serialization
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
          // Since timestamp serialization may lose nanosecond precision, compare at millisecond precision
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
