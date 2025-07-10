package net.yan100.compose.meta.annotations.orm

import java.lang.annotation.Inherited

/**
 * ## 计算属性
 *
 * 表示该元素为计算属性，大多情况下，无需进行处理
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.FUNCTION)
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class MetaFormula()
