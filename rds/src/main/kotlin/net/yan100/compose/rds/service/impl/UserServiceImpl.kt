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

import java.time.LocalDateTime
import net.yan100.compose.rds.entities.FullUsr
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.repositories.IFullUserRepo
import net.yan100.compose.rds.repositories.IUsrRepo
import net.yan100.compose.rds.service.IUserService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(private val userRepo: IUsrRepo, private val fullRepo: IFullUserRepo) :
  IUserService, CrudService<Usr>(userRepo) {
  override fun findUserByAccount(account: String): Usr? {
    return userRepo.findByAccount(account)
  }

  override fun findFullUserByAccount(account: String): FullUsr? {
    return fullRepo.findByAccount(account)
  }

  override fun findAccountByWechatOpenId(openId: String): String? {
    return userRepo.findAccountByUserInfoWechatOpenid(openId)
  }

  override fun findAccountByPhone(phone: String): String? {
    return userRepo.findAccountByUserInfoPhone(phone)
  }

  override fun findPwdEncByAccount(account: String): String? = userRepo.findPwdEncByAccount(account)

  override fun existsByAccount(account: String): Boolean = userRepo.existsAllByAccount(account)

  override fun existsByWechatOpenId(openId: String): Boolean {
    return userRepo.existsByWechatOpenId(openId)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?) {
    if (null == dateTime || LocalDateTime.now().isBefore(dateTime)) {
      userRepo.saveUserBanTimeByAccount(dateTime, account)
    }
  }
}
