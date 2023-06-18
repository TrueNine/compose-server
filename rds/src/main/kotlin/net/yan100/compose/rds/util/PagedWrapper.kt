package net.yan100.compose.rds.util

import net.yan100.compose.rds.base.PagedRequestParam
import net.yan100.compose.rds.base.PagedResponseResult
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
  /**
   * ## 构造一个空的参数
   */
  @JvmStatic
  fun <T> empty(): PagedResponseResult<T> = PagedResponseResult.empty()

  @JvmField
  val DEFAULT_MAX: PagedRequestParam =
    PagedRequestParam(
      PagedRequestParam.MIN_OFFSET,
      PagedRequestParam.MAX_PAGE_SIZE,
      false
    )

  @JvmField
  val UN_PAGE: PagedRequestParam =
    PagedRequestParam(
      PagedRequestParam.MIN_OFFSET,
      PagedRequestParam.MAX_PAGE_SIZE,
      true
    )

  @JvmStatic
  fun <T> result(jpaPage: Page<T>): PagedResponseResult<T> =
    PagedResponseResult<T>()
      .apply {
        dataList = jpaPage.content
        pageSize = jpaPage.totalPages
        total = jpaPage.totalElements
        size = jpaPage.content.size

        offset = if (jpaPage.pageable.isPaged) jpaPage.pageable.offset else 0
      }

  @JvmStatic
  fun param(paramSetting: PagedRequestParam? = DEFAULT_MAX): Pageable {
    return if (false == paramSetting?.unPage) {
      PageRequest.of(
        paramSetting.offset ?: 0,
        paramSetting.pageSize ?: PagedRequestParam.MAX_PAGE_SIZE
      )
    } else Pageable.unpaged()
  }

  /**
   * ## 将一个 Sequence 包装为分页数据
   * @param pageParam 分页数据
   * @param lazySequence 序列
   */
  @JvmStatic
  fun <T> warpBy(pageParam: PagedRequestParam = DEFAULT_MAX, lazySequence: () -> Sequence<T>): PagedResponseResult<T> {
    val sequence = lazySequence()
    val list = sequence.take(pageParam.offset + pageParam.pageSize).toList()
    val endSize = minOf(pageParam.pageSize, list.size)
    return PagedResponseResult<T>().apply {
      this.dataList = list.subList(0, endSize)
      this.total = sequence.count().toLong()
      this.offset = (pageParam.offset.toLong()) * pageParam.pageSize
      this.size = endSize
      this.pageSize = if (list.isEmpty()) 0 else sequence.count() / pageParam.pageSize
    }
  }
}

typealias Pw = PagedWrapper
typealias Pq = PagedRequestParam
typealias Pr<T> = PagedResponseResult<T>

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
