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

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.SimpleExpression
import com.querydsl.core.types.dsl.StringPath
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import net.yan100.compose.core.Pq
import net.yan100.compose.rds.core.entities.IEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.repository.query.FluentQuery

open class QueryDslExtensionFUnctionsArg1<E : IEntity, T : EntityPathBase<E>>(
  /** ## 当前使用的表达式 */
  val q: T,
  /** ## BooleanBuilder 用于构建条件 */
  val bb: BooleanBuilder,
)

val prototypeBooleanBuilder = BooleanBuilder()

/**
 * ## BooleanBuilder
 *
 * @param e EntityPath
 * @param fn 执行函数
 */
inline fun <E : IEntity, T : EntityPathBase<E>, R> querydsl(e: T, crossinline fn: QueryDslExtensionFUnctionsArg1<E, T>.() -> R): R =
  fn(QueryDslExtensionFUnctionsArg1(e, prototypeBooleanBuilder.clone()))

open class QueryDslExtensionFUnctionsArg3<E : IEntity, T : EntityPathBase<E>>(
  q: T,
  bb: BooleanBuilder,
  /** ## JPA Query Factory */
  val qf: JPAQueryFactory,
) : QueryDslExtensionFUnctionsArg1<E, T>(q, bb)

/**
 * ## 带自定义构造对象的 querydsl 查询方法
 *
 * 在 Spring 环境中，传入的 entityManager 需被 PersistenceContext 注所注解，才能保证其获取的 EntityManager 是同一个上下文的
 *
 * @see jakarta.persistence.EntityManager
 * @see jakarta.persistence.PersistenceContext
 */
@JvmName("querydslByBooleanBuilderAndEntityManager")
inline fun <E : IEntity, T : EntityPathBase<E>, R> querydsl(
    e: T,
    entityManager: EntityManager,
    crossinline fn: QueryDslExtensionFUnctionsArg3<E, T>.() -> R,
): R = fn(QueryDslExtensionFUnctionsArg3(e, BooleanBuilder(), JPAQueryFactory(entityManager)))

/* order by extension functions */

inline fun querydslOrderBy(crossinline orderFn: MutableList<Sort.Order>.() -> Unit): MutableList<Sort.Order> {
  val i = mutableListOf<Sort.Order>()
  orderFn(i)
  return i
}

fun <E> FluentQuery.FetchableFluentQuery<E>.sortBy(orders: MutableList<Sort.Order>): FluentQuery.FetchableFluentQuery<E> {
  return sortBy(orders.asQuerySort())
}

fun <E> FluentQuery.FetchableFluentQuery<E>.page(pq: Pq?): Page<E> {
  return page(pq.page)
}

/* path or expression extension functions */

fun <T> SimpleExpression<T>.eqOrIsNull(value: T): BooleanExpression {
  return eq(value).or(isNull)
}

fun StringPath.eqOrIsNullOrIsEmpty(value: String): BooleanExpression {
  return eqOrIsNull(value).or(isEmpty)
}

fun <T> JPAQuery<T>.limitOffset(pq: Pq = Pq.DEFAULT_MAX) {
  limit(pq.safePageSize.toLong())
  offset(pq.safeOffset.toLong() * pq.safePageSize.toLong())
}
