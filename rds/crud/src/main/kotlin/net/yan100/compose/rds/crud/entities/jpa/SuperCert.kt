package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import net.yan100.compose.RefId
import net.yan100.compose.datetime
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.crud.converters.AuditTypingConverter
import net.yan100.compose.rds.crud.converters.CertContentTypingConverter
import net.yan100.compose.rds.crud.converters.CertPointTypingConverter
import net.yan100.compose.rds.crud.converters.CertTypingConverter
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.typing.AuditTyping
import net.yan100.compose.rds.typing.CertContentTyping
import net.yan100.compose.rds.typing.CertPointTyping
import net.yan100.compose.rds.typing.CertTyping
import net.yan100.compose.string

@MetaDef
interface SuperCert : IJpaEntity {
  var userInfoId: RefId?

  var wmCode: string?

  var wmAttId: RefId?

  var attId: RefId

  var createUserId: RefId?

  var createDeviceId: string?

  var createIp: String?

  /** 创建时间 */
  var createDatetime: datetime?

  /** 证件备注 */
  var remark: string?

  /** 审核状态 */
  @get:Convert(converter = AuditTypingConverter::class)
  var auditStatus: AuditTyping

  /** 证件描述 */
  var doc: string?

  /** 证件名称 */
  var name: String?

  /** 用户 id */
  var userId: RefId?

  /** 证件打印类型 */
  @get:Convert(converter = CertPointTypingConverter::class)
  var poType: CertPointTyping?

  /** 证件内容类型 */
  @get:Convert(converter = CertContentTypingConverter::class)
  var coType: CertContentTyping?

  /** 证件类型 */
  @get:Convert(converter = CertTypingConverter::class) var doType: CertTyping?
}
