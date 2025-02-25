package net.yan100.compose.client.jimmer

import net.yan100.compose.core.RefId
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Id

@Entity
interface JimmerEntity {
  @Id val id: String
  val name: RefId
  val fullName: FullName?
}
