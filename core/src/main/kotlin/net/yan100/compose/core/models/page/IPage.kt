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

import kotlin.math.ceil

interface IPage<T> {
  var dataList: List<T>
  var total: Long
  var size: Int
  var pageSize: Int
  var offset: Long

  private data class DefaultPage<T>(
    override var dataList: List<T>,
    override var total: Long,
    override var size: Int,
    override var pageSize: Int,
    override var offset: Long,
  ) : IPage<T>

  fun component1(): List<T> = dataList

  fun component2(): Long = total

  companion object {
    private fun emptyOne(dataList: List<*>?): Int {
      return if (dataList.isNullOrEmpty()) 1 else dataList.size
    }

    private fun defaultTotal(): Long {
      return 0
    }

    /**
     * @param dataList 数据列表
     * @param pageSize 分页参数页面大小
     * @param offset 偏移页码
     * @param total 数据总数
     */
    @JvmStatic
    operator fun <T> get(
      dataList: List<T> = emptyList(),
      pageSize: Int? = null,
      offset: Int = 0,
      total: Long? = null,
    ): IPage<T> {
      return of(dataList, total, dataList.size, pageSize, offset)
    }

    /**
     * @param dataList 数据列表
     * @param pageSize 分页参数页面大小
     * @param offset 偏移页码
     * @param total 数据总数
     */
    @JvmStatic
    fun <T> of(
      dataList: List<T> = emptyList(),
      total: Long? = null,
      size: Int? = null,
      pageSize: Int? = null,
      offset: Int = 0,
    ): IPage<T> {
      val safeTotal = total ?: defaultTotal()
      val safeSize = size ?: dataList.size
      val safePageSize = pageSize ?: (safeTotal / if (safeSize == 0) 1 else safeSize).toInt()

      val e = ceil(safeTotal.toDouble() / safePageSize.toDouble()).toInt()

      return DefaultPage(dataList, safeTotal, safeSize, e, offset.toLong())
    }


    @JvmStatic
    fun <T> empty(): IPage<T> {
      return DefaultPage(
        dataList = emptyList(),
        total = 0,
        size = 0,
        pageSize = 0,
        offset = 0
      )
    }
  }
}
