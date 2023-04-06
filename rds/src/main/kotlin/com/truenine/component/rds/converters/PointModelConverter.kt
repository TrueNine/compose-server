package com.truenine.component.rds.converters

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.models.PointModel
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Component
@Converter
class PointModelConverter : AttributeConverter<PointModel, String> {

  init {
    log.debug("注册 地理位置模型converter = {}", this)
  }

  companion object {
    @JvmStatic
    private val log = LogKt.getLog(PointModelConverter::class)
  }

  override fun convertToDatabaseColumn(attribute: PointModel?): String? {
    return if (null != attribute) "" else null
  }

  override fun convertToEntityAttribute(dbData: String?): PointModel? {
    log.debug("地址 = {} 类型 = {}", dbData, dbData?.javaClass)
    return null
  }
}
