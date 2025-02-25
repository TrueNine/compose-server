package net.yan100.compose.rds.crud.entities

import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaName
import net.yan100.compose.rds.core.entities.IJpaEntity

@MetaDef
@MetaName("gen_table")
abstract class SuperGen : IJpaEntity, Cloneable {
  abstract var strNullable: String?
  abstract var intNonNull: Int
}
