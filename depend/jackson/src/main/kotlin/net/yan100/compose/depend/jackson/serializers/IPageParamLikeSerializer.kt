package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import net.yan100.compose.Pq
import net.yan100.compose.domain.IPageParam

class IPageParamLikeSerializer : JsonDeserializer<IPageParam>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): IPageParam? {
    // 检查 JsonParser 是否为空
    if (p == null) return null

    // 按需解析字段，减少中间对象的分配
    var offset: Int? = null
    var pageSize: Int? = null
    var unPage: Boolean? = null

    // 遍历 JSON 流，只处理目标字段
    while (p.nextToken() != JsonToken.END_OBJECT) {
      when (p.currentName()) {
        "o" -> {
          p.nextToken()
          offset = p.intValueOrNull()
        }

        "s" -> {
          p.nextToken()
          pageSize = p.intValueOrNull()
        }

        "u" -> {
          p.nextToken()
          unPage = p.booleanValueOrNull()
        }

        else -> p.skipChildren() // 跳过不相关字段
      }
    }

    // 检查是否有足够信息构建结果
    return if (offset == null && pageSize == null) null else Pq[offset, pageSize, unPage]
  }

  // 扩展函数：安全解析 Long 值
  private fun JsonParser.longValueOrNull(): Long? = if (currentToken.isNumeric) longValue else null

  // 扩展函数：安全解析 Int 值
  private fun JsonParser.intValueOrNull(): Int? = if (currentToken.isNumeric) intValue else null

  // 扩展函数：安全解析 Boolean 值
  private fun JsonParser.booleanValueOrNull(): Boolean? =
    if (currentToken == JsonToken.VALUE_TRUE || currentToken == JsonToken.VALUE_FALSE) booleanValue else null
}
