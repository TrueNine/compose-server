package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.User
import net.yan100.compose.rds.entity.UserInfo
import net.yan100.compose.rds.repository.UserInfoRepo
import net.yan100.compose.rds.service.UserInfoService
import org.springframework.stereotype.Service

@Service
class UserInfoServiceImpl(private val infoRepo: UserInfoRepo) : UserInfoService, BaseServiceImpl<UserInfo>(infoRepo) {
  override fun findUserByWechatOpenId(openId: String): User? {
    return infoRepo.findUserByWechatOpenId(openId)
  }

  override fun findUserByPhone(phone: String): User? {
    return infoRepo.findUserByPhone(phone)
  }

  override fun findByUserId(userId: String): UserInfo? {
    return infoRepo.findByUserId(userId)
  }

  override fun existsByPhone(phone: String): Boolean {
    return infoRepo.existsByPhone(phone)
  }

  override fun existsByWechatOpenId(openId: String): Boolean {
    return infoRepo.existsByWechatOpenId(openId)
  }
}
