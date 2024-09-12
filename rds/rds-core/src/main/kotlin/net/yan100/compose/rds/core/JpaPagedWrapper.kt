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
package net.yan100.compose.rds.core

import net.yan100.compose.core.Pq
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

/**
 * 分页包装器
 *
 * @author TrueNine
 * @since 2022-12-27
 */
object JpaPagedWrapper {
  @JvmField val DEFAULT_MAX: JpaPagedRequestParam = JpaPagedRequestParam(Pq.MIN_OFFSET, Pq.MAX_PAGE_SIZE, false)

  @JvmStatic
  fun <T> result(jpaPage: Page<T>): PagedResponseResult<T> {
    return PagedResponseResult<T>().apply {
      dataList = jpaPage.content
      pageSize = jpaPage.totalPages
      total = jpaPage.totalElements
      size = jpaPage.content.size
      offset = if (jpaPage.pageable.isPaged) jpaPage.pageable.offset else 0
    }
  }

  /** 根据新的list计算结果集 <br/> 例如：查询 A 表，但是返回 B 表处理后的结果 */
  @JvmStatic
  fun <R> resultByNewList(jpaPage: Page<*>, newList: List<R>): PagedResponseResult<R> {
    return PagedResponseResult<R>().apply {
      dataList = newList
      pageSize = jpaPage.totalPages
      total = jpaPage.totalElements
      size = newList.size
      offset = if (jpaPage.pageable.isPaged) jpaPage.pageable.offset else 0
    }
  }

  @JvmStatic
  fun param(paramSetting: Pq? = Pq.DEFAULT_MAX): Pageable {
    return if (true != paramSetting?.unPage || null == paramSetting.unPage) {
      PageRequest.of(paramSetting?.offset ?: 0, paramSetting?.pageSize ?: Pq.MAX_PAGE_SIZE)
    } else Pageable.unpaged()
  }

  /**
   * ## 将一个 Sequence 包装为分页数据
   *
   * @param pageParam 分页数据
   * @param lazySequence 序列
   */
  @JvmStatic
  fun <T> warpBy(pageParam: Pq = Pq.DEFAULT_MAX, lazySequence: () -> Sequence<T>): PagedResponseResult<T> {
    val sequence = lazySequence()
    val list = sequence.take((pageParam.offset ?: 0) + (pageParam.pageSize ?: 0)).toList()
    val endSize = minOf(pageParam.pageSize ?: 0, list.size)
    return PagedResponseResult<T>().apply {
      this.dataList = list.subList(0, endSize)
      this.total = sequence.count().toLong()
      this.offset = ((pageParam.offset ?: 0).toLong()) * (pageParam.pageSize ?: 0)
      this.size = endSize
      this.pageSize = if (list.isEmpty()) 0 else sequence.count() / (pageParam.pageSize ?: 1)
    }
  }
}



