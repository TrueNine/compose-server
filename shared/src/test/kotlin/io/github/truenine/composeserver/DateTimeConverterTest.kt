package io.github.truenine.composeserver

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.stream.Stream
import kotlin.test.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

/** DateTimeConverter 转换器单元测试 */
class DateTimeConverterTest {

  // 固定基准时间，使用UTC时区以避免时区问题
  private val fixedTestInstant = Instant.parse("2023-01-01T12:30:45Z")
  private val fixedTestMillis = 1672576245000L // 对应 2023-01-01T12:30:45Z
  private val fixedTestLocalDateTime = LocalDateTime.of(2023, 1, 1, 12, 30, 45)
  private val fixedTestLocalDate = LocalDate.of(2023, 1, 1)
  private val fixedTestLocalTime = LocalTime.of(12, 30, 45)
  private val defaultZone = ZoneId.systemDefault()
  private val utcZone = ZoneOffset.UTC

  @Nested
  inner class PlusMillisFunctionGroup {
    @Test
    fun `正常调用 plusMillisFromCurrent 时，返回增加指定毫秒数后的 Instant`() {
      val before = System.currentTimeMillis()
      val result = DateTimeConverter.plusMillisFromCurrent(1000)
      val after = System.currentTimeMillis()

      val expectedMin = before + 1000
      val expectedMax = after + 1000

      assert(result.toEpochMilli() in expectedMin..expectedMax)
    }

    @Test
    fun `正常调用 plusMillis 时，返回增加指定毫秒数后的 Instant`() {
      val result = DateTimeConverter.plusMillis(1000, 500)
      assertEquals(1500, result.toEpochMilli())
    }
  }

  @Nested
  inner class LocalToInstantFunctionGroup {
    @Test
    fun `正常将 LocalTime 转换为 Instant 时，返回正确的 Instant`() {
      val result = DateTimeConverter.localTimeToInstant(fixedTestLocalTime, utcZone)
      assertEquals(45000 + 30 * 60 * 1000 + 12 * 60 * 60 * 1000, result.toEpochMilli())
    }

    @Test
    fun `正常将 LocalDate 转换为 Instant 时，返回正确的 Instant`() {
      val result = DateTimeConverter.localDateToInstant(fixedTestLocalDate, utcZone)
      assertEquals(LocalDateTime.of(fixedTestLocalDate, LocalTime.MIDNIGHT).toInstant(utcZone).toEpochMilli(), result.toEpochMilli())
    }

    @Test
    fun `正常将 LocalDateTime 转换为 Instant 时，返回正确的 Instant`() {
      val result = DateTimeConverter.localDatetimeToInstant(fixedTestLocalDateTime, utcZone)
      assertEquals(fixedTestMillis, result.toEpochMilli())
    }
  }

  @Nested
  inner class MillisToLocalFunctionGroup {
    @Test
    fun `正常将毫秒时间戳转换为 LocalDateTime 时，返回正确的 LocalDateTime`() {
      val result = DateTimeConverter.millisToLocalDateTime(fixedTestMillis, utcZone)
      assertEquals(fixedTestLocalDateTime, result)
    }

    @Test
    fun `正常将毫秒时间戳转换为 LocalDate 时，返回正确的 LocalDate`() {
      val result = DateTimeConverter.millisToLocalDate(fixedTestMillis, utcZone)
      assertEquals(fixedTestLocalDate, result)
    }

    @Test
    fun `正常将毫秒时间戳转换为 LocalTime 时，返回正确的 LocalTime`() {
      val result = DateTimeConverter.millisToLocalTime(fixedTestMillis, utcZone)
      assertEquals(fixedTestLocalTime, result)
    }
  }

  @Nested
  inner class InstantToLocalFunctionGroup {
    @Test
    fun `正常将 Instant 转换为 LocalDateTime 时，返回正确的 LocalDateTime`() {
      val result = DateTimeConverter.instantToLocalDateTime(fixedTestInstant, utcZone)
      assertEquals(fixedTestLocalDateTime, result)
    }

    @Test
    fun `正常将 Instant 转换为 LocalDate 时，返回正确的 LocalDate`() {
      val result = DateTimeConverter.instantToLocalDate(fixedTestInstant, utcZone)
      assertEquals(fixedTestLocalDate, result)
    }

    @Test
    fun `正常将 Instant 转换为 LocalTime 时，返回正确的 LocalTime`() {
      val result = DateTimeConverter.instantToLocalTime(fixedTestInstant, utcZone)
      assertEquals(fixedTestLocalTime, result)
    }
  }

  @Nested
  inner class OtherConversionFunctionGroup {
    @Test
    fun `正常将 LocalDateTime 转换为毫秒时间戳时，返回正确的毫秒值`() {
      val result = DateTimeConverter.localDatetimeToMillis(fixedTestLocalDateTime, utcZone)
      assertEquals(fixedTestMillis, result)
    }

    @Test
    fun `正常将 Instant 转换为毫秒时间戳时，返回正确的毫秒值`() {
      val result = DateTimeConverter.instantToMillis(fixedTestInstant)
      assertEquals(fixedTestMillis, result)
    }
  }

  @Nested
  inner class TimezoneHandlingGroup {
    @ParameterizedTest
    @MethodSource("io.github.truenine.composeserver.DateTimeConverterTest#timezoneTestCases")
    fun `正常处理不同时区时，正确转换时区`(zoneId: ZoneId, instant: Instant, expectedLocalDateTime: LocalDateTime) {
      val result = DateTimeConverter.instantToLocalDateTime(instant, zoneId)
      assertEquals(expectedLocalDateTime, result)
    }
  }

  @Nested
  inner class EdgeCasesGroup {
    @Test
    fun `边界值测试极端时间点时，正确处理时间转换`() {
      val epochInstant = Instant.EPOCH
      val result = DateTimeConverter.instantToLocalDateTime(epochInstant, utcZone)
      assertEquals(LocalDateTime.of(1970, 1, 1, 0, 0, 0), result)
    }

    @Test
    fun `边界值测试日期变更线时，正确处理时间转换`() {
      val eastZone = ZoneId.of("Pacific/Kiritimati") // UTC+14
      val westZone = ZoneId.of("Pacific/Niue") // UTC-11

      val testInstant = Instant.parse("2023-01-01T00:00:00Z")

      val eastResult = DateTimeConverter.instantToLocalDateTime(testInstant, eastZone)
      val westResult = DateTimeConverter.instantToLocalDateTime(testInstant, westZone)

      // 东边应该是1月1日14点
      assertEquals(LocalDateTime.of(2023, 1, 1, 14, 0, 0), eastResult)

      // 西边应该是12月31日13点
      assertEquals(LocalDateTime.of(2022, 12, 31, 13, 0, 0), westResult)
    }
  }

  companion object {
    @JvmStatic
    fun timezoneTestCases(): Stream<Arguments> {
      val testInstant = Instant.parse("2023-01-01T12:00:00Z")

      return Stream.of(
        Arguments.of(ZoneId.of("UTC"), testInstant, LocalDateTime.of(2023, 1, 1, 12, 0, 0)),
        Arguments.of(ZoneId.of("Asia/Shanghai"), testInstant, LocalDateTime.of(2023, 1, 1, 20, 0, 0)),
        Arguments.of(ZoneId.of("America/New_York"), testInstant, LocalDateTime.of(2023, 1, 1, 7, 0, 0)),
      )
    }
  }
}
