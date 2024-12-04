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
package net.yan100.compose.rds.crud.service.aggregator

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.RefId
import net.yan100.compose.core.datetime
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.entities.jpa.UserInfo

interface IAccountAggregator {
  @Schema(title = "修改账号密码")
  data class ModifyPasswordDto(
    var account: String? = null,
    var oldPassword: String? = null,
    var newPassword: String? = null,
  )

  @Schema(title = "登录账号")
  data class LoginDto(
    @Schema(title = "账号")
    var account: String? = null,
    @Schema(title = "密码")
    var password: String? = null,
  )

  @Schema(title = "账号注册")
  data class RegisterDto(
    @Schema(title = "创建此账户的 id （无需指定）", accessMode = Schema.AccessMode.WRITE_ONLY, hidden = true, deprecated = true)
    var createUserId: RefId? = null,
    @Schema(title = "账号")
    var account: String? = null,
    var nickName: String? = null,
    @Schema(title = "密码")
    var password: String? = null,
    @Schema(title = "描述")
    var description: String? = null,
  )

  /**
   * ## 为一个用户信息分配一个死账号
   *
   * @param createUserId 创建人
   * @param userInfoId 用户信息 id
   */
  fun assignAccountToUserInfo(createUserId: RefId, userInfoId: RefId): UserAccount?

  fun assignAccount(usr: UserAccount, createUserId: RefId, userInfo: UserInfo?, roleGroup: Set<String>? = null): UserAccount

  /** ## 注册账号 */
  fun registerAccount(param: RegisterDto): UserAccount?

  /** ## 根据 微信公众号给予的 openid 进行注册 */
  fun registerAccountForWxpa(param: RegisterDto, openId: String): UserAccount?

  /** ## 登录指定账号，返回用户信息 */
  fun login(param: LoginDto): UserAccount?

  /** 根据账号修改密码 */
  fun modifyPassword(param: ModifyPasswordDto): Boolean

  /** 根据账号校验密码正确性 */
  fun verifyPassword(account: String, password: String): Boolean

  /** ## 封禁账号到指定时间 */
  fun banWith(account: String, dateTime: datetime)
}
