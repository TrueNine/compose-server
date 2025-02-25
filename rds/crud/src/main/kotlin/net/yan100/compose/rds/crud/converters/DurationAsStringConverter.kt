package net.yan100.compose.rds.crud.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.Duration
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
class DurationAsStringConverter : AttributeConverter<Duration, String> {
  override fun convertToDatabaseColumn(attribute: Duration?): String? {
    return attribute?.toString()
  }

  override fun convertToEntityAttribute(dbData: String?): Duration? {
    return dbData?.run { Duration.parse(this) }
  }
}
