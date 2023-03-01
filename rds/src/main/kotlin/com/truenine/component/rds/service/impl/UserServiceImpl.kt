package com.truenine.component.rds.service.impl

import com.truenine.component.core.lang.Str
import com.truenine.component.rds.dao.UserDao
import com.truenine.component.rds.dao.UserInfoDao
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
  override fun findUserById(id: String): UserDao? {
    return userRepo.findById(id).orElse(null)
  }

  override fun findUserByAccount(account: String): UserDao? {
    return userRepo.findByAccount(account)
  }

  override fun findPwdEncByAccount(account: String): String? {
    return userRepo.findPwdEncByAccount(account)
  }

  override fun existsByAccount(account: String): Boolean {
    return userRepo.existsAllByAccount(account)
  }

  override fun findUserInfoById(id: String): UserInfoDao? {
    return userInfoRepo.findByUserId(id)
  }

  override fun findUserInfoByAccount(account: String): UserInfoDao? {
    return findUserByAccount(account)?.let {
      userInfoRepo.findByUserId(it.id)
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun saveUser(user: UserDao): UserDao {
    return userRepo.save(user)
  }

  override fun saveUserInfo(@Valid userInfo: UserInfoDao): UserInfoDao? {
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
    userInfo: UserInfoDao
  ): UserInfoDao? {
    return findUserByAccount(account)?.run {
      userInfo.userId = this.id
      saveUserInfo(userInfo)
    }
  }

  override fun deleteUser(user: UserDao) {
    userRepo.delete(user)
  }

  override fun deleteUserInfo(userInfo: UserInfoDao) {
    userInfoRepo.delete(userInfo)
  }
}
