package com.truenine.component.core.annotations

import java.lang.annotation.Inherited

/**
 * 该注解标志这这个类、方法、参数……
 * <br></br>
 * 可能在未来版本移除或者变更，因为底层依赖不稳定
 * <br></br>
 * 或者目前也没有找到好的解决方式
 *
 * @author TrueNine
 * @since 2022-10-26
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
annotation class BetaTest
