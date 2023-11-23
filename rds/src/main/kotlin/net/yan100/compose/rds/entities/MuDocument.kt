package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import net.yan100.compose.core.alias.BigText
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.rds.converters.AuditTypingConverter
import net.yan100.compose.rds.converters.DocumentContentTypingConverter
import net.yan100.compose.rds.converters.DocumentPointTypingConverter
import net.yan100.compose.rds.converters.DocumentTypingConverter
import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.typing.AuditTyping
import net.yan100.compose.rds.typing.DocumentContentTyping
import net.yan100.compose.rds.typing.DocumentPointTyping
import net.yan100.compose.rds.typing.DocumentTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@MappedSuperclass
open class SuperMuDocument : BaseEntity() {
  companion object {
    const val TABLE_NAME = "mu_document"

    const val ATT_ID = "att_id"
    const val CREATE_USER_ID = "create_user_id"
    const val CREATE_DEVICE_ID = "create_device_id"
    const val CREATE_IP = "create_ip"
    const val CREATE_DATETIME = "create_datetime"
    const val REMARK = "remark"
    const val DOC = "doc"
    const val USER_ID = "user_id"
    const val MARK_USER_ID = "mark_user_id"
    const val NAME = "name"
    const val AUDIT_STATUS = "audit_status"
    const val C_TYPE = "c_type"
    const val D_TYPE = "d_type"
    const val P_TYPE = "p_type"
  }

  @Schema(title = "外联附件 id")
  @Column(name = ATT_ID)
  open var attId: ReferenceId? = null

  @Schema(title = "创建人 id")
  @Column(name = CREATE_USER_ID)
  open var createUserId: ReferenceId? = null

  @Schema(title = "创建人设备 id")
  @Column(name = CREATE_DEVICE_ID)
  open var createDeviceId: SerialCode? = null

  @Schema(title = "创建 ip")
  @Column(name = CREATE_IP)
  open var createIp: String? = null

  @Schema(title = "创建时间")
  @Column(name = CREATE_DATETIME)
  open var createDatetime: LocalDateTime? = null

  @Schema(title = "证件备注")
  @Column(name = REMARK)
  open var remark: SerialCode? = null

  @Schema(title = "审核状态")
  @Column(name = AUDIT_STATUS)
  @Convert(converter = AuditTypingConverter::class)
  open var auditStatus: AuditTyping? = null

  @Schema(title = "证件描述")
  @Column(name = DOC)
  open var doc: BigText? = null

  @Schema(title = "证件名称")
  @Column(name = NAME)
  open var name: String? = null

  @Schema(title = "标记用户 id")
  @Column(name = MARK_USER_ID)
  open var markUserId: ReferenceId? = null

  @Schema(title = "用户 id")
  @Column(name = USER_ID)
  open var userId: ReferenceId? = null

  @Schema(title = "证件打印类型")
  @Column(name = P_TYPE)
  @Convert(converter = DocumentPointTypingConverter::class)
  open var pType: DocumentPointTyping? = null

  @Schema(title = "证件内容类型")
  @Column(name = C_TYPE)
  @Convert(converter = DocumentContentTypingConverter::class)
  open var cType: DocumentContentTyping? = null

  @Schema(title = "证件类型")
  @Column(name = D_TYPE)
  @Convert(converter = DocumentTypingConverter::class)
  open var dType: DocumentTyping? = null
}


@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = SuperMuDocument.TABLE_NAME)
open class MuDocument : SuperMuDocument()
