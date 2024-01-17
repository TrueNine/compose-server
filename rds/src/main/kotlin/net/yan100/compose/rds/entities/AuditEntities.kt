package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Table
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.rds.Col
import net.yan100.compose.rds.converters.AuditTypingConverter
import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.typing.AuditTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime


@MappedSuperclass
open class SuperAudit : BaseEntity() {
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

  @Schema(title = "审核人设备 id")
  @Col(name = AUDIT_DEVICE_ID)
  open var auditDeviceId: SerialCode? = null

  @Schema(title = "审核人 ip")
  @Col(name = AUDIT_IP)
  open var auditIp: String? = null

  @Schema(title = "审核类型")
  @Col(name = REF_TYPE)
  open var refType: Int? = null

  @Schema(title = "审核外键")
  @Col(name = REF_ID)
  open var refId: ReferenceId? = null

  @Schema(title = "审核备注")
  @Col(name = REMARK)
  open var remark: String? = null

  @Schema(title = "创建时间")
  @Col(name = CREATE_DATETIME)
  open var createDatetime: LocalDateTime? = null

  @Schema(title = "审核状态")
  @Col(name = STATUS)
  @Convert(converter = AuditTypingConverter::class)
  open var state: AuditTyping? = null
}


@Entity
@DynamicUpdate
@DynamicInsert
@Schema(title = "审核条目")
@Table(name = SuperAudit.TABLE_NAME)
open class Audit : SuperAudit()
