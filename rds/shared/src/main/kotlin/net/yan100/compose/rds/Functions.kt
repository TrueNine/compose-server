package net.yan100.compose.rds

import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.repositories.IQuerydslExtensionRepository
import org.springframework.data.repository.query.FluentQuery


fun <E : IJpaEntity, R> IQuerydslExtensionRepository<E>.findByQueryDsl(
  predicate: com.querydsl.core.types.Predicate,
  optFn: (q: FluentQuery.FetchableFluentQuery<E>) -> R,
): R {
  return this.findBy(predicate) { it: FluentQuery.FetchableFluentQuery<E> ->
    optFn(it)
  }
}
