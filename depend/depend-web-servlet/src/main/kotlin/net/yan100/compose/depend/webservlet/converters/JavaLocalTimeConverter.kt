package net.yan100.compose.depend.webservlet.converters

import net.yan100.compose.core.extensionfunctions.toLocalTime
import net.yan100.compose.core.log.slf4j
import org.springframework.core.convert.converter.Converter
import java.time.LocalTime

private val log = slf4j(JavaLocalTimeConverter::class)

open class JavaLocalTimeConverter : Converter<String?, LocalTime?> {
  override fun convert(source: String): LocalTime? {
    log.trace("转换时间 = {}", source)
    return source.toLongOrNull()?.toLocalTime()
  }
}
