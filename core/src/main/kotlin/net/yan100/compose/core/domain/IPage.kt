package net.yan100.compose.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.beans.Transient
import java.io.Serializable
import net.yan100.compose.core.Pq

/**
 * ## 计算总页数
 *
 * @param total 数据总数
 * @param pageSize 分页页面大小
 * @return 总页数
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
  @Deprecated("无需此属性") override var o: Long? = null,
  override var p: Int =
    calcTotalPageSize(t, pageParam?.safePageSize ?: Pq.MAX_PAGE_SIZE),
) : IPage<T>, Serializable {

  override fun toString(): String {
    return "IPage(dataList=$d, total=$t, offset=$o, pageSize=$p, size=${d.size}, [pageParam]=$pageParam)"
  }
}

/**
 * # 分页结果
 *
 * @author TrueNine
 * @since 2024-09-14
 */
interface IPage<T : Any?> : IPageLike<T> {
  /** ## 原始分页请求参数 */
  @get:JsonIgnore
  @get:Transient
  @set:JsonIgnore
  @set:Transient
  @Deprecated("不推荐使用")
  var pageParam: IPageParam?

  operator fun get(index: Int): T = d.toList()[index]

  /**
   * ## 转换分页结果
   *
   * @param transform 转换函数
   */
  fun <R : Any> transferTo(transform: (T) -> R): IPage<R> {
    return DefaultPageResult(
      pageParam = pageParam,
      d = d.map(transform),
      t = t,
      o = o,
      p = p,
    )
  }

  companion object {
    /**
     * @param dataList 数据列表
     * @param total 数据总数
     * @param pageParam 原始分页参数
     */
    @JvmStatic
    @Suppress("DEPRECATION_ERROR")
    @Deprecated("不推荐")
    operator fun <T : Any> get(
      dataList: Collection<T>,
      total: Long = dataList.size.toLong(),
      pageParam: IPageParam? = null,
    ): IPage<T> {
      return of(dataList, total, pageParam)
    }

    /**
     * ## 构建分页结果
     *
     * @param dataList 数据列表
     * @param total 数据总行数
     * @param totalPageNumber 数据总页数
     */
    operator fun <T : Any> get(
      dataList: Collection<T>,
      total: Long,
      totalPageNumber: Int,
    ): IPage<T> {
      return DefaultPageResult(null, dataList, total, null, totalPageNumber)
    }

    /**
     * @param dataList 数据列表
     * @param total 数据总数
     * @param offset 偏移页码
     * @param requestParamPageSize 请求参数的页码
     */
    @JvmStatic
    @Suppress("DEPRECATION_ERROR")
    @Deprecated("不推荐")
    operator fun <T : Any> get(
      dataList: Collection<T>,
      total: Long,
      offset: Int,
      requestParamPageSize: Int,
      unPage: Boolean?,
    ): IPage<T> {
      return get(
        dataList,
        total,
        IPageParam[offset, requestParamPageSize, unPage ?: true],
      )
    }

    /**
     * @param dataList 数据列表
     * @param total 数据总数
     * @param pageParam 原始分页参数
     */
    @JvmStatic
    @Deprecated(
      "use get",
      replaceWith = ReplaceWith("get()"),
      level = DeprecationLevel.ERROR,
    )
    fun <T : Any> of(
      dataList: Collection<T>,
      total: Long,
      pageParam: IPageParam?,
    ): IPage<T> =
      DefaultPageResult(pageParam = pageParam, d = dataList, t = total)

    @JvmStatic
    fun <T : Any> one(data: T?): IPage<T> =
      if (data != null) get(listOf(data)) else emptyWith<T>()

    @JvmStatic fun <T : Any> emptyWith(): IPage<T> = get(emptyList())

    @JvmStatic
    fun <T : Any> unPage(dataList: Collection<T>): IPage<T> {
      return DefaultPageResult(null, dataList, dataList.size.toLong(), null, 1)
    }

    @JvmStatic fun empty(): IPage<*> = emptyWith<Any>()
  }
}
