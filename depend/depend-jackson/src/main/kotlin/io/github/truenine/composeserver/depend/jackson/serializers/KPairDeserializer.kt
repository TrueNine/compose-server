package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException

/**
 * Kotlin Pair反序列化器
 *
 * 支持从JSON数组格式反序列化为Kotlin Pair对象 预期JSON格式: [first, second]
 */
class KPairDeserializer : JsonDeserializer<Pair<*, *>>() {

  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Pair<*, *> {
    if (p == null) {
      throw JsonMappingException.from(ctxt, "JsonParser cannot be null for Pair deserialization")
    }

    // 检查是否为数组开始
    if (p.currentToken != JsonToken.START_ARRAY) {
      throw JsonMappingException.from(ctxt, "Expected START_ARRAY token for Pair deserialization, got: ${p.currentToken}")
    }

    // 读取第一个元素
    p.nextToken()
    val first = p.readValueAs(Any::class.java)

    // 读取第二个元素
    p.nextToken()
    val second = p.readValueAs(Any::class.java)

    // 检查数组结束
    p.nextToken()
    if (p.currentToken != JsonToken.END_ARRAY) {
      throw JsonMappingException.from(ctxt, "Expected exactly 2 elements in array for Pair deserialization")
    }

    return Pair(first, second)
  }
}
