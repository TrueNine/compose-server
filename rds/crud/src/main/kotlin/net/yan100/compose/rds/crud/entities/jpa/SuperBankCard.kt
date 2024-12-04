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

import net.yan100.compose.core.RefId
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaTreeEntity

@MetaDef
interface SuperBankCard : IJpaTreeEntity {
  /**
   * 银行预留手机号
   */
  var reservePhone: string?

  /**
   * 用户信息id
   */
  var userInfoId: RefId?

  /**
   * 开户行
   */
  var issueAddressDetails: String?

  /**
   * ## 银行类型
   *
   * 中国银行、建设银行
   */
  var bankType: String?

  /**
   * ## 银行组织
   *
   * 银联、mastercard、visa
   */
  var bankGroup: Int?

  /**
   * 所属国家代码
   */
  var country: Int?

  /**
   * ## 银行卡号
   */
  var code: string

  /**
   * 所属用户 id
   */
  var userId: RefId?
}
