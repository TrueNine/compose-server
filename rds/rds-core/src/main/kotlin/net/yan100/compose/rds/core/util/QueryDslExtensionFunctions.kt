package net.yan100.compose.rds.core.util


import com.querydsl.core.types.dsl.EntityPathBase
import net.yan100.compose.rds.core.entities.IEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.repository.query.FluentQuery

fun <E : IEntity, T : EntityPathBase<E>, R> querydsl(
  e: T,
  fn: (queryHandle: T) -> R
): R {
  return fn(e)
}

fun <E : IEntity, T : EntityPathBase<E>, C, R> querydsl(
  e: T,
  c: C,
  fn: (queryHandle: T, companions: C) -> R
): R {
  return fn(e, c)
}


fun <E> FluentQuery.FetchableFluentQuery<E>.sortBy(orders: MutableList<Sort.Order>): FluentQuery.FetchableFluentQuery<E> {
  return sortBy(orders.asQuerySort())
}

fun <E> FluentQuery.FetchableFluentQuery<E>.page(pq: Pq?): Page<E> {
  return page(pq.page)
}

