package net.yan100.compose.rds.converters.typing

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.GenderTyping
import org.springframework.stereotype.Component

/**
 * # 性别枚举
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Converter
@Component
class GenderTypingConverter : AttributeConverter<GenderTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: GenderTyping?): Int? = attribute?.getValue()

  override fun convertToEntityAttribute(dbData: Int?): GenderTyping? = dbData?.run { GenderTyping.findVal(dbData) }
}
