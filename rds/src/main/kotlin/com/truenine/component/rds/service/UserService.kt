package com.truenine.component.rds.service

import com.truenine.component.rds.entity.UserEntity
import com.truenine.component.rds.entity.UserInfoEntity

interface UserService {
  fun findUserById(id: String): UserEntity?
  fun findUserByAccount(account: String): UserEntity?
  fun findPwdEncByAccount(account: String): String?
  fun existsByAccount(account: String): Boolean

  fun findUserInfoById(id: String): UserInfoEntity?
  fun findUserInfoByAccount(account: String): UserInfoEntity?

  fun saveUser(user: UserEntity): UserEntity?
  fun saveUserInfo(userInfo: UserInfoEntity): UserInfoEntity?
  fun saveUserInfoByAccount(
    account: String,
    userInfo: UserInfoEntity
  ): UserInfoEntity?

  fun deleteUser(user: UserEntity)
  fun deleteUserInfo(userInfo: UserInfoEntity)
}
