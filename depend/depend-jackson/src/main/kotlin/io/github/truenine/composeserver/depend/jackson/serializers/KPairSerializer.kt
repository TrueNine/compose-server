package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * Kotlin Pair序列化器
 *
 * 将Kotlin Pair对象序列化为JSON数组格式 输出格式: [first, second]
 */
class KPairSerializer : JsonSerializer<Pair<*, *>>() {

  override fun serialize(value: Pair<*, *>?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    if (gen == null) return

    if (value == null) {
      gen.writeNull()
      return
    }

    gen.writeStartArray()
    gen.writeObject(value.first)
    gen.writeObject(value.second)
    gen.writeEndArray()
  }
}
