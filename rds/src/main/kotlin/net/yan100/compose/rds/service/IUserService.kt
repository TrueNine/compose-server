package net.yan100.compose.rds.service

import net.yan100.compose.rds.entity.FullUser
import net.yan100.compose.rds.entity.User
import net.yan100.compose.rds.service.base.IService
import java.time.LocalDateTime

interface IUserService : IService<User> {
  fun findUserByAccount(account: String): User?
  fun findFullUserByAccount(account: String): FullUser?

  fun findPwdEncByAccount(account: String): String?
  fun existsByAccount(account: String): Boolean
  fun existsByWechatOpenId(openId: String): Boolean
  fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?)
}
