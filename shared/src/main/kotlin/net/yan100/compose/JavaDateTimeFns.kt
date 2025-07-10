package net.yan100.compose

import java.time.*
import java.time.temporal.TemporalAdjusters

/**
 * 毫秒时间戳转LocalDateTime
 *
 * @param zoneId 时区
 * @return LocalDateTime对象
 */
fun Long.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
  return DTimer.millisToLocalDateTime(this, zoneId)
}

/**
 * 毫秒时间戳转LocalDate
 *
 * @param zoneId 时区
 * @return LocalDate对象
 */
fun Long.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
  return DTimer.millisToLocalDate(this, zoneId)
}

/**
 * 毫秒时间戳转LocalTime
 *
 * @param zoneId 时区
 * @return LocalTime对象
 */
fun Long.toLocalTime(zoneId: ZoneId = ZoneId.of(DTimer.ZONE_GMT)): LocalTime {
  return DTimer.millisToLocalTime(this, zoneId)
}

/**
 * 毫秒时间戳转Instant
 *
 * @return Instant对象
 */
fun Long.toInstant(): Instant {
  return Instant.ofEpochMilli(this)
}

/**
 * LocalTime转毫秒时间戳
 *
 * @param zoneId 时区
 * @return 毫秒时间戳
 */
fun LocalTime.toMillis(zoneId: ZoneId = ZoneId.of(DTimer.ZONE_GMT)): Long {
  return DTimer.localTimeToInstant(this, zoneId).toEpochMilli()
}

/**
 * LocalDate转毫秒时间戳
 *
 * @param zoneId 时区
 * @return 毫秒时间戳
 */
fun LocalDate.toMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
  return DTimer.localDateToInstant(this, zoneId).toEpochMilli()
}

/**
 * LocalDateTime转毫秒时间戳
 *
 * @param zoneId 时区
 * @return 毫秒时间戳
 */
fun LocalDateTime.toMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
  return DTimer.localDatetimeToMillis(this, zoneId)
}

/** # ISO8601 时间戳毫秒标准 */
val LocalDateTime.iso8601LongUtc: Long
  get() = toInstant(ZoneOffset.UTC).toEpochMilli()

/** # ISO8601 时间戳 秒 标准 */
val LocalDateTime.iso8601LongUtcSecond: Long
  get() = this.toEpochSecond(ZoneOffset.UTC)

operator fun LocalDateTime.minus(other: LocalDateTime): Duration {
  return Duration.between(this, other)
}

operator fun LocalDate.minus(other: LocalDate): Period {
  return Period.between(this, other)
}

fun nowDatetimeFirstDayOfMonth(): LocalDateTime = LocalDateTime.now().firstDayOfMonth()

fun LocalDateTime.firstDayOfMonth(): LocalDateTime = with(TemporalAdjusters.firstDayOfMonth())

fun nowDatetimeLastDayOfMonth(): LocalDateTime = LocalDateTime.now().lastDayOfMonth()

fun LocalDateTime.lastDayOfMonth(): LocalDateTime = with(TemporalAdjusters.lastDayOfMonth())

fun nowDateFirstDayOfMonth(): LocalDate = LocalDate.now().lastDayOfMonth()

fun LocalDate.lastDayOfMonth(): LocalDate = with(TemporalAdjusters.lastDayOfMonth())

/**
 * Instant转LocalDateTime
 *
 * @param zoneId 时区
 * @return LocalDateTime对象
 */
fun Instant.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
  return DTimer.instantToLocalDateTime(this, zoneId)
}

/**
 * Instant转LocalDate
 *
 * @param zoneId 时区
 * @return LocalDate对象
 */
fun Instant.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
  return DTimer.instantToLocalDate(this, zoneId)
}

/**
 * Instant转LocalTime
 *
 * @param zoneId 时区
 * @return LocalTime对象
 */
fun Instant.toLocalTime(zoneId: ZoneId = ZoneId.of(DTimer.ZONE_GMT)): LocalTime {
  return DTimer.instantToLocalTime(this, zoneId)
}

/**
 * Instant转毫秒时间戳
 *
 * @return 毫秒时间戳
 */
fun Instant.toMillis(): Long {
  return DTimer.instantToMillis(this)
}

/**
 * 获取当前时间的Instant对象
 *
 * @return 当前时间的Instant
 */
fun now(): Instant {
  return Instant.now()
}

/**
 * 获取当前毫秒时间戳
 *
 * @return 当前时间的毫秒时间戳
 */
fun nowMillis(): Long {
  return System.currentTimeMillis()
}
