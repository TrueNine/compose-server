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
package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import net.yan100.compose.core.RefId
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.typing.BloodTyping
import net.yan100.compose.rds.core.typing.DegreeTyping
import net.yan100.compose.rds.core.typing.GenderTyping
import net.yan100.compose.rds.crud.converters.BloodTypingConverter
import net.yan100.compose.rds.crud.converters.DegreeTypingConverter
import java.math.BigDecimal
import java.time.LocalDate

@MetaDef
interface SuperHouseholdCert : IJpaEntity {
  /**
   * 用户信息id
   */
  var userInfoId: RefId?

  /**
   * 户口签发时间
   */
  var issueDate: LocalDate?

  /**
   * 证件签发服务地址
   */
  var serviceAddressDetailsId: String?

  /**
   * 兵役状况
   */
  var militaryServiceStatus: String?

  /**
   * 职业
   */
  var occupation: String?

  /**
   * 学历
   */
  @get:Convert(converter = DegreeTypingConverter::class)
  var educationLevel: DegreeTyping?

  /**
   * 户口所属身份证号
   */
  var idcardCode: string

  /**
   * 户口签发地址详情
   */
  var originAddressDetailsId: RefId?

  /**
   * 出生地址
   */
  var placeBirthAddressDetailsId: RefId?

  /**
   * 血型
   */
  @get:Convert(converter = BloodTypingConverter::class)
  var bloodType: BloodTyping?

  /**
   * 身高
   */
  var height: BigDecimal?

  /**
   * 生日
   */
  var birthday: LocalDate?

  /**
   * 民族
   */
  var ethnicGroup: String?

  /**
   * 性别
   */
  var gender: GenderTyping

  /**
   * 与户主的关系
   */
  var relationship: String?

  /**
   * 曾用名
   */
  var oldName: String?

  /**
   * 户口页所属人名称
   */
  var name: String?

  /**
   * 签发机关
   */
  var issueOrgan: String?

  /**
   * 户口所在区域
   */
  var addressDetailsId: RefId?

  /**
   * 户号
   */
  var code: string?

  /**
   * 户主名称
   */
  var householdPrimaryName: String?

  /**
   * 户口类别
   */
  var householdType: Int?

  /**
   * 外联用户（所属用户）
   */
  var userId: RefId?
}

