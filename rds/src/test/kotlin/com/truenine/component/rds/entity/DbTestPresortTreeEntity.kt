package com.truenine.component.rds.entity

import com.truenine.component.rds.base.PresortTreeEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.Hibernate

@Entity
@Table(name = "db_test_presort_tree")
data class DbTestPresortTreeEntity(var title: String?) : PresortTreeEntity() {
  constructor() : this(null)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    other as DbTestPresortTreeEntity

    return id != null && id == other.id
  }

  override fun hashCode(): Int = javaClass.hashCode()
}
