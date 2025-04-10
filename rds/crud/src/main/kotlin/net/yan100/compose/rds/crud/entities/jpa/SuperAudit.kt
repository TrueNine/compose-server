package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import net.yan100.compose.RefId
import net.yan100.compose.datetime
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.crud.converters.AuditTypingConverter
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.typing.AuditTyping
import net.yan100.compose.string

/** ## 审核条目 */
@MetaDef
interface SuperAudit : IJpaEntity {
  /** 审核人设备 id */
  var auditDeviceId: string?

  /** 审核人 ip */
  var auditIp: String?

  /** 审核类型 */
  var refType: Int?

  /** 审核外键 */
  var refId: RefId

  /** 审核备注 */
  var remark: String?

  /** 创建时间 */
  var createDatetime: datetime

  /** 审核状态 */
  @get:Convert(converter = AuditTypingConverter::class) var state: AuditTyping
}
