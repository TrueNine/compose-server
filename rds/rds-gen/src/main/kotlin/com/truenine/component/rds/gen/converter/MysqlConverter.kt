package com.truenine.component.rds.gen.converter

import com.truenine.component.rds.gen.models.ConvertTypeModel

class MysqlConverter : AbstractTypeConverter() {
  private val rule = mutableMapOf<String, ConvertTypeModel>()

  init {
    rule["point"] = ConvertTypeModel("PointModel", "com.truenine.component.rds.models.PointModel")
    rule["double"] = ConvertTypeModel("Double")
    rule["decimal"] = ConvertTypeModel("BigDecimal", "java.math.BigDecimal")
    rule["bigint"] = ConvertTypeModel("Long")
    rule["bigint unsigned"] = ConvertTypeModel("Long")
    rule["json"] = ConvertTypeModel("String")
    rule["tinyint"] = ConvertTypeModel("Byte")
    rule["int"] = ConvertTypeModel("Integer")
    rule["datetime"] = ConvertTypeModel("LocalDateTime", "java.time.LocalDateTime")
    rule["date"] = ConvertTypeModel("LocalDate", "java.time.LocalDate")
    rule["time"] = ConvertTypeModel("LocalTime", "java.time.LocalTime")
    rule["blob"] = ConvertTypeModel("Byte[]")
    super.putAll(rule)
  }
}
