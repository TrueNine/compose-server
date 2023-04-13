package com.truenine.component.rds.entity

import com.truenine.component.rds.base.BaseEntity
import com.truenine.component.rds.converters.PointModelConverter
import com.truenine.component.rds.models.PointModel
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "db_test_service")
data class DbTestBaseServiceEntity(
  var title: String? = null,
  @Convert(converter = PointModelConverter::class)
  var center: PointModel? = null
) : BaseEntity() {

}
