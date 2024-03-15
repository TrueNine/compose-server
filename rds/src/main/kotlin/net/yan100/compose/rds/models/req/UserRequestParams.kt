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
package net.yan100.compose.rds.models.req

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.consts.Regexes

@Schema(title = "登录账号")
class LoginAccountReq {
  @get:Schema(title = "账号") @get:NotBlank(message = "账号不能为空") var account: String? = null

  @get:Schema(title = "密码") @get:NotBlank(message = "密码不能为空") var password: String? = null
}

@Schema(title = "修改账号密码")
class ModifyAccountPasswordReq {
  @get:NotBlank(message = "账号不能为空") var account: String? = null

  @get:NotBlank(message = "密码不能为空")
  @get:Pattern(regexp = Regexes.PASSWORD, message = "密码必须匹配规则为：" + Regexes.PASSWORD)
  var oldPassword: String? = null

  @get:NotBlank(message = "新密码不能为空")
  @get:Pattern(regexp = Regexes.PASSWORD, message = "密码必须匹配规则为：" + Regexes.PASSWORD)
  var newPassword: String? = null
}

@Schema(title = "账号注册")
class RegisterAccountReq {
  @Schema(
    title = "创建此账户的 id （无需指定）",
    accessMode = Schema.AccessMode.WRITE_ONLY,
    hidden = true,
    deprecated = true
  )
  var createUserId: RefId? = null

  @get:Schema(title = "账号") @get:NotBlank(message = "账号不可为空") var account: String? = null

  @get:NotBlank(message = "昵称不能为空")
  @get:Size(max = 128, min = 4, message = "昵称最长 128，最短 4")
  var nickName: String? = null

  @get:Schema(title = "密码")
  @get:NotBlank(message = "密码不能为空")
  @get:Size(max = 100, min = 8, message = "密码最短8位，最长100")
  @get:Pattern(regexp = Regexes.PASSWORD, message = "密码必须匹配规则为：" + Regexes.PASSWORD)
  var password: String? = null

  @get:Schema(title = "描述") var description: String? = null
}
