package io.github.truenine.composeserver.ksp.meta.annotations

import java.lang.annotation.Inherited

/**
 * ## Instructs the generator that this element should be discarded
 *
 * This is a compatibility design: the current KSP plugin does not support
 * multi-round compilation well. Once annotated, this element will be
 * discarded and will not participate in any subsequent code generation.
 *
 * @author TrueNine
 * @since 2024-12-01
 */
@Target(
  AnnotationTarget.PROPERTY,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.FILE,
  AnnotationTarget.TYPE,
)
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class MetaSkipGeneration
