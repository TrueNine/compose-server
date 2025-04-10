package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import java.math.BigDecimal
import java.time.LocalDate
import net.yan100.compose.RefId
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.crud.converters.BloodTypingConverter
import net.yan100.compose.rds.crud.converters.DegreeTypingConverter
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.typing.BloodTyping
import net.yan100.compose.rds.typing.DegreeTyping
import net.yan100.compose.rds.typing.GenderTyping
import net.yan100.compose.string

@MetaDef
interface SuperHouseholdCert : IJpaEntity {
  /** 用户信息id */
  var userInfoId: RefId?

  /** 户口签发时间 */
  var issueDate: LocalDate?

  /** 证件签发服务地址 */
  var serviceAddressDetailsId: String?

  /** 兵役状况 */
  var militaryServiceStatus: String?

  /** 职业 */
  var occupation: String?

  /** 学历 */
  @get:Convert(converter = DegreeTypingConverter::class)
  var educationLevel: DegreeTyping?

  /** 户口所属身份证号 */
  var idcardCode: string

  /** 户口签发地址详情 */
  var originAddressDetailsId: RefId?

  /** 出生地址 */
  var placeBirthAddressDetailsId: RefId?

  /** 血型 */
  @get:Convert(converter = BloodTypingConverter::class)
  var bloodType: BloodTyping?

  /** 身高 */
  var height: BigDecimal?

  /** 生日 */
  var birthday: LocalDate?

  /** 民族 */
  var ethnicGroup: String?

  /** 性别 */
  var gender: GenderTyping

  /** 与户主的关系 */
  var relationship: String?

  /** 曾用名 */
  var oldName: String?

  /** 户口页所属人名称 */
  var name: String?

  /** 签发机关 */
  var issueOrgan: String?

  /** 户口所在区域 */
  var addressDetailsId: RefId?

  /** 户号 */
  var code: string?

  /** 户主名称 */
  var householdPrimaryName: String?

  /** 户口类别 */
  var householdType: Int?

  /** 外联用户（所属用户） */
  var userId: RefId?
}
