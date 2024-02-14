package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.DegreeTyping
import org.springframework.stereotype.Component

@Component
@Converter
class DegreeTypingConverter : AttributeConverter<DegreeTyping?, Int?> {
    override fun convertToDatabaseColumn(attribute: DegreeTyping?): Int? = attribute?.value
    override fun convertToEntityAttribute(dbData: Int?): DegreeTyping? = DegreeTyping.findVal(dbData)
}
