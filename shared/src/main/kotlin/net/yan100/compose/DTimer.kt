package net.yan100.compose

import java.time.*

/**
 * # 时间工具类
 *
 * 提供日期时间转换、格式化等常用操作工具
 *
 * @author TrueNine
 * @since 2022-12-16
 */
object DTimer {
  const val ZONE_GMT = "Etc/GMT"
  const val DATE = "yyyy-MM-dd"
  const val TIME = "HH:mm:ss"
  const val DATETIME = "$DATE $TIME"

  /**
   * 从当前时间增加指定毫秒数
   *
   * @param plusMillis 要增加的毫秒数
   * @return 增加后的Instant对象
   */
  @JvmStatic
  fun plusMillisFromCurrent(plusMillis: Long): Instant = 
    Instant.ofEpochMilli(System.currentTimeMillis() + plusMillis)

  /**
   * 在指定时间基础上增加毫秒数
   *
   * @param current 基准时间戳（毫秒）
   * @param plusMillis 要增加的毫秒数
   * @return 增加后的Instant对象
   */
  @JvmStatic
  @JvmOverloads
  fun plusMillis(
    current: Long,
    plusMillis: Long = System.currentTimeMillis(),
  ): Instant = Instant.ofEpochMilli(current + plusMillis)

  /**
   * 将LocalTime转换为Instant对象（基于1970-01-01）
   *
   * @param lt 要转换的LocalTime
   * @param zoneId 时区
   * @return 转换后的Instant对象
   */
  @JvmStatic
  @JvmOverloads
  fun localTimeToInstant(lt: LocalTime, zoneId: ZoneId = ZoneId.of(ZONE_GMT)): Instant {
    val meta = LocalDate.of(1970, 1, 1)
    return lt.atDate(meta).atZone(zoneId).toInstant()
  }

  /**
   * 将LocalDate转换为Instant对象
   *
   * @param ld 要转换的LocalDate
   * @param zoneId 时区
   * @return 转换后的Instant对象
   */
  @JvmStatic
  @JvmOverloads
  fun localDateToInstant(
    ld: LocalDate,
    zoneId: ZoneId = ZoneId.systemDefault(),
  ): Instant = ld.atStartOfDay().atZone(zoneId).toInstant()

  /**
   * 将LocalDateTime转换为Instant对象
   *
   * @param ldt 要转换的LocalDateTime
   * @param zoneId 时区
   * @return 转换后的Instant对象
   */
  @JvmStatic
  @JvmOverloads
  fun localDatetimeToInstant(
    ldt: LocalDateTime,
    zoneId: ZoneId = ZoneId.systemDefault(),
  ): Instant = ldt.atZone(zoneId).toInstant()

  /**
   * 将毫秒时间戳转换为LocalDateTime对象
   *
   * @param millis 毫秒时间戳
   * @param zoneId 时区
   * @return 转换后的LocalDateTime对象
   */
  @JvmStatic
  @JvmOverloads
  fun millisToLocalDateTime(
    millis: Long,
    zoneId: ZoneId = ZoneId.systemDefault(),
  ): LocalDateTime = Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDateTime()

  /**
   * 将毫秒时间戳转换为LocalDate对象
   *
   * @param millis 毫秒时间戳
   * @param zoneId 时区
   * @return 转换后的LocalDate对象
   */
  @JvmStatic
  @JvmOverloads
  fun millisToLocalDate(
    millis: Long,
    zoneId: ZoneId = ZoneId.systemDefault(),
  ): LocalDate = Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()

  /**
   * 将Instant转换为LocalDateTime对象
   *
   * @param instant 要转换的Instant
   * @param zoneId 时区
   * @return 转换后的LocalDateTime对象
   */
  @JvmStatic
  @JvmOverloads
  fun instantToLocalDateTime(
    instant: Instant,
    zoneId: ZoneId = ZoneId.systemDefault(),
  ): LocalDateTime = instant.atZone(zoneId).toLocalDateTime()

  /**
   * 将Instant转换为LocalDate对象
   *
   * @param instant 要转换的Instant
   * @param zoneId 时区
   * @return 转换后的LocalDate对象
   */
  @JvmStatic
  @JvmOverloads
  fun instantToLocalDate(
    instant: Instant,
    zoneId: ZoneId = ZoneId.systemDefault(),
  ): LocalDate = instant.atZone(zoneId).toLocalDate()

  /**
   * 将Instant转换为LocalTime对象
   *
   * @param instant 要转换的Instant
   * @param zoneId 时区
   * @return 转换后的LocalTime对象
   */
  @JvmStatic
  @JvmOverloads
  fun instantToLocalTime(
    instant: Instant,
    zoneId: ZoneId = ZoneId.of(ZONE_GMT),
  ): LocalTime = instant.atZone(zoneId).toLocalTime()

  /**
   * 将LocalDateTime转换为毫秒时间戳
   *
   * @param datetime 要转换的LocalDateTime
   * @param zoneId 时区
   * @return 毫秒时间戳
   */
  @JvmStatic
  @JvmOverloads
  fun localDatetimeToMillis(
    datetime: LocalDateTime,
    zoneId: ZoneId = ZoneOffset.UTC,
  ): Long = datetime.atZone(zoneId).toInstant().toEpochMilli()

  /**
   * 将毫秒时间戳转换为LocalTime对象
   *
   * @param millis 毫秒时间戳
   * @param zoneId 时区
   * @return 转换后的LocalTime对象
   */
  @JvmStatic
  @JvmOverloads
  fun millisToLocalTime(
    millis: Long,
    zoneId: ZoneId = ZoneId.of(ZONE_GMT),
  ): LocalTime = Instant.ofEpochMilli(millis).atZone(zoneId).toLocalTime()

  /**
   * 将Instant转换为毫秒时间戳
   *
   * @param instant 要转换的Instant
   * @return 毫秒时间戳
   */
  @JvmStatic
  fun instantToMillis(instant: Instant): Long = instant.toEpochMilli()
}
