package net.yan100.compose.core.autoconfig

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import net.yan100.compose.core.annotations.BigIntegerAsString
import net.yan100.compose.core.annotations.SensitiveRef
import net.yan100.compose.core.annotations.Strategy
import net.yan100.compose.core.lang.hasText

/**
 * jackson json 脱敏属性策略配置
 *
 * @author TrueNine
 * @since 2023-02-20
 */
class SensitiveSerializer
  : JsonSerializer<String>(), ContextualSerializer {

  private lateinit var strategy: Strategy

  override fun serialize(
    value: String?,
    gen: JsonGenerator?,
    serializers: SerializerProvider?
  ) {
    value?.let {
      gen?.writeString(strategy.desensitizeSerializer().invoke(it))
    }
  }

  override fun createContextual(
    prov: SerializerProvider?,
    property: BeanProperty?
  ): JsonSerializer<*>? {
    val ref: SensitiveRef? = property?.getAnnotation(SensitiveRef::class.java)
    return ref?.value?.let {
      strategy = it
      return this
    } ?: prov?.findValueSerializer(property?.type, property)
  }
}


/**
 * jackson json 长整型转字符串序列化器
 *
 * @param <Long>
 * @since 2023-05-09
 */
class LongAsStringSerializer : JsonSerializer<Long?>(),
  ContextualSerializer {

  override fun serialize(value: Long?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    value?.let { gen?.writeString(it.toString()) }
  }

  override fun createContextual(prov: SerializerProvider?, property: BeanProperty?): JsonSerializer<*>? {
    return property?.let { p ->
      val ref: BigIntegerAsString? = p.getAnnotation(BigIntegerAsString::class.java)
      if (null != ref && p.type.rawClass == Long::class.java) this
      else prov?.findValueSerializer(property.type, property)
    } ?: prov?.findNullValueSerializer(property)
  }
}


class StringAsLongDeserializer : JsonDeserializer<Long?>(),
  ContextualSerializer {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Long? {
    return p?.let { parser ->
      if (parser.text.hasText()) {
        parser.text.toLongOrNull()
      } else null
    }
  }

  override fun createContextual(prov: SerializerProvider?, property: BeanProperty?): JsonSerializer<*>? {
    return property?.let { p ->
      val ref: BigIntegerAsString? = p.getAnnotation(BigIntegerAsString::class.java)
      if (null != ref && p.type.rawClass == Long::class.java) LongAsStringSerializer()
      else prov?.findValueSerializer(property.type, property)
    } ?: prov?.findNullValueSerializer(property)
  }
}