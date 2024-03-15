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
import jakarta.persistence.*
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.rds.converters.AuditTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.AuditTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@MappedSuperclass
abstract class SuperAuditAttachment : IEntity() {
  companion object {
    const val TABLE_NAME = "audit_attachment"

    const val ATT_ID = "att_id"
    const val AUDIT_ID = "audit_id"
    const val STATUS = "status"
  }

  @Schema(title = "审核文件状态")
  @Column(name = STATUS)
  @Convert(converter = AuditTypingConverter::class)
  lateinit var status: AuditTyping

  @Schema(title = "审核条目 id") @Column(name = AUDIT_ID) lateinit var auditId: RefId

  @Schema(title = "附件 id") @Column(name = ATT_ID) lateinit var attId: RefId
}

@Entity
@DynamicUpdate
@DynamicInsert
@Schema(title = "审核附带的附件")
@Table(name = SuperAuditAttachment.TABLE_NAME)
class AuditAttachment : SuperAuditAttachment()
