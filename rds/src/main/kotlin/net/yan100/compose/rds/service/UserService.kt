package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.FullUserEntity
import net.yan100.compose.rds.entity.UserEntity
import java.time.LocalDateTime

interface UserService : BaseService<UserEntity> {
  fun findUserByAccount(account: String): UserEntity?
  fun findFullUserByAccount(account: String): FullUserEntity?

  fun findPwdEncByAccount(account: String): String?

  fun existsByAccount(account: String): Boolean
  fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?)
}
