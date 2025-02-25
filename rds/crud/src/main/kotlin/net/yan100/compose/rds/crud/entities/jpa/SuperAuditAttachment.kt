package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import net.yan100.compose.core.RefId
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.typing.AuditTyping
import net.yan100.compose.rds.crud.converters.AuditTypingConverter

@MetaDef
interface SuperAuditAttachment : IJpaEntity {
  /** 审核文件状态 */
  @get:Convert(converter = AuditTypingConverter::class) var status: AuditTyping

  /** 审核条目 id */
  var auditId: RefId

  /** 附件 id */
  var attId: RefId
}
