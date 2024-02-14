package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.CertTyping
import org.springframework.stereotype.Component

@Component
@Converter
class CertTypingConverter : AttributeConverter<CertTyping?, Int?> {
    override fun convertToDatabaseColumn(attribute: CertTyping?): Int? = attribute?.value
    override fun convertToEntityAttribute(dbData: Int?): CertTyping? = CertTyping.findVal(dbData)
}
