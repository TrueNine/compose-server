package io.github.truenine.composeserver.rds.annotations

import java.lang.annotation.Inherited
import org.springframework.transaction.annotation.Transactional

/**
 * Marks a function, class, or property as adhering to the ACID (Atomicity, Consistency, Isolation, Durability) principles. This annotation ensures that the
 * annotated element is transactional and will roll back in case of exceptions, including [Exception], [Throwable], and [Error], ensuring data integrity and
 * reliability. The annotation is inheritable and retained at runtime, making it available for reflection. It can be applied to functions, classes, types,
 * property getters, and property setters.
 */
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Transactional(rollbackFor = [Exception::class, Throwable::class, Error::class])
annotation class ACID
