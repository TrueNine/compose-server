package io.github.truenine.composeserver.ksp.meta.annotations.orm

import java.lang.annotation.Inherited

/**
 * ## Computed property
 *
 * Indicates that the annotated element is a computed property and, in most
 * cases, does not need to be processed by code generation.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.FUNCTION)
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class MetaFormula()
