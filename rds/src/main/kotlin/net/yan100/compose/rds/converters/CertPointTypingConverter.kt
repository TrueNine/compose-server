package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.CertPointTyping
import org.springframework.stereotype.Component

@Component
@Converter
class CertPointTypingConverter : AttributeConverter<CertPointTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: CertPointTyping?): Int? = attribute?.getValue()
  override fun convertToEntityAttribute(dbData: Int?): CertPointTyping? = CertPointTyping.findVal(dbData)
}
