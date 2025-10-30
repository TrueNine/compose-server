package io.github.truenine.composeserver.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.truenine.composeserver.Pq
import java.beans.Transient
import java.io.Serializable

/**
 * ## Calculate total page count
 *
 * @param total Total number of records
 * @param pageSize Page size for pagination
 * @return Total number of pages
 */
private fun calcTotalPageSize(total: Long, pageSize: Int): Int {
  if (total <= 0 || pageSize <= 0) return 0
  val m = (total / pageSize).toInt()
  return if (total % pageSize == 0L) m else m + 1
}

private class DefaultPageResult<T : Any>(
  @JsonIgnore @kotlin.jvm.Transient override var pageParam: IPageParam? = null,
  override var d: Collection<T>,
  override var t: Long,
  @Deprecated("This property is not needed") override var o: Long? = null,
  override var p: Int = calcTotalPageSize(t, pageParam?.safePageSize ?: Pq.MAX_PAGE_SIZE),
) : IPage<T>, Serializable {

  override fun toString(): String {
    return "IPage(dataList=$d, total=$t, offset=$o, pageSize=$p, size=${d.size}, [pageParam]=$pageParam)"
  }
}

/**
 * # Pagination result
 *
 * @author TrueNine
 * @since 2024-09-14
 */
interface IPage<T : Any?> : IPageLike<T> {
  /** ## Original pagination request parameters */
  @get:JsonIgnore @get:Transient @set:JsonIgnore @set:Transient @Deprecated("Not recommended for use") var pageParam: IPageParam?

  operator fun get(index: Int): T = d.toList()[index]

  /**
   * ## Transform pagination result
   *
   * @param transform Transformation function
   */
  fun <R : Any> transferTo(transform: (T) -> R): IPage<R> {
    return DefaultPageResult(pageParam = pageParam, d = d.map(transform), t = t, o = o, p = p)
  }

  companion object {
    /**
     * @param dataList Data list
     * @param total Total number of records
     * @param pageParam Original pagination parameters
     */
    @JvmStatic
    @Suppress("DEPRECATION_ERROR")
    @Deprecated("Not recommended")
    operator fun <T : Any> get(dataList: Collection<T>, total: Long = dataList.size.toLong(), pageParam: IPageParam? = null): IPage<T> {
      return of(dataList, total, pageParam)
    }

    /**
     * ## Build pagination result
     *
     * @param dataList Data list
     * @param total Total number of records
     * @param totalPageNumber Total number of pages
     */
    operator fun <T : Any> get(dataList: Collection<T>, total: Long, totalPageNumber: Int): IPage<T> {
      return DefaultPageResult(null, dataList, total, null, totalPageNumber)
    }

    /**
     * @param dataList Data list
     * @param total Total number of records
     * @param offset Page offset
     * @param requestParamPageSize Request parameter page size
     */
    @JvmStatic
    @Suppress("DEPRECATION")
    @Deprecated("Not recommended")
    operator fun <T : Any> get(dataList: Collection<T>, total: Long, offset: Int, requestParamPageSize: Int, unPage: Boolean?): IPage<T> {
      return get(dataList, total, IPageParam[offset, requestParamPageSize, unPage != false])
    }

    /**
     * @param dataList Data list
     * @param total Total number of records
     * @param pageParam Original pagination parameters
     */
    @JvmStatic
    @Deprecated("use get", replaceWith = ReplaceWith("get()"), level = DeprecationLevel.ERROR)
    fun <T : Any> of(dataList: Collection<T>, total: Long, pageParam: IPageParam?): IPage<T> = DefaultPageResult(pageParam = pageParam, d = dataList, t = total)

    @Deprecated("DEPRECATION") @JvmStatic fun <T : Any> one(data: T?): IPage<T> = if (data != null) get(listOf(data)) else emptyWith<T>()

    @JvmStatic fun <T : Any> emptyWith(): IPage<T> = get(emptyList())

    @JvmStatic
    fun <T : Any> unPage(dataList: Collection<T>): IPage<T> {
      return DefaultPageResult(null, dataList, dataList.size.toLong(), null, 1)
    }

    @JvmStatic fun empty(): IPage<*> = emptyWith<Any>()
  }
}
