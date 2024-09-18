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
package net.yan100.compose.rds.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import net.yan100.compose.core.*
import net.yan100.compose.core.consts.IRegexes
import net.yan100.compose.core.domain.IIdcard2Code
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.converters.GenderTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.userinfo.GenderTyping

@MetaDef
@MappedSuperclass
abstract class SuperIdcard2 : IIdcard2Code, IEntity() {
  @get:Schema(title = "用户信息")
  abstract var userInfoId: RefId?

  @get:Schema(title = "签发机构")
  abstract var issueOrgan: String?

  @get:Schema(title = "身份证过期时间")
  abstract var expireDate: datetime?

  @get:Schema(title = "民族")
  abstract var ethnicGroup: String?

  @get:Schema(title = "生日")
  abstract var birthday: datetime?

  @get:NotBlank
  @get:Pattern(regexp = IRegexes.CHINA_ID_CARD, message = "身份证格式不对")
  @get:Schema(title = "身份证号")
  abstract var code: string

  @get:Schema(title = "性别")
  @get:Convert(converter = GenderTypingConverter::class)
  abstract var gender: GenderTyping?

  @get:Schema(title = "外联 地址详情id（出生地）")
  abstract var addressDetailsId: ReferenceId?

  @get:Schema(title = "名称")
  abstract var name: String

  @get:Schema(title = "外联 用户（所属用户）")
  abstract var userId: RefId?

  @get:Transient
  @get:JsonIgnore
  override val idcard2Code: string get() = this.code
}
