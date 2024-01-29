package net.yan100.compose.rds.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Transient
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

import net.yan100.compose.rds.core.entities.PageableEntity
import net.yan100.compose.rds.core.entities.PageableEntity.Companion.MAX_PAGE_SIZE
import net.yan100.compose.rds.core.entities.PageableEntity.Companion.MIN_OFFSET

/**
 * 分页入参
 *
 * @author TrueNine
 * @since 2022-12-31
 */
@Schema(title = "分页请求入参")
open class PagedRequestParam @JvmOverloads constructor(
  offset: Int? = MIN_OFFSET, pageSize: Int? = MAX_PAGE_SIZE, unPage: Boolean? = false
) : PageableEntity {

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
  @get:Max(
    value = MAX_PAGE_SIZE.toLong(),
    message = "分页最大参数为${MAX_PAGE_SIZE}"
  )
  @setparam:Max(
    value = MAX_PAGE_SIZE.toLong(),
    message = "分页最大参数为${MAX_PAGE_SIZE}"
  )
  @get:Transient
  @set:Transient
  @Transient
  override var pageSize: Int? = MAX_PAGE_SIZE

  @get:JsonIgnore
  @get:Transient
  @set:Transient
  @Transient
  override var unPage: Boolean? = false

  init {
    this.offset = offset
    this.pageSize = pageSize
    this.unPage = unPage
  }
}
