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

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.validation.constraints.Pattern
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.datetime
import net.yan100.compose.core.consts.Regexes
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.ksp.core.annotations.MetaName
import net.yan100.compose.rds.core.entities.IEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

/**
 * API请求记录
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MappedSuperclass
@MetaDef
abstract class SuperApiCallRecord : IEntity() {
  /** api id */
  @get:Schema(title = "调用的 API ID")
  abstract var apiId: RefId

  /** 设备 id, 浏览器为 agent */
  @get:Schema(title = "设备 id", description = "浏览器为 ua ，其他自定义唯一标识即可")
  abstract var deviceCode: String?

  /** 请求 ip */
  @get:Schema(title = "请求 ip")
  @get:Pattern(regexp = Regexes.IP_V4)
  abstract var reqIp: String?

  /** 登录 ip */
  @get:Schema(title = "登录 ip")
  @get:Pattern(regexp = Regexes.IP_V4)
  abstract var loginIp: String?

  /** 响应码 */
  @get:Schema(title = "响应码")
  abstract var respCode: Int?

  /** 请求结果 */
  @get:Schema(title = "请求结果")
  abstract var respResultEnc: String?

  @get:Schema(title = "请求路径")
  abstract var reqPath: String?

  @get:Schema(title = "请求方法")
  abstract var reqMethod: String?

  @get:Schema(title = "请求协议")
  abstract var reqProtocol: String?

  @get:Schema(title = "请求时间")
  abstract var reqDatetime: datetime?

  @get:Schema(title = "响应时间")
  abstract var respDatetime: datetime?

  @get:JsonIgnore
  @get:Transient
  val uriDeep: Int get() = reqPath?.split("/")?.filter { it.isNotBlank() }?.size ?: 0
}
