package net.yan100.compose.rds.jimmer.entities

import net.yan100.compose.core.RefId
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface IAnyEntity {
  val id: RefId

}
