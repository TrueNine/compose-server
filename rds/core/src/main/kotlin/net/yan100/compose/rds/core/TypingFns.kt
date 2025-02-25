package net.yan100.compose.rds.core

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.typing.IntTyping
import net.yan100.compose.core.typing.StringTyping

/** # int 枚举转换器 */
@Converter
class IntTypingConverterDelegate<T : IntTyping?>(
  private val searchValueFn: (b: Int?) -> T?
) : AttributeConverter<T, Int?> {
  override fun convertToDatabaseColumn(attribute: T?): Int? = attribute?.value

  override fun convertToEntityAttribute(dbData: Int?): T? =
    searchValueFn(dbData)
}

/** # string 枚举转换器 */
@Converter
class StringTypingConverterDelegate<T : StringTyping?>(
  private val searchValueFn: (b: String?) -> T?
) : AttributeConverter<T, String?> {
  override fun convertToDatabaseColumn(attribute: T?): String? =
    attribute?.value

  override fun convertToEntityAttribute(dbData: String?): T? =
    searchValueFn(dbData)
}

fun <T : IntTyping?> intTyping(
  c: (b: Int?) -> T?
): IntTypingConverterDelegate<T> {
  return IntTypingConverterDelegate(c)
}

fun <T : StringTyping> stringTyping(
  c: (b: String?) -> T?
): StringTypingConverterDelegate<T> {
  return StringTypingConverterDelegate(c)
}
