package net.yan100.compose.rds.entity

import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.core.lang.WGS84
import net.yan100.compose.rds.converters.WGS84Converter
import net.yan100.compose.rds.core.entity.BaseEntity

@Entity
@Table(name = DbTestServiceEntity.TABLE_NAME)
class DbTestServiceEntity : BaseEntity() {
  var title: String? = null

  @Convert(converter = WGS84Converter::class)
  var center: WGS84? = null

  companion object {

    const val TABLE_NAME = "db_test_service"
  }
}
