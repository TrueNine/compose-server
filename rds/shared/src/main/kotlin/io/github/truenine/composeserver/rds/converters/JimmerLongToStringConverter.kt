package io.github.truenine.composeserver.rds.converters

import io.github.truenine.composeserver.RefId
import org.babyfish.jimmer.jackson.Converter

class JimmerLongToStringConverter : Converter<RefId, String> {
  override fun output(value: RefId): String {
    return value.toString()
  }

  override fun input(jsonValue: String): RefId {
    return jsonValue.toLong()
  }
}
