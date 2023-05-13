package net.yan100.compose.core.annotations

import java.lang.annotation.Inherited

/**
 * 当前被标记目标未被实现，暂时不可用
 *
 * @author TrueNine
 * @date 2022-11-08
 */
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.ANNOTATION_CLASS,
  AnnotationTarget.CONSTRUCTOR,
  AnnotationTarget.FIELD,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
  AnnotationTarget.CLASS
)
@MustBeDocumented
annotation class UnImplemented
