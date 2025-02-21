package net.yan100.compose.rds.core.converters.jimmer

import net.yan100.compose.core.RefId
import org.babyfish.jimmer.jackson.Converter

class JimmerLongToStringConverter : Converter<RefId, String> {
  override fun output(value: RefId?): String? {
    return value?.toString()
  }

  override fun input(jsonValue: String?): RefId? {
    return jsonValue?.toLongOrNull()
  }
}
