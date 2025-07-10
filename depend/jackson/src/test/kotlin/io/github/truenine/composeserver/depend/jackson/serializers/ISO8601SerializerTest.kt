package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import net.yan100.compose.toMillis
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ISO8601SerializerTest {

  private lateinit var objectMapper: ObjectMapper
  private val zoneOffset = ZoneOffset.UTC

  @BeforeEach
  fun setup() {
    objectMapper = ObjectMapper()
    // 注册KotlinModule以支持Kotlin数据类
    objectMapper.registerModule(KotlinModule.Builder().build())
    objectMapper.registerKotlinModule()

    // 创建一个简单模块并添加序列化器
    val serializerModule = SimpleModule()
    serializerModule.addSerializer(LocalDate::class.java, ISO8601Serializer.ISO8601DateSerializer(zoneOffset))
    serializerModule.addSerializer(LocalDateTime::class.java, ISO8601Serializer.ISO8601DateTimeSerializer(zoneOffset))
    serializerModule.addSerializer(LocalTime::class.java, ISO8601Serializer.ISO8601TimeSerializer(zoneOffset))

    // 创建一个简单模块并添加反序列化器
    val deserializerModule = SimpleModule()
    deserializerModule.addDeserializer(LocalDate::class.java, ISO8601Deserializer.LocalDateDeserializerX(zoneOffset))
    deserializerModule.addDeserializer(LocalDateTime::class.java, ISO8601Deserializer.LocalDateTimeDeserializerZ(zoneOffset))
    deserializerModule.addDeserializer(LocalTime::class.java, ISO8601Deserializer.LocalTimeDeserializerY(zoneOffset))

    // 注册模块
    objectMapper.registerModule(serializerModule)
    objectMapper.registerModule(deserializerModule)
  }

  @Nested
  inner class ISO8601DateSerializerGroup {

    @Test
    fun `正常序列化LocalDate时，应返回对应的时间戳`() {
      // 创建测试数据
      val testDate = LocalDate.of(2023, 5, 15)
      val testData = LocalDateWrapper(testDate)

      // 序列化
      val json = objectMapper.writeValueAsString(testData)

      // 验证结果是否为数字格式
      val expectedTimestamp = testDate.toMillis(zoneOffset)
      val expectedJson = """{"date":$expectedTimestamp}"""
      assertEquals(expectedJson, json)
    }

    @Test
    fun `正常反序列化LocalDate时，应正确还原日期`() {
      // 准备测试数据
      val originalDate = LocalDate.of(2023, 5, 15)
      val timestamp = originalDate.toMillis(zoneOffset)
      val json = """{"date":$timestamp}"""

      // 反序列化
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)

      // 验证
      assertNotNull(result.date)
      assertEquals(originalDate, result.date)
    }

    @ParameterizedTest
    @ValueSource(strings = ["1970-01-01", "2023-12-31", "2024-02-29"])
    fun `边界日期值序列化时，应正确处理`(dateStr: String) {
      // 解析测试日期
      val testDate = LocalDate.parse(dateStr)
      val testData = LocalDateWrapper(testDate)

      // 序列化
      val json = objectMapper.writeValueAsString(testData)

      // 反序列化进行验证
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)
      assertEquals(testDate, result.date)
    }
  }

  @Nested
  inner class ISO8601DateTimeSerializerGroup {

    @Test
    fun `正常序列化LocalDateTime时，应返回对应的时间戳`() {
      // 创建测试数据
      val testDateTime = LocalDateTime.of(2023, 5, 15, 10, 30, 45)
      val testData = LocalDateTimeWrapper(testDateTime)

      // 序列化
      val json = objectMapper.writeValueAsString(testData)

      // 验证结果是否为数字格式
      val expectedTimestamp = testDateTime.toMillis(zoneOffset)
      val expectedJson = """{"dateTime":$expectedTimestamp}"""
      assertEquals(expectedJson, json)
    }

    @Test
    fun `正常反序列化LocalDateTime时，应正确还原日期时间`() {
      // 准备测试数据
      val originalDateTime = LocalDateTime.of(2023, 5, 15, 10, 30, 45)
      val timestamp = originalDateTime.toMillis(zoneOffset)
      val json = """{"dateTime":$timestamp}"""

      // 反序列化
      val result = objectMapper.readValue(json, LocalDateTimeWrapper::class.java)

      // 验证
      assertNotNull(result.dateTime)
      assertEquals(originalDateTime, result.dateTime)
    }

    @Test
    fun `边界时间值序列化时，应正确处理`() {
      // 测试最小时间
      val minDateTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0)
      val minTestData = LocalDateTimeWrapper(minDateTime)

      // 序列化
      val minJson = objectMapper.writeValueAsString(minTestData)

      // 反序列化验证
      val minResult = objectMapper.readValue(minJson, LocalDateTimeWrapper::class.java)
      assertEquals(minDateTime, minResult.dateTime)

      // 测试当前时间
      val nowDateTime = LocalDateTime.now()
      val nowTestData = LocalDateTimeWrapper(nowDateTime)

      // 序列化
      val nowJson = objectMapper.writeValueAsString(nowTestData)

      // 反序列化验证
      val nowResult = objectMapper.readValue(nowJson, LocalDateTimeWrapper::class.java)

      // 由于毫秒精度可能不同，所以比较年月日时分秒
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
    fun `正常序列化LocalTime时，应返回对应的时间戳`() {
      // 创建测试数据
      val testTime = LocalTime.of(10, 30, 45)
      val testData = LocalTimeWrapper(testTime)

      // 序列化
      val json = objectMapper.writeValueAsString(testData)

      // 验证结果是否为数字格式
      val expectedTimestamp = testTime.toMillis(zoneOffset)
      val expectedJson = """{"time":$expectedTimestamp}"""
      assertEquals(expectedJson, json)
    }

    @Test
    fun `正常反序列化LocalTime时，应正确还原时间`() {
      // 准备测试数据
      val originalTime = LocalTime.of(10, 30, 45)
      val timestamp = originalTime.toMillis(zoneOffset)
      val json = """{"time":$timestamp}"""

      // 反序列化
      val result = objectMapper.readValue(json, LocalTimeWrapper::class.java)

      // 验证
      assertNotNull(result.time)
      assertEquals(originalTime.hour, result.time.hour)
      assertEquals(originalTime.minute, result.time.minute)
      assertEquals(originalTime.second, result.time.second)
    }

    @ParameterizedTest
    @ValueSource(strings = ["00:00:00", "12:00:00", "23:59:59"])
    fun `边界时间值序列化时，应正确处理`(timeStr: String) {
      // 解析测试时间
      val testTime = LocalTime.parse(timeStr)
      val testData = LocalTimeWrapper(testTime)

      // 序列化
      val json = objectMapper.writeValueAsString(testData)

      // 反序列化进行验证
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
    fun `使用TypeInfo时，应正确序列化和反序列化`() {
      // 配置对象映射器支持多态性
      val polymorphicMapper = ObjectMapper()
      polymorphicMapper.registerModule(KotlinModule.Builder().build())

      // 创建并注册序列化和反序列化模块
      val serializerModule = SimpleModule()
      serializerModule.addSerializer(LocalDate::class.java, ISO8601Serializer.ISO8601DateSerializer(zoneOffset))
      serializerModule.addSerializer(LocalDateTime::class.java, ISO8601Serializer.ISO8601DateTimeSerializer(zoneOffset))
      serializerModule.addSerializer(LocalTime::class.java, ISO8601Serializer.ISO8601TimeSerializer(zoneOffset))

      val deserializerModule = SimpleModule()
      deserializerModule.addDeserializer(LocalDate::class.java, ISO8601Deserializer.LocalDateDeserializerX(zoneOffset))
      deserializerModule.addDeserializer(LocalDateTime::class.java, ISO8601Deserializer.LocalDateTimeDeserializerZ(zoneOffset))
      deserializerModule.addDeserializer(LocalTime::class.java, ISO8601Deserializer.LocalTimeDeserializerY(zoneOffset))

      polymorphicMapper.registerModule(serializerModule)
      polymorphicMapper.registerModule(deserializerModule)

      // 创建包含多种时间类型的复合对象
      val dateTime = LocalDateTime.of(2023, 5, 15, 10, 30, 45)
      val date = dateTime.toLocalDate()
      val time = dateTime.toLocalTime()

      val testData = TypeInfoWrapper(date = date, dateTime = dateTime, time = time)

      // 序列化
      val json = polymorphicMapper.writeValueAsString(testData)

      // 反序列化
      val result = polymorphicMapper.readValue(json, TypeInfoWrapper::class.java)

      // 验证
      assertEquals(date, result.date)
      assertEquals(dateTime, result.dateTime)
      assertEquals(time.hour, result.time?.hour)
      assertEquals(time.minute, result.time?.minute)
      assertEquals(time.second, result.time?.second)
    }
  }

  // 测试用包装类

  data class LocalDateWrapper(val date: LocalDate?)

  data class LocalDateTimeWrapper(val dateTime: LocalDateTime?)

  data class LocalTimeWrapper(val time: LocalTime?)

  data class TypeInfoWrapper(
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) val date: LocalDate?,
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) val dateTime: LocalDateTime?,
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) val time: LocalTime?,
  )
}
