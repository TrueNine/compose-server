package com.truenine.component.rds.entity

import com.truenine.component.rds.base.BaseEntity
import com.truenine.component.rds.base.PointModel
import com.truenine.component.rds.converters.PointModelConverter
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.Hibernate

@Entity
@Table(name = "db_test_service")
data class DbTestBaseServiceEntity(
  var title: String? = null,
  @Convert(converter = PointModelConverter::class)
  var center: PointModel? = null
) : BaseEntity() {

  companion object {
    const val TABLE_NAME = "abc"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    other as DbTestBaseServiceEntity
    return id != null && id == other.id
  }

  override fun hashCode(): Int = javaClass.hashCode()

  @Override
  override fun toString(): String {
    return this::class.simpleName + "(id = $id , rlv = $rlv , ldf = $ldf , title = $title , center = $center )"
  }

}
