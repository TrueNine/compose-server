package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.entities.User
import net.yan100.compose.rds.entities.UserInfo
import net.yan100.compose.rds.repositories.UserInfoRepo
import net.yan100.compose.rds.service.IUserInfoService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class UserInfoServiceImpl(private val infoRepo: UserInfoRepo) : IUserInfoService, CrudService<UserInfo>(infoRepo) {
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
    return infoRepo.existsByWechatOpenid(openId)
  }
}
