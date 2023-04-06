package com.truenine.component.rds.dao

import com.truenine.component.rds.base.PresortTreeDao
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "db_test_presort_tree")
data class DbTestPresortTreeDao(var title: String?) : PresortTreeDao() {
  constructor() : this(null)
}
