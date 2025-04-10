package net.yan100.compose.rds.crud.entities

import jakarta.persistence.MappedSuperclass
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.entities.IJpaEntity

@MappedSuperclass
@MetaDef
abstract class SuperDbTestAllLateEntity : IJpaEntity {
  abstract var a: Int
  abstract var b: Double
  abstract var c: String
  abstract var d: Boolean
}
