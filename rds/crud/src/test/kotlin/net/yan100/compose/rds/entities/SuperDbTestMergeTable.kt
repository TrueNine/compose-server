package net.yan100.compose.rds.entities

import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.i32
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
@MappedSuperclass
abstract class SuperDbTestMergeTable : IEntity {
  abstract var name: String
  abstract var age: i32
}
