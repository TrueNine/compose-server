/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import net.yan100.compose.core.RefId
import net.yan100.compose.core.datetime
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.typing.AuditTyping
import net.yan100.compose.rds.core.typing.cert.CertContentTyping
import net.yan100.compose.rds.core.typing.cert.CertPointTyping
import net.yan100.compose.rds.core.typing.cert.CertTyping
import net.yan100.compose.rds.crud.converters.AuditTypingConverter
import net.yan100.compose.rds.crud.converters.CertContentTypingConverter
import net.yan100.compose.rds.crud.converters.CertPointTypingConverter
import net.yan100.compose.rds.crud.converters.CertTypingConverter

@MetaDef
interface SuperCert : IJpaEntity {
  var userInfoId: RefId?

  var wmCode: string?

  var wmAttId: RefId?

  var attId: RefId

  var createUserId: RefId?

  var createDeviceId: string?

  var createIp: String?

  /**
   * 创建时间
   */
  var createDatetime: datetime?

  /**
   * 证件备注
   */
  var remark: string?

  /**
   * 审核状态
   */
  @get:Convert(converter = AuditTypingConverter::class)
  var auditStatus: AuditTyping

  /**
   * 证件描述
   */
  var doc: string?

  /**
   * 证件名称
   */
  var name: String?

  /**
   * 用户 id
   */
  var userId: RefId?

  /**
   * 证件打印类型
   */
  @get:Convert(converter = CertPointTypingConverter::class)
  var poType: CertPointTyping?

  /**
   * 证件内容类型
   */
  @get:Convert(converter = CertContentTypingConverter::class)
  var coType: CertContentTyping?

  /**
   * 证件类型
   */
  @get:Convert(converter = CertTypingConverter::class)
  var doType: CertTyping?
}

