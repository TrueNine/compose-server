package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.User
import net.yan100.compose.rds.entity.UserInfo

interface UserInfoService : BaseService<UserInfo> {
  fun findUserByWechatOpenId(openId: String): User?
  fun findUserByPhone(phone: String): User?
  fun findByUserId(userId: String): UserInfo?
}
