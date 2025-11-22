package io.github.truenine.composeserver

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

/**
 * Convert millisecond timestamp to LocalDateTime
 *
 * @param zoneId Timezone
 * @return LocalDateTime object
 */
fun Long.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
  return DateTimeConverter.millisToLocalDateTime(this, zoneId)
}

/**
 * Convert millisecond timestamp to LocalDate
 *
 * @param zoneId Timezone
 * @return LocalDate object
 */
fun Long.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
  return DateTimeConverter.millisToLocalDate(this, zoneId)
}

/**
 * Convert millisecond timestamp to LocalTime
 *
 * @param zoneId Timezone
 * @return LocalTime object
 */
fun Long.toLocalTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalTime {
  return DateTimeConverter.millisToLocalTime(this, zoneId)
}

/**
 * Convert millisecond timestamp to Instant
 *
 * @return Instant object
 */
fun Long.toInstant(): Instant {
  return Instant.ofEpochMilli(this)
}

/**
 * Convert LocalTime to millisecond timestamp.
 *
 * @param zoneId Timezone
 * @return Millisecond timestamp
 */
fun LocalTime.toMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
  return DateTimeConverter.localTimeToInstant(this, zoneId).toEpochMilli()
}

/**
 * Convert LocalDate to millisecond timestamp.
 *
 * @param zoneId Timezone
 * @return Millisecond timestamp
 */
fun LocalDate.toMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
  return DateTimeConverter.localDateToInstant(this, zoneId).toEpochMilli()
}

/**
 * Convert LocalDateTime to millisecond timestamp.
 *
 * @param zoneId Timezone
 * @return Millisecond timestamp
 */
fun LocalDateTime.toMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
  return DateTimeConverter.localDatetimeToMillis(this, zoneId)
}

/** # ISO8601 millisecond timestamp (UTC) */
val LocalDateTime.iso8601LongUtc: Long
  get() = toInstant(ZoneOffset.UTC).toEpochMilli()

/** # ISO8601 second-level timestamp (UTC) */
val LocalDateTime.iso8601LongUtcSecond: Long
  get() = this.toEpochSecond(ZoneOffset.UTC)

/**
 * Calculate duration between two LocalDateTime values.
 *
 * @param other Subtrahend LocalDateTime
 * @return Duration between the two values
 */
operator fun LocalDateTime.minus(other: LocalDateTime): Duration = Duration.between(other, this)

/**
 * Calculate period between two LocalDate values.
 *
 * @param other Subtrahend LocalDate
 * @return Period between the two values
 */
operator fun LocalDate.minus(other: LocalDate): Period = Period.between(other, this)

/**
 * Get LocalDateTime of the first day of the current month.
 *
 * @return LocalDateTime representing the first day of current month
 */
fun nowDateTimeFirstDayOfMonth(): LocalDateTime = LocalDateTime.now().firstDayOfMonth()

/**
 * Get LocalDateTime of the first day of the month for the given LocalDateTime.
 *
 * @return LocalDateTime representing the first day of the month
 */
fun LocalDateTime.firstDayOfMonth(): LocalDateTime = with(TemporalAdjusters.firstDayOfMonth())

/**
 * Get LocalDateTime of the last day of the current month.
 *
 * @return LocalDateTime representing the last day of current month
 */
fun nowDateTimeLastDayOfMonth(): LocalDateTime = LocalDateTime.now().lastDayOfMonth()

/**
 * Get LocalDateTime of the last day of the month for the given LocalDateTime.
 *
 * @return LocalDateTime representing the last day of the month
 */
fun LocalDateTime.lastDayOfMonth(): LocalDateTime = with(TemporalAdjusters.lastDayOfMonth())

/**
 * Get LocalDate of the first day of the current month.
 *
 * @return LocalDate representing the first day of current month
 */
fun nowDateFirstDayOfMonth(): LocalDate = LocalDate.now().firstDayOfMonth()

/**
 * Get LocalDate of the first day of the month for the given LocalDate.
 *
 * @return LocalDate representing the first day of the month
 */
fun LocalDate.firstDayOfMonth(): LocalDate = with(TemporalAdjusters.firstDayOfMonth())

/**
 * Get LocalDate of the last day of the month for the given LocalDate.
 *
 * @return LocalDate representing the last day of the month
 */
fun LocalDate.lastDayOfMonth(): LocalDate = with(TemporalAdjusters.lastDayOfMonth())

/**
 * Convert Instant to LocalDateTime.
 *
 * @param zoneId Timezone
 * @return LocalDateTime instance
 */
fun Instant.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
  return DateTimeConverter.instantToLocalDateTime(this, zoneId)
}

/**
 * Convert Instant to LocalDate.
 *
 * @param zoneId Timezone
 * @return LocalDate instance
 */
fun Instant.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
  return DateTimeConverter.instantToLocalDate(this, zoneId)
}

/**
 * Convert Instant to LocalTime.
 *
 * @param zoneId Timezone
 * @return LocalTime instance
 */
fun Instant.toLocalTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalTime {
  return DateTimeConverter.instantToLocalTime(this, zoneId)
}

/**
 * Convert Instant to millisecond timestamp.
 *
 * @return Millisecond timestamp
 */
fun Instant.toMillis(): Long {
  return DateTimeConverter.instantToMillis(this)
}

/**
 * Get current time as Instant.
 *
 * @return Current Instant
 */
fun now(): Instant {
  return Instant.now()
}

/**
 * Get current millisecond timestamp.
 *
 * @return Current time in milliseconds
 */
fun nowMillis(): Long {
  return System.currentTimeMillis()
}
