package net.yan100.compose.rds.jimmer.entities

import net.yan100.compose.core.datetime
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface IEntity : IAnyEntity {
  val ldf: Boolean?
  val crd: datetime
  val mrd: datetime
}
