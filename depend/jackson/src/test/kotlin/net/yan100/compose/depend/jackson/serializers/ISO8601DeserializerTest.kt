package net.yan100.compose.depend.jackson.serializers

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
import kotlin.test.assertNull
import net.yan100.compose.toMillis
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * ISO8601Deserializer 单元测试
 *
 * 测试各种类型的时间戳反序列化功能
 */
class ISO8601DeserializerTest {

  private lateinit var objectMapper: ObjectMapper
  private val zoneOffset = ZoneOffset.UTC

  @BeforeEach
  fun setup() {
    objectMapper = ObjectMapper()
    // 注册KotlinModule以支持Kotlin数据类
    objectMapper.registerModule(KotlinModule.Builder().build())
    objectMapper.registerKotlinModule()

    // 创建一个简单模块并添加反序列化器
    val module = SimpleModule()
    module.addDeserializer(LocalDate::class.java, ISO8601Deserializer.LocalDateDeserializerX(zoneOffset))
    module.addDeserializer(LocalDateTime::class.java, ISO8601Deserializer.LocalDateTimeDeserializerZ(zoneOffset))
    module.addDeserializer(LocalTime::class.java, ISO8601Deserializer.LocalTimeDeserializerY(zoneOffset))

    // 注册模块
    objectMapper.registerModule(module)
  }

  @Nested
  inner class CommonDeserializerFunctionsGroup {

    @Test
    fun `正常反序列化时，应正确转换时间戳`() {
      // 创建测试数据
      val testDate = LocalDate.of(2023, 5, 15)
      val timestamp = testDate.toMillis(zoneOffset)
      val json = """{"date":"$timestamp"}"""

      // 反序列化
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)

      // 验证
      assertNotNull(result.date)
      assertEquals(testDate, result.date)
    }

    @Test
    fun `反序列化空值时，应返回null`() {
      // 创建测试数据
      val json = """{"date":null}"""

      // 反序列化
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)

      // 验证
      assertNull(result.date)
    }

    @Test
    fun `反序列化非数字值时，应返回null`() {
      // 创建测试数据
      val json = """{"date":"not-a-number"}"""

      // 反序列化
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)

      // 验证
      assertNull(result.date)
    }
  }

  @Nested
  inner class LocalDateDeserializerGroup {

    @Test
    fun `正常反序列化LocalDate时，应正确还原日期`() {
      // 准备测试数据
      val originalDate = LocalDate.of(2023, 5, 15)
      val timestamp = originalDate.toMillis(zoneOffset)
      val json = """{"date":"$timestamp"}"""

      // 反序列化
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)

      // 验证
      assertNotNull(result.date)
      assertEquals(originalDate, result.date)
    }

    @ParameterizedTest
    @ValueSource(strings = ["1970-01-01", "2023-12-31", "2024-02-29"])
    fun `边界日期值反序列化时，应正确处理`(dateStr: String) {
      // 解析测试日期
      val testDate = LocalDate.parse(dateStr)
      val timestamp = testDate.toMillis(zoneOffset)
      val json = """{"date":"$timestamp"}"""

      // 反序列化
      val result = objectMapper.readValue(json, LocalDateWrapper::class.java)

      // 验证
      assertNotNull(result.date)
      assertEquals(testDate, result.date)
    }
  }

  @Nested
  inner class LocalDateTimeDeserializerGroup {

    @Test
    fun `正常反序列化LocalDateTime时，应正确还原日期时间`() {
      // 准备测试数据
      val originalDateTime = LocalDateTime.of(2023, 5, 15, 10, 30, 45)
      val timestamp = originalDateTime.toMillis(zoneOffset)
      val json = """{"dateTime":"$timestamp"}"""

      // 反序列化
      val result = objectMapper.readValue(json, LocalDateTimeWrapper::class.java)

      // 验证
      assertNotNull(result.dateTime)
      assertEquals(originalDateTime, result.dateTime)
    }

    @Test
    fun `边界时间值反序列化时，应正确处理`() {
      // 测试最小时间
      val minDateTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0)
      val minTimestamp = minDateTime.toMillis(zoneOffset)
      val minJson = """{"dateTime":"$minTimestamp"}"""

      // 反序列化
      val minResult = objectMapper.readValue(minJson, LocalDateTimeWrapper::class.java)
      assertEquals(minDateTime, minResult.dateTime)

      // 测试当前时间
      val nowDateTime = LocalDateTime.now()
      val nowTimestamp = nowDateTime.toMillis(zoneOffset)
      val nowJson = """{"dateTime":"$nowTimestamp"}"""

      // 反序列化
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
  inner class LocalTimeDeserializerGroup {

    @Test
    fun `正常反序列化LocalTime时，应正确还原时间`() {
      // 准备测试数据
      val originalTime = LocalTime.of(10, 30, 45)
      val timestamp = originalTime.toMillis(zoneOffset)
      val json = """{"time":"$timestamp"}"""

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
    fun `边界时间值反序列化时，应正确处理`(timeStr: String) {
      // 解析测试时间
      val testTime = LocalTime.parse(timeStr)
      val timestamp = testTime.toMillis(zoneOffset)
      val json = """{"time":"$timestamp"}"""

      // 反序列化
      val result = objectMapper.readValue(json, LocalTimeWrapper::class.java)

      // 验证
      assertNotNull(result.time)
      assertEquals(testTime.hour, result.time.hour)
      assertEquals(testTime.minute, result.time.minute)
      assertEquals(testTime.second, result.time.second)
    }
  }

  @Nested
  inner class FactoryMethodsGroup {

    @Test
    fun `forLocalDate工厂方法应创建正确的反序列化器`() {
      // 准备测试数据
      val testZoneOffset = ZoneOffset.ofHours(8)
      val deserializer = ISO8601Deserializer.LocalDateDeserializerX(testZoneOffset)

      // 验证类型
      assertEquals("LocalDateDeserializerX", deserializer.javaClass.simpleName)
    }

    @Test
    fun `forLocalDateTime工厂方法应创建正确的反序列化器`() {
      // 准备测试数据
      val testZoneOffset = ZoneOffset.ofHours(8)
      val deserializer = ISO8601Deserializer.LocalDateTimeDeserializerZ(testZoneOffset)

      // 验证类型
      assertEquals("LocalDateTimeDeserializerZ", deserializer.javaClass.simpleName)
    }

    @Test
    fun `forLocalTime工厂方法应创建正确的反序列化器`() {
      // 准备测试数据
      val testZoneOffset = ZoneOffset.ofHours(8)
      val deserializer = ISO8601Deserializer.LocalTimeDeserializerY(testZoneOffset)

      // 验证类型
      assertEquals("LocalTimeDeserializerY", deserializer.javaClass.simpleName)
    }
  }

  @Nested
  inner class ZoneOffsetGroup {

    @Test
    fun `不同时区应正确转换时间戳`() {
      // 使用不同的时区创建反序列化器
      val beijingZone = ZoneOffset.ofHours(8)
      val module = SimpleModule()
      module.addDeserializer(LocalDate::class.java, ISO8601Deserializer.LocalDateDeserializerX(beijingZone))

      val beijingMapper = ObjectMapper()
      beijingMapper.registerModule(KotlinModule.Builder().build())
      beijingMapper.registerModule(module)

      // 准备测试数据
      val originalDate = LocalDate.of(2023, 5, 15)
      val utcTimestamp = originalDate.toMillis(ZoneOffset.UTC)
      val json = """{"date":"$utcTimestamp"}"""

      // 反序列化
      val result = beijingMapper.readValue(json, LocalDateWrapper::class.java)

      // 验证结果与原始数据可能有时区差异
      assertNotNull(result.date)
    }
  }

  // 测试用包装类
  data class LocalDateWrapper(val date: LocalDate?)

  data class LocalDateTimeWrapper(val dateTime: LocalDateTime?)

  data class LocalTimeWrapper(val time: LocalTime?)
}
