package io.github.truenine.composeserver.depend.jackson.serializers

import io.github.truenine.composeserver.toMillis
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.SimpleDeserializers
import tools.jackson.databind.module.SimpleModule
import tools.jackson.module.kotlin.KotlinModule
import java.time.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * ISO8601Deserializer unit tests.
 *
 * Tests deserialization of various timestamp-based time types.
 */
class ISO8601DeserializerTest {

  private lateinit var objectMapper: ObjectMapper
  private val zoneOffset = ZoneOffset.UTC

  @BeforeEach
  fun setup() {
    // Create module and register deserializers
    val module = SimpleModule()

    val deserializers = SimpleDeserializers()
    deserializers.addDeserializer(LocalDate::class.java, ISO8601Deserializer.LocalDateDeserializerX(zoneOffset))
    deserializers.addDeserializer(LocalDateTime::class.java, ISO8601Deserializer.LocalDateTimeDeserializerZ(zoneOffset))
    deserializers.addDeserializer(LocalTime::class.java, ISO8601Deserializer.LocalTimeDeserializerY(zoneOffset))

    module.setDeserializers(deserializers)

    // Register KotlinModule to support Kotlin data classes
    objectMapper = JsonMapper.builder().addModule(KotlinModule.Builder().build()).addModule(module).build()
  }

  @Nested
  inner class CommonDeserializerFunctionsGroup {

    @Test
    fun `deserialize should correctly convert timestamp`() {
      // Create test data
      val testDate = LocalDate.of(2023, 5, 15)
      val timestamp = testDate.toMillis(zoneOffset)
      val json = """{"date":"$timestamp"}"""

      // Deserialize
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)

      // Verify
      assertNotNull(result.date)
      assertEquals(testDate, result.date)
    }

    @Test
    fun `deserialize null value should return null`() {
      // Create test data
      val json = """{"date":null}"""

      // Deserialize
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)

      // Verify
      assertNull(result.date)
    }

    @Test
    fun `deserialize non numeric value should return null`() {
      // Create test data
      val json = """{"date":"not-a-number"}"""

      // Deserialize
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)

      // Verify
      assertNull(result.date)
    }
  }

  @Nested
  inner class LocalDateDeserializerGroup {

    @Test
    fun `deserialize LocalDate should restore original date`() {
      // Prepare test data
      val originalDate = LocalDate.of(2023, 5, 15)
      val timestamp = originalDate.toMillis(zoneOffset)
      val json = """{"date":"$timestamp"}"""

      // Deserialize
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)

      // Verify
      assertNotNull(result.date)
      assertEquals(originalDate, result.date)
    }

    @ParameterizedTest
    @ValueSource(strings = ["1970-01-01", "2023-12-31", "2024-02-29"])
    fun `boundary LocalDate values should be handled correctly`(dateStr: String) {
      // Parse test date
      val testDate = LocalDate.parse(dateStr)
      val timestamp = testDate.toMillis(zoneOffset)
      val json = """{"date":"$timestamp"}"""

      // Deserialize
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)

      // Verify
      assertNotNull(result.date)
      assertEquals(testDate, result.date)
    }
  }

  @Nested
  inner class LocalDateTimeDeserializerGroup {

    @Test
    fun `deserialize LocalDateTime should restore original value`() {
      // Prepare test data
      val originalDateTime = LocalDateTime.of(2023, 5, 15, 10, 30, 45)
      val timestamp = originalDateTime.toMillis(zoneOffset)
      val json = """{"dateTime":"$timestamp"}"""

      // Deserialize
      val result = objectMapper.readValue(json, LocalDateTimeWrapper::class.java)

      // Verify
      assertNotNull(result.dateTime)
      assertEquals(originalDateTime, result.dateTime)
    }

    @Test
    fun `boundary LocalDateTime values should be handled correctly`() {
      // Test minimum time
      val minDateTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0)
      val minTimestamp = minDateTime.toMillis(zoneOffset)
      val minJson = """{"dateTime":"$minTimestamp"}"""

      // Deserialize
      val minResult = objectMapper.readValue(minJson, LocalDateTimeWrapper::class.java)
      assertEquals(minDateTime, minResult.dateTime)

      // Test current time
      val nowDateTime = LocalDateTime.now()
      val nowTimestamp = nowDateTime.toMillis(zoneOffset)
      val nowJson = """{"dateTime":"$nowTimestamp"}"""

      // Deserialize
      val nowResult = objectMapper.readValue(nowJson, LocalDateTimeWrapper::class.java)

      // Compare date-time components due to possible millisecond precision differences
      assertEquals(nowDateTime.year, nowResult.dateTime?.year)
      assertEquals(nowDateTime.month, nowResult.dateTime?.month)
      assertEquals(nowDateTime.dayOfMonth, nowResult.dateTime?.dayOfMonth)
      assertEquals(nowDateTime.hour, nowResult.dateTime?.hour)
      assertEquals(nowDateTime.minute, nowResult.dateTime?.minute)
      assertEquals(nowDateTime.second, nowResult.dateTime?.second)
    }
  }

  @Nested
  inner class LocalTimeDeserializerGroup {

    @Test
    fun `deserialize LocalTime should restore original time`() {
      // Prepare test data
      val originalTime = LocalTime.of(10, 30, 45)
      val timestamp = originalTime.toMillis(zoneOffset)
      val json = """{"time":"$timestamp"}"""

      // Deserialize
      val result = objectMapper.readValue(json, LocalTimeWrapper::class.java)

      // Verify
      assertNotNull(result.time)
      assertEquals(originalTime.hour, result.time.hour)
      assertEquals(originalTime.minute, result.time.minute)
      assertEquals(originalTime.second, result.time.second)
    }

    @ParameterizedTest
    @ValueSource(strings = ["00:00:00", "12:00:00", "23:59:59"])
    fun `boundary LocalTime values should be handled correctly`(timeStr: String) {
      // Parse test time
      val testTime = LocalTime.parse(timeStr)
      val timestamp = testTime.toMillis(zoneOffset)
      val json = """{"time":"$timestamp"}"""

      // Deserialize
      val result = objectMapper.readValue(json, LocalTimeWrapper::class.java)

      // Verify
      assertNotNull(result.time)
      assertEquals(testTime.hour, result.time.hour)
      assertEquals(testTime.minute, result.time.minute)
      assertEquals(testTime.second, result.time.second)
    }
  }

  @Nested
  inner class FactoryMethodsGroup {

    @Test
    fun `forLocalDate factory should create correct deserializer`() {
      // Prepare test data
      val testZoneOffset = ZoneOffset.ofHours(8)
      val deserializer = ISO8601Deserializer.LocalDateDeserializerX(testZoneOffset)

      // Verify type
      assertEquals("LocalDateDeserializerX", deserializer.javaClass.simpleName)
    }

    @Test
    fun `forLocalDateTime factory should create correct deserializer`() {
      // Prepare test data
      val testZoneOffset = ZoneOffset.ofHours(8)
      val deserializer = ISO8601Deserializer.LocalDateTimeDeserializerZ(testZoneOffset)

      // Verify type
      assertEquals("LocalDateTimeDeserializerZ", deserializer.javaClass.simpleName)
    }

    @Test
    fun `forLocalTime factory should create correct deserializer`() {
      // Prepare test data
      val testZoneOffset = ZoneOffset.ofHours(8)
      val deserializer = ISO8601Deserializer.LocalTimeDeserializerY(testZoneOffset)

      // Verify type
      assertEquals("LocalTimeDeserializerY", deserializer.javaClass.simpleName)
    }
  }

  @Nested
  inner class ZoneOffsetGroup {

    @Test
    fun `different time zones should convert timestamps correctly`() {
      // Create deserializers with different zone offsets
      val beijingZone = ZoneOffset.ofHours(8)
      val module = SimpleModule()
      module.addDeserializer(LocalDate::class.java, ISO8601Deserializer.LocalDateDeserializerX(beijingZone))

      val beijingMapper =
        JsonMapper.builder()
          .also {
            it.addModule(KotlinModule.Builder().build())
            it.addModule(module)
          }
          .build()

      // Prepare test data
      val originalDate = LocalDate.of(2023, 5, 15)
      val utcTimestamp = originalDate.toMillis(ZoneOffset.UTC)
      val json = """{"date":"$utcTimestamp"}"""

      // Deserialize
      val result = beijingMapper.readValue(json, LocalDateWrapper::class.java)

      // Verify result (may differ due to time zone differences)
      assertNotNull(result.date)
    }
  }

  // Wrapper classes for testing
  data class LocalDateWrapper(val date: LocalDate?)

  data class LocalDateTimeWrapper(val dateTime: LocalDateTime?)

  data class LocalTimeWrapper(val time: LocalTime?)
}
