package io.github.truenine.composeserver.depend.servlet.converters

import java.time.LocalDateTime
import net.yan100.compose.slf4j
import net.yan100.compose.toLocalDateTime
import org.springframework.core.convert.converter.Converter

private val log = slf4j(JavaLocalDateTimeConverter::class)

open class JavaLocalDateTimeConverter : Converter<String?, LocalDateTime?> {
  override fun convert(source: String): LocalDateTime? {
    log.trace("转换日期时间 = {}", source)
    return source.toLongOrNull()?.toLocalDateTime()
  }
}
