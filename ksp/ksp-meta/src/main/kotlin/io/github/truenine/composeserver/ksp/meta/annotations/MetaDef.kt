package io.github.truenine.composeserver.ksp.meta.annotations

import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@MustBeDocumented
@Repeatable
@Inherited
@Target(
  AnnotationTarget.FUNCTION,
  AnnotationTarget.TYPE,
  AnnotationTarget.CLASS,
  AnnotationTarget.FIELD,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
)
@Retention(AnnotationRetention.BINARY)
annotation class MetaDef(
  /**
   * ## Whether this is a side-effect object
   *
   * For example: in JPA, when two entities share the same table but represent different views, one entity may need all fields marked as non-updatable and used
   * only as a read/query object.
   */
  val shadow: Boolean = false,

  /**
   * ## Extended superclass or additional specified type
   *
   * For example: in JPA, classes may by default extend [io.github.truenine.composeserver.rds.core.entities.IEntity], but if you need to extend another class,
   * you can explicitly specify it here, such as [io.github.truenine.composeserver.rds.core.entities.ITreeEntity].
   */
  val extendBy: KClass<*> = Unit::class,
)
