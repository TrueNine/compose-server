/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core

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
  const val ZONE_UTC = "UTC"
  const val ZONE_GMT = "Etc/GMT"
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
  fun getDurationBySecond(second: Long): Duration {
    return Duration.of(second, ChronoUnit.SECONDS)
  }

  @JvmStatic
  fun getDurationByMillis(millis: Long): Duration {
    return Duration.of(millis, ChronoUnit.MILLIS)
  }

  @JvmStatic
  fun plusMillisFromCurrent(plusMillis: Long): Date {
    return plusMillis(System.currentTimeMillis(), plusMillis)
  }

  @JvmStatic
  @JvmOverloads
  fun plusMillis(current: Long, plusMillis: Long = System.currentTimeMillis()): Date {
    return Date(current + plusMillis)
  }

  @JvmStatic
  @JvmOverloads
  fun localTimeToDate(lt: LocalTime, zoneId: ZoneId = ZoneId.of("GMT")): Date {
    val meta = LocalDate.of(1970, 1, 1)
    val ldt = lt.atDate(meta)
    return Date.from(ldt.atZone(zoneId).toInstant())
  }

  @JvmStatic
  @JvmOverloads
  fun localDateToDate(ld: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Date {
    return Date.from(ld.atStartOfDay().atZone(zoneId).toInstant())
  }

  @JvmStatic
  @JvmOverloads
  fun localDatetimeToDate(ldt: LocalDateTime, zoneId: ZoneId = ZoneId.systemDefault()): Date {
    return Date.from(ldt.atZone(zoneId).toInstant())
  }

  @JvmStatic
  @JvmOverloads
  fun dateToLocalDatetime(date: Date, zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return date.toInstant().atZone(zoneId).toLocalDateTime()
  }

  @JvmStatic
  @JvmOverloads
  fun millisToLocalDateTime(millis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDateTime()
  }

  @JvmStatic
  @JvmOverloads
  fun dateToLocalDate(date: Date, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
    return date.toInstant().atZone(zoneId).toLocalDate()
  }

  @JvmStatic
  @JvmOverloads
  fun millisToLocalDate(millis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
  }

  @JvmStatic
  @JvmOverloads
  fun dateToLocalTime(date: Date, zoneId: ZoneId = ZoneId.of(ZONE_GMT)): LocalTime {
    return date.toInstant().atZone(zoneId).toLocalTime()
  }

  @JvmStatic
  @JvmOverloads
  fun localDatetimeToMillis(datetime: LocalDateTime, zoneId: ZoneId = ZoneOffset.UTC): Long {
    return datetime.atZone(zoneId).toInstant().toEpochMilli()
  }

  @JvmStatic
  @JvmOverloads
  fun millisToLocalTime(millis: Long, zoneId: ZoneId = ZoneId.of(ZONE_GMT)): LocalTime {
    return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalTime()
  }
}
