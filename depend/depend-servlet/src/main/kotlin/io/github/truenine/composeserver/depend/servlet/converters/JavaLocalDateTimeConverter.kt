package io.github.truenine.composeserver.depend.servlet.converters

import io.github.truenine.composeserver.slf4j
import io.github.truenine.composeserver.toLocalDateTime
import org.springframework.core.convert.converter.Converter
import java.time.LocalDateTime

private val log = slf4j(JavaLocalDateTimeConverter::class)

open class JavaLocalDateTimeConverter : Converter<String, LocalDateTime> {
  override fun convert(source: String): LocalDateTime? {
    log.trace("Converting date-time = {}", source)
    return source.toLongOrNull()?.toLocalDateTime()
  }
}
