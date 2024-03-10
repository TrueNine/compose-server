/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
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
