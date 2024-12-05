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
package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.core.datetime
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.FullUserAccount
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.repositories.jimmer.IJimmerUserAccountRepo
import net.yan100.compose.rds.crud.repositories.jpa.IFullUserAccountRepo
import net.yan100.compose.rds.crud.repositories.jpa.IUserAccountRepo
import net.yan100.compose.rds.crud.service.IUserAccountService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserAccountServiceImpl(
  private val userRepo: IUserAccountRepo,
  private val fullRepo: IFullUserAccountRepo,
  private val jimmerUserAccountRepo: IJimmerUserAccountRepo
) : IUserAccountService, ICrud<UserAccount> by jpa(userRepo) {

  override fun fetchByAccount(account: String): UserAccount? {
    return userRepo.findByAccount(account)
  }

  override fun foundByUserInfoId(userInfoId: String): Boolean {
    return userRepo.existsByUserInfoId(userInfoId)
  }

  override fun findFullUserByAccount(account: String): FullUserAccount? {
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

  @ACID
  override fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?) {
    if (null == dateTime || datetime.now().isBefore(dateTime)) {
      userRepo.saveUserBanTimeByAccount(dateTime, account)
    }
  }
}
