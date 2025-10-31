package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Verifies the date-time extension functions defined in JavaDateTimeFns.kt.
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
  fun convertsEpochMillisToLocalDateTime() {
    val result = testMillis.toLocalDateTime(utcZone)

    log.info("Epoch millis {} converted to LocalDateTime {}", testMillis, result)

    assertEquals(testLocalDateTime, result, "Epoch millis should convert to LocalDateTime")
  }

  @Test
  fun convertsEpochMillisToLocalDate() {
    val result = testMillis.toLocalDate(utcZone)

    log.info("Epoch millis {} converted to LocalDate {}", testMillis, result)

    assertEquals(testLocalDate, result, "Epoch millis should convert to LocalDate")
  }

  @Test
  fun convertsEpochMillisToLocalTime() {
    val result = testMillis.toLocalTime(utcZone)

    log.info("Epoch millis {} converted to LocalTime {}", testMillis, result)

    assertEquals(testLocalTime, result, "Epoch millis should convert to LocalTime")
  }

  @Test
  fun convertsEpochMillisToInstant() {
    val result = testMillis.toInstant()

    log.info("Epoch millis {} converted to Instant {}", testMillis, result)

    assertEquals(testInstant, result, "Epoch millis should convert to Instant")
  }

  @Test
  fun convertsLocalDateTimeToEpochMillis() {
    val result = testLocalDateTime.toMillis(utcZone)

    log.info("LocalDateTime {} converted to epoch millis {}", testLocalDateTime, result)

    assertEquals(testMillis, result, "LocalDateTime should convert to epoch millis")
  }

  @Test
  fun convertsLocalDateToEpochMillis() {
    val result = testLocalDate.toMillis(utcZone)
    val expectedMillis = testLocalDate.atStartOfDay(utcZone).toInstant().toEpochMilli()

    log.info("LocalDate {} converted to epoch millis {}", testLocalDate, result)

    assertEquals(expectedMillis, result, "LocalDate should convert to epoch millis")
  }

  @Test
  fun convertsLocalTimeToEpochMillis() {
    val result = testLocalTime.toMillis(utcZone)

    log.info("LocalTime {} converted to epoch millis {}", testLocalTime, result)

    // Conversion should be anchored at 1970-01-01
    val expected = testLocalTime.atDate(LocalDate.of(1970, 1, 1)).toInstant(utcZone).toEpochMilli()
    assertEquals(expected, result, "LocalTime should convert to epoch millis")
  }

  @Test
  fun convertsInstantToLocalDateTime() {
    val result = testInstant.toLocalDateTime(utcZone)

    log.info("Instant {} converted to LocalDateTime {}", testInstant, result)

    assertEquals(testLocalDateTime, result, "Instant should convert to LocalDateTime")
  }

  @Test
  fun convertsInstantToEpochMillis() {
    val result = testInstant.toMillis()

    log.info("Instant {} converted to epoch millis {}", testInstant, result)

    assertEquals(testMillis, result, "Instant should convert to epoch millis")
  }

  @Test
  fun exposesIso8601EpochValues() {
    val iso8601Millis = testLocalDateTime.iso8601LongUtc
    val iso8601Seconds = testLocalDateTime.iso8601LongUtcSecond

    log.info("LocalDateTime {} ISO8601 millis {}", testLocalDateTime, iso8601Millis)
    log.info("LocalDateTime {} ISO8601 seconds {}", testLocalDateTime, iso8601Seconds)

    assertEquals(testMillis, iso8601Millis, "ISO8601 millisecond timestamp should match")
    assertEquals(testMillis / 1000, iso8601Seconds, "ISO8601 second timestamp should match")
  }

  @Test
  fun subtractsLocalDateTimes() {
    val dateTime1 = LocalDateTime.of(2023, 1, 1, 12, 0, 0)
    val dateTime2 = LocalDateTime.of(2023, 1, 1, 10, 0, 0)

    val duration = dateTime1 - dateTime2

    log.info("Duration difference: {} - {} = {}", dateTime1, dateTime2, duration)

    assertEquals(Duration.ofHours(2), duration, "Duration should equal two hours")
  }

  @Test
  fun subtractsLocalDates() {
    val date1 = LocalDate.of(2023, 1, 10)
    val date2 = LocalDate.of(2023, 1, 1)

    val period = date1 - date2

    log.info("Period difference: {} - {} = {}", date1, date2, period)

    assertEquals(Period.ofDays(9), period, "Period should equal nine days")
  }

  @Test
  fun computesFirstAndLastDayOfMonthForDateTime() {
    val testDate = LocalDateTime.of(2023, 2, 15, 10, 30, 45)

    val firstDay = testDate.firstDayOfMonth()
    val lastDay = testDate.lastDayOfMonth()

    log.info("Original date-time: {}", testDate)
    log.info("First day of month: {}", firstDay)
    log.info("Last day of month: {}", lastDay)

    assertEquals(LocalDateTime.of(2023, 2, 1, 10, 30, 45), firstDay, "Should return the first day of the month")
    assertEquals(LocalDateTime.of(2023, 2, 28, 10, 30, 45), lastDay, "Should return the last day of the month")
  }

  @Test
  fun verifiesCurrentTimeHelpers() {
    val currentInstant = now()
    val currentMillis = nowMillis()
    val currentFirstDay = nowDateTimeFirstDayOfMonth()
    val currentLastDay = nowDateTimeLastDayOfMonth()

    log.info("Current Instant: {}", currentInstant)
    log.info("Current epoch millis: {}", currentMillis)
    log.info("Current month first day: {}", currentFirstDay)
    log.info("Current month last day: {}", currentLastDay)

    // Allow for a small timing difference when validating current time
    val now = System.currentTimeMillis()
    assertTrue(kotlin.math.abs(currentMillis - now) < 1000, "Current epoch millis should be close to system time")
    assertTrue(kotlin.math.abs(currentInstant.toEpochMilli() - now) < 1000, "Current Instant should be close to system time")
  }

  @Test
  fun convertsBetweenTimeZones() {
    val shanghaiZone = ZoneId.of("Asia/Shanghai")
    val newYorkZone = ZoneId.of("America/New_York")

    val shanghaiTime = testMillis.toLocalDateTime(shanghaiZone)
    val newYorkTime = testMillis.toLocalDateTime(newYorkZone)

    log.info("UTC time: {}", testLocalDateTime)
    log.info("Asia/Shanghai time: {}", shanghaiTime)
    log.info("America/New_York time: {}", newYorkTime)

    // Validate time-zone conversions
    val shanghaiMillis = shanghaiTime.toMillis(shanghaiZone)
    val newYorkMillis = newYorkTime.toMillis(newYorkZone)

    assertEquals(testMillis, shanghaiMillis, "Asia/Shanghai conversion should round trip to epoch millis")
    assertEquals(testMillis, newYorkMillis, "America/New_York conversion should round trip to epoch millis")
  }

  @Test
  fun computesFirstAndLastDayOfMonthForDate() {
    val testDate = LocalDate.of(2023, 2, 15)

    val firstDay = testDate.firstDayOfMonth()
    val lastDay = testDate.lastDayOfMonth()

    log.info("Original date: {}", testDate)
    log.info("First day of month: {}", firstDay)
    log.info("Last day of month: {}", lastDay)

    assertEquals(LocalDate.of(2023, 2, 1), firstDay, "Should return the first day of the month")
    assertEquals(LocalDate.of(2023, 2, 28), lastDay, "Should return the last day of the month")
  }

  @Test
  fun handlesLeapYearLastDay() {
    val leapYearDate = LocalDate.of(2020, 2, 15) // 2020 is a leap year
    val normalYearDate = LocalDate.of(2021, 2, 15) // 2021 is not a leap year

    val leapYearLastDay = leapYearDate.lastDayOfMonth()
    val normalYearLastDay = normalYearDate.lastDayOfMonth()

    log.info("Last day of February (leap year): {}", leapYearLastDay)
    log.info("Last day of February (common year): {}", normalYearLastDay)

    assertEquals(29, leapYearLastDay.dayOfMonth, "Leap-year February should have 29 days")
    assertEquals(28, normalYearLastDay.dayOfMonth, "Common-year February should have 28 days")
  }

  @Test
  fun computesCurrentDateFirstDayOfMonth() {
    val result = nowDateFirstDayOfMonth()
    val now = LocalDate.now()

    log.info("Current date first day: {}", result)

    assertEquals(now.year, result.year, "Year should match")
    assertEquals(now.monthValue, result.monthValue, "Month should match")
    assertEquals(1, result.dayOfMonth, "Should be the first day of the month")
  }

  @Test
  fun convertsInstantToDateAndTime() {
    val instantResult = testInstant.toLocalDate(utcZone)
    val timeResult = testInstant.toLocalTime(utcZone)

    log.info("Instant {} converted to LocalDate {}", testInstant, instantResult)
    log.info("Instant {} converted to LocalTime {}", testInstant, timeResult)

    assertEquals(testLocalDate, instantResult, "Instant should convert to LocalDate")
    assertEquals(testLocalTime, timeResult, "Instant should convert to LocalTime")
  }

  @Test
  fun handlesNegativeDurations() {
    val dateTime1 = LocalDateTime.of(2023, 1, 1, 10, 0, 0)
    val dateTime2 = LocalDateTime.of(2023, 1, 1, 12, 0, 0)

    val duration = dateTime1 - dateTime2

    log.info("Negative duration: {} - {} = {}", dateTime1, dateTime2, duration)

    assertEquals(Duration.ofHours(-2), duration, "Should return a negative duration")
  }

  @Test
  fun handlesMonthBoundaryTransition() {
    val endOfMonth = LocalDateTime.of(2023, 1, 31, 23, 59, 59)
    val startOfNextMonth = LocalDateTime.of(2023, 2, 1, 0, 0, 0)

    val duration = startOfNextMonth - endOfMonth

    log.info("Month boundary duration: {} - {} = {}", startOfNextMonth, endOfMonth, duration)

    assertEquals(Duration.ofSeconds(1), duration, "Boundary should yield a one-second duration")
  }
}
