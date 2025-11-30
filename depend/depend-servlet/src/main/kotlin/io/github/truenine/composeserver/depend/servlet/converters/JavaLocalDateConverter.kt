package io.github.truenine.composeserver.depend.servlet.converters

import io.github.truenine.composeserver.slf4j
import io.github.truenine.composeserver.toLocalDate
import org.springframework.core.convert.converter.Converter
import java.time.LocalDate

private val log = slf4j(JavaLocalDateConverter::class)

open class JavaLocalDateConverter : Converter<String, LocalDate> {
  override fun convert(source: String): LocalDate? {
    log.trace("Converting date = {}", source)
    return source.toLongOrNull()?.toLocalDate()
  }
}
