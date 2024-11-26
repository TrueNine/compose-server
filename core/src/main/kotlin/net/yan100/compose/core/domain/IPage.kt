/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import net.yan100.compose.core.Pq
import java.beans.Transient
import java.io.Serializable

/**
 * ## 计算总页数
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
  @JsonIgnore
  @kotlin.jvm.Transient
  override var pageParam: IPageParam?,
  override var d: Collection<T>,
  override var t: Long,
  override var o: Long = pageParam?.safeOffset ?: Pq.MIN_OFFSET,
  override var p: Int = calcTotalPageSize(t, pageParam?.safePageSize ?: Pq.MAX_PAGE_SIZE),
) : IPage<T>, Serializable {
  override fun toString(): String {
    return "IPage(dataList=$d, total=$t, offset=$o, pageSize=$p, size=$size, [pageParam]=$pageParam)"
  }
}

/**
 * # 分页结果
 * @author TrueNine
 * @since 2024-09-14
 */
interface IPage<T : Any?> {
  /**
   * ## Data List
   * 数据列表
   */
  var d: Collection<T>

  /**
   * ## Page Offset
   * 当前所在页面 起始位置为 0 默认为 0
   */
  var o: Long

  /**
   * ## Total Page Size
   */
  var p: Int

  /**
   * ## Total Elements Size
   * 所有内容总数 起始位置为 0 默认为 0
   */
  var t: Long

  /**
   * ## 原始分页请求参数
   */
  @get:JsonIgnore
  @get:Transient
  @set:JsonIgnore
  @set:Transient
  var pageParam: IPageParam?

  /**
   * ## Data List Size（多余）
   *
   * 前端不应显示，浪费字段
   * 如果设置此字段，会间接裁剪数据
   */
  var size: Int
    @JsonIgnore
    @Transient
    set(value) {
      if (value >= 0) d = d.toList().subList(0, value)
    }
    @JsonIgnore
    @Transient
    get() = d.size

  fun component1(): Collection<T> = d
  fun component2(): Long = t
  fun component3(): Long = o

  operator fun get(index: Int): T = d.toList()[index]

  companion object {
    /**
     * @param dataList 数据列表
     * @param total 数据总数
     * @param  pageParam 原始分页参数
     */
    @JvmStatic
    @Suppress("DEPRECATION_ERROR")
    operator fun <T : Any> get(
      dataList: Collection<T> = emptyList(),
      total: Long = dataList.size.toLong(),
      pageParam: IPageParam = IPageParam.DEFAULT_MAX
    ): IPage<T> {
      require(pageParam.safePageSize >= 0) { "pageSize ${pageParam.safePageSize} must be greater than 0" }
      return of(dataList, total, pageParam)
    }

    /**
     * @param dataList 数据列表
     * @param total 数据总数
     * @param offset 偏移页码
     * @param requestParamPageSize 请求参数的页码
     */
    @JvmStatic
    @Suppress("DEPRECATION_ERROR")
    operator fun <T : Any> get(
      dataList: Collection<T> = emptyList(),
      total: Long,
      offset: Long,
      requestParamPageSize: Int,
      unPage: Boolean? = false
    ): IPage<T> {
      return get(dataList, total, IPageParam[offset, requestParamPageSize, unPage ?: true])
    }

    /**
     * @param dataList 数据列表
     * @param total 数据总数
     * @param pageParam 原始分页参数
     */
    @JvmStatic
    @Deprecated("use get", replaceWith = ReplaceWith("get()"), level = DeprecationLevel.ERROR)
    fun <T : Any> of(
      dataList: Collection<T>,
      total: Long,
      pageParam: IPageParam
    ): IPage<T> =
      DefaultPageResult(pageParam = pageParam, d = dataList, t = total)

    @JvmStatic
    fun <T : Any> one(data: T?): IPage<T> = if (data != null) get(listOf(data)) else empty()

    @JvmStatic
    fun <T : Any> empty(): IPage<T> = get()
  }
}
