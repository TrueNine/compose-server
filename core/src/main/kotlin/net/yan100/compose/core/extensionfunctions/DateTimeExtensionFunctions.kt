/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.extensionfunctions

import java.time.*
import java.util.*
import net.yan100.compose.core.util.DTimer

fun Long.toDate(): Date {
  return Date(this)
}

fun Long.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
  return DTimer.millisToLocalDateTime(this, zoneId)
}

fun Long.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
  return DTimer.millisToLocalDate(this)
}

fun Long.toLocalTime(zoneId: ZoneId = ZoneId.of(DTimer.ZONE_GMT)): LocalTime {
  return DTimer.millisToLocalTime(this)
}

fun Date.toLong(): Long {
  return this.time
}

fun Date.toLocalDatetime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
  return DTimer.dateToLocalDatetime(this, zoneId)
}

fun Date.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
  return DTimer.dateToLocalDate(this, zoneId)
}

fun Date.toLocalTime(zoneId: ZoneId = ZoneId.of(DTimer.ZONE_GMT)): LocalTime {
  return DTimer.dateToLocalTime(this, zoneId)
}

fun LocalTime.toDate(zoneId: ZoneId = ZoneId.of(DTimer.ZONE_GMT)): Date {
  return DTimer.localTimeToDate(this, zoneId)
}

fun LocalDate.toDate(zoneId: ZoneId = ZoneId.systemDefault()): Date {
  return DTimer.localDateToDate(this, zoneId)
}

fun LocalDateTime.toDate(zoneId: ZoneId = ZoneId.systemDefault()): Date {
  return DTimer.localDatetimeToDate(this, zoneId)
}

fun LocalDateTime.toLong(): Long {
  return DTimer.localDatetimeToMillis(this)
}

/** # ISO8601 时间戳毫秒标准 */
val LocalDateTime.iso8601LongUtc: Long
  get() = this.toInstant(ZoneOffset.UTC).toEpochMilli()

/** # ISO8601 时间戳 秒 标准 */
val LocalDateTime.iso8601LongUtcSecond: Long
  get() = this.toEpochSecond(ZoneOffset.UTC)
