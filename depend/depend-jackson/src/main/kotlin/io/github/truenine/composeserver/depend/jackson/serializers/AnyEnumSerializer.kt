package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.github.truenine.composeserver.IAnyEnum
import io.github.truenine.composeserver.IIntEnum
import io.github.truenine.composeserver.IStringEnum

@Deprecated(message = "API 负担过大", level = DeprecationLevel.ERROR)
class AnyEnumSerializer : JsonSerializer<IAnyEnum>() {

  override fun handledType(): Class<IAnyEnum> {
    return IAnyEnum::class.java
  }

  override fun serialize(value: IAnyEnum?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    when (value) {
      is IStringEnum -> gen?.writeString(value.value)
      is IIntEnum -> gen?.writeNumber(value.value)
      null -> gen?.writeNull()
      else -> gen?.writeNull()
    }
  }
}
