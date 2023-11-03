package net.yan100.compose.rds.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import net.yan100.compose.core.annotations.SensitiveRef
import net.yan100.compose.core.annotations.Strategy
import net.yan100.compose.core.models.IDisabilityCode2
import net.yan100.compose.core.models.IIdcard2Code
import net.yan100.compose.rds.base.BaseEntity
import net.yan100.compose.rds.converters.GenderTypingConverter
import net.yan100.compose.rds.typing.GenderTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "第二代身份证")
@Table(name = Idcard2.TABLE_NAME)
open class Idcard2 : IIdcard2Code, BaseEntity() {
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

  @SensitiveRef(Strategy.ADDRESS)
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

  @SensitiveRef(Strategy.ID_CARD)
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

  @SensitiveRef(Strategy.NAME)
  @Schema(title = "名称")
  @Column(name = NAME)
  open var name: String? = null

  @Schema(title = "外联 用户（所属用户）")
  @Column(name = USER_ID)
  open var userId: String? = null

  @get:Transient
  @get:JsonIgnore
  override val idcard2Code: String get() = this.code!!
}


@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = DisabilityCertificate2.TABLE_NAME)
open class DisabilityCertificate2 : IDisabilityCode2, BaseEntity() {
  companion object {
    const val TABLE_NAME = "disability_certificate_2"

    const val USER_ID = "user_id"
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

  @Schema(title = "残疾证编号")
  @Column(name = CODE)
  open var code: String? = null

  @SensitiveRef(Strategy.NAME)
  @Schema(title = "姓名")
  @Column(name = NAME)
  open var name: String? = null

  @Schema(title = "外联用户（所属用户）")
  @Column(name = USER_ID)
  open var userId: String? = null

  @get:Transient
  @get:JsonIgnore
  override val disabilityCode: String get() = this.code!!
}


@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = HouseholdRegistrationCard.TABLE_NAME)
open class HouseholdRegistrationCard : BaseEntity() {
  companion object {
    const val TABLE_NAME = "household_registration_card"

    const val USER_ID = "user_id"
    const val HOUSEHOLD_TYPE = "household_type"
    const val HOUSEHOLD_PRIMARY_NAME = "household_primary_name"
    const val CODE = "code"
    const val ADDRESS_DETAILS_ID = "address_details_id"
    const val ISSUE_ORGAN = "issue_organ"
    const val NAME = "name"
    const val OLD_NAME = "old_name"
    const val RELATIONSHIP = "relationship"
    const val GENDER = "gender"
    const val ETHNIC_GROUP = "ethnic_group"
    const val BIRTHDAY = "birthday"
    const val HEIGHT = "height"
    const val BLOOD_TYPE = "blood_type"
    const val PLACE_BIRTH_ADDRESS_DETAILS_ID = "place_birth_address_details_id"
    const val ORIGIN_ADDRESS_DETAILS_ID = "origin_address_details_id"
    const val IDCARD_CODE = "idcard_code"
    const val EDUCATION_LEVEL = "education_level"
    const val OCCUPATION = "occupation"
    const val MILITARY_SERVICE_STATUS = "military_service_status"
    const val SERVICE_ADDRESS_DETAILS_ID = "service_address_details_id"
    const val ISSUE_DATE = "issue_date"
  }


  @Schema(title = "户口签发时间")
  @Column(name = ISSUE_DATE)
  open var issueDate: LocalDate? = null


  @Schema(title = "证件签发服务地址")
  @Column(name = SERVICE_ADDRESS_DETAILS_ID)
  open var serviceAddressDetailsId: String? = null

  @Schema(title = "兵役状况")
  @Column(name = MILITARY_SERVICE_STATUS)
  open var militaryServiceStatus: String? = null

  @Schema(title = "职业")
  @Column(name = OCCUPATION)
  open var occupation: String? = null

  @Schema(title = "学历")
  @Column(name = EDUCATION_LEVEL)
  open var educationLevel: Int? = null

  @Schema(title = "户口所属身份证号")
  @Column(name = IDCARD_CODE)
  open var idcardCode: String? = null


  @Schema(title = "户口签发地址详情")
  @Column(name = ORIGIN_ADDRESS_DETAILS_ID)
  open var originAddressDetailsId: String? = null

  @Schema(title = "出生地址")
  @Column(name = PLACE_BIRTH_ADDRESS_DETAILS_ID)
  open var placeBirthAddressDetailsId: String? = null


  @Schema(title = "血型")
  @Column(name = BLOOD_TYPE)
  open var bloodType: Int? = null

  @Schema(title = "身高")
  @Column(name = HEIGHT)
  open var height: BigDecimal? = null

  @Schema(title = "生日")
  @Column(name = BIRTHDAY)
  open var birthday: LocalDate? = null


  @Schema(title = "民族")
  @Column(name = ETHNIC_GROUP)
  open var ethnicGroup: String? = null

  @Schema(title = "性别")
  @Column(name = GENDER)
  open var gender: GenderTyping? = null

  @Schema(title = "与户主的关系")
  @Column(name = RELATIONSHIP)
  open var relationship: String? = null

  @Schema(title = "曾用名")
  @Column(name = OLD_NAME)
  open var oldName: String? = null

  @SensitiveRef(Strategy.NAME)
  @Schema(title = "户口页所属人名称")
  @Column(name = NAME)
  open var name: String? = null

  @SensitiveRef(Strategy.ADDRESS)
  @Schema(title = "签发机关")
  @Column(name = ISSUE_ORGAN)
  open var issueOrgan: String? = null

  @Schema(title = "户口所在区域")
  @Column(name = ADDRESS_DETAILS_ID)
  open var addressDetailsId: String? = null

  @Schema(title = "户号")
  @Column(name = CODE)
  open var code: String? = null

  @SensitiveRef(Strategy.NAME)
  @Schema(title = "户主名称")
  @Column(name = HOUSEHOLD_PRIMARY_NAME)
  open var householdPrimaryName: String? = null

  @Schema(title = "户口类别")
  @Column(name = HOUSEHOLD_TYPE)
  open var householdType: Int? = null

  @Schema(title = "外联用户（所属用户）")
  @Column(name = USER_ID)
  open var userId: String? = null

}


@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = BankCard.TABLE_NAME)
open class BankCard : BaseEntity() {
  companion object {
    const val TABLE_NAME = "bank_card"
    const val USER_ID = "user_id"
    const val CODE = "code"
    const val COUNTRY = "country"
    const val BANK_GROUP = "bank_group"
    const val BANK_TYPE = "bank_type"
    const val ISSUE_ADDRESS_DETAILS = "issue_address_details"
  }

  @Schema(title = "开户行")
  @Column(name = ISSUE_ADDRESS_DETAILS)
  open var issueAddressDetails: String? = null


  @Schema(title = "银行类型", example = "中国银行、建设银行")
  @Column(name = BANK_TYPE)
  open var bankType: String? = null

  @Schema(title = "银行组织", example = "银联")
  @Column(name = BANK_GROUP)
  open var bankGroup: Int? = null

  @Schema(title = "所属国家")
  @Column(name = COUNTRY)
  open var country: Int? = null


  @SensitiveRef(Strategy.BANK_CARD_CODE)
  @Schema(title = "银行卡号")
  @Column(name = CODE)
  open var code: String? = null

  @Column(name = USER_ID)
  open var userId: String? = null
}
