package net.yan100.compose.rds.service

import net.yan100.compose.rds.entities.User
import net.yan100.compose.rds.entities.UserInfo
import net.yan100.compose.rds.service.base.IService

interface IUserInfoService : IService<UserInfo> {
  fun findUserByWechatOpenId(openId: String): User?
  fun findUserByPhone(phone: String): User?
  fun findByUserId(userId: String): UserInfo?
  fun existsByPhone(phone: String): Boolean
  fun existsByWechatOpenId(openId: String): Boolean
}
