/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.service.impl

import net.yan100.compose.core.alias.RefId
import net.yan100.compose.rds.entities.UserInfo
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.repositories.IUserInfoRepo
import net.yan100.compose.rds.service.IUserInfoService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class UserInfoServiceImpl(private val infoRepo: IUserInfoRepo) :
  IUserInfoService, CrudService<UserInfo>(infoRepo) {
  override fun findAllIdByUserId(userId: RefId): List<RefId> {
    return infoRepo.findAllIdByUserId(userId)
  }

  override fun findUserIdById(id: RefId): RefId? {
    return infoRepo.findUserIdById(id)
  }

  override fun findUserByWechatOpenId(openId: String): Usr? {
    return infoRepo.findUserByWechatOpenId(openId)
  }

  override fun findUserByPhone(phone: String): Usr? {
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
