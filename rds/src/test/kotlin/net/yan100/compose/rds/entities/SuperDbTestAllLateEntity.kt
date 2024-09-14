package net.yan100.compose.rds.entities

import jakarta.persistence.MappedSuperclass
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

@MappedSuperclass
@MetaDef
abstract class SuperDbTestAllLateEntity : IEntity() {
  abstract var a: Int
  abstract var b: Double
  abstract var c: String
  abstract var d: Boolean
}
