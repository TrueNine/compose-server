package com.truenine.component.rds.entity

import com.truenine.component.rds.base.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.Hibernate
import java.time.Period


@Entity
@Table(name = "db_test_period_converter")
data class DbTestPeriodConverterEntity(
  var periods: Period?
) : BaseEntity() {
  constructor() : this(null)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    other as DbTestPeriodConverterEntity

    return id != null && id == other.id
  }

  override fun hashCode(): Int = javaClass.hashCode()

  @Override
  override fun toString(): String {
    return this::class.simpleName + "(id = $id , rlv = $rlv , ldf = $ldf )"
  }

}
