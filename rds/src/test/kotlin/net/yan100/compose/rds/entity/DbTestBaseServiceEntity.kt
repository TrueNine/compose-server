package net.yan100.compose.rds.entity

import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.rds.converters.PointModelConverter

@Entity
@Table(name = DbTestBaseServiceEntity.TABLE_NAME)
data class DbTestBaseServiceEntity(
  var title: String? = null,
  @Convert(converter = PointModelConverter::class)
  var center: net.yan100.compose.rds.base.PointModel? = null
) : net.yan100.compose.rds.base.BaseEntity() {
  companion object {
    const val TABLE_NAME = "db_test_service"
  }
}
