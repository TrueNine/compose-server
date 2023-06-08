package net.yan100.compose.rds.gen.converter

import net.yan100.compose.rds.gen.models.ConvertTypeModel

class MysqlConverter : AbstractTypeConverter() {
  private val rule = mutableMapOf<String, ConvertTypeModel>()

  init {
    rule["point"] = ConvertTypeModel("WGS84", "com.truenine.component.rds.base.WGS84")
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
