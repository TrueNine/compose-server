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
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.alias.string
import net.yan100.compose.rds.Col
import net.yan100.compose.rds.core.entities.ITreeEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@MappedSuperclass
abstract class SuperBankCard : ITreeEntity() {
  companion object {
    const val TABLE_NAME = "bank_card"

    const val USER_INFO_ID = "user_info_id"
    const val RESERVE_PHONE = "reserve_phone"
    const val USER_ID = "user_id"
    const val CODE = "code"
    const val COUNTRY = "country"
    const val BANK_GROUP = "bank_group"
    const val BANK_TYPE = "bank_type"
    const val ISSUE_ADDRESS_DETAILS = "issue_address_details"
  }

  @Schema(title = "银行预留手机号") @Col(name = RESERVE_PHONE) var reservePhone: string? = null

  @Schema(title = "用户信息id") @Col(name = USER_INFO_ID) var userInfoId: RefId? = null

  @Schema(title = "开户行") @Col(name = ISSUE_ADDRESS_DETAILS) var issueAddressDetails: String? = null

  @Schema(title = "银行类型", example = "中国银行、建设银行") @Column(name = BANK_TYPE) var bankType: String? = null

  @Schema(title = "银行组织", example = "银联") @Column(name = BANK_GROUP) var bankGroup: Int? = null

  @Schema(title = "所属国家") @Column(name = COUNTRY) var country: Int? = null

  @Schema(title = "银行卡号") @Column(name = CODE) lateinit var code: SerialCode

  @Column(name = USER_ID) var userId: RefId? = null
}

@Entity @DynamicUpdate @DynamicInsert @Table(name = SuperBankCard.TABLE_NAME) class BankCard : SuperBankCard()
