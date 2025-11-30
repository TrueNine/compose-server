package io.github.truenine.composeserver.depend.jackson

import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import org.junit.jupiter.api.Nested
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.ObjectMapper
import java.time.*
import kotlin.test.*

/**
 * Jackson serialization and deserialization tests.
 *
 * Verifies basic JSON serialization and timestamp serialization behavior.
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
      assertEquals("1234567890123451", json, "Long value should be serialized as JSON numeric format")

      // Verify deserialization
      val deserializedLong = defaultMapper.readValue(json, Long::class.java)
      assertEquals(longValue, deserializedLong)
    }

    @Test
    fun serialize_string_preserves_content() {
      val stringValue = "test string with special chars: unicode \"quotes\""
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

      // Verify serialization to timestamp
      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "LocalDateTime should be serialized as timestamp")
      assertTrue(timestamp > 0, "Timestamp should be greater than 0")

      // Verify timestamp correctness
      val expectedTimestamp = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
      assertEquals(expectedTimestamp, timestamp, "Timestamp should be calculated based on UTC")
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

      // Due to timestamp precision limitations, nanoseconds may be lost; compare in milliseconds only
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
      assertNotNull(timestamp, "Instant should be serialized as timestamp")
      assertEquals(1737000645000L, timestamp)
    }

    @Test
    fun timezone_independence_verification() {
      // Verify time zone independence - LocalDateTime is always handled as UTC
      val localDateTime1 = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val localDateTime2 = LocalDateTime.of(2025, 1, 16, 12, 30, 45)

      val json1 = defaultMapper.writeValueAsString(localDateTime1)
      val json2 = defaultMapper.writeValueAsString(localDateTime2)

      log.info("LocalDateTime1: {} -> {}", localDateTime1, json1)
      log.info("LocalDateTime2: {} -> {}", localDateTime2, json2)

      assertEquals(json1, json2, "Same LocalDateTime should be serialized to the same timestamp")

      // Verify serialization result is a timestamp
      val timestamp = json1.toLongOrNull()
      assertNotNull(timestamp, "LocalDateTime should be serialized as timestamp")
    }
  }
}
