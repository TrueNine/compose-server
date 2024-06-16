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
package net.yan100.compose.rds.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Transient
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import net.yan100.compose.core.models.page.IPageParam
import net.yan100.compose.rds.core.entities.IPageableEntity
import net.yan100.compose.rds.core.util.Pq

/**
 * 分页入参
 *
 * @author TrueNine
 * @since 2022-12-31
 */
@Schema(title = "分页请求入参")
open class PagedRequestParam @JvmOverloads constructor(offset: Int? = Pq.MIN_OFFSET, pageSize: Int? = Pq.MAX_PAGE_SIZE, unPage: Boolean? = false) :
  IPageableEntity, IPageParam {

  @get:JsonIgnore
  @get:Transient
  @set:Transient
  @Transient
  @get:Min(value = Pq.MIN_OFFSET.toLong(), message = "分页页码最小为0")
  @setparam:Min(value = Pq.MIN_OFFSET.toLong(), message = "分页页码最小为0")
  override var offset: Int? = Pq.MIN_OFFSET

  @get:JsonIgnore
  @get:Min(value = 1, message = "页面大小最小为1")
  @setparam:Min(value = 1, message = "页面大小最小为1")
  @get:Max(value = Pq.MAX_PAGE_SIZE.toLong(), message = "分页最大参数为${Pq.MIN_OFFSET}")
  @setparam:Max(value = Pq.MAX_PAGE_SIZE.toLong(), message = "分页最大参数为${Pq.MAX_PAGE_SIZE}")
  @get:Transient
  @set:Transient
  @Transient
  override var pageSize: Int? = Pq.MAX_PAGE_SIZE

  @get:JsonIgnore @get:Transient @set:Transient @Transient override var unPage: Boolean? = false

  init {
    this.offset = offset
    this.pageSize = pageSize
    this.unPage = unPage
  }
}
