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
package net.yan100.compose.rds.entities.cert

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.alias.date
import net.yan100.compose.core.models.IDisCode2
import net.yan100.compose.rds.converters.DisTypingConverter
import net.yan100.compose.rds.converters.GenderTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.cert.DisTyping
import net.yan100.compose.rds.core.typing.userinfo.GenderTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@MappedSuperclass
abstract class SuperDisCert2 : IDisCode2, IEntity() {
  companion object {
    const val TABLE_NAME = "dis_cert_2"

    const val USER_ID = "user_id"
    const val USER_INFO_ID = "user_info_id"
    const val NAME = "name"
    const val CODE = "code"
    const val GENDER = "gender"
    const val TYPE = "type"
    const val LEVEL = "level"
    const val ISSUE_DATE = "issue_date"
    const val EXPIRE_DATE = "expire_date"
    const val ADDRESS_DETAILS_ID = "address_details_id"
    const val GUARDIAN = "guardian"
    const val GUARDIAN_PHONE = "guardian_phone"
    const val BIRTHDAY = "birthday"
  }

  @Schema(title = "用户信息id") @Column(name = USER_INFO_ID) var userInfoId: RefId? = null

  @Schema(title = "出生日期") @Column(name = BIRTHDAY) var birthday: LocalDate? = null

  @Schema(title = "监护人联系电话") @Column(name = GUARDIAN_PHONE) var guardianPhone: String? = null

  @Schema(title = "监护人姓名") @Column(name = GUARDIAN) var guardian: String? = null

  @Schema(title = "家庭住址") @Column(name = ADDRESS_DETAILS_ID) var addressDetailsId: String? = null

  @Schema(title = "证件过期时间") @Column(name = EXPIRE_DATE) var expireDate: date? = null

  @Schema(title = "签发时间") @Column(name = ISSUE_DATE) var issueDate: LocalDate? = null

  @Schema(title = "残疾级别") @Column(name = LEVEL) var level: Int? = null

  @Schema(title = "残疾类别") @Column(name = TYPE) @Convert(converter = DisTypingConverter::class) lateinit var type: DisTyping

  @Schema(title = "性别") @Column(name = GENDER) @Convert(converter = GenderTypingConverter::class) lateinit var gender: GenderTyping

  @NotNull @Schema(title = "残疾证编号") @Column(name = CODE) lateinit var code: SerialCode

  @Schema(title = "姓名") @Column(name = NAME) lateinit var name: String

  @Schema(title = "外联用户（所属用户）") @Column(name = USER_ID) var userId: ReferenceId? = null

  @get:Transient
  @get:JsonIgnore
  override val disabilityCode: String
    get() = this.code
}

@Entity @DynamicInsert @DynamicUpdate @Table(name = SuperDisCert2.TABLE_NAME) class DisCert2 : SuperDisCert2()
