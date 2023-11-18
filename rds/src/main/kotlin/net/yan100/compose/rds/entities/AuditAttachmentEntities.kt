package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Table
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.rds.core.entities.BaseEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@MappedSuperclass
open class SuperAuditAttachment : BaseEntity() {
  companion object {
    const val TABLE_NAME = "audit_attachment"

    const val ATT_ID = "att_id"
    const val AUDIT_ID = "audit_id"
    const val STATUS = "status"
  }

  @Schema(title = "审核文件状态")
  @Column(name = STATUS)
  open var status: Int? = null

  @Schema(title = "审核条目 id")
  @Column(name = AUDIT_ID)
  open var auditId: ReferenceId? = null

  @Schema(title = "附件 id")
  @Column(name = ATT_ID)
  open var attId: ReferenceId? = null
}

@Entity
@DynamicUpdate
@DynamicInsert
@Schema(title = "审核附带的附件")
@Table(name = SuperAuditAttachment.TABLE_NAME)
open class AuditAttachment : SuperAuditAttachment()
