package net.yan100.compose.rds.annotations

import java.lang.annotation.Inherited
import org.springframework.transaction.annotation.Transactional

/**
 * ## spring 中 事务注解 别称
 *
 * 对齐 默认加上了 Exception Throwable Error
 *
 * @see [Transactional]
 * @see [Exception]
 * @see [Throwable]
 * @see [Error]
 * @author TrueNine
 * @since 2024-09-11
 */
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Target(
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
)
@Transactional(rollbackFor = [Exception::class, Throwable::class, Error::class])
annotation class ACID
