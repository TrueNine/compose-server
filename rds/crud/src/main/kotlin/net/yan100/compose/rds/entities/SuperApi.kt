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
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import net.yan100.compose.core.RefId
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

/**
 * api
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
@MappedSuperclass
abstract class SuperApi : IEntity() {
  /** ## 权限 id */
  @get:Schema(title = "权限 id")
  abstract var permissionsId: RefId?

  /** 名称 */
  @get:Schema(title = "名称")
  abstract var name: String?

  /** 描述 */
  @get:Schema(title = "描述")
  abstract var doc: String?

  /** 路径 */
  @get:Schema(title = "路径")
  abstract var apiPath: String?

  /** 请求方式 */
  @get:Schema(title = "请求方式")
  abstract var apiMethod: String?

  /** 请求协议 */
  @get:Schema(title = "请求协议")
  abstract var apiProtocol: String?

  @get:JsonIgnore
  @get:Transient
  val uriDeep: Int get() = apiPath?.split("/")?.filter { it.isNotBlank() }?.size ?: 0
}
