package net.yan100.compose.rds.crud.entities

import jakarta.persistence.MappedSuperclass
import net.yan100.compose.i32
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.entities.IJpaEntity

@MetaDef
@MappedSuperclass
abstract class SuperDbTestMergeTable : IJpaEntity {
  abstract var name: String
  abstract var age: i32
}
