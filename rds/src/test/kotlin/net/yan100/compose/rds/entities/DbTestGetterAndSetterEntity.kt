package net.yan100.compose.rds.entities

import jakarta.persistence.*
import net.yan100.compose.core.i32
import net.yan100.compose.rds.core.entities.IEntity

@Entity
@Access(AccessType.PROPERTY)
class DbTestGetterAndSetterEntity : IEntity() {

  @delegate:Transient
  @get:Column(name = "name")
  var name: String by late()

  @delegate:Transient
  @get:Column(name = "aged")
  var aged: i32 by late()
}
