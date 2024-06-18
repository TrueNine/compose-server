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
package net.yan100.compose.core.models.page

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient
import net.yan100.compose.core.extensionfunctions.number.toSafeInt

interface IPageParam {
  data class DefaultPageParam(
    @get:Transient override var offset: Int? = MIN_OFFSET,
    @get:Transient override var pageSize: Int? = MAX_PAGE_SIZE,
    @get:Transient override var unPage: Boolean? = false
  ) : IPageParam

  companion object {
    const val MIN_OFFSET: Int = 0
    const val MAX_PAGE_SIZE: Int = 42

    fun of(offset: Int? = MIN_OFFSET, pageSize: Int? = MAX_PAGE_SIZE, unPage: Boolean? = false): IPageParam = DefaultPageParam(offset, pageSize, unPage)
  }

  @get:Transient var pageSize: Int?

  @get:Transient
  val safePageSize: Int
    get() = pageSize ?: 0

  @get:Transient var offset: Int?

  @get:JsonIgnore
  val safeOffset: Int
    get() = offset ?: 0

  @get:JsonIgnore @get:Transient var unPage: Boolean?

  @get:Transient
  @get:JsonIgnore
  val safeUnPage: Boolean
    get() = unPage ?: false

  fun <T> T.fromLongRange(range: LongRange): T {
    offset = range.first.toInt()
    pageSize = range.last.toInt() - range.first.toInt()
    return this
  }

  fun ofSafeTotal(total: Long): IPageParam {
    if (total <= 0) {
      offset = 0
      pageSize = 1
      return this
    }
    pageSize = if (safePageSize >= total) total.toSafeInt() else safePageSize

    val c = (safeOffset + 1) * safePageSize
    offset = if (c > total) (total / safePageSize).toInt() else safeOffset

    return this
  }

  fun fromRange(range: IntRange): IPageParam {
    offset = range.first
    pageSize = range.last - range.first
    return this
  }

  private val safeRandEnd: Int
    get() {
      val end = (safeOffset + (safePageSize - 1))
      return end
    }

  fun toRange(): IntRange = IntRange(safeOffset, safeRandEnd)

  fun toLongRange(): LongRange = LongRange(safeOffset.toLong(), safeRandEnd.toLong())
}
