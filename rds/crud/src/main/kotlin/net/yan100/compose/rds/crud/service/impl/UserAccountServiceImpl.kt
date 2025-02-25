package net.yan100.compose.rds.crud.service.impl

import java.time.LocalDateTime
import net.yan100.compose.core.RefId
import net.yan100.compose.core.datetime
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.FullUserAccount
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.repositories.jpa.IFullUserAccountRepo
import net.yan100.compose.rds.crud.repositories.jpa.IUserAccountRepo
import net.yan100.compose.rds.crud.service.IUserAccountService
import org.springframework.stereotype.Service

@Service
class UserAccountServiceImpl(
  private val userRepo: IUserAccountRepo,
  private val fullRepo: IFullUserAccountRepo,
) : IUserAccountService, ICrud<UserAccount> by jpa(userRepo) {

  override fun fetchByAccount(account: String): UserAccount? {
    return userRepo.findByAccount(account)
  }

  override fun foundByUserInfoId(userInfoId: RefId): Boolean {
    return userRepo.existsByUserInfoId(userInfoId)
  }

  override fun findFullUserByAccount(account: String): FullUserAccount? {
    return fullRepo.findByAccount(account)
  }

  override fun findAccountByWechatOpenId(openId: String): String? {
    return userRepo.findAccountByUserInfoWechatOpenid(openId)
  }

  override fun findAccountByPhone(phone: String): String? {
    return userRepo.findAccountByUserInfoPhone(phone)
  }

  override fun findPwdEncByAccount(account: String): String? =
    userRepo.findPwdEncByAccount(account)

  override fun existsByAccount(account: String): Boolean =
    userRepo.existsAllByAccount(account)

  override fun existsByWechatOpenId(openId: String): Boolean {
    return userRepo.existsByWechatOpenId(openId)
  }

  @ACID
  override fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?) {
    if (null == dateTime || datetime.now().isBefore(dateTime)) {
      userRepo.saveUserBanTimeByAccount(dateTime, account)
    }
  }
}
