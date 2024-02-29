package net.yan100.compose.depend.webservlet.converters

import net.yan100.compose.core.extensionfunctions.toLocalDateTime
import net.yan100.compose.core.log.slf4j
import org.springframework.core.convert.converter.Converter
import java.time.LocalDateTime

private val log = slf4j(JavaLocalDateTimeConverter::class)

open class JavaLocalDateTimeConverter : Converter<String?, LocalDateTime?> {
  override fun convert(source: String): LocalDateTime? {
    log.trace("转换日期时间 = {}", source)
    return source.toLongOrNull()?.toLocalDateTime()
  }
}
