package net.yan100.compose.rds.jimmer.entities

import net.yan100.compose.core.RefId
import net.yan100.compose.core.datetime
import net.yan100.compose.core.string
import org.babyfish.jimmer.sql.Entity

@Entity
interface UserAccount : IEntity {

  val account: string
  val pwdEnc: string
  val createUserId: RefId?
  val nickName: string?
  val doc: string?
  val banTime: datetime?
  val lastLoginTime: datetime


}
