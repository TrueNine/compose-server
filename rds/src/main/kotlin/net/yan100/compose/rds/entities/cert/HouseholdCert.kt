package net.yan100.compose.rds.entities.cert

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.rds.Col
import net.yan100.compose.rds.converters.BloodTypingConverter
import net.yan100.compose.rds.converters.DegreeTypingConverter
import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.typing.BloodTyping
import net.yan100.compose.rds.typing.DegreeTyping
import net.yan100.compose.rds.typing.GenderTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.math.BigDecimal
import java.time.LocalDate

@MappedSuperclass
open class SuperHouseholdCert : BaseEntity() {
  companion object {
    const val TABLE_NAME = "household_cert"

    const val USER_ID = "user_id"
    const val USER_INFO_ID = "user_info_id"
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

  @Schema(title = "用户信息id")
  @Col(name = USER_INFO_ID)
  open var userInfoId: RefId? = null

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
  @Convert(converter = DegreeTypingConverter::class)
  @Column(name = EDUCATION_LEVEL)
  open var educationLevel: DegreeTyping? = null

  @Schema(title = "户口所属身份证号")
  @Column(name = IDCARD_CODE)
  open var idcardCode: SerialCode? = null


  @Schema(title = "户口签发地址详情")
  @Column(name = ORIGIN_ADDRESS_DETAILS_ID)
  open var originAddressDetailsId: ReferenceId? = null

  @Schema(title = "出生地址")
  @Column(name = PLACE_BIRTH_ADDRESS_DETAILS_ID)
  open var placeBirthAddressDetailsId: ReferenceId? = null


  @Schema(title = "血型")
  @Column(name = BLOOD_TYPE)
  @Convert(converter = BloodTypingConverter::class)
  open var bloodType: BloodTyping? = null

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

  @Schema(title = "户口页所属人名称")
  @Column(name = NAME)
  open var name: String? = null

  @Schema(title = "签发机关")
  @Column(name = ISSUE_ORGAN)
  open var issueOrgan: String? = null

  @Schema(title = "户口所在区域")
  @Column(name = ADDRESS_DETAILS_ID)
  open var addressDetailsId: String? = null

  @Schema(title = "户号")
  @Column(name = CODE)
  open var code: SerialCode? = null

  @Schema(title = "户主名称")
  @Column(name = HOUSEHOLD_PRIMARY_NAME)
  open var householdPrimaryName: String? = null

  @Schema(title = "户口类别")
  @Column(name = HOUSEHOLD_TYPE)
  open var householdType: Int? = null

  @Schema(title = "外联用户（所属用户）")
  @Column(name = USER_ID)
  open var userId: ReferenceId? = null
}

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = SuperHouseholdCert.TABLE_NAME)
open class HouseholdCert : SuperHouseholdCert()

