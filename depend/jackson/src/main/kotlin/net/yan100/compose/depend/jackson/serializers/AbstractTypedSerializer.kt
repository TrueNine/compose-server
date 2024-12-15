package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer

abstract class AbstractTypedSerializer<T>(
  private val typeToken: JsonToken? = null
) : JsonSerializer<T>() {
  override fun serializeWithType(
    value: T,
    gen: JsonGenerator?,
    serializers: SerializerProvider?,
    typeSer: TypeSerializer
  ) {
    val typeIdDef = when (typeToken) {
      // TODO 有待完善
      JsonToken.START_ARRAY -> typeSer.writeTypePrefix(gen, typeSer.typeId(value, null))
      else -> typeSer.writeTypePrefix(gen, typeSer.typeId(value, null))
    }
    serialize(value, gen, serializers)
    typeSer.writeTypeSuffix(gen, typeIdDef)
  }
}
