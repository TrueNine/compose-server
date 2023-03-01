package io.tn.rds.util

import io.tn.rds.dto.PageParam
import io.tn.rds.dto.PagedData
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

/**
 * 分页包装器
 *
 * @author TrueNine
 * @since 2022-12-27
 */
object PageWrap {

  @JvmStatic
  val MIN_PAGE_SIZE = 42

  @JvmStatic
  val DEFAULT_PARAM: PageParam = PageParam(0, DEFAULT_BUFFER_SIZE)


  @JvmStatic
  fun <T> data(jpaPage: Page<T>): PagedData<T> =
    PagedData<T>()
      .apply {
        contents = jpaPage.content
        totalPage = jpaPage.totalPages
        totalSize = jpaPage.totalElements
        currentSize = jpaPage.content.size
        offset = jpaPage.pageable.offset
      }

  @JvmStatic
  fun param(paramSetting: PageParam? = DEFAULT_PARAM): Pageable {
    return PageRequest.of(
      paramSetting?.offset ?: 0,
      paramSetting?.pageSize ?: MIN_PAGE_SIZE
    )
  }
}
