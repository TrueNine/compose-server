package io.github.truenine.composeserver

import io.github.truenine.composeserver.DateTimeConverter.plusMillis
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Date and time converter providing comprehensive conversion operations between different time types.
 *
 * This converter provides comprehensive date/time conversion methods between different Java time types (Instant, LocalDateTime, LocalDate, LocalTime) and
 * millisecond timestamps, with timezone support.
 *
 * @author TrueNine
 * @since 2022-12-16
 */
object DateTimeConverter {
  /**
   * GMT timezone identifier
   *
   * @deprecated Use ZoneId.systemDefault() or specific timezone instead of hardcoded GMT
   */
  @Deprecated("Use ZoneId.systemDefault() or specific timezone instead of hardcoded GMT", ReplaceWith("ZoneId.systemDefault()")) const val ZONE_GMT = "Etc/GMT"

  /** Standard date format pattern */
  const val DATE = "yyyy-MM-dd"

  /** Standard time format pattern */
  const val TIME = "HH:mm:ss"

  /** Standard datetime format pattern */
  const val DATETIME = "$DATE $TIME"

  /**
   * Creates an Instant by adding milliseconds to the current system time.
   *
   * This method is useful for creating future timestamps relative to the current moment, commonly used for expiration times or scheduling.
   *
   * @param plusMillis the number of milliseconds to add to current time
   * @return an Instant representing the calculated future time
   * @sample
   *
   * ```kotlin
   * // Create a timestamp 5 minutes from now
   * val futureTime = DTimer.plusMillisFromCurrent(5 * 60 * 1000)
   * ```
   */
  @JvmStatic fun plusMillisFromCurrent(plusMillis: Long): Instant = Instant.ofEpochMilli(System.currentTimeMillis() + plusMillis)

  /**
   * Creates an Instant by adding milliseconds to a base timestamp.
   *
   * This method allows for flexible time calculations based on any reference point, with the current time as the default addend for convenience.
   *
   * @param current the base timestamp in milliseconds since epoch
   * @param plusMillis the number of milliseconds to add (defaults to current system time)
   * @return an Instant representing the calculated time
   * @sample
   *
   * ```kotlin
   * // Add 1 hour to a specific timestamp
   * val baseTime = 1640995200000L // 2022-01-01 00:00:00 UTC
   * val result = DTimer.plusMillis(baseTime, 3600000L)
   * ```
   */
  @JvmStatic @JvmOverloads fun plusMillis(current: Long, plusMillis: Long = System.currentTimeMillis()): Instant = Instant.ofEpochMilli(current + plusMillis)

  /**
   * Converts a LocalTime to an Instant using epoch date (1970-01-01) as the date component.
   *
   * This conversion is useful when you need to work with time-only values in an Instant context, such as for time-based calculations or comparisons.
   *
   * @param lt the LocalTime to convert
   * @param zoneId the timezone to use for conversion (defaults to system default)
   * @return an Instant representing the time on epoch date in the specified timezone
   * @sample
   *
   * ```kotlin
   * val timeOnly = LocalTime.of(14, 30, 0) // 2:30 PM
   * val instant = DTimer.localTimeToInstant(timeOnly)
   * // Results in 1970-01-01T14:30:00 in system timezone
   * ```
   */
  @JvmStatic
  @JvmOverloads
  fun localTimeToInstant(lt: LocalTime, zoneId: ZoneId = ZoneId.systemDefault()): Instant {
    val meta = LocalDate.of(1970, 1, 1)
    return lt.atDate(meta).atZone(zoneId).toInstant()
  }

  /**
   * Converts a LocalDate to an Instant representing the start of that date.
   *
   * The conversion uses the start of day (00:00:00) in the specified timezone, which is essential for date-based queries and operations.
   *
   * @param ld the LocalDate to convert
   * @param zoneId the timezone to use for conversion (defaults to system default)
   * @return an Instant representing the start of the specified date
   * @sample
   *
   * ```kotlin
   * val date = LocalDate.of(2023, 12, 25)
   * val instant = DTimer.localDateToInstant(date)
   * // Results in 2023-12-25T00:00:00 in system timezone
   * ```
   */
  @JvmStatic
  @JvmOverloads
  fun localDateToInstant(ld: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Instant = ld.atStartOfDay().atZone(zoneId).toInstant()

  /**
   * Converts a LocalDateTime to an Instant using the specified timezone.
   *
   * This is the most direct conversion for datetime values that need to be stored or transmitted as UTC timestamps while preserving the original timezone
   * context.
   *
   * @param ldt the LocalDateTime to convert
   * @param zoneId the timezone to use for conversion (defaults to system default)
   * @return an Instant representing the datetime in UTC
   * @sample
   *
   * ```kotlin
   * val dateTime = LocalDateTime.of(2023, 12, 25, 15, 30, 0)
   * val instant = DTimer.localDatetimeToInstant(dateTime, ZoneId.of("Asia/Shanghai"))
   * ```
   */
  @JvmStatic @JvmOverloads fun localDatetimeToInstant(ldt: LocalDateTime, zoneId: ZoneId = ZoneId.systemDefault()): Instant = ldt.atZone(zoneId).toInstant()

  /**
   * Converts a millisecond timestamp to a LocalDateTime in the specified timezone.
   *
   * This conversion is fundamental for displaying timestamps in user-friendly formats while respecting timezone preferences.
   *
   * @param millis the timestamp in milliseconds since epoch
   * @param zoneId the timezone to use for conversion (defaults to system default)
   * @return a LocalDateTime representing the timestamp in the specified timezone
   * @sample
   *
   * ```kotlin
   * val timestamp = 1640995200000L // 2022-01-01 00:00:00 UTC
   * val dateTime = DTimer.millisToLocalDateTime(timestamp, ZoneId.of("Asia/Tokyo"))
   * ```
   */
  @JvmStatic
  @JvmOverloads
  fun millisToLocalDateTime(millis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDateTime()

  /**
   * Converts a millisecond timestamp to a LocalDate in the specified timezone.
   *
   * This method extracts only the date component from a timestamp, useful for date-based filtering and grouping operations.
   *
   * @param millis the timestamp in milliseconds since epoch
   * @param zoneId the timezone to use for conversion (defaults to system default)
   * @return a LocalDate representing the date portion of the timestamp
   * @sample
   *
   * ```kotlin
   * val timestamp = 1640995200000L // 2022-01-01 00:00:00 UTC
   * val date = DTimer.millisToLocalDate(timestamp, ZoneId.of("America/New_York"))
   * ```
   */
  @JvmStatic
  @JvmOverloads
  fun millisToLocalDate(millis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate = Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()

  /**
   * Converts an Instant to a LocalDateTime in the specified timezone.
   *
   * This conversion is essential for displaying UTC timestamps in local time formats while maintaining timezone awareness for user interfaces.
   *
   * @param instant the Instant to convert
   * @param zoneId the timezone to use for conversion (defaults to system default)
   * @return a LocalDateTime representing the instant in the specified timezone
   * @sample
   *
   * ```kotlin
   * val instant = Instant.now()
   * val localDateTime = DTimer.instantToLocalDateTime(instant, ZoneId.of("Europe/London"))
   * ```
   */
  @JvmStatic
  @JvmOverloads
  fun instantToLocalDateTime(instant: Instant, zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime = instant.atZone(zoneId).toLocalDateTime()

  /**
   * Converts an Instant to a LocalDate in the specified timezone.
   *
   * This method is particularly useful for date-based operations where you need to determine which calendar date an instant falls on in a specific timezone.
   *
   * @param instant the Instant to convert
   * @param zoneId the timezone to use for conversion (defaults to system default)
   * @return a LocalDate representing the date of the instant in the specified timezone
   * @sample
   *
   * ```kotlin
   * val instant = Instant.parse("2023-12-31T23:30:00Z")
   * val date = DTimer.instantToLocalDate(instant, ZoneId.of("Asia/Tokyo"))
   * // May result in 2024-01-01 due to timezone offset
   * ```
   */
  @JvmStatic @JvmOverloads fun instantToLocalDate(instant: Instant, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate = instant.atZone(zoneId).toLocalDate()

  /**
   * Converts an Instant to a LocalTime in the specified timezone.
   *
   * This conversion extracts only the time component, useful for time-based analysis and display where the date is not relevant.
   *
   * @param instant the Instant to convert
   * @param zoneId the timezone to use for conversion (defaults to system default)
   * @return a LocalTime representing the time portion of the instant
   * @sample
   *
   * ```kotlin
   * val instant = Instant.parse("2023-12-25T15:30:45Z")
   * val time = DTimer.instantToLocalTime(instant, ZoneId.systemDefault())
   * // Results in time in system timezone
   * ```
   */
  @JvmStatic @JvmOverloads fun instantToLocalTime(instant: Instant, zoneId: ZoneId = ZoneId.systemDefault()): LocalTime = instant.atZone(zoneId).toLocalTime()

  /**
   * Converts a LocalDateTime to a millisecond timestamp using the specified timezone.
   *
   * This conversion is crucial for storing datetime values as timestamps while preserving the original timezone context for accurate time calculations.
   *
   * @param datetime the LocalDateTime to convert
   * @param zoneId the timezone to interpret the datetime in (defaults to UTC)
   * @return the timestamp in milliseconds since epoch
   * @sample
   *
   * ```kotlin
   * val dateTime = LocalDateTime.of(2023, 12, 25, 15, 30, 0)
   * val millis = DTimer.localDatetimeToMillis(dateTime, ZoneId.of("Asia/Shanghai"))
   * ```
   */
  @JvmStatic
  @JvmOverloads
  fun localDatetimeToMillis(datetime: LocalDateTime, zoneId: ZoneId = ZoneOffset.UTC): Long = datetime.atZone(zoneId).toInstant().toEpochMilli()

  /**
   * Converts a millisecond timestamp to a LocalTime in the specified timezone.
   *
   * This method extracts only the time component from a timestamp, useful for time-based operations where the date component is not needed.
   *
   * @param millis the timestamp in milliseconds since epoch
   * @param zoneId the timezone to use for conversion (defaults to system default)
   * @return a LocalTime representing the time portion of the timestamp
   * @sample
   *
   * ```kotlin
   * val timestamp = 1640995200000L // 2022-01-01 00:00:00 UTC
   * val time = DTimer.millisToLocalTime(timestamp, ZoneId.systemDefault())
   * // Results in time in system timezone
   * ```
   */
  @JvmStatic
  @JvmOverloads
  fun millisToLocalTime(millis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalTime = Instant.ofEpochMilli(millis).atZone(zoneId).toLocalTime()

  /**
   * Converts an Instant to a millisecond timestamp.
   *
   * This is a direct conversion that extracts the epoch millisecond value from an Instant, commonly used for database storage and API serialization.
   *
   * @param instant the Instant to convert
   * @return the timestamp in milliseconds since epoch
   * @sample
   *
   * ```kotlin
   * val instant = Instant.now()
   * val millis = DTimer.instantToMillis(instant)
   * ```
   */
  @JvmStatic fun instantToMillis(instant: Instant): Long = instant.toEpochMilli()
}
