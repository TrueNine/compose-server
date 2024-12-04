package net.yan100.compose.rds.crud.entities.jimmer

import net.yan100.compose.core.string
import net.yan100.compose.rds.core.entities.IJimmerPersistentEntity
import org.babyfish.jimmer.sql.Entity

@Entity
interface UserAccount : IJimmerPersistentEntity {
  val account: string
  val pwdEnc: string
  val nickName: string?
}
