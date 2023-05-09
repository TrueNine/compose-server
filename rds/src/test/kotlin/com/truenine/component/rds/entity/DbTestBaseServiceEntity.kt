package com.truenine.component.rds.entity

import com.truenine.component.rds.base.BaseEntity
import com.truenine.component.rds.base.PointModel
import com.truenine.component.rds.converters.PointModelConverter
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = DbTestBaseServiceEntity.TABLE_NAME)
data class DbTestBaseServiceEntity(
  var title: String? = null,
  @Convert(converter = PointModelConverter::class)
  var center: PointModel? = null
) : BaseEntity() {
  companion object {
    const val TABLE_NAME = "db_test_service"
  }
}
