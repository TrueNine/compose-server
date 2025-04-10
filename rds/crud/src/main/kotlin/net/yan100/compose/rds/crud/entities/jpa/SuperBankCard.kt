package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.RefId
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.entities.IJpaTreeEntity
import net.yan100.compose.string

@MetaDef
interface SuperBankCard : IJpaTreeEntity {
  /** 银行预留手机号 */
  var reservePhone: string?

  /** 用户信息id */
  var userInfoId: RefId?

  /** 开户行 */
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

  /** 所属国家代码 */
  var country: Int?

  /** ## 银行卡号 */
  var code: string

  /** 所属用户 id */
  var userId: RefId?
}
