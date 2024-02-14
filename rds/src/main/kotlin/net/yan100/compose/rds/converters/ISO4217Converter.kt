package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.lang.ISO4217
import org.springframework.stereotype.Component

@Converter
@Component
class ISO4217Converter : AttributeConverter<ISO4217?, String?> {
    override fun convertToDatabaseColumn(attribute: ISO4217?): String? = attribute?.value
    override fun convertToEntityAttribute(dbData: String?): ISO4217? = ISO4217.findVal(dbData)
}
