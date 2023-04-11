package com.truenine.component.rds.entity

import com.truenine.component.rds.base.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.Hibernate
import java.time.Duration


@Entity
@Table(name = "db_test_duration_converter")
data class DbTestDurationConverterEntity(
  var durations: Duration?
) : BaseEntity() {
  constructor() : this(null)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    other as DbTestDurationConverterEntity
    return id != null && id == other.id
  }

  override fun hashCode(): Int = javaClass.hashCode()
}
