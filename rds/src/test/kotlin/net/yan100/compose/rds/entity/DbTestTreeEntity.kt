package net.yan100.compose.rds.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.Hibernate

@Entity
@Table(name = "db_test_presort_tree")
data class DbTestTreeEntity(var title: String?) : net.yan100.compose.rds.base.TreeEntity() {
  constructor() : this(null)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    other as DbTestTreeEntity

    return id != null && id == other.id
  }

  override fun hashCode(): Int = javaClass.hashCode()
}
