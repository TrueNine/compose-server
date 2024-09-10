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
package net.yan100.compose.rds.entities.attachment

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.alias.string
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.core.entities.ITreeEntity

@MetaDef
@MappedSuperclass
abstract class SuperBankCard : ITreeEntity() {
  @get:Schema(title = "银行预留手机号")
  abstract var reservePhone: string?

  @get:Schema(title = "用户信息id")
  abstract var userInfoId: RefId?

  @get:Schema(title = "开户行")
  abstract var issueAddressDetails: String?

  @get:Schema(title = "银行类型", example = "中国银行、建设银行")
  abstract var bankType: String?

  @get:Schema(title = "银行组织", example = "银联")
  abstract var bankGroup: Int?

  @get:Schema(title = "所属国家")
  abstract var country: Int?

  @get:Schema(title = "银行卡号")
  abstract var code: SerialCode

  @get:Schema(title = "用户 id")
  abstract var userId: RefId?
}
