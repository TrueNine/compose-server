package net.yan100.compose.rds.entities

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.typing.cert.DisTyping
import org.springframework.stereotype.Component

@Converter
@Component
class DisTypingConverter :AttributeConverter<DisTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: DisTyping?)=attribute?.value
  override fun convertToEntityAttribute(dbData: Int?): DisTyping? = DisTyping.findVal(dbData)
}
