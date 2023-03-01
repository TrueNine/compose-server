package io.tn.rds.service

import io.tn.rds.dao.UserDao
import io.tn.rds.dao.UserInfoDao

interface UserService {
  fun findUserById(id: String): UserDao?
  fun findUserByAccount(account: String): UserDao?
  fun findPwdEncByAccount(account: String): String?
  fun existsByAccount(account: String): Boolean

  fun findUserInfoById(id: String): UserInfoDao?
  fun findUserInfoByAccount(account: String): UserInfoDao?

  fun saveUser(user: UserDao): UserDao?
  fun saveUserInfo(userInfo: UserInfoDao): UserInfoDao?
  fun saveUserInfoByAccount(
    account: String,
    userInfo: UserInfoDao
  ): UserInfoDao?

  fun deleteUser(user: UserDao)
  fun deleteUserInfo(userInfo: UserInfoDao)
}
