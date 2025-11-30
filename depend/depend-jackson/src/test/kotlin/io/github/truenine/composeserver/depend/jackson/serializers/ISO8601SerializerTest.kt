package io.github.truenine.composeserver.depend.jackson.serializers

import io.github.truenine.composeserver.toMillis
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.*
import tools.jackson.module.kotlin.KotlinModule
import java.time.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ISO8601SerializerTest {

  private lateinit var objectMapper: ObjectMapper
  private val zoneOffset = ZoneOffset.UTC

  @BeforeEach
  fun setup() {
    // Create module and register serializers and deserializers
    val module = SimpleModule()

    val serializers = SimpleSerializers()
    serializers.addSerializer(LocalDate::class.java, ISO8601Serializer.ISO8601DateSerializer(zoneOffset))
    serializers.addSerializer(LocalDateTime::class.java, ISO8601Serializer.ISO8601DateTimeSerializer(zoneOffset))
    serializers.addSerializer(LocalTime::class.java, ISO8601Serializer.ISO8601TimeSerializer(zoneOffset))

    val deserializers = SimpleDeserializers()
    deserializers.addDeserializer(LocalDate::class.java, ISO8601Deserializer.LocalDateDeserializerX(zoneOffset))
    deserializers.addDeserializer(LocalDateTime::class.java, ISO8601Deserializer.LocalDateTimeDeserializerZ(zoneOffset))
    deserializers.addDeserializer(LocalTime::class.java, ISO8601Deserializer.LocalTimeDeserializerY(zoneOffset))

    module.setSerializers(serializers)
    module.setDeserializers(deserializers)

    // Register KotlinModule to support Kotlin data classes
    objectMapper = JsonMapper.builder().addModule(KotlinModule.Builder().build()).addModule(module).build()
  }

  @Nested
  inner class ISO8601DateSerializerGroup {

    @Test
    fun `serialize LocalDate should return matching timestamp`() {
      // Create test data
      val testDate = LocalDate.of(2023, 5, 15)
      val testData = LocalDateWrapper(testDate)

      // Serialize
      val json = objectMapper.writeValueAsString(testData)

      // Verify result is numeric
      val expectedTimestamp = testDate.toMillis(zoneOffset)
      val expectedJson = """{"date":$expectedTimestamp}"""
      assertEquals(expectedJson, json)
    }

    @Test
    fun `deserialize LocalDate should restore original date`() {
      // Prepare test data
      val originalDate = LocalDate.of(2023, 5, 15)
      val timestamp = originalDate.toMillis(zoneOffset)
      val json = """{"date":$timestamp}"""

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
      val testData = LocalDateWrapper(testDate)

      // Serialize
      val json = objectMapper.writeValueAsString(testData)

      // Deserialize and verify
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)
      assertEquals(testDate, result.date)
    }
  }

  @Nested
  inner class ISO8601DateTimeSerializerGroup {

    @Test
    fun `serialize LocalDateTime should return matching timestamp`() {
      // Create test data
      val testDateTime = LocalDateTime.of(2023, 5, 15, 10, 30, 45)
      val testData = LocalDateTimeWrapper(testDateTime)

      // Serialize
      val json = objectMapper.writeValueAsString(testData)

      // Verify result is numeric
      val expectedTimestamp = testDateTime.toMillis(zoneOffset)
      val expectedJson = """{"dateTime":$expectedTimestamp}"""
      assertEquals(expectedJson, json)
    }

    @Test
    fun `deserialize LocalDateTime should restore original value`() {
      // Prepare test data
      val originalDateTime = LocalDateTime.of(2023, 5, 15, 10, 30, 45)
      val timestamp = originalDateTime.toMillis(zoneOffset)
      val json = """{"dateTime":$timestamp}"""

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
      val minTestData = LocalDateTimeWrapper(minDateTime)

      // Serialize
      val minJson = objectMapper.writeValueAsString(minTestData)

      // Deserialize and verify
      val minResult = objectMapper.readValue(minJson, LocalDateTimeWrapper::class.java)
      assertEquals(minDateTime, minResult.dateTime)

      // Test current time
      val nowDateTime = LocalDateTime.now()
      val nowTestData = LocalDateTimeWrapper(nowDateTime)

      // Serialize
      val nowJson = objectMapper.writeValueAsString(nowTestData)

      // Deserialize and verify
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
  inner class ISO8601TimeSerializerGroup {

    @Test
    fun `serialize LocalTime should return matching timestamp`() {
      // Create test data
      val testTime = LocalTime.of(10, 30, 45)
      val testData = LocalTimeWrapper(testTime)

      // Serialize
      val json = objectMapper.writeValueAsString(testData)

      // Verify result is numeric
      val expectedTimestamp = testTime.toMillis(zoneOffset)
      val expectedJson = """{"time":$expectedTimestamp}"""
      assertEquals(expectedJson, json)
    }

    @Test
    fun `deserialize LocalTime should restore original time`() {
      // Prepare test data
      val originalTime = LocalTime.of(10, 30, 45)
      val timestamp = originalTime.toMillis(zoneOffset)
      val json = """{"time":$timestamp}"""

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
      val testData = LocalTimeWrapper(testTime)

      // Serialize
      val json = objectMapper.writeValueAsString(testData)

      // Deserialize and verify
      val result = objectMapper.readValue(json, LocalTimeWrapper::class.java)
      assertNotNull(result.time)
      assertEquals(testTime.hour, result.time.hour)
      assertEquals(testTime.minute, result.time.minute)
      assertEquals(testTime.second, result.time.second)
    }
  }

  @Nested
  inner class SerializeWithTypeGroup {

    @Test
    fun `serialize and deserialize correctly with TypeInfo`() {
      val polymorphicMapper =
        JsonMapper.builder()
          .also {
            // Create and register serializer and deserializer modules
            val serializerModule = SimpleModule()
            serializerModule.addSerializer(LocalDate::class.java, ISO8601Serializer.ISO8601DateSerializer(zoneOffset))
            serializerModule.addSerializer(LocalDateTime::class.java, ISO8601Serializer.ISO8601DateTimeSerializer(zoneOffset))
            serializerModule.addSerializer(LocalTime::class.java, ISO8601Serializer.ISO8601TimeSerializer(zoneOffset))

            val deserializerModule = SimpleModule()
            deserializerModule.addDeserializer(LocalDate::class.java, ISO8601Deserializer.LocalDateDeserializerX(zoneOffset))
            deserializerModule.addDeserializer(LocalDateTime::class.java, ISO8601Deserializer.LocalDateTimeDeserializerZ(zoneOffset))
            deserializerModule.addDeserializer(LocalTime::class.java, ISO8601Deserializer.LocalTimeDeserializerY(zoneOffset))

            it.addModule(serializerModule)
            it.addModule(deserializerModule)
          }
          .addModule(KotlinModule.Builder().build())
          .build()

      // Create composite object containing multiple time types
      val dateTime = LocalDateTime.of(2023, 5, 15, 10, 30, 45)
      val date = dateTime.toLocalDate()
      val time = dateTime.toLocalTime()

      val testData = TypeInfoWrapper(date = date, dateTime = dateTime, time = time)

      // Serialize
      val json = polymorphicMapper.writeValueAsString(testData)

      // Deserialize
      val result = polymorphicMapper.readValue(json, TypeInfoWrapper::class.java)

      // Verify
      assertEquals(date, result.date)
      assertEquals(dateTime, result.dateTime)
      assertEquals(time.hour, result.time?.hour)
      assertEquals(time.minute, result.time?.minute)
      assertEquals(time.second, result.time?.second)
    }
  }

  // Wrapper classes for testing

  data class LocalDateWrapper(val date: LocalDate?)

  data class LocalDateTimeWrapper(val dateTime: LocalDateTime?)

  data class LocalTimeWrapper(val time: LocalTime?)

  data class TypeInfoWrapper(val date: LocalDate?, val dateTime: LocalDateTime?, val time: LocalTime?)
}
