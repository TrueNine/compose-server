package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.lang.slf4j
import org.springframework.stereotype.Component

/**
 * 将数据库内以字符串存储的坐标转换为 x y 形式
 *
 * @author TrueNine
 * @since 2023-04-19
 */
@Component
@Converter(autoApply = true)
class PointModelConverter : AttributeConverter<net.yan100.compose.rds.base.PointModel, String> {

  init {
    log.debug("注册 地理位置模型converter = {}", this)
  }

  companion object {
    @JvmStatic
    private val log = slf4j(PointModelConverter::class)
  }

  override fun convertToDatabaseColumn(attribute: net.yan100.compose.rds.base.PointModel?): String? =
    attribute?.run {
      "P(${attribute.x},${attribute.y})"
    }

  override fun convertToEntityAttribute(dbData: String?): net.yan100.compose.rds.base.PointModel? {
    log.trace("地址 = {} 类型 = {}", dbData, dbData?.javaClass)
    return dbData?.let { exp ->
      val group = exp.replace(Regex("""(?i)P\(|\)"""), "").split(",")
        .map { it.trim().toBigDecimalOrNull() }
      net.yan100.compose.rds.base.PointModel(group[0], group[1])
    }
  }
}
