package com.truenine.component.rds.service.impl

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.entity.UserEntity
import com.truenine.component.rds.repository.UserRepository
import com.truenine.component.rds.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserServiceImpl(
  private val userRepo: UserRepository
) : UserService, BaseServiceImpl<UserEntity>(userRepo) {
  override fun findUserByAccount(account: String): UserEntity? = userRepo.findByAccount(account)

  override fun findPwdEncByAccount(account: String): String? = userRepo.findPwdEncByAccount(account)

  override fun existsByAccount(account: String): Boolean = userRepo.existsAllByAccount(account)

  @Transactional(rollbackFor = [Exception::class])
  override fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?) {
    if (null == dateTime ||
      LocalDateTime.now().isBefore(dateTime)
    ) {
      userRepo.saveUserBanTimeByAccount(dateTime, account)
    }
  }
}
