package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.service.base.CrudService
import net.yan100.compose.rds.entity.FullUser
import net.yan100.compose.rds.entity.User
import net.yan100.compose.rds.repository.FullUserRepository
import net.yan100.compose.rds.repository.UserRepo
import net.yan100.compose.rds.service.IUserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserServiceImpl(
  private val userRepo: UserRepo,
  private val fullRepo: FullUserRepository
) : IUserService, CrudService<User>(userRepo) {
  override fun findUserByAccount(account: String): User? {
    return userRepo.findByAccount(account)
  }

  override fun findFullUserByAccount(account: String): FullUser? {
    return fullRepo.findByAccount(account)
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
