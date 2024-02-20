/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.core.util

import com.querydsl.core.types.dsl.EntityPathBase
import net.yan100.compose.rds.core.entities.IEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.repository.query.FluentQuery

inline fun <E : IEntity, T : EntityPathBase<E>, R> querydsl(
  e: T,
  crossinline fn: (queryHandle: T) -> R
): R {
  return fn(e)
}

inline fun <E : IEntity, T : EntityPathBase<E>, C, R> querydsl(
  e: T,
  c: C,
  crossinline fn: (queryHandle: T, companions: C) -> R
): R {
  return fn(e, c)
}

inline fun querydslOrderBy(
  crossinline orderFn: (it: MutableList<Sort.Order>) -> Unit
): MutableList<Sort.Order> {
  val i = mutableListOf<Sort.Order>()
  orderFn(i)
  return i
}

fun <E> FluentQuery.FetchableFluentQuery<E>.sortBy(
  orders: MutableList<Sort.Order>
): FluentQuery.FetchableFluentQuery<E> {
  return sortBy(orders.asQuerySort())
}

fun <E> FluentQuery.FetchableFluentQuery<E>.page(pq: Pq?): Page<E> {
  return page(pq.page)
}
