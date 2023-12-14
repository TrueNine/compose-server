package net.yan100.compose.rds.service

import net.yan100.compose.rds.entities.FullUsr
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.service.base.IService
import java.time.LocalDateTime

interface IUserService : IService<Usr> {
  fun findUserByAccount(account: String): Usr?
  fun findFullUserByAccount(account: String): FullUsr?

  fun findAccountByWechatOpenId(openId: String): String?
  fun findAccountByPhone(phone: String): String?

  fun findPwdEncByAccount(account: String): String?
  fun existsByAccount(account: String): Boolean
  fun existsByWechatOpenId(openId: String): Boolean
  fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?)
}
