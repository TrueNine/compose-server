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

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.RefId
import net.yan100.compose.core.datetime
import net.yan100.compose.core.string
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.converters.AuditTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.AuditTyping

@MetaDef
@Schema(title = "审核条目")
@MappedSuperclass
abstract class SuperAudit : IEntity() {


  @get:Schema(title = "审核人设备 id")
  abstract var auditDeviceId: string?

  @get:Schema(title = "审核人 ip")
  abstract var auditIp: String?

  @get:Schema(title = "审核类型")
  abstract var refType: Int?

  @get:Schema(title = "审核外键")
  abstract var refId: RefId

  @get:Schema(title = "审核备注")
  abstract var remark: String?

  @get:Schema(title = "创建时间")
  abstract var createDatetime: datetime

  @get:Schema(title = "审核状态")
  @get:Convert(converter = AuditTypingConverter::class)
  abstract var state: AuditTyping
}
