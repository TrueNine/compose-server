package net.yan100.compose.rds.crud.service

import java.time.LocalDateTime
import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.crud.entities.jpa.FullUserAccount
import net.yan100.compose.rds.crud.entities.jpa.UserAccount

interface IUserAccountService : ICrud<UserAccount> {
  fun fetchByAccount(account: String): UserAccount?

  fun foundByUserInfoId(userInfoId: RefId): Boolean

  fun findFullUserByAccount(account: String): FullUserAccount?

  fun findAccountByWechatOpenId(openId: String): String?

  fun findAccountByPhone(phone: String): String?

  fun findPwdEncByAccount(account: String): String?

  fun existsByAccount(account: String): Boolean

  fun existsByWechatOpenId(openId: String): Boolean

  fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?)
}
