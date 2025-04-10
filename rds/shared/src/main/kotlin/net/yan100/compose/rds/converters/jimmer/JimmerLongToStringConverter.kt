package net.yan100.compose.rds.converters.jimmer

import net.yan100.compose.RefId
import org.babyfish.jimmer.jackson.Converter

class JimmerLongToStringConverter : Converter<RefId, String> {
  override fun output(value: RefId): String {
    return value.toString()
  }

  override fun input(jsonValue: String): RefId {
    return jsonValue.toLong()
  }
}
