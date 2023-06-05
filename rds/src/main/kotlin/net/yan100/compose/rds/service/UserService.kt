package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.FullUser
import net.yan100.compose.rds.entity.User
import java.time.LocalDateTime

interface UserService : BaseService<User> {
  fun findUserByAccount(account: String): User?
  fun findFullUserByAccount(account: String): FullUser?

  fun findPwdEncByAccount(account: String): String?

  fun existsByAccount(account: String): Boolean
  fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?)
}
