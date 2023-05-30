package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.UserEntity
import net.yan100.compose.rds.entity.UserInfoEntity
import net.yan100.compose.rds.repository.UserInfoRepository
import net.yan100.compose.rds.service.UserInfoService
import org.springframework.stereotype.Service

@Service
class UserInfoServiceImpl(private val infoRepo: UserInfoRepository) : UserInfoService, BaseServiceImpl<UserInfoEntity>(infoRepo) {
  override fun findUserByWechatOpenId(openId: String): UserEntity? {
    return infoRepo.findUserByWechatOpenId(openId)
  }

  override fun findUserByPhone(phone: String): UserEntity? {
    return infoRepo.findUserByPhone(phone)
  }

  override fun findByUserId(userId: String): UserInfoEntity? {
    return infoRepo.findByUserId(userId)
  }
}
