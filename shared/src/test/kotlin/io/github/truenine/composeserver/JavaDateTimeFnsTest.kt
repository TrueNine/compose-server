package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import java.time.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * # Java 日期时间扩展函数测试
 *
 * 测试 JavaDateTimeFns.kt 中定义的日期时间相关扩展函数
 */
class JavaDateTimeFnsTest {

  private val testMillis = 1672576245000L // 2023-01-01T12:30:45Z
  private val testLocalDateTime = LocalDateTime.of(2023, 1, 1, 12, 30, 45)
  private val testLocalDate = LocalDate.of(2023, 1, 1)
  private val testLocalTime = LocalTime.of(12, 30, 45)
  private val testInstant = Instant.ofEpochMilli(testMillis)
  private val utcZone = ZoneOffset.UTC
  private val systemZone = ZoneId.systemDefault()

  @Test
  fun `测试 Long 转 LocalDateTime 扩展函数`() {
    val result = testMillis.toLocalDateTime(utcZone)

    log.info("毫秒时间戳: {} 转换为 LocalDateTime: {}", testMillis, result)

    assertEquals(testLocalDateTime, result, "毫秒时间戳应该正确转换为LocalDateTime")
  }

  @Test
  fun `测试 Long 转 LocalDate 扩展函数`() {
    val result = testMillis.toLocalDate(utcZone)

    log.info("毫秒时间戳: {} 转换为 LocalDate: {}", testMillis, result)

    assertEquals(testLocalDate, result, "毫秒时间戳应该正确转换为LocalDate")
  }

  @Test
  fun `测试 Long 转 LocalTime 扩展函数`() {
    val result = testMillis.toLocalTime(utcZone)

    log.info("毫秒时间戳: {} 转换为 LocalTime: {}", testMillis, result)

    assertEquals(testLocalTime, result, "毫秒时间戳应该正确转换为LocalTime")
  }

  @Test
  fun `测试 Long 转 Instant 扩展函数`() {
    val result = testMillis.toInstant()

    log.info("毫秒时间戳: {} 转换为 Instant: {}", testMillis, result)

    assertEquals(testInstant, result, "毫秒时间戳应该正确转换为Instant")
  }

  @Test
  fun `测试 LocalDateTime 转毫秒时间戳扩展函数`() {
    val result = testLocalDateTime.toMillis(utcZone)

    log.info("LocalDateTime: {} 转换为毫秒时间戳: {}", testLocalDateTime, result)

    assertEquals(testMillis, result, "LocalDateTime应该正确转换为毫秒时间戳")
  }

  @Test
  fun `测试 LocalDate 转毫秒时间戳扩展函数`() {
    val result = testLocalDate.toMillis(utcZone)
    val expectedMillis = testLocalDate.atStartOfDay(utcZone).toInstant().toEpochMilli()

    log.info("LocalDate: {} 转换为毫秒时间戳: {}", testLocalDate, result)

    assertEquals(expectedMillis, result, "LocalDate应该正确转换为毫秒时间戳")
  }

  @Test
  fun `测试 LocalTime 转毫秒时间戳扩展函数`() {
    val result = testLocalTime.toMillis(utcZone)

    log.info("LocalTime: {} 转换为毫秒时间戳: {}", testLocalTime, result)

    // LocalTime转换应该基于1970-01-01的日期
    val expected = testLocalTime.atDate(LocalDate.of(1970, 1, 1)).toInstant(utcZone).toEpochMilli()
    assertEquals(expected, result, "LocalTime应该正确转换为毫秒时间戳")
  }

  @Test
  fun `测试 Instant 转 LocalDateTime 扩展函数`() {
    val result = testInstant.toLocalDateTime(utcZone)

    log.info("Instant: {} 转换为 LocalDateTime: {}", testInstant, result)

    assertEquals(testLocalDateTime, result, "Instant应该正确转换为LocalDateTime")
  }

  @Test
  fun `测试 Instant 转毫秒时间戳扩展函数`() {
    val result = testInstant.toMillis()

    log.info("Instant: {} 转换为毫秒时间戳: {}", testInstant, result)

    assertEquals(testMillis, result, "Instant应该正确转换为毫秒时间戳")
  }

  @Test
  fun `测试 ISO8601 时间戳扩展属性`() {
    val iso8601Millis = testLocalDateTime.iso8601LongUtc
    val iso8601Seconds = testLocalDateTime.iso8601LongUtcSecond

    log.info("LocalDateTime: {} ISO8601毫秒: {}", testLocalDateTime, iso8601Millis)
    log.info("LocalDateTime: {} ISO8601秒: {}", testLocalDateTime, iso8601Seconds)

    assertEquals(testMillis, iso8601Millis, "ISO8601毫秒时间戳应该正确")
    assertEquals(testMillis / 1000, iso8601Seconds, "ISO8601秒时间戳应该正确")
  }

  @Test
  fun `测试 LocalDateTime 减法操作符`() {
    val dateTime1 = LocalDateTime.of(2023, 1, 1, 12, 0, 0)
    val dateTime2 = LocalDateTime.of(2023, 1, 1, 10, 0, 0)

    val duration = dateTime1 - dateTime2

    log.info("时间差: {} - {} = {}", dateTime1, dateTime2, duration)

    assertEquals(Duration.ofHours(2), duration, "时间差应该为2小时")
  }

  @Test
  fun `测试 LocalDate 减法操作符`() {
    val date1 = LocalDate.of(2023, 1, 10)
    val date2 = LocalDate.of(2023, 1, 1)

    val period = date1 - date2

    log.info("日期差: {} - {} = {}", date1, date2, period)

    assertEquals(Period.ofDays(9), period, "日期差应该为9天")
  }

  @Test
  fun `测试月份第一天和最后一天扩展函数`() {
    val testDate = LocalDateTime.of(2023, 2, 15, 10, 30, 45)

    val firstDay = testDate.firstDayOfMonth()
    val lastDay = testDate.lastDayOfMonth()

    log.info("原始日期: {}", testDate)
    log.info("月份第一天: {}", firstDay)
    log.info("月份最后一天: {}", lastDay)

    assertEquals(LocalDateTime.of(2023, 2, 1, 10, 30, 45), firstDay, "应该返回月份第一天")
    assertEquals(LocalDateTime.of(2023, 2, 28, 10, 30, 45), lastDay, "应该返回月份最后一天")
  }

  @Test
  fun `测试当前时间相关函数`() {
    val currentInstant = now()
    val currentMillis = nowMillis()
    val currentFirstDay = nowDateTimeFirstDayOfMonth()
    val currentLastDay = nowDateTimeLastDayOfMonth()

    log.info("当前Instant: {}", currentInstant)
    log.info("当前毫秒时间戳: {}", currentMillis)
    log.info("当前月份第一天: {}", currentFirstDay)
    log.info("当前月份最后一天: {}", currentLastDay)

    // 验证时间的合理性（允许一定的时间差）
    val now = System.currentTimeMillis()
    assertTrue(kotlin.math.abs(currentMillis - now) < 1000, "当前毫秒时间戳应该接近系统时间")
    assertTrue(kotlin.math.abs(currentInstant.toEpochMilli() - now) < 1000, "当前Instant应该接近系统时间")
  }

  @Test
  fun `测试不同时区的转换`() {
    val shanghaiZone = ZoneId.of("Asia/Shanghai")
    val newYorkZone = ZoneId.of("America/New_York")

    val shanghaiTime = testMillis.toLocalDateTime(shanghaiZone)
    val newYorkTime = testMillis.toLocalDateTime(newYorkZone)

    log.info("UTC时间: {}", testLocalDateTime)
    log.info("上海时间: {}", shanghaiTime)
    log.info("纽约时间: {}", newYorkTime)

    // 验证时区转换的正确性
    val shanghaiMillis = shanghaiTime.toMillis(shanghaiZone)
    val newYorkMillis = newYorkTime.toMillis(newYorkZone)

    assertEquals(testMillis, shanghaiMillis, "上海时间转换回毫秒应该一致")
    assertEquals(testMillis, newYorkMillis, "纽约时间转换回毫秒应该一致")
  }

  @Test
  fun `测试 LocalDate 月份第一天和最后一天扩展函数`() {
    val testDate = LocalDate.of(2023, 2, 15)

    val firstDay = testDate.firstDayOfMonth()
    val lastDay = testDate.lastDayOfMonth()

    log.info("原始日期: {}", testDate)
    log.info("月份第一天: {}", firstDay)
    log.info("月份最后一天: {}", lastDay)

    assertEquals(LocalDate.of(2023, 2, 1), firstDay, "应该返回月份第一天")
    assertEquals(LocalDate.of(2023, 2, 28), lastDay, "应该返回月份最后一天")
  }

  @Test
  fun `测试闰年二月最后一天`() {
    val leapYearDate = LocalDate.of(2020, 2, 15) // 2020是闰年
    val normalYearDate = LocalDate.of(2021, 2, 15) // 2021不是闰年

    val leapYearLastDay = leapYearDate.lastDayOfMonth()
    val normalYearLastDay = normalYearDate.lastDayOfMonth()

    log.info("闰年二月最后一天: {}", leapYearLastDay)
    log.info("平年二月最后一天: {}", normalYearLastDay)

    assertEquals(29, leapYearLastDay.dayOfMonth, "闰年二月应该有29天")
    assertEquals(28, normalYearLastDay.dayOfMonth, "平年二月应该有28天")
  }

  @Test
  fun `测试 nowDateFirstDayOfMonth 函数`() {
    val result = nowDateFirstDayOfMonth()
    val now = LocalDate.now()

    log.info("当前日期第一天: {}", result)

    assertEquals(now.year, result.year, "年份应该一致")
    assertEquals(now.monthValue, result.monthValue, "月份应该一致")
    assertEquals(1, result.dayOfMonth, "应该是月份第一天")
  }

  @Test
  fun `测试 Instant 转 LocalDate 和 LocalTime 扩展函数`() {
    val instantResult = testInstant.toLocalDate(utcZone)
    val timeResult = testInstant.toLocalTime(utcZone)

    log.info("Instant: {} 转换为 LocalDate: {}", testInstant, instantResult)
    log.info("Instant: {} 转换为 LocalTime: {}", testInstant, timeResult)

    assertEquals(testLocalDate, instantResult, "Instant应该正确转换为LocalDate")
    assertEquals(testLocalTime, timeResult, "Instant应该正确转换为LocalTime")
  }

  @Test
  fun `测试负数时间差`() {
    val dateTime1 = LocalDateTime.of(2023, 1, 1, 10, 0, 0)
    val dateTime2 = LocalDateTime.of(2023, 1, 1, 12, 0, 0)

    val duration = dateTime1 - dateTime2

    log.info("负数时间差: {} - {} = {}", dateTime1, dateTime2, duration)

    assertEquals(Duration.ofHours(-2), duration, "应该得到负数时间差")
  }

  @Test
  fun `测试边界情况 - 月末到月初`() {
    val endOfMonth = LocalDateTime.of(2023, 1, 31, 23, 59, 59)
    val startOfNextMonth = LocalDateTime.of(2023, 2, 1, 0, 0, 0)

    val duration = startOfNextMonth - endOfMonth

    log.info("月末到月初时间差: {} - {} = {}", startOfNextMonth, endOfMonth, duration)

    assertEquals(Duration.ofSeconds(1), duration, "月末到月初应该是1秒差")
  }
}
