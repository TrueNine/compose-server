package com.truenine.component.rds.util

import com.truenine.component.rds.base.PagedRequestParam
import com.truenine.component.rds.base.PagedResponseResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.io.Serializable

/**
 * 分页包装器
 *
 * @author TrueNine
 * @since 2022-12-27
 */
object PagedWrapper {
  const val MAX_PAGE_SIZE = 42

  @JvmField
  val ZERO: PagedRequestParam =
    PagedRequestParam(
      0,
      DEFAULT_BUFFER_SIZE
    )


  @JvmStatic
  fun <T> result(jpaPage: Page<T>): PagedResponseResult<T> =
    PagedResponseResult<T>()
      .apply {
        dataList = jpaPage.content
        pageSize = jpaPage.totalPages
        total = jpaPage.totalElements
        size = jpaPage.content.size
        offset = jpaPage.pageable.offset
      }

  @JvmStatic
  fun param(paramSetting: PagedRequestParam? = ZERO): Pageable {
    return PageRequest.of(
      paramSetting?.offset ?: 0,
      paramSetting?.pageSize ?: MAX_PAGE_SIZE
    )
  }

  @JvmStatic
  fun <T> help(param: PagedRequestParam, repository: JpaRepository<T, Serializable>): PagedResponseResult<T> {
    val a = param(param)
    return result(repository.findAll(a))
  }
}
