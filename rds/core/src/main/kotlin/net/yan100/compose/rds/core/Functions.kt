package net.yan100.compose.rds.core

import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.entities.IJpaPersistentEntity
import net.yan100.compose.rds.core.repositories.IQuerydslExtensionRepository
import net.yan100.compose.rds.core.service.SpringBootStarterDataJpaCrudServiceDelegateProvider
import org.springframework.data.repository.query.FluentQuery
import kotlin.reflect.KClass

/**
 * ## 委托属性实现方式
 */
@Suppress("DEPRECATION_ERROR")
inline fun <reified T : IJpaEntity, R : IRepo<T>> jpa(
  repo: R,
  vararg supportedTypes: KClass<out IJpaPersistentEntity> = arrayOf(T::class)
): ICrud<T> {
  return SpringBootStarterDataJpaCrudServiceDelegateProvider(repo, supportedTypes.toList())
}

fun <E : IJpaEntity, R> IQuerydslExtensionRepository<E>.findByQueryDsl(
  predicate: com.querydsl.core.types.Predicate,
  optFn: (q: FluentQuery.FetchableFluentQuery<E>) -> R
): R {
  return this.findBy(predicate) { it: FluentQuery.FetchableFluentQuery<E> -> optFn(it) }
}
