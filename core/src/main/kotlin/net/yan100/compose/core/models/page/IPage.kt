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

interface IPage<T> {
  var dataList: List<T>
  var total: Long
  var size: Int
  var pageSize: Int
  var offset: Long

  data class DefaultPage<T>(
    override var dataList: List<T> = emptyList(),
    override var total: Long = 0,
    override var size: Int = dataList.size,
    override var pageSize: Int = 0,
    override var offset: Long = 0,
  ) : IPage<T>

  companion object {
    @JvmStatic
    fun <T> empty(): IPage<T> {
      return DefaultPage()
    }
  }
}