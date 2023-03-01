package com.truenine.component.rds.gen.util.converter

class MysqlConverter : AbstractTypeConverter() {
  private val rule = mutableMapOf<String, CType>()

  init {
    rule["point"] = CType("Point", "org.springframework.data.geo.Point")
    rule["double"] = CType("Double")
    rule["decimal"] = CType("BigDecimal", "java.math.BigDecimal")
    rule["bigint"] = CType("Long")
    rule["bigint unsigned"] = CType("Long")
    rule["json"] = CType("String")
    rule["tinyint"] = CType("Byte")
    rule["int"] = CType("Integer")
    rule["datetime"] = CType("LocalDateTime", "java.time.LocalDateTime")
    rule["date"] = CType("LocalDate", "java.time.LocalDate")
    rule["time"] = CType("LocalTime", "java.time.LocalTime")
    rule["blob"] = CType("Byte[]")

    super.putAll(rule)
  }
}
