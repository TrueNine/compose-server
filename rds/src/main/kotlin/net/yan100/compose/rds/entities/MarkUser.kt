package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import net.yan100.compose.core.alias.BigText
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.alias.TypeInt
import net.yan100.compose.core.consts.Regexes
import net.yan100.compose.rds.converters.AuditTypingConverter
import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.typing.AuditTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@MappedSuperclass
open class SuperMarkUser : BaseEntity() {
  companion object {
    const val TABLE_NAME = "mark_user"

    const val USER_MARK_TYPE = "user_mark_type"
    const val AUDIT_STATUS = "audit_status"
    const val USER_ID = "user_id"
    const val ACCOUNT = "account"
    const val ACTUAL_NAME = "actual_name"
    const val PHONE = "phone"
    const val SPARE_PHONE = "spare_phone"
    const val IDCARD = "idcard"
    const val EMAIL = "email"
    const val REMARK = "remark"
    const val WECHAT_OPENID = "wechat_openid"
    const val WECHAT_ACCOUNT = "wechat_account"
    const val QQ_OPENID = "qq_openid"
    const val QQ_ACCOUNT = "qq_account"
    const val ADDRESS_CODE = "address_code"
    const val ADDRESS_DETAILS_ID = "address_details_id"
  }

  @Schema(title = "地址详情id")
  @Column(name = ADDRESS_DETAILS_ID)
  open var addressDetailsId: ReferenceId? = null

  @Schema(title = "所在地地址代码")
  @Column(name = ADDRESS_CODE)
  open var addressCode: SerialCode? = null

  @Schema(title = "qq 账号")
  @Column(name = QQ_ACCOUNT)
  open var qqAccount: SerialCode? = null

  @Schema(title = "qq openid")
  @Column(name = QQ_OPENID)
  open var qqOpenid: SerialCode? = null

  @Schema(title = "微信账户")
  @Column(name = WECHAT_ACCOUNT)
  open var wechatAccount: SerialCode? = null

  @Schema(title = "微信 openid")
  @Column(name = WECHAT_OPENID)
  open var wechatOpenid: SerialCode? = null

  @Schema(title = "用户备注")
  @Column(name = REMARK)
  open var remark: BigText? = null

  @Email
  @Schema(title = "邮箱")
  @Column(name = EMAIL)
  open var email: String? = null

  @Schema(title = "身份证号")
  @Column(name = IDCARD)
  open var idcard: SerialCode? = null

  @Pattern(regexp = Regexes.CHINA_PHONE)
  @Schema(title = "备用手机")
  @Column(name = SPARE_PHONE)
  open var sparePhone: SerialCode? = null

  @Pattern(regexp = Regexes.CHINA_PHONE)
  @Schema(title = "电话号码")
  @Column(name = PHONE)
  open var phone: SerialCode? = null

  @Schema(title = "真实姓名")
  @Column(name = ACTUAL_NAME)
  open var actualName: String? = null

  @Schema(title = "账户")
  @Column(name = ACCOUNT)
  open var account: SerialCode? = null

  @Schema(title = "外联用户 id")
  @Column(name = USER_ID)
  open var userId: ReferenceId? = null

  @Schema(title = "审核状态")
  @Column(name = AUDIT_STATUS)
  @Convert(converter = AuditTypingConverter::class)
  open var auditStatus: AuditTyping? = null

  @Schema(title = "标记用户的类型", examples = ["企业", "个人", "员工", "营销对象"])
  @Column(name = USER_MARK_TYPE)
  open var userMarkType: TypeInt? = null
}


@Entity
@DynamicUpdate
@DynamicInsert
@Schema(title = "标记用户")
@Table(name = SuperMarkUser.TABLE_NAME)
open class MarkUser : SuperMarkUser()
