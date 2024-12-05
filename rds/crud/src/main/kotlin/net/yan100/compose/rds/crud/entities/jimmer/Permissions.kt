package net.yan100.compose.rds.crud.entities.jimmer

import net.yan100.compose.rds.core.entities.IJimmerEntity
import org.babyfish.jimmer.sql.Entity

@Entity
interface Permissions : IJimmerEntity {
  val name: String
  val doc: String?
}
