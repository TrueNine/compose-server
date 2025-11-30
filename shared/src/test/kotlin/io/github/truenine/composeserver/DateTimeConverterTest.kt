package io.github.truenine.composeserver

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.*
import java.util.stream.Stream
import kotlin.test.assertEquals

/** DateTimeConverter unit tests. */
class DateTimeConverterTest {

  // Fixed reference values using UTC to avoid time-zone issues
  private val fixedTestInstant = Instant.parse("2023-01-01T12:30:45Z")
  private val fixedTestMillis = 1672576245000L // Represents 2023-01-01T12:30:45Z
  private val fixedTestLocalDateTime = LocalDateTime.of(2023, 1, 1, 12, 30, 45)
  private val fixedTestLocalDate = LocalDate.of(2023, 1, 1)
  private val fixedTestLocalTime = LocalTime.of(12, 30, 45)
  private val defaultZone = ZoneId.systemDefault()
  private val utcZone = ZoneOffset.UTC

  @Nested
  inner class PlusMillisFunctionGroup {
    @Test
    fun returnsInstantOffsetFromCurrentTime() {
      val before = System.currentTimeMillis()
      val result = DateTimeConverter.plusMillisFromCurrent(1000)
      val after = System.currentTimeMillis()

      val expectedMin = before + 1000
      val expectedMax = after + 1000

      assert(result.toEpochMilli() in expectedMin..expectedMax)
    }

    @Test
    fun addsMillisToInstant() {
      val result = DateTimeConverter.plusMillis(1000, 500)
      assertEquals(1500, result.toEpochMilli())
    }
  }

  @Nested
  inner class LocalToInstantFunctionGroup {
    @Test
    fun convertsLocalTimeToInstant() {
      val result = DateTimeConverter.localTimeToInstant(fixedTestLocalTime, utcZone)
      assertEquals(45000 + 30 * 60 * 1000 + 12 * 60 * 60 * 1000, result.toEpochMilli())
    }

    @Test
    fun convertsLocalDateToInstant() {
      val result = DateTimeConverter.localDateToInstant(fixedTestLocalDate, utcZone)
      assertEquals(LocalDateTime.of(fixedTestLocalDate, LocalTime.MIDNIGHT).toInstant(utcZone).toEpochMilli(), result.toEpochMilli())
    }

    @Test
    fun convertsLocalDateTimeToInstant() {
      val result = DateTimeConverter.localDatetimeToInstant(fixedTestLocalDateTime, utcZone)
      assertEquals(fixedTestMillis, result.toEpochMilli())
    }
  }

  @Nested
  inner class MillisToLocalFunctionGroup {
    @Test
    fun convertsMillisToLocalDateTime() {
      val result = DateTimeConverter.millisToLocalDateTime(fixedTestMillis, utcZone)
      assertEquals(fixedTestLocalDateTime, result)
    }

    @Test
    fun convertsMillisToLocalDate() {
      val result = DateTimeConverter.millisToLocalDate(fixedTestMillis, utcZone)
      assertEquals(fixedTestLocalDate, result)
    }

    @Test
    fun convertsMillisToLocalTime() {
      val result = DateTimeConverter.millisToLocalTime(fixedTestMillis, utcZone)
      assertEquals(fixedTestLocalTime, result)
    }
  }

  @Nested
  inner class InstantToLocalFunctionGroup {
    @Test
    fun convertsInstantToLocalDateTime() {
      val result = DateTimeConverter.instantToLocalDateTime(fixedTestInstant, utcZone)
      assertEquals(fixedTestLocalDateTime, result)
    }

    @Test
    fun convertsInstantToLocalDate() {
      val result = DateTimeConverter.instantToLocalDate(fixedTestInstant, utcZone)
      assertEquals(fixedTestLocalDate, result)
    }

    @Test
    fun convertsInstantToLocalTime() {
      val result = DateTimeConverter.instantToLocalTime(fixedTestInstant, utcZone)
      assertEquals(fixedTestLocalTime, result)
    }
  }

  @Nested
  inner class OtherConversionFunctionGroup {
    @Test
    fun convertsLocalDateTimeToMillis() {
      val result = DateTimeConverter.localDatetimeToMillis(fixedTestLocalDateTime, utcZone)
      assertEquals(fixedTestMillis, result)
    }

    @Test
    fun convertsInstantToMillis() {
      val result = DateTimeConverter.instantToMillis(fixedTestInstant)
      assertEquals(fixedTestMillis, result)
    }
  }

  @Nested
  inner class TimezoneHandlingGroup {
    @ParameterizedTest
    @MethodSource("io.github.truenine.composeserver.DateTimeConverterTest#timezoneTestCases")
    fun convertsInstantAcrossTimezones(zoneId: ZoneId, instant: Instant, expectedLocalDateTime: LocalDateTime) {
      val result = DateTimeConverter.instantToLocalDateTime(instant, zoneId)
      assertEquals(expectedLocalDateTime, result)
    }
  }

  @Nested
  inner class EdgeCasesGroup {
    @Test
    fun handlesExtremeEpochValues() {
      val epochInstant = Instant.EPOCH
      val result = DateTimeConverter.instantToLocalDateTime(epochInstant, utcZone)
      assertEquals(LocalDateTime.of(1970, 1, 1, 0, 0, 0), result)
    }

    @Test
    fun handlesInternationalDateLine() {
      val eastZone = ZoneId.of("Pacific/Kiritimati") // UTC+14
      val westZone = ZoneId.of("Pacific/Niue") // UTC-11

      val testInstant = Instant.parse("2023-01-01T00:00:00Z")

      val eastResult = DateTimeConverter.instantToLocalDateTime(testInstant, eastZone)
      val westResult = DateTimeConverter.instantToLocalDateTime(testInstant, westZone)

      // Eastern hemisphere should observe January 1st 14:00
      assertEquals(LocalDateTime.of(2023, 1, 1, 14, 0, 0), eastResult)

      // Western hemisphere should observe December 31st 13:00
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
