/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Transient
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import net.yan100.compose.rds.core.entities.IPageableEntity
import net.yan100.compose.rds.core.entities.IPageableEntity.Companion.MAX_PAGE_SIZE
import net.yan100.compose.rds.core.entities.IPageableEntity.Companion.MIN_OFFSET

/**
 * 分页入参
 *
 * @author TrueNine
 * @since 2022-12-31
 */
@Schema(title = "分页请求入参")
class PagedRequestParam
@JvmOverloads
constructor(offset: Int? = MIN_OFFSET, pageSize: Int? = MAX_PAGE_SIZE, unPage: Boolean? = false) :
  IPageableEntity {

  @get:JsonIgnore
  @get:Transient
  @set:Transient
  @Transient
  @get:Min(value = MIN_OFFSET.toLong(), message = "分页页码最小为0")
  @setparam:Min(value = MIN_OFFSET.toLong(), message = "分页页码最小为0")
  override var offset: Int? = MIN_OFFSET

  @get:JsonIgnore
  @get:Min(value = 1, message = "页面大小最小为1")
  @setparam:Min(value = 1, message = "页面大小最小为1")
  @get:Max(value = MAX_PAGE_SIZE.toLong(), message = "分页最大参数为${MAX_PAGE_SIZE}")
  @setparam:Max(value = MAX_PAGE_SIZE.toLong(), message = "分页最大参数为${MAX_PAGE_SIZE}")
  @get:Transient
  @set:Transient
  @Transient
  override var pageSize: Int? = MAX_PAGE_SIZE

  @get:JsonIgnore @get:Transient @set:Transient @Transient override var unPage: Boolean? = false

  init {
    this.offset = offset
    this.pageSize = pageSize
    this.unPage = unPage
  }
}