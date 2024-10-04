package net.yan100.compose.rds.core.entities

import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "ientity")
open class TestIEntity : IEntity() {
  var name: String? = null
  var doc: String? = null
}
