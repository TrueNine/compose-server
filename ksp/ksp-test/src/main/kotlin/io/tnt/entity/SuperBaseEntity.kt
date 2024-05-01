package io.tnt.entity

import net.yan100.compose.ksp.ksp.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
class SuperBaseEntity : IEntity() {
  var baseField: String? = null
}
