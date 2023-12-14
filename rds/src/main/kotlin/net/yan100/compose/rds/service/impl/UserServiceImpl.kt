package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.entities.FullUsr
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.repositories.IFullUserRepo
import net.yan100.compose.rds.repositories.IUsrRepo
import net.yan100.compose.rds.service.IUserService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserServiceImpl(
  private val userRepo: IUsrRepo,
  private val fullRepo: IFullUserRepo
) : IUserService, CrudService<Usr>(userRepo) {
  override fun findUserByAccount(account: String): Usr? {
    return userRepo.findByAccount(account)
  }

  override fun findFullUserByAccount(account: String): FullUsr? {
    return fullRepo.findByAccount(account)
  }

  override fun findAccountByWechatOpenId(openId: String): String? {
    return userRepo.findAccountByUserInfoWechatOpenid(openId)
  }

  override fun findAccountByPhone(phone: String): String? {
    return userRepo.findAccountByUserInfoPhone(phone)
  }

  override fun findPwdEncByAccount(account: String): String? = userRepo.findPwdEncByAccount(account)

  override fun existsByAccount(account: String): Boolean = userRepo.existsAllByAccount(account)
  override fun existsByWechatOpenId(openId: String): Boolean {
    return userRepo.existsByWechatOpenId(openId)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?) {
    if (null == dateTime ||
      LocalDateTime.now().isBefore(dateTime)
    ) {
      userRepo.saveUserBanTimeByAccount(dateTime, account)
    }
  }
}
