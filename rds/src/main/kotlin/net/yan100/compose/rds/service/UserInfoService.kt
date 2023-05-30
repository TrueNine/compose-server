package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.UserEntity
import net.yan100.compose.rds.entity.UserInfoEntity

interface UserInfoService : BaseService<UserInfoEntity> {
  fun findUserByWechatOpenId(openId: String): UserEntity?
  fun findUserByPhone(phone: String): UserEntity?
  fun findByUserId(userId: String): UserInfoEntity?
}
