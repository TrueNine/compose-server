package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import net.yan100.compose.toLocalDate
import net.yan100.compose.toLocalDateTime
import net.yan100.compose.toLocalTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.temporal.Temporal

/**
 * ISO8601时间反序列化抽象基类
 *
 * 用于处理时间戳字符串反序列化为Java时间类型(LocalDate, LocalTime, LocalDateTime等)
 * @param T 目标时间类型
 * @param zoneOffset 时区偏移量，用于时间戳转换
 */
abstract class ISO8601Deserializer<T : Temporal>(protected val zoneOffset: ZoneOffset) :
  JsonDeserializer<T>() {

  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): T? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    return timestamp?.let { convertTimestamp(it) }
  }

  /**
   * 将时间戳转换为目标时间类型
   * @param timestamp 时间戳
   * @return 转换后的时间对象
   */
  protected abstract fun convertTimestamp(timestamp: Long): T

  /**
   * LocalDate 类型的反序列化器
   */
  class LocalDateDeserializerX(zoneOffset: ZoneOffset) : ISO8601Deserializer<LocalDate>(zoneOffset) {
    override fun convertTimestamp(timestamp: Long): LocalDate {
      return timestamp.toLocalDate(zoneOffset)
    }
  }

  /**
   * LocalDateTime 类型的反序列化器
   */
  class LocalDateTimeDeserializerZ(zoneOffset: ZoneOffset) : ISO8601Deserializer<LocalDateTime>(zoneOffset) {
    override fun convertTimestamp(timestamp: Long): LocalDateTime {
      return timestamp.toLocalDateTime(zoneOffset)
    }
  }

  /**
   * LocalTime 类型的反序列化器
   */
  class LocalTimeDeserializerY(zoneOffset: ZoneOffset) : ISO8601Deserializer<LocalTime>(zoneOffset) {
    override fun convertTimestamp(timestamp: Long): LocalTime {
      return timestamp.toLocalTime(zoneOffset)
    }
  }

  companion object {
    /**
     * 向后兼容别名
     */
    @JvmField
    val LocalDateDeserializer = LocalDateDeserializerX::class.java

    /**
     * 向后兼容别名
     */
    @JvmField
    val LocalDateTimeDeserializer = LocalDateTimeDeserializerZ::class.java

    /**
     * 向后兼容别名
     */
    @JvmField
    val LocalTimeDeserializer = LocalTimeDeserializerY::class.java

    /**
     * 创建LocalDate反序列化器实例
     * @param zoneOffset 时区偏移量
     * @return LocalDate反序列化器
     */
    @JvmStatic
    fun forLocalDate(zoneOffset: ZoneOffset): LocalDateDeserializerX {
      return LocalDateDeserializerX(zoneOffset)
    }

    /**
     * 创建LocalDateTime反序列化器实例
     * @param zoneOffset 时区偏移量
     * @return LocalDateTime反序列化器
     */
    @JvmStatic
    fun forLocalDateTime(zoneOffset: ZoneOffset): LocalDateTimeDeserializerZ {
      return LocalDateTimeDeserializerZ(zoneOffset)
    }

    /**
     * 创建LocalTime反序列化器实例
     * @param zoneOffset 时区偏移量
     * @return LocalTime反序列化器
     */
    @JvmStatic
    fun forLocalTime(zoneOffset: ZoneOffset): LocalTimeDeserializerY {
      return LocalTimeDeserializerY(zoneOffset)
    }

    /**
     * LocalDateDeserializer 的别名，用于向后兼容
     * @param zoneOffset 时区偏移量
     * @return LocalDate反序列化器
     */
    @JvmStatic
    fun LocalDateDeserializer(zoneOffset: ZoneOffset): LocalDateDeserializerX {
      return LocalDateDeserializerX(zoneOffset)
    }

    /**
     * LocalDateTimeDeserializer 的别名，用于向后兼容
     * @param zoneOffset 时区偏移量
     * @return LocalDateTime反序列化器
     */
    @JvmStatic
    fun LocalDateTimeDeserializer(zoneOffset: ZoneOffset): LocalDateTimeDeserializerZ {
      return LocalDateTimeDeserializerZ(zoneOffset)
    }

    /**
     * LocalTimeDeserializer 的别名，用于向后兼容
     * @param zoneOffset 时区偏移量
     * @return LocalTime反序列化器
     */
    @JvmStatic
    fun LocalTimeDeserializer(zoneOffset: ZoneOffset): LocalTimeDeserializerY {
      return LocalTimeDeserializerY(zoneOffset)
    }
  }
} 
