package net.yan100.compose.rds.core

import net.yan100.compose.rds.core.entities.IAnyEntity
import net.yan100.compose.rds.core.entities.IEntity
import org.springframework.data.repository.query.FluentQuery
import kotlin.reflect.KClass

/**
 * ## 委托属性实现方式
 */
@Suppress("DEPRECATION_ERROR")
inline fun <reified T : IEntity, R : IRepo<T>> jpa(
  repo: R,
  vararg supportedTypes: KClass<out IAnyEntity> = arrayOf(T::class)
): ICrud<T> {
  return SpringBootStarterDataJpaCrudServiceDelegateProvider(repo, supportedTypes.toList())
}

fun <E : IEntity, R> IQuerydslExtensionRepository<E>.findByQueryDsl(
  predicate: com.querydsl.core.types.Predicate,
  optFn: (q: FluentQuery.FetchableFluentQuery<E>) -> R
): R {
  return this.findBy(predicate) { it: FluentQuery.FetchableFluentQuery<E> -> optFn(it) }
}
