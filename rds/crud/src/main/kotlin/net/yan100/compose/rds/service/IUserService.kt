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
package net.yan100.compose.rds.service

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.entities.FullUsr
import net.yan100.compose.rds.entities.Usr
import java.time.LocalDateTime

interface IUserService : ICrud<Usr> {
  fun findUserByAccount(account: String): Usr?

  fun existsByUserInfoId(userInfoId: String): Boolean

  fun findFullUserByAccount(account: String): FullUsr?

  fun findAccountByWechatOpenId(openId: String): String?

  fun findAccountByPhone(phone: String): String?

  fun findPwdEncByAccount(account: String): String?

  fun existsByAccount(account: String): Boolean

  fun existsByWechatOpenId(openId: String): Boolean

  fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?)
}