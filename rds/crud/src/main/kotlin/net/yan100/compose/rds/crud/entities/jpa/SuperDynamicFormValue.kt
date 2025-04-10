package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.RefId
import net.yan100.compose.datetime
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.entities.IJpaEntity

@MetaDef
interface SuperDynamicFormValue : IJpaEntity {
  var apiFor: String?

  var platformFor: String?

  var inputUserId: RefId?

  @get:Deprecated("不建议使用，使用 user_id") var inputUserInfoId: RefId?

  var inputDatetime: datetime?

  /**
   * ## 审核 id
   *
   * 充分 描述了当前值的审核状态
   */
  var auditId: RefId?

  var formGroupId: RefId

  /**
   * ## 填入的 form 值类型
   *
   * json
   */
  var jsonValues: String?
}
