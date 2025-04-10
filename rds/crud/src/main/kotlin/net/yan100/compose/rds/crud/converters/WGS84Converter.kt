package net.yan100.compose.rds.crud.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.domain.Coordinate
import org.springframework.stereotype.Component

/**
 * 将数据库内以字符串存储的坐标转换为 x y 形式
 *
 * @author TrueNine
 * @since 2023-04-19
 */
@Component
@Converter(autoApply = true)
class WGS84Converter : AttributeConverter<Coordinate, String> {
  override fun convertToDatabaseColumn(attribute: Coordinate?): String? =
    attribute?.run { "P(${attribute.x},${attribute.y})" }

  override fun convertToEntityAttribute(dbData: String?): Coordinate? {
    return dbData?.let { exp ->
      val group =
        exp.replace(Regex("""(?i)P\(|\)"""), "").split(",").map {
          it.trim().toBigDecimalOrNull()
        }
      Coordinate(group[0], group[1])
    }
  }
}
