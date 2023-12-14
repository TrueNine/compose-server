package net.yan100.compose.rds.entities

import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.rds.core.entities.TreeEntity

@Entity
@Table(name = "db_test_presort_tree")
open class DbTestTreeEntity : TreeEntity() {
  open lateinit var title: String
}
