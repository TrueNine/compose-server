package net.yan100.compose.rds.core.annotations

import jakarta.transaction.Transactional
import java.lang.annotation.Inherited

/**
 * ## spring 中 事务注解 别称
 *
 * 对齐 默认加上了 Exception Throwable Error
 * @author TrueNine
 * @since 2024-09-11
 * @see [Transactional]
 * @see [Exception]
 * @see [Throwable]
 * @see [Error]
 */
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Transactional(rollbackOn = [Exception::class, Throwable::class, Error::class])
annotation class ACID

