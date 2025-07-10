package io.github.truenine.composeserver.meta.annotations

import java.lang.annotation.Inherited

/**
 * ## 指示生成器，该元素应被丢弃
 *
 * 这是一个兼容性设计，因为目前 开发的 ksp 插件对多轮编译支持不佳。 被注解后，该元素将被丢弃，随后，丢弃的元素将不会参与到后续的生成当中。
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
