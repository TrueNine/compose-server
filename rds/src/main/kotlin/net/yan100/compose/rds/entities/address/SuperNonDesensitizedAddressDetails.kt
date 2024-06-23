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
package net.yan100.compose.rds.entities.address

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import net.yan100.compose.core.alias.NonDesensitizedRef
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.consts.Regexes
import net.yan100.compose.ksp.core.annotations.MetaDef

@MetaDef
abstract class SuperNonDesensitizedAddressDetails : SuperAddressDetails() {
  /** 联系电话 */
  @get:NonDesensitizedRef
  @get:NotBlank(message = "手机号不能为空")
  @get:Pattern(regexp = Regexes.CHINA_PHONE, message = "请输入正确的电话号码")
  abstract override var phone: String?

  /** 地址详情 */
  @get:NonDesensitizedRef @get:NotBlank(message = "详细地址不能为空") @get:Schema(title = "地址详情") abstract override var addressDetails: String

  /** 地址代码 */
  @get:NonDesensitizedRef @get:NotBlank(message = "地址代码不能为空") abstract override var addressCode: SerialCode

  /** 联系人名称 */
  @get:NonDesensitizedRef @get:NotBlank(message = "姓名不能为空") abstract override var name: String?
}
