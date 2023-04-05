package com.truenine.component.rds.dao

import com.truenine.component.rds.base.BaseDao
import com.truenine.component.rds.base.PreSortTreeDao
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "db_test_presort_tree")
data class DbTestPresortTreeDao(var title: String?) : PreSortTreeDao() {
  constructor() : this(null)
}
