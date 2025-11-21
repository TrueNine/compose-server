package io.github.truenine.composeserver.depend.servlet.converters

import io.github.truenine.composeserver.slf4j
import io.github.truenine.composeserver.toLocalTime
import java.time.LocalTime
import org.springframework.core.convert.converter.Converter

private val log = slf4j(JavaLocalTimeConverter::class)

open class JavaLocalTimeConverter : Converter<String, LocalTime> {
  override fun convert(source: String): LocalTime? {
    log.trace("Converting time = {}", source)
    return source.toLongOrNull()?.toLocalTime()
  }
}
