package net.yan100.compose.rds.core.entities

import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "ientity")
open class TestIEntity : IJpaEntity by entity() {
  fun a() {
    this.id
  }

  open var name: String? = null
  open var doc: String? = null
}
