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

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Table
import net.yan100.compose.core.alias.*
import net.yan100.compose.rds.core.entities.IEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDate

@MappedSuperclass
abstract class SuperBizCert : IEntity() {
  companion object {
    const val TABLE_NAME = "biz_cert"

    const val TYPE = "type"
    const val TITLE = "title"
    const val USER_ID = "user_id"
    const val USER_INFO_ID = "user_info_id"
    const val REG_CAPITAL = "reg_capital"
    const val CREATE_DATE = "create_date"
    const val BIZ_RANGE = "biz_range"
    const val ISSUE_DATE = "issue_date"
    const val LEADER_NAME = "leader_name"
    const val ADDRESS_CODE = "address_code"
    const val UNI_CREDIT_CODE = "uni_credit_code"
    const val ADDRESS_DETAILS_ID = "address_details_id"
  }

  @Schema(title = "用户信息id") @Column(name = USER_INFO_ID) var userInfoId: RefId? = null

  @Schema(title = "签发时间") @Column(name = ISSUE_DATE) var issueDate: LocalDate? = null

  @Schema(title = "地址详情") @Column(name = ADDRESS_DETAILS_ID) var addressDetailsId: ReferenceId? = null

  @Schema(title = "注册地") @Column(name = ADDRESS_CODE) var addressCode: SerialCode? = null

  @Schema(title = "经营范围") @Column(name = BIZ_RANGE) var bizRange: BigText? = null

  @Schema(title = "法人姓名") @Column(name = LEADER_NAME) var leaderName: String? = null

  @Schema(title = "类型") @Column(name = TYPE) var type: Int? = null

  @Schema(title = "统一社会信用代码") @Column(name = UNI_CREDIT_CODE) var uniCreditCode: SerialCode? = null

  @Schema(title = "成立日期") @Column(name = CREATE_DATE) var createDate: LocalDate? = null

  @Schema(title = "注册资本") @Column(name = REG_CAPITAL) var regCapital: decimal? = null

  @Schema(title = "营业执照名称") @Column(name = TITLE) lateinit var title: String

  @Schema(title = "所属上传用户") @Column(name = USER_ID) var userId: RefId? = null
}

@Entity @DynamicUpdate @DynamicInsert @Table(name = SuperBizCert.TABLE_NAME) class BizCert : SuperBizCert()
