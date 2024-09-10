package net.yan100.compose.rds.repositories.base

import net.yan100.compose.rds.core.entities.IEntity
import org.springframework.data.repository.query.FluentQuery

fun <E : IEntity, R> IAnyRepo<E>.findByQueryDsl(predicate: com.querydsl.core.types.Predicate, optFn: (q: FluentQuery.FetchableFluentQuery<E>) -> R): R {
  return findBy(predicate) { it: FluentQuery.FetchableFluentQuery<E> -> optFn(it) }
}
