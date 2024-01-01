package net.yan100.compose.rds.entities.cert

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.models.IDisCode2
import net.yan100.compose.rds.converters.GenderTypingConverter
import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.typing.GenderTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDate

@MappedSuperclass
open class SuperDisCert2 : IDisCode2, BaseEntity() {
  companion object {
    const val TABLE_NAME = "dis_cert_2"

    const val USER_ID = "user_id"
    const val USER_INFO_ID = "user_info_id"
    const val NAME = "name"
    const val CODE = "code"
    const val GENDER = "gender"
    const val TYPE = "type"
    const val LEVEL = "level"
    const val ISSUE_DATE = "issue_date"
    const val EXPIRE_DATE = "expire_date"
    const val ADDRESS_DETAILS_ID = "address_details_id"
    const val GUARDIAN = "guardian"
    const val GUARDIAN_PHONE = "guardian_phone"
    const val BIRTHDAY = "birthday"
  }

  @Schema(title = "用户信息id")
  @Column(name = USER_INFO_ID)
  open var userInfoId: RefId? = null

  @Schema(title = "出生日期")
  @Column(name = BIRTHDAY)
  open var birthday: LocalDate? = null

  @Schema(title = "监护人联系电话")
  @Column(name = GUARDIAN_PHONE)
  open var guardianPhone: String? = null


  @Schema(title = "监护人姓名")
  @Column(name = GUARDIAN)
  open var guardian: String? = null

  @Schema(title = "家庭住址")
  @Column(name = ADDRESS_DETAILS_ID)
  open var addressDetailsId: String? = null

  @Schema(title = "证件过期时间")
  @Column(name = EXPIRE_DATE)
  open var expireDate: LocalDate? = null

  @Schema(title = "签发时间")
  @Column(name = ISSUE_DATE)
  open var issueDate: LocalDate? = null

  @Schema(title = "残疾级别")
  @Column(name = LEVEL)
  open var level: Int? = null

  @Schema(title = "残疾类别")
  @Column(name = TYPE)
  open var type: Int? = null

  @Convert(converter = GenderTypingConverter::class)
  @Schema(title = "性别")
  @Column(name = GENDER)
  open var gender: GenderTyping? = null

  @NotNull
  @Schema(title = "残疾证编号")
  @Column(name = CODE)
  open var code: SerialCode? = null


  @Schema(title = "姓名")
  @Column(name = NAME)
  open var name: String? = null

  @Schema(title = "外联用户（所属用户）")
  @Column(name = USER_ID)
  open var userId: ReferenceId? = null

  @get:Transient
  @get:JsonIgnore
  override val disabilityCode: String get() = this.code!!
}

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = SuperDisCert2.TABLE_NAME)
open class DisCert2 : SuperDisCert2()
