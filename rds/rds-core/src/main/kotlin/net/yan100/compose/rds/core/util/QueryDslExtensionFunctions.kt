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

inline fun querydslOrderBy(crossinline orderFn: (it: MutableList<Sort.Order>) -> Unit): MutableList<Sort.Order> {
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

