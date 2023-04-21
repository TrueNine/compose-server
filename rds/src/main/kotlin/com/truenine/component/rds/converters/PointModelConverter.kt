package com.truenine.component.rds.converters

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.base.PointModel
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

/**
 * 将数据库内以字符串存储的坐标转换为 x y 形式
 *
 * @author TrueNine
 * @since 2023-04-19
 */
@Component
@Converter(autoApply = true)
class PointModelConverter : AttributeConverter<PointModel, String> {

  init {
    log.debug("注册 地理位置模型converter = {}", this)
  }

  companion object {
    @JvmStatic
    private val log = LogKt.getLog(PointModelConverter::class)
  }

  override fun convertToDatabaseColumn(attribute: PointModel?): String? =
    attribute?.run {
      "P(${attribute.x},${attribute.y})"
    }

  override fun convertToEntityAttribute(dbData: String?): PointModel? {
    log.trace("地址 = {} 类型 = {}", dbData, dbData?.javaClass)
    return dbData?.let { exp ->
      val group = exp.replace(Regex("""(?i)P\(|\)"""), "").split(",")
        .map { it.trim().toBigDecimalOrNull() }
      PointModel(group[0], group[1])
    }
  }
}
