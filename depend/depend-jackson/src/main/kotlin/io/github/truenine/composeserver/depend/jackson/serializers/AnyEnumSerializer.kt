package io.github.truenine.composeserver.depend.jackson.serializers

import io.github.truenine.composeserver.*
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer

@Deprecated(message = "API surface is too heavy", level = DeprecationLevel.ERROR)
class AnyEnumSerializer : ValueSerializer<IAnyEnum>() {

  override fun handledType(): Class<IAnyEnum> {
    return IAnyEnum::class.java
  }

  override fun serialize(value: IAnyEnum?, gen: JsonGenerator?, ctxt: SerializationContext?) {
    when (value) {
      is IStringEnum -> gen?.writeString(value.value)
      is IIntEnum -> gen?.writeNumber(value.value)
      null -> gen?.writeNull()
      else -> gen?.writeNull()
    }
  }
}
