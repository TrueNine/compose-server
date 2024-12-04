package net.yan100.compose.rds.core.entities

import net.yan100.compose.core.datetime
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface IJimmerEntity : IJimmerPersistentEntity {
  //@LogicalDeleted("true")
  val ldf: Boolean?

  val crd: datetime?
  val mrd: datetime?

  //@Version
  val rlv: Long?
}
