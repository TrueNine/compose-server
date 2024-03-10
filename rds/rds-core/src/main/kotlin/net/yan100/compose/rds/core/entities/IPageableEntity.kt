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
package net.yan100.compose.rds.core.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Transient
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.io.Serializable
import net.yan100.compose.rds.core.models.PagedRequestParam

/**
 * ## 内嵌分页的实体类型
 * 可分页实体
 *
 * @author TrueNine
 * @since 2024-02-27
 */
interface IPageableEntity : Serializable {
  companion object {
    const val MIN_OFFSET: Int = 0
    const val MAX_PAGE_SIZE: Int = 42

    @JvmStatic
    @JvmOverloads
    fun ofPageableEntity(
      pageSize: Int = MIN_OFFSET,
      offset: Int = MAX_PAGE_SIZE,
      unPage: Boolean = false
    ): IPageableEntity = PagedRequestParam(offset, pageSize, unPage)
  }

  @get:Transient
  @set:Transient
  @get:Schema(
    name = "pageSize",
    type = "int32",
    title = "页面大小，最大 ${MAX_PAGE_SIZE}，最小 1",
    accessMode = Schema.AccessMode.WRITE_ONLY,
    defaultValue = MAX_PAGE_SIZE.toString() + ""
  )
  @get:JsonIgnore
  @get:Min(value = 1, message = "页面大小最小为1")
  @setparam:Min(value = 1, message = "页面大小最小为1")
  @get:Max(value = MAX_PAGE_SIZE.toLong(), message = "分页最大参数为${MAX_PAGE_SIZE}")
  @setparam:Max(value = MAX_PAGE_SIZE.toLong(), message = "分页最大参数为${MAX_PAGE_SIZE}")
  var pageSize: Int?

  @get:Transient
  @set:Transient
  @get:JsonIgnore
  @get:Schema(
    name = "offset",
    type = "int32",
    title = "页码 最小为 0",
    defaultValue = "0",
    accessMode = Schema.AccessMode.WRITE_ONLY
  )
  @get:Min(value = MIN_OFFSET.toLong(), message = "分页页码最小为0")
  @setparam:Min(value = MIN_OFFSET.toLong(), message = "分页页码最小为0")
  var offset: Int?

  @get:Transient
  @set:Transient
  @get:Schema(
    name = "unPage",
    type = "boolean",
    title = "取消分页请求",
    defaultValue = "false",
    accessMode = Schema.AccessMode.WRITE_ONLY,
  )
  @get:JsonIgnore
  var unPage: Boolean?
}
