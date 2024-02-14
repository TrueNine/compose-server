package net.yan100.compose.core.lang

import net.yan100.compose.core.lang.DTimer.ZONE_GMT
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
    fun plusMillis(current: Long, plusMillis: Long = System.currentTimeMillis()): Date {
        return Date(current + plusMillis)
    }

    @JvmStatic
    fun localTimeToDate(lt: LocalTime, zoneId: ZoneId = ZoneId.of("GMT")): Date {
        val meta = LocalDate.of(1970, 1, 1)
        val ldt = lt.atDate(meta)
        return Date.from(
            ldt.atZone(zoneId)
                .toInstant()
        )
    }

    @JvmStatic
    fun localDateToDate(ld: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Date {
        return Date.from(
            ld.atStartOfDay().atZone(zoneId)
                .toInstant()
        )
    }

    @JvmStatic
    fun localDatetimeToDate(ldt: LocalDateTime, zoneId: ZoneId = ZoneId.systemDefault()): Date {
        return Date.from(
            ldt.atZone(zoneId)
                .toInstant()
        )
    }

    @JvmStatic
    fun dateToLocalDatetime(date: Date, zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
        return date.toInstant()
            .atZone(zoneId)
            .toLocalDateTime()
    }

    @JvmStatic
    fun millisToLocalDateTime(millis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
        return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDateTime()
    }

    @JvmStatic
    fun dateToLocalDate(date: Date, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
        return date.toInstant().atZone(zoneId)
            .toLocalDate()
    }

    @JvmStatic
    fun millisToLocalDate(millis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
        return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
    }

    @JvmStatic
    fun dateToLocalTime(date: Date, zoneId: ZoneId = ZoneId.of(ZONE_GMT)): LocalTime {
        return date.toInstant().atZone(zoneId).toLocalTime()
    }

    @JvmStatic
    fun millisToLocalTime(millis: Long, zoneId: ZoneId = ZoneId.of(ZONE_GMT)): LocalTime {
        return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalTime()
    }
}

fun Long.toDate(): Date {
    return Date(this)
}

fun Long.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return DTimer.millisToLocalDateTime(this, zoneId)
}

fun Long.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
    return DTimer.millisToLocalDate(this)
}

fun Long.toLocalTime(zoneId: ZoneId = ZoneId.of(ZONE_GMT)): LocalTime {
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

fun Date.toLocalTime(zoneId: ZoneId = ZoneId.of(ZONE_GMT)): LocalTime {
    return DTimer.dateToLocalTime(this, zoneId)
}

fun LocalTime.toDate(zoneId: ZoneId = ZoneId.of(ZONE_GMT)): Date {
    return DTimer.localTimeToDate(this, zoneId)
}

fun LocalDate.toDate(zoneId: ZoneId = ZoneId.systemDefault()): Date {
    return DTimer.localDateToDate(this, zoneId)
}

fun LocalDateTime.toDate(zoneId: ZoneId = ZoneId.systemDefault()): Date {
    return DTimer.localDatetimeToDate(this, zoneId)
}
