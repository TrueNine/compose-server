package io.github.truenine.composeserver.depend.servlet.converters

import org.springframework.core.convert.converter.Converter

/**
 * ## 此转换器针对 form data 直接提交的 byte 数组
 *
 * @author TrueNine
 * @since 2024-02-29
 */
@Deprecated(message = "API 负担过大", level = DeprecationLevel.ERROR)
class StringArrayToByteArrayConverter : Converter<Array<String>, ByteArray> {
  override fun convert(source: Array<String>): ByteArray {
    return source.map { it.toByte() }.toByteArray()
  }
}
