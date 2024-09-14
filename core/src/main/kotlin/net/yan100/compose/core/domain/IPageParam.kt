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
import jakarta.persistence.Transient
import net.yan100.compose.core.domain.IPageParam.Companion.MAX_PAGE_SIZE
import net.yan100.compose.core.domain.IPageParam.Companion.MIN_OFFSET
import net.yan100.compose.core.toSafeInt
import java.io.Serializable

/** ## 一个默认分页实现 */
private class DefaultPageParam(
  @Transient override var o: Long? = MIN_OFFSET,
  @Transient override var s: Int? = MAX_PAGE_SIZE,
  @Transient override var u: Boolean? = false,
) : IPageParam

/**
 * # 分页参数
 *
 * 为所有的分页场景准备的分页参数
 *
 * @author TrueNine
 * @since 2024-06-20
 */
interface IPageParam : Serializable {
  companion object {
    /** ## 最小偏移量 */
    const val MIN_OFFSET: Long = 0

    /** ## 最大分页页面大小 */
    const val MAX_PAGE_SIZE: Int = 42

    /** ## 默认 最大分页实现常量 */
    val DEFAULT_MAX: IPageParam = DefaultPageParam(MIN_OFFSET, MAX_PAGE_SIZE, false)

    @JvmStatic
    operator fun get(pageSize: Int? = MAX_PAGE_SIZE, offset: Long? = MIN_OFFSET, unPage: Boolean? = false): IPageParam {
      return DefaultPageParam(offset, pageSize, unPage)
    }
  }

  /** ## 分页 页面 大小 */
  @get:Transient
  var s: Int?

  /**
   * ## UnPaged（禁用分页）
   */
  @get:JsonIgnore
  @get:Transient
  var u: Boolean?

  @get:Transient
  var o: Long?

  @Transient
  @JsonIgnore
  operator fun plus(total: Long): IPageParam {
    if (total <= 0) {
      o = 0
      s = 1
      return this
    }
    s = if (safePageSize >= total) total.toSafeInt() else safePageSize
    val c = (safeOffset + 1) * safePageSize
    o = if (c > total) (total / safePageSize) else safeOffset
    return this
  }

  @get:JsonIgnore
  @get:Transient
  val safeOffset: Long get() = o ?: 0

  /** ## 分页 页面 偏移量 null any */
  @get:Transient
  @get:JsonIgnore
  val safePageSize: Int get() = s ?: 0

  @get:JsonIgnore
  @get:Transient
  private val safeRangeOffset: Long get() = (safePageSize.toLong() * safeOffset)

  @get:Transient
  @get:JsonIgnore
  private val safeRandEnd: Long
    get() {
      val end = (safeRangeOffset + (safePageSize))
      return end
    }

  @Transient
  @JsonIgnore
  fun toLongRange(): LongRange = LongRange(safeRangeOffset, safeRandEnd)
}
