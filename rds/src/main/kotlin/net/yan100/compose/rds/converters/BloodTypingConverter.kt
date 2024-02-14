package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.BloodTyping
import org.springframework.stereotype.Component

@Component
@Converter
class BloodTypingConverter : AttributeConverter<BloodTyping?, Int?> {
    override fun convertToDatabaseColumn(attribute: BloodTyping?): Int? = attribute?.value
    override fun convertToEntityAttribute(dbData: Int?): BloodTyping? = BloodTyping.findVal(dbData)
}
