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
import jakarta.persistence.Convert
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.consts.Regexes
import net.yan100.compose.core.models.WGS84
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.converters.WGS84Converter
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
abstract class SuperAddressDetails : IEntity() {
  /** 地址 id */
  abstract var addressId: RefId

  /** 联系电话 */
  @get:NotBlank(message = "手机号不能为空") @get:Pattern(regexp = Regexes.CHINA_PHONE, message = "请输入正确的电话号码") abstract var phone: SerialCode?

  /** ## 用户 id */
  @get:NotBlank(message = "用户 id 不能数为空") abstract var userId: RefId

  /** 联系人名称 */
  @get:NotBlank(message = "请留一个姓名") @get:Schema(title = "联系人名称") abstract var name: String?

  /** 地址代码 */
  @get:NotBlank(message = "地址代码不能为空") abstract var addressCode: SerialCode

  /** 地址详情 */
  @get:NotBlank(message = "详细地址不能为空") abstract var addressDetails: String

  /** 定位 */
  @get:Schema(title = "定位") @get:Convert(converter = WGS84Converter::class) abstract var center: WGS84?
}
