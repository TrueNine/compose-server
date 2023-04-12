package com.truenine.component.rds.service.impl

import com.truenine.component.core.lang.hasText
import com.truenine.component.core.lang.requireAll
import com.truenine.component.rds.entity.UserEntity
import com.truenine.component.rds.entity.UserInfoEntity
import com.truenine.component.rds.repo.UserInfoRepo
import com.truenine.component.rds.repo.UserRepo
import com.truenine.component.rds.service.UserService
import jakarta.validation.Valid
import org.springframework.stereotype.Service
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


  override fun saveUser(user: UserEntity): UserEntity {
    return userRepo.save(user)
  }

  override fun saveUserInfo(@Valid userInfo: UserInfoEntity): UserInfoEntity? {
    require(userInfo.birthday != null) {
      "传入的出生日期为 null $userInfo"
    }
    requireAll(
      hasText(userInfo.userId),
      LocalDate.now().isAfter(userInfo.birthday),
    ) { "用户不合法 $userInfo" }
    return userInfoRepo.save(userInfo)
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
