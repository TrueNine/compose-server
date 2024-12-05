package net.yan100.compose.rds.crud.entities.jimmer

import net.yan100.compose.rds.core.entities.IJimmerEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToMany

@Entity
interface Role : IJimmerEntity {
  val name: String
  val doc: String?

  @ManyToMany
  val permissions: List<Permissions>
}
