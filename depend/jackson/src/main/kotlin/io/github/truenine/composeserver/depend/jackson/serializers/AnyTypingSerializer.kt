package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.github.truenine.composeserver.IAnyTyping
import io.github.truenine.composeserver.IIntTyping
import io.github.truenine.composeserver.IStringTyping

@Deprecated(message = "API 负担过大", level = DeprecationLevel.ERROR)
class AnyTypingSerializer : JsonSerializer<IAnyTyping>() {

  override fun handledType(): Class<IAnyTyping> {
    return IAnyTyping::class.java
  }

  override fun serialize(value: IAnyTyping?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    when (value) {
      is IStringTyping -> gen?.writeString(value.value)
      is IIntTyping -> gen?.writeNumber(value.value)
      null -> gen?.writeNull()
      else -> gen?.writeNull()
    }
  }
}
