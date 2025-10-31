package io.github.truenine.composeserver.depend.servlet.converters

import org.springframework.core.convert.converter.Converter

/**
 * ## This converter is for byte arrays submitted directly via form data
 *
 * @author TrueNine
 * @since 2024-02-29
 */
@Deprecated(message = "API burden is too high", level = DeprecationLevel.ERROR)
class StringArrayToByteArrayConverter : Converter<Array<String>, ByteArray> {
  override fun convert(source: Array<String>): ByteArray {
    return source.map { it.toByte() }.toByteArray()
  }
}
