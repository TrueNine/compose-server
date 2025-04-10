package net.yan100.compose.depend.servlet.converters

import net.yan100.compose.slf4j
import net.yan100.compose.toLocalDate
import org.springframework.core.convert.converter.Converter
import java.time.LocalDate

private val log = slf4j(JavaLocalDateConverter::class)

open class JavaLocalDateConverter : Converter<String?, LocalDate?> {
  override fun convert(source: String): LocalDate? {
    log.trace("转换日期 = {}", source)
    return source.toLongOrNull()?.toLocalDate()
  }
}
