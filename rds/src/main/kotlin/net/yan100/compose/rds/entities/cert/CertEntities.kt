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
package net.yan100.compose.rds.entities.cert

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import net.yan100.compose.core.alias.*
import net.yan100.compose.rds.converters.AuditTypingConverter
import net.yan100.compose.rds.converters.CertContentTypingConverter
import net.yan100.compose.rds.converters.CertPointTypingConverter
import net.yan100.compose.rds.converters.CertTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.AuditTyping
import net.yan100.compose.rds.core.typing.cert.CertContentTyping
import net.yan100.compose.rds.core.typing.cert.CertPointTyping
import net.yan100.compose.rds.core.typing.cert.CertTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@MappedSuperclass
abstract class SuperCert : IEntity() {
  companion object {
    const val TABLE_NAME = "cert"

    const val ATT_ID = "att_id"
    const val USER_INFO_ID = "user_info_id"
    const val CREATE_USER_ID = "create_user_id"
    const val CREATE_DEVICE_ID = "create_device_id"
    const val CREATE_IP = "create_ip"
    const val CREATE_DATETIME = "create_datetime"
    const val REMARK = "remark"
    const val DOC = "doc"
    const val USER_ID = "user_id"
    const val NAME = "name"
    const val AUDIT_STATUS = "audit_status"
    const val CO_TYPE = "co_type"
    const val DO_TYPE = "do_type"
    const val PO_TYPE = "po_type"
    const val WM_ATT_ID = "wm_att_id"
    const val WM_CODE = "wm_code"
  }

  @Schema(title = "用户信息id") @Column(name = USER_INFO_ID) var userInfoId: ReferenceId? = null

  @Schema(title = "水印码") @Column(name = WM_CODE) var wmCode: SerialCode? = null

  @Schema(title = "水印证件 id") @Column(name = WM_ATT_ID) var wmAttId: RefId? = null

  @Schema(title = "原始附件 id") @Column(name = ATT_ID) lateinit var attId: RefId

  @Schema(title = "创建人 id") @Column(name = CREATE_USER_ID) var createUserId: RefId? = null

  @Schema(title = "创建人设备 id")
  @Column(name = CREATE_DEVICE_ID)
  var createDeviceId: SerialCode? = null

  @Schema(title = "创建 ip") @Column(name = CREATE_IP) var createIp: String? = null

  @Schema(title = "创建时间") @Column(name = CREATE_DATETIME) var createDatetime: datetime? = null

  @Schema(title = "证件备注") @Column(name = REMARK) var remark: SerialCode? = null

  @Schema(title = "审核状态")
  @Column(name = AUDIT_STATUS)
  @Convert(converter = AuditTypingConverter::class)
  lateinit var auditStatus: AuditTyping

  @Schema(title = "证件描述") @Column(name = DOC) var doc: BigText? = null

  @Schema(title = "证件名称") @Column(name = NAME) var name: String? = null

  @Schema(title = "用户 id") @Column(name = USER_ID) var userId: ReferenceId? = null

  @Schema(title = "证件打印类型")
  @Column(name = PO_TYPE)
  @Convert(converter = CertPointTypingConverter::class)
  var poType: CertPointTyping? = null

  @Schema(title = "证件内容类型")
  @Column(name = CO_TYPE)
  @Convert(converter = CertContentTypingConverter::class)
  var coType: CertContentTyping? = null

  @Schema(title = "证件类型")
  @Column(name = DO_TYPE)
  @Convert(converter = CertTypingConverter::class)
  var doType: CertTyping? = null
}

@Entity @DynamicUpdate @DynamicInsert @Table(name = SuperCert.TABLE_NAME) class Cert : SuperCert()
