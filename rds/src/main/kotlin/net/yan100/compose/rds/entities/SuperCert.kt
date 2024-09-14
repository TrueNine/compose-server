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
package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.RefId
import net.yan100.compose.core.datetime
import net.yan100.compose.core.string
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.converters.AuditTypingConverter
import net.yan100.compose.rds.converters.CertContentTypingConverter
import net.yan100.compose.rds.converters.CertPointTypingConverter
import net.yan100.compose.rds.converters.CertTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.AuditTyping
import net.yan100.compose.rds.core.typing.cert.CertContentTyping
import net.yan100.compose.rds.core.typing.cert.CertPointTyping
import net.yan100.compose.rds.core.typing.cert.CertTyping

@MetaDef
@MappedSuperclass
abstract class SuperCert : IEntity() {
  @get:Schema(title = "用户信息id")
  abstract var userInfoId: RefId?

  @get:Schema(title = "水印码")
  abstract var wmCode: string?

  @get:Schema(title = "水印证件 id")
  abstract var wmAttId: RefId?

  @get:Schema(title = "原始附件 id")
  abstract var attId: RefId

  @get:Schema(title = "创建人 id")
  abstract var createUserId: RefId?

  @get:Schema(title = "创建人设备 id")
  abstract var createDeviceId: string?

  @get:Schema(title = "创建 ip")
  abstract var createIp: String?

  @get:Schema(title = "创建时间")
  abstract var createDatetime: datetime?

  @get:Schema(title = "证件备注")
  abstract var remark: string?

  @get:Schema(title = "审核状态")
  @get:Convert(converter = AuditTypingConverter::class)
  abstract var auditStatus: AuditTyping

  @get:Schema(title = "证件描述")
  abstract var doc: string?

  @get:Schema(title = "证件名称")
  abstract var name: String?

  @get:Schema(title = "用户 id")
  abstract var userId: RefId?

  @get:Schema(title = "证件打印类型")
  @get:Convert(converter = CertPointTypingConverter::class)
  abstract var poType: CertPointTyping?

  @get:Schema(title = "证件内容类型")
  @get:Convert(converter = CertContentTypingConverter::class)
  abstract var coType: CertContentTyping?

  @get:Schema(title = "证件类型")
  @get:Convert(converter = CertTypingConverter::class)
  abstract var doType: CertTyping?
}

