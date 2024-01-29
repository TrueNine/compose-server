package net.yan100.compose.rds.entities

import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.rds.core.entities.IEntity
import org.hibernate.Hibernate
import java.time.Period


@Entity
@Table(name = "db_test_period_converter")
data class DbTestPeriodConverterEntity(
  var periods: Period?
) : IEntity() {
  constructor() : this(null)

  override fun equals(o: Any?): Boolean {
    if (this === o) return true
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false
    o as DbTestPeriodConverterEntity

    return id != null && id == o.id
  }

  override fun hashCode(): Int = javaClass.hashCode()

  @Override
  override fun toString(): String {
    return this::class.simpleName + "(id = $id , rlv = $rlv , ldf = $ldf )"
  }

}
