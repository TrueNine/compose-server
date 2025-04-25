package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer

abstract class AbstractTypedSerializer<T> : JsonSerializer<T>() {
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
}
