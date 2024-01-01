package net.yan100.compose.rds.entities.cert

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import net.yan100.compose.core.alias.BigText
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.rds.converters.AuditTypingConverter
import net.yan100.compose.rds.converters.CertContentTypingConverter
import net.yan100.compose.rds.converters.CertPointTypingConverter
import net.yan100.compose.rds.converters.CertTypingConverter
import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.typing.AuditTyping
import net.yan100.compose.rds.typing.CertContentTyping
import net.yan100.compose.rds.typing.CertPointTyping
import net.yan100.compose.rds.typing.CertTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@MappedSuperclass
open class SuperCert : BaseEntity() {
  companion object {
    const val TABLE_NAME = "cert"

    const val ATT_ID = "att_id"
    const val USER_INFO_ID = "user_info_id"
    const val CREATE_USER_ID = "create_user_id"
    const val CREATE_DEVICE_ID = "create_device_id"
    const val CREATE_IP = "create_ip"
    const val CREATE_DATETIME = "create_datetime"
    const val REMARK = "remark"
    const val DOC = "doc"
    const val USER_ID = "user_id"
    const val NAME = "name"
    const val AUDIT_STATUS = "audit_status"
    const val CO_TYPE = "co_type"
    const val DO_TYPE = "do_type"
    const val PO_TYPE = "po_type"
    const val WM_ATT_ID = "wm_att_id"
    const val WM_CODE = "wm_code"
  }

  @Schema(title = "用户信息id")
  @Column(name = USER_INFO_ID)
  open var userInfoId: ReferenceId? = null

  @Schema(title = "水印码")
  @Column(name = WM_CODE)
  open var wmCode: SerialCode? = null

  @Schema(title = "水印证件 id")
  @Column(name = WM_ATT_ID)
  open var wmAttId: ReferenceId? = null

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

  @Schema(title = "用户 id")
  @Column(name = USER_ID)
  open var userId: ReferenceId? = null

  @Schema(title = "证件打印类型")
  @Column(name = PO_TYPE)
  @Convert(converter = CertPointTypingConverter::class)
  open var poType: CertPointTyping? = null

  @Schema(title = "证件内容类型")
  @Column(name = CO_TYPE)
  @Convert(converter = CertContentTypingConverter::class)
  open var coType: CertContentTyping? = null

  @Schema(title = "证件类型")
  @Column(name = DO_TYPE)
  @Convert(converter = CertTypingConverter::class)
  open var doType: CertTyping? = null
}


@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = SuperCert.TABLE_NAME)
open class Cert : SuperCert()
