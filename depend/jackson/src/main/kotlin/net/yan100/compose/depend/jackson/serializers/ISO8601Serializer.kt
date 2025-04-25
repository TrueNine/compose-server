package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import net.yan100.compose.toMillis
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

/**
 * # ISO8601 标准毫秒时间戳序列化器
 *
 * 为 Java 时间类型提供 ISO8601 标准的 JSON 序列化支持，将日期时间转换为时间戳格式
 * @param T 需要序列化的时间类型，支持 LocalDate、LocalDateTime 和 LocalTime
 * @author TrueNine
 * @since 2025-04-26
 */
sealed class ISO8601Serializer<T> : JsonSerializer<T>() {
  /**
   * ## 带类型信息的序列化
   *
   * @param value 需要序列化的值
   * @param gen JSON生成器
   * @param serializers 序列化提供者
   * @param typeSer 类型序列化器
   */
  override fun serializeWithType(
    value: T,
    gen: JsonGenerator?,
    serializers: SerializerProvider?,
    typeSer: TypeSerializer,
  ) {
    // 使用 VALUE_NUMBER_INT 作为 shape，因为 date/time 类型似乎序列化为数字
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, shape))
    serialize(value, gen, serializers)
    typeSer.writeTypeSuffix(gen, typeIdDef)
  }

  /**
   * # LocalDate ISO8601 序列化器
   *
   * 将 LocalDate 类型转换为 ISO8601 标准的时间戳
   * @param zoneOffset 时区偏移量，用于时区转换
   */
  class ISO8601DateSerializer(private val zoneOffset: ZoneOffset = ZoneOffset.UTC) : ISO8601Serializer<LocalDate>() {
    /**
     * 无参构造函数，用于Jackson反序列化
     */
    constructor() : this(ZoneOffset.UTC)
    
    /**
     * ## 返回处理的类型
     * @return LocalDate 类
     */
    override fun handledType(): Class<LocalDate> = LocalDate::class.java

    /**
     * ## 序列化 LocalDate
     *
     * @param value LocalDate 对象
     * @param gen JSON生成器
     * @param serializers 序列化提供者
     */
    override fun serialize(
      value: LocalDate,
      gen: JsonGenerator?,
      serializers: SerializerProvider?,
    ) {
      gen?.writeNumber(value.toMillis(zoneOffset))
    }
  }

  /**
   * # LocalDateTime ISO8601 序列化器
   *
   * 将 LocalDateTime 类型转换为 ISO8601 标准的时间戳
   * @param zoneOffset 时区偏移量，用于时区转换
   */
  class ISO8601DateTimeSerializer(private val zoneOffset: ZoneOffset = ZoneOffset.UTC) : ISO8601Serializer<LocalDateTime>() {
    /**
     * 无参构造函数，用于Jackson反序列化
     */
    constructor() : this(ZoneOffset.UTC)
    
    /**
     * ## 返回处理的类型
     * @return LocalDateTime 类
     */
    override fun handledType(): Class<LocalDateTime> = LocalDateTime::class.java

    /**
     * ## 序列化 LocalDateTime
     *
     * @param value LocalDateTime 对象
     * @param gen JSON生成器
     * @param serializers 序列化提供者
     */
    override fun serialize(
      value: LocalDateTime,
      gen: JsonGenerator?,
      serializers: SerializerProvider?,
    ) {
      gen?.writeNumber(value.toMillis(zoneOffset))
    }
  }

  /**
   * # LocalTime ISO8601 序列化器
   *
   * 将 LocalTime 类型转换为 ISO8601 标准的时间戳
   * @param zoneOffset 时区偏移量，用于时区转换
   */
  class ISO8601TimeSerializer(private val zoneOffset: ZoneOffset = ZoneOffset.UTC) : ISO8601Serializer<LocalTime>() {
    /**
     * 无参构造函数，用于Jackson反序列化
     */
    constructor() : this(ZoneOffset.UTC)
    
    /**
     * ## 返回处理的类型
     * @return LocalTime 类
     */
    override fun handledType(): Class<LocalTime> = LocalTime::class.java

    /**
     * ## 序列化 LocalTime
     *
     * @param value LocalTime 对象
     * @param gen JSON生成器
     * @param serializers 序列化提供者
     */
    override fun serialize(
      value: LocalTime,
      gen: JsonGenerator?,
      serializers: SerializerProvider?,
    ) {
      gen?.writeNumber(value.toMillis(zoneOffset))
    }
  }
}


