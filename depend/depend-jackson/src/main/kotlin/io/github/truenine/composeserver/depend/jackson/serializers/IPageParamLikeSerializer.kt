package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import io.github.truenine.composeserver.Pq
import io.github.truenine.composeserver.domain.IPageParam

/**
 * IPageParam和IPageParamLike反序列化器
 *
 * 支持从JSON对象反序列化为IPageParam实例 预期JSON格式: {"o": offset, "s": pageSize, "u": unPage}
 */
class IPageParamLikeSerializer : JsonDeserializer<IPageParam>() {

  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): IPageParam? {
    if (p == null) {
      throw JsonMappingException.from(ctxt, "JsonParser cannot be null for IPageParam deserialization")
    }

    // 检查当前token是否为对象开始
    if (p.currentToken != JsonToken.START_OBJECT) {
      throw JsonMappingException.from(ctxt, "Expected START_OBJECT token for IPageParam deserialization, got: ${p.currentToken}")
    }

    var offset: Int? = null
    var pageSize: Int? = null
    var unPage: Boolean? = null

    // 遍历JSON对象的字段
    while (p.nextToken() != JsonToken.END_OBJECT) {
      val fieldName = p.currentName()
      p.nextToken() // 移动到字段值

      when (fieldName) {
        "o" -> {
          offset = p.intValueOrNull()
        }
        "s" -> {
          pageSize = p.intValueOrNull()
        }
        "u" -> {
          unPage = p.booleanValueOrNull()
        }
        else -> {
          // 跳过未知字段
          p.skipChildren()
        }
      }
    }

    // 使用Pq工厂方法创建IPageParam实例
    return Pq[offset, pageSize, unPage]
  }

  /** 安全解析Int值的扩展函数 */
  private fun JsonParser.intValueOrNull(): Int? {
    return when {
      currentToken.isNumeric -> intValue
      currentToken == JsonToken.VALUE_NULL -> null
      else -> throw JsonMappingException.from(null as DeserializationContext?, "Expected numeric value for int field, got: $currentToken")
    }
  }

  /** 安全解析Boolean值的扩展函数 */
  private fun JsonParser.booleanValueOrNull(): Boolean? {
    return when (currentToken) {
      JsonToken.VALUE_TRUE -> true
      JsonToken.VALUE_FALSE -> false
      JsonToken.VALUE_NULL -> null
      else -> throw JsonMappingException.from(null as DeserializationContext?, "Expected boolean value for boolean field, got: $currentToken")
    }
  }
}
