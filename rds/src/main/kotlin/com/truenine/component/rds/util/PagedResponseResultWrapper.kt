package com.truenine.component.rds.util

import com.truenine.component.rds.base.PageModelRequestParam
import com.truenine.component.rds.base.PagedResponseResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

/**
 * 分页包装器
 *
 * @author TrueNine
 * @since 2022-12-27
 */
object PagedResponseResultWrapper {
  const val MAX_PAGE_SIZE = 42

  @JvmField
  val ZERO: PageModelRequestParam =
    PageModelRequestParam(
      0,
      DEFAULT_BUFFER_SIZE
    )


  @JvmStatic
  fun <T> data(jpaPage: Page<T>): PagedResponseResult<T> =
    PagedResponseResult<T>()
      .apply {
        dataList = jpaPage.content
        pageSize = jpaPage.totalPages
        total = jpaPage.totalElements
        size = jpaPage.content.size
        offset = jpaPage.pageable.offset
      }

  @JvmStatic
  fun param(paramSetting: PageModelRequestParam? = ZERO): Pageable {
    return PageRequest.of(
      paramSetting?.offset ?: 0,
      paramSetting?.pageSize ?: MAX_PAGE_SIZE
    )
  }
}
