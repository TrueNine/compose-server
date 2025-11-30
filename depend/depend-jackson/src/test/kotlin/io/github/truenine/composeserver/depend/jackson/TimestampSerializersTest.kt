package io.github.truenine.composeserver.depend.jackson

import io.github.truenine.composeserver.depend.jackson.serializers.*
import io.github.truenine.composeserver.testtoolkit.log
import java.time.*
import kotlin.test.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.*

/**
 * Timestamp serializer tests
 *
 * Tests timestamp serialization and deserialization for all time types.
 */
class TimestampSerializersTest {

  private lateinit var mapper: ObjectMapper

  @BeforeEach
  fun setup() {
    val module = SimpleModule()

    val serializers = SimpleSerializers()
    serializers.addSerializer(LocalDateTime::class.java, LocalDateTimeTimestampSerializer())
    serializers.addSerializer(Instant::class.java, InstantTimestampSerializer())
    serializers.addSerializer(ZonedDateTime::class.java, ZonedDateTimeTimestampSerializer())
    serializers.addSerializer(OffsetDateTime::class.java, OffsetDateTimeTimestampSerializer())

    val deserializers = SimpleDeserializers()
    deserializers.addDeserializer(LocalDateTime::class.java, LocalDateTimeTimestampDeserializer())
    deserializers.addDeserializer(Instant::class.java, InstantTimestampDeserializer())
    deserializers.addDeserializer(ZonedDateTime::class.java, ZonedDateTimeTimestampDeserializer())
    deserializers.addDeserializer(OffsetDateTime::class.java, OffsetDateTimeTimestampDeserializer())

    module.setSerializers(serializers)
    module.setDeserializers(deserializers)

    mapper = JsonMapper.builder().addModule(module).build()
  }

  @Nested
  inner class LocalDateTimeTimestampTest {

    @Test
    fun `serialize LocalDateTime to timestamp`() {
      val dateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val json = mapper.writeValueAsString(dateTime)

      log.info("LocalDateTime serialized to: {}", json)

      // Verify serialization to a numeric timestamp
      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "Serialization result should be a numeric timestamp")
      assertTrue(timestamp > 0, "Timestamp should be greater than 0")
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
      assertNotNull(timestamp, "Serialization result should be a numeric timestamp")
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
      // Use a millisecond-precision Instant to avoid precision loss issues
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
      assertNotNull(timestamp, "Serialization result should be a numeric timestamp")
      assertTrue(timestamp > 0, "Timestamp should be greater than 0")
    }

    @Test
    fun `deserialize timestamp to ZonedDateTime`() {
      val originalZonedDateTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Asia/Shanghai"))
      val timestamp = originalZonedDateTime.toInstant().toEpochMilli()

      val deserializedZonedDateTime = mapper.readValue(timestamp.toString(), ZonedDateTime::class.java)

      log.info("Timestamp {} deserialized to ZonedDateTime: {}", timestamp, deserializedZonedDateTime)

      // Since deserialization uses the UTC timezone, compare the Instants
      assertEquals(originalZonedDateTime.toInstant(), deserializedZonedDateTime.toInstant())
    }

    @Test
    fun `round trip ZonedDateTime serialization preserves instant`() {
      val originalZonedDateTime = ZonedDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneId.of("Asia/Shanghai"))

      val json = mapper.writeValueAsString(originalZonedDateTime)
      val deserializedZonedDateTime = mapper.readValue(json, ZonedDateTime::class.java)

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
      val json = mapper.writeValueAsString(offsetDateTime)

      log.info("OffsetDateTime serialized to: {}", json)

      val timestamp = json.toLongOrNull()
      assertNotNull(timestamp, "Serialization result should be a numeric timestamp")
      assertTrue(timestamp > 0, "Timestamp should be greater than 0")
    }

    @Test
    fun `deserialize timestamp to OffsetDateTime`() {
      val originalOffsetDateTime = OffsetDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.ofHours(8))
      val timestamp = originalOffsetDateTime.toInstant().toEpochMilli()

      val deserializedOffsetDateTime = mapper.readValue(timestamp.toString(), OffsetDateTime::class.java)

      log.info("Timestamp {} deserialized to OffsetDateTime: {}", timestamp, deserializedOffsetDateTime)

      // Since deserialization uses a UTC offset, compare the Instants
      assertEquals(originalOffsetDateTime.toInstant(), deserializedOffsetDateTime.toInstant())
    }

    @Test
    fun `round trip OffsetDateTime serialization preserves instant`() {
      val originalOffsetDateTime = OffsetDateTime.of(2025, 1, 16, 12, 30, 45, 0, ZoneOffset.ofHours(8))

      val json = mapper.writeValueAsString(originalOffsetDateTime)
      val deserializedOffsetDateTime = mapper.readValue(json, OffsetDateTime::class.java)

      log.info("Round trip: {} -> {} -> {}", originalOffsetDateTime, json, deserializedOffsetDateTime)

      // Offset may differ, but the instant in time should be the same
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

      // The same instant in time should serialize to the same timestamp
      assertEquals(utcJson, shanghaiJson, "Different timezones for the same instant should serialize to the same timestamp")
      assertEquals(utcJson, newYorkJson, "Different timezones for the same instant should serialize to the same timestamp")
    }
  }
}
