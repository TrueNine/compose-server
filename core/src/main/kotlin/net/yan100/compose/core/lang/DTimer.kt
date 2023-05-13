package net.yan100.compose.core.lang

import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * 时间工具类
 *
 * @author TrueNine
 * @since 2022-12-16
 */
object DTimer {
  const val MILLIS = "SSS"
  const val DATE = "yyyy-MM-dd"
  const val TIME = "HH:mm:ss"
  const val DATETIME = DATE + " " + TIME
  const val DATETIME_M = DATETIME + " " + MILLIS
  const val TIME_M = TIME + " " + MILLIS
  const val DATE_S = "yyyy/MM/dd"
  const val DATETIME_S = DATE_S + " " + TIME
  const val DATETIME_M_S = DATETIME_S + " " + MILLIS

  @JvmStatic
  fun sleepMillis(millis: Long) {
    try {
      Thread.sleep(millis)
    } catch (e: InterruptedException) {
      throw RuntimeException(e)
    }
  }

  @JvmStatic
  fun sleep(second: Long) {
    sleepMillis(second * 1000)
  }

  @JvmStatic
  fun sleepOne() {
    sleep(1)
  }

  @JvmStatic
  fun ofSecondDuration(second: Long): Duration {
    return Duration.of(second, ChronoUnit.SECONDS)
  }

  @JvmStatic
  fun ofMillisDuration(millis: Long): Duration {
    return Duration.of(millis, ChronoUnit.MILLIS)
  }

  @JvmStatic
  fun plusMillisFromCurrent(plusMillis: Long): Date {
    return plusMillis(System.currentTimeMillis(), plusMillis)
  }

  @JvmStatic
  fun plusMillis(current: Long, plusMillis: Long): Date {
    return Date(current + plusMillis)
  }

  @JvmStatic
  fun localTimeToDate(lt: LocalTime): Date {
    val meta = LocalDate.of(1970, 1, 1)
    val ldt = lt.atDate(meta)
    return Date.from(
      ldt.atZone(ZoneId.of("GMT"))
        .toInstant()
    )
  }

  @JvmStatic
  fun localDateToDate(ld: LocalDate): Date {
    return Date.from(
      ld.atStartOfDay().atZone(ZoneId.systemDefault())
        .toInstant()
    )
  }

  @JvmStatic
  fun localDatetimeToDate(ldt: LocalDateTime): Date {
    return Date.from(
      ldt.atZone(ZoneId.systemDefault())
        .toInstant()
    )
  }

  @JvmStatic
  fun dateToLocalDatetime(date: Date): LocalDateTime = date.toInstant()
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime()

  @JvmStatic
  fun dateToLocalDate(date: Date) =
    date.toInstant().atZone(ZoneId.systemDefault())
      .toLocalDate()

  @JvmStatic
  fun dateToLocalTime(date: Date): LocalTime =
    date.toInstant().atZone(ZoneId.of("GMT"))
      .toLocalTime()
}
