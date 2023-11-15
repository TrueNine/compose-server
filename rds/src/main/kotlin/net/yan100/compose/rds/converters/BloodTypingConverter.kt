package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.BloodTyping
import org.springframework.stereotype.Component

@Component
@Converter
class BloodTypingConverter : AttributeConverter<BloodTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: BloodTyping?): Int? {
    return attribute?.getValue()
  }

  override fun convertToEntityAttribute(dbData: Int?): BloodTyping? {
    return BloodTyping.findVal(dbData)
  }
}
