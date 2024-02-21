/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.service.aggregator

import java.time.LocalDateTime
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.rds.entities.UserInfo
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.models.req.LoginAccountReq
import net.yan100.compose.rds.models.req.ModifyAccountPasswordReq
import net.yan100.compose.rds.models.req.RegisterAccountReq

interface IAccountAggregator {
  fun assignAccount(
    usr: Usr,
    createUserId: ReferenceId,
    userInfo: UserInfo?,
    roleGroup: Set<String>?,
    allowAssignRoot: Boolean = false
  ): Usr

  /** 注册账号 */
  fun registerAccount(param: RegisterAccountReq): Usr?

  /** 登录指定账号，返回用户信息 */
  fun login(param: LoginAccountReq): Usr?

  /** 根据账号修改密码 */
  fun modifyPassword(param: ModifyAccountPasswordReq): Boolean

  /** 根据账号校验密码正确性 */
  fun verifyPassword(account: String, password: String): Boolean

  /** 封禁账号到指定时间 */
  fun bannedAccountTo(account: String, dateTime: LocalDateTime)
}
