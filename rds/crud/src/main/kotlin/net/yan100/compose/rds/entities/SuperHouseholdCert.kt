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
import net.yan100.compose.core.RefId
import net.yan100.compose.core.ReferenceId
import net.yan100.compose.core.domain.IIdcard2Code
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.converters.BloodTypingConverter
import net.yan100.compose.rds.converters.DegreeTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.userinfo.BloodTyping
import net.yan100.compose.rds.core.typing.userinfo.DegreeTyping
import net.yan100.compose.rds.core.typing.userinfo.GenderTyping
import java.math.BigDecimal
import java.time.LocalDate

@MetaDef
@MappedSuperclass
abstract class SuperHouseholdCert : IIdcard2Code, IEntity {
  @get:Schema(title = "用户信息id")
  abstract var userInfoId: RefId?

  @get:Schema(title = "户口签发时间")
  abstract var issueDate: LocalDate?

  @get:Schema(title = "证件签发服务地址")
  abstract var serviceAddressDetailsId: String?

  @get:Schema(title = "兵役状况")
  abstract var militaryServiceStatus: String?

  @get:Schema(title = "职业")
  abstract var occupation: String?

  @get:Schema(title = "学历")
  @get:Convert(converter = DegreeTypingConverter::class)
  abstract var educationLevel: DegreeTyping?

  @get:Schema(title = "户口所属身份证号")
  abstract var idcardCode: string

  @get:Schema(title = "户口签发地址详情")
  abstract var originAddressDetailsId: ReferenceId?

  @get:Schema(title = "出生地址")
  abstract var placeBirthAddressDetailsId: ReferenceId?

  @get:Schema(title = "血型")
  @get:Convert(converter = BloodTypingConverter::class)
  abstract var bloodType: BloodTyping?

  @get:Schema(title = "身高")
  abstract var height: BigDecimal?

  @get:Schema(title = "生日")
  abstract var birthday: LocalDate?

  @get:Schema(title = "民族")
  abstract var ethnicGroup: String?

  @get:Schema(title = "性别")
  abstract var gender: GenderTyping

  @get:Schema(title = "与户主的关系")
  abstract var relationship: String?

  @get:Schema(title = "曾用名")
  abstract var oldName: String?

  @get:Schema(title = "户口页所属人名称")
  abstract var name: String?

  @get:Schema(title = "签发机关")
  abstract var issueOrgan: String?

  @get:Schema(title = "户口所在区域")
  abstract var addressDetailsId: RefId?

  @get:Schema(title = "户号")
  abstract var code: string?

  @get:Schema(title = "户主名称")
  abstract var householdPrimaryName: String?

  @get:Schema(title = "户口类别")
  abstract var householdType: Int?

  @get:Schema(title = "外联用户（所属用户）")
  abstract var userId: ReferenceId?

  @get:Transient
  @get:JsonIgnore
  override val idcard2Code: String get() = idcardCode
}

