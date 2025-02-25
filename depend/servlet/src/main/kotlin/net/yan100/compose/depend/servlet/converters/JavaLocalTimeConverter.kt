package net.yan100.compose.depend.servlet.converters

import java.time.LocalTime
import net.yan100.compose.core.slf4j
import net.yan100.compose.core.toLocalTime
import org.springframework.core.convert.converter.Converter

private val log = slf4j(JavaLocalTimeConverter::class)

open class JavaLocalTimeConverter : Converter<String?, LocalTime?> {
  override fun convert(source: String): LocalTime? {
    log.trace("转换时间 = {}", source)
    return source.toLongOrNull()?.toLocalTime()
  }
}
