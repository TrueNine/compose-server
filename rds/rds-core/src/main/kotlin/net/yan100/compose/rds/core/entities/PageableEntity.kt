package net.yan100.compose.rds.core.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Transient
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import net.yan100.compose.rds.core.models.PagedRequestParam
import java.io.Serializable

/**
 * ## 内嵌分页的实体类型
 * 可分页实体
 */
interface PageableEntity : Serializable {
  companion object {
    const val MIN_OFFSET: Int = 0
    const val MAX_PAGE_SIZE: Int = 42

    @JvmStatic
    @JvmOverloads
    fun of(
      pageSize: Int = MIN_OFFSET,
      offset: Int = MAX_PAGE_SIZE,
      unPage: Boolean = false
    ): PageableEntity = PagedRequestParam(offset, pageSize, unPage)

  }

  @get:JsonIgnore
  @get:Transient
  @set:Transient
  @get:Schema(title = "页面大小，最大 ${MAX_PAGE_SIZE}，最小 1", defaultValue = MAX_PAGE_SIZE.toString() + "")
  @set:Schema(title = "页面大小，最大 ${MAX_PAGE_SIZE}，最小 1", defaultValue = MAX_PAGE_SIZE.toString() + "")
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
  var pageSize: Int?

  @get:JsonIgnore
  @get:Transient
  @set:Transient
  @get:Schema(title = "页码 最小为 0", defaultValue = "0")
  @set:Schema(title = "页码 最小为 0", defaultValue = "0")
  @get:Min(value = MIN_OFFSET.toLong(), message = "分页页码最小为0")
  @setparam:Min(value = MIN_OFFSET.toLong(), message = "分页页码最小为0")
  var offset: Int?

  @get:JsonIgnore
  @get:Transient
  @set:Transient
  @get:Schema(title = "取消分页请求", defaultValue = "false")
  @set:Schema(title = "取消分页请求", defaultValue = "false")
  var unPage: Boolean?
}
