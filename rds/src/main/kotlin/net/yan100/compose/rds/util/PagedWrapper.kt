package net.yan100.compose.rds.util

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
  val DEFAULT_MAX: net.yan100.compose.rds.base.PagedRequestParam =
    net.yan100.compose.rds.base.PagedRequestParam(
      net.yan100.compose.rds.base.PagedRequestParam.MIN_OFFSET,
      net.yan100.compose.rds.base.PagedRequestParam.MAX_PAGE_SIZE
    )


  @JvmStatic
  fun <T> result(jpaPage: Page<T>): net.yan100.compose.rds.base.PagedResponseResult<T> =
    net.yan100.compose.rds.base.PagedResponseResult<T>()
      .apply {
        dataList = jpaPage.content
        pageSize = jpaPage.totalPages
        total = jpaPage.totalElements
        size = jpaPage.content.size
        offset = jpaPage.pageable.offset
      }

  @JvmStatic
  fun param(paramSetting: net.yan100.compose.rds.base.PagedRequestParam? = DEFAULT_MAX): Pageable {
    return PageRequest.of(
      paramSetting?.offset ?: 0,
      paramSetting?.pageSize ?: net.yan100.compose.rds.base.PagedRequestParam.MAX_PAGE_SIZE
    )
  }
}

typealias Pw = PagedWrapper
typealias Pq = net.yan100.compose.rds.base.PagedRequestParam
typealias Pr<T> = net.yan100.compose.rds.base.PagedResponseResult<T>

/**
 * # 对分页结果的封装，使得其返回包装对象
 */
val <T> Page<T>.result: Pr<T>
  get() = Pw.result(this)


/**
 * # 对分页参数的封装，返回一个包装的对象
 */
val Pq?.page: Pageable
  get() = Pw.param(this)