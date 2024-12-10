package net.yan100.compose.rds.core.entities

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table

@Entity
@Table(name = "user_account")
interface JimmerUserAccount : IJimmerEntity {
  val account: String
  val pwdEnc: String
  val nickName: String
}
