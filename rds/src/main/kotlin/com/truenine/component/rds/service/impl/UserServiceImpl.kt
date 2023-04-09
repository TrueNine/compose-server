package com.truenine.component.rds.service.impl

import com.truenine.component.core.lang.Str
import com.truenine.component.rds.entity.UserEntity
import com.truenine.component.rds.entity.UserInfoEntity
import com.truenine.component.rds.repo.UserInfoRepo
import com.truenine.component.rds.repo.UserRepo
import com.truenine.component.rds.service.UserService
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
open class UserServiceImpl(
  private val userRepo: UserRepo,
  private val userInfoRepo: UserInfoRepo
) : UserService {
  override fun findUserById(id: String): UserEntity? {
    return userRepo.findById(id).orElse(null)
  }

  override fun findUserByAccount(account: String): UserEntity? {
    return userRepo.findByAccount(account)
  }

  override fun findPwdEncByAccount(account: String): String? {
    return userRepo.findPwdEncByAccount(account)
  }

  override fun existsByAccount(account: String): Boolean {
    return userRepo.existsAllByAccount(account)
  }

  override fun findUserInfoById(id: String): UserInfoEntity? {
    return userInfoRepo.findByUserId(id)
  }

  override fun findUserInfoByAccount(account: String): UserInfoEntity? {
    return findUserByAccount(account)?.let {
      userInfoRepo.findByUserId(it.id)
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun saveUser(user: UserEntity): UserEntity {
    return userRepo.save(user)
  }

  override fun saveUserInfo(@Valid userInfo: UserInfoEntity): UserInfoEntity? {
    return userInfo.takeIf {
      Str.hasText(it.userId)
        && it.birthday?.run {
        LocalDate.now().isAfter(this)
      } ?: true
    }?.run {
      userInfoRepo.save(userInfo)
    }
  }

  override fun saveUserInfoByAccount(
    account: String,
    userInfo: UserInfoEntity
  ): UserInfoEntity? {
    return findUserByAccount(account)?.run {
      userInfo.userId = this.id
      saveUserInfo(userInfo)
    }
  }

  override fun deleteUser(user: UserEntity) {
    userRepo.delete(user)
  }

  override fun deleteUserInfo(userInfo: UserInfoEntity) {
    userInfoRepo.delete(userInfo)
  }
}
