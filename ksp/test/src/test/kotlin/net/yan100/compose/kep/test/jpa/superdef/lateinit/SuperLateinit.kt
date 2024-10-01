package net.yan100.compose.kep.test.jpa.superdef.lateinit

import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
abstract class SuperLateinit : IEntity() {
  abstract var name: String
  abstract var doc: String?
}
