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
import jakarta.persistence.Entity
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Table
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.alias.datetime
import net.yan100.compose.rds.Col
import net.yan100.compose.rds.converters.AuditTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.AuditTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@MappedSuperclass
abstract class SuperAudit : IEntity() {
  companion object {
    const val TABLE_NAME = "audit"

    const val STATUS = "status"
    const val REMARK = "remark"
    const val CREATE_DATETIME = "create_datetime"
    const val REF_ID = "ref_id"
    const val REF_TYPE = "ref_type"
    const val AUDIT_IP = "audit_ip"
    const val AUDIT_DEVICE_ID = "audit_device_id"
  }

  @Schema(title = "审核人设备 id") @Col(name = AUDIT_DEVICE_ID) var auditDeviceId: SerialCode? = null

  @Schema(title = "审核人 ip") @Col(name = AUDIT_IP) var auditIp: String? = null

  @Schema(title = "审核类型") @Col(name = REF_TYPE) var refType: Int? = null

  @Schema(title = "审核外键") @Col(name = REF_ID) lateinit var refId: RefId

  @Schema(title = "审核备注") @Col(name = REMARK) var remark: String? = null

  @Schema(title = "创建时间") @Col(name = CREATE_DATETIME) lateinit var createDatetime: datetime

  @Schema(title = "审核状态")
  @Col(name = STATUS)
  @Convert(converter = AuditTypingConverter::class)
  lateinit var state: AuditTyping
}

@Entity
@DynamicUpdate
@DynamicInsert
@Schema(title = "审核条目")
@Table(name = SuperAudit.TABLE_NAME)
class Audit : SuperAudit()
