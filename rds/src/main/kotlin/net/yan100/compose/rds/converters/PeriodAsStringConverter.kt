package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component
import java.time.Period


@Component
@Converter(autoApply = true)
class PeriodAsStringConverter : AttributeConverter<Period, String> {
    override fun convertToDatabaseColumn(attribute: Period?): String? {
        return attribute?.run {
            this.toString()
        }
    }

    override fun convertToEntityAttribute(dbData: String?): Period? {
        return dbData?.run {
            Period.parse(this)
        }
    }
}
