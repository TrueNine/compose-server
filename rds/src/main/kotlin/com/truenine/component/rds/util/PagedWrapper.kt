package com.truenine.component.rds.util

import com.truenine.component.rds.base.PagedRequestParam
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
object PagedWrapper {

  @JvmField
  val DEFAULT_MAX: PagedRequestParam = PagedRequestParam(
    PagedRequestParam.MIN_OFFSET,
    PagedRequestParam.MAX_PAGE_SIZE
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
  fun param(paramSetting: PagedRequestParam? = DEFAULT_MAX): Pageable {
    return PageRequest.of(
      paramSetting?.offset ?: 0,
      paramSetting?.pageSize ?: PagedRequestParam.MAX_PAGE_SIZE
    )
  }
}

/**
 * # 对分页结果的封装，使得其返回包装对象
 */
val <T> Page<T>.result: PagedResponseResult<T>
  get() = PagedWrapper.result(this)


/**
 * # 对分页参数的封装，返回一个包装的对象
 */
val PagedRequestParam?.page: Pageable
  get() = PagedWrapper.param(this)
