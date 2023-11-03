package net.yan100.compose.rds.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.rds.core.entity.TreeEntity

@Entity
@Table(name = "db_test_presort_tree")
open class DbTestTreeEntity : TreeEntity() {
  lateinit var title: String
}
