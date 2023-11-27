package net.yan100.compose.rds.entities

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.typing.documents.DisabilityTyping
import org.springframework.stereotype.Component

@Converter
@Component
class DisabilityTypingConverter :AttributeConverter<DisabilityTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: DisabilityTyping?)=attribute?.getValue()
  override fun convertToEntityAttribute(dbData: Int?): DisabilityTyping? = DisabilityTyping.findVal(dbData)
}
