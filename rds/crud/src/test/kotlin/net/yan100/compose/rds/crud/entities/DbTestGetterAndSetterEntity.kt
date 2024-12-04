package net.yan100.compose.rds.crud.entities

import jakarta.persistence.*
import net.yan100.compose.core.Id
import net.yan100.compose.core.RefId
import net.yan100.compose.core.datetime
import net.yan100.compose.core.i32
import net.yan100.compose.core.i64
import net.yan100.compose.rds.core.entities.IJpaEntity
import kotlin.properties.Delegates

@Entity
@Access(AccessType.PROPERTY)
class DbTestGetterAndSetterEntity : IJpaEntity {
  private var internalId: RefId = ""

  @jakarta.persistence.Id
  override fun getId(): Id {
    return internalId
  }

  override var rlv: i64?
    get() = TODO("Not yet implemented")
    set(value) {}
  override var crd: datetime?
    get() = TODO("Not yet implemented")
    set(value) {}
  override var mrd: datetime?
    get() = TODO("Not yet implemented")
    set(value) {}
  override var ldf: Boolean?
    get() = TODO("Not yet implemented")
    set(value) {}

  override fun setId(id: Id) {
    this.internalId = id
  }

  @delegate:Transient
  @get:Column(name = "name")
  var name: String by @Suppress("DEPRECATION_ERROR") Delegates.notNull()

  @delegate:Transient
  @get:Column(name = "aged")
  var aged: i32 by @Suppress("DEPRECATION_ERROR") Delegates.notNull()
}
