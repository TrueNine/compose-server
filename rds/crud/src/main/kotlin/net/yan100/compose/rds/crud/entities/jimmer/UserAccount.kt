package net.yan100.compose.rds.crud.entities.jimmer

import net.yan100.compose.core.datetime
import net.yan100.compose.core.string
import net.yan100.compose.core.toLong
import net.yan100.compose.rds.core.entities.IJimmerPersistentEntity
import org.babyfish.jimmer.Formula
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.JoinTable
import org.babyfish.jimmer.sql.ManyToMany

@Entity
interface UserAccount : IJimmerPersistentEntity {
  val account: string
  val pwdEnc: string
  val nickName: string?

  /**
   * 被封禁到期时间
   */
  val banTime: datetime?

  val lastLoginTime: datetime

  /**
   * 该账号是否被封禁
   */
  @Formula(dependencies = ["banTime"])
  val disabled: Boolean get() = banTime != null && banTime!!.toLong() > System.currentTimeMillis()

  /**
   * 账号拥有的角色组
   */
  @ManyToMany
  val roleGroups: List<RoleGroup>

  /**
   * 账号所属部门
   */
  @ManyToMany
  @JoinTable(
    name = "user_dept",
    joinColumnName = "user_id",
    inverseJoinColumnName = "dept_id"
  )
  val departments: List<Dept>
}
