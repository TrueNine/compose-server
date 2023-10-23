package net.yan100.compose.rds.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.rds.base.BaseEntity
import net.yan100.compose.rds.converters.typing.GenderTypingConverter
import net.yan100.compose.rds.typing.GenderTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "第二代身份证")
@Table(name = Idcard2.TABLE_NAME)
open class Idcard2 : BaseEntity() {
  companion object {
    const val TABLE_NAME = "idcard_2"
    const val NAME = "name"
    const val USER_ID = "user_id"
    const val ADDRESS_DETAILS_ID = "address_details_id"
    const val GENDER = "gender"
    const val CODE = "code"
    const val BIRTHDAY = "birthday"
    const val EXPIRE_DATE = "expire_date"
    const val ISSUE_ORGAN = "issue_organ"
    const val ETHNIC_GROUP = "ethnic_group"
  }

  @Schema(title = "签发机构")
  @Column(name = ISSUE_ORGAN)
  open var issueOrgan: String? = null

  @Schema(title = "身份证过期时间")
  @Column(name = EXPIRE_DATE)
  open var expireDate: LocalDate? = null

  @Schema(title = "民族")
  @Column(name = ETHNIC_GROUP)
  open var ethnicGroup: String? = null

  @Schema(title = "生日")
  @Column(name = BIRTHDAY)
  open var birthday: LocalDateTime? = null

  @Schema(title = "身份证号")
  @Column(name = CODE)
  open var code: String? = null

  @Schema(title = "性别")
  @Column(name = GENDER)
  @Convert(converter = GenderTypingConverter::class)
  open var gender: GenderTyping? = null

  @Schema(title = "外联 地址详情id（出生地）")
  @Column(name = ADDRESS_DETAILS_ID)
  open var addressDetailsId: String? = null

  @Schema(title = "名称")
  @Column(name = NAME)
  open var name: String? = null

  @Schema(title = "外联 用户（所属用户）")
  @Column(name = USER_ID)
  open var userId: String? = null

  // TODO 等待测试
  fun getMetaBirthday(): LocalDate? {
    this.code?.let {
      val global = it.substring(6, 6 + 8)
      val year = global.substring(0, 4)
      val day = global.substring(4, 8)
    }
    return null
  }
}
