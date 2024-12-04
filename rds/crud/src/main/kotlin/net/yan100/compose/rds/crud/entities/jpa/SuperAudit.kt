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
import net.yan100.compose.core.datetime
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.typing.AuditTyping
import net.yan100.compose.rds.crud.converters.AuditTypingConverter

/**
 * ## 审核条目
 */
@MetaDef
interface SuperAudit : IJpaEntity {
  /**
   * 审核人设备 id
   */
  var auditDeviceId: string?

  /**
   * 审核人 ip
   */
  var auditIp: String?

  /**
   * 审核类型
   */
  var refType: Int?

  /**
   * 审核外键
   */
  var refId: RefId

  /**
   * 审核备注
   */
  var remark: String?

  /**
   * 创建时间
   */
  var createDatetime: datetime

  /**
   * 审核状态
   */
  @get:Convert(converter = AuditTypingConverter::class)
  var state: AuditTyping
}
