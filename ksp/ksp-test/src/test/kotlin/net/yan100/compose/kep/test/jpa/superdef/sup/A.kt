package net.yan100.compose.kep.test.jpa.superdef.sup

import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
abstract class SuperA : IEntity() {
  abstract var a: String
  abstract var b: String?
  abstract var c: Int
  abstract var d: Int?
}

@MetaDef
abstract class SuperB : SuperA()
