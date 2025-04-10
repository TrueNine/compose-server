package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import net.yan100.compose.RefId
import net.yan100.compose.datetime
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.crud.converters.GenderTypingConverter
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.typing.GenderTyping
import net.yan100.compose.string

@MetaDef
interface SuperIdcard2 : IJpaEntity {
  /** 用户信息 */
  var userInfoId: RefId?

  /** 签发机构 */
  var issueOrgan: String?

  /** 身份证过期时间 */
  var expireDate: datetime?

  /** 民族 */
  var ethnicGroup: String?

  /** 生日 */
  var birthday: datetime?

  /** 身份证号 */
  var code: string

  /** 性别 */
  @get:Convert(converter = GenderTypingConverter::class)
  var gender: GenderTyping?

  /** 外联 地址详情id（出生地） */
  var addressDetailsId: RefId?

  /** 名称 */
  var name: String

  /** 外联 用户（所属用户） */
  var userId: RefId?
}
