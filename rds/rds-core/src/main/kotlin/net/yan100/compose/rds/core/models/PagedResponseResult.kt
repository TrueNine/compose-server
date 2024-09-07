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
package net.yan100.compose.rds.core.models

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.models.page.IPage
import java.io.Serial
import java.io.Serializable

/**
 * 分页数据包装
 *
 * @param <T> 分页参数类型
 * @author TrueNine
 * @since 2022-12-31
 */
@Schema(title = "分页列表信息")
data class PagedResponseResult<T>(
  @Schema(title = "数据列表") override var dataList: List<T> = emptyList(),
  @get:Schema(title = "结果总数") override var total: Long = 0L,
  @Schema(title = "当前页面大小") override var size: Int = 0,
  @Schema(title = "总页数") override var pageSize: Int = 0,
  @Schema(title = "当前页码") override var offset: Long = 0L,
) : Serializable, IPage<T> {

  companion object {
    @Serial private val serialVersionUID = 1L

    fun <T> empty(): PagedResponseResult<T> {
      val r = PagedResponseResult<T>()
      r.dataList = mutableListOf()
      r.offset = 0L
      r.pageSize = 0
      r.size = 0
      r.total = 0
      return r
    }
  }
}
