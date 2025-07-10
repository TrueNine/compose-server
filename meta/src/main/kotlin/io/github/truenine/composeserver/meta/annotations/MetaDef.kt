package io.github.truenine.composeserver.meta.annotations

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
   * ## 是否为副作用对象
   *
   * 例如：jpa 同一个表，但实体不同，则需要将另一个实体所有字段设定为不更新，仅为查询对象
   */
  val shadow: Boolean = false,

  /**
   * ## 扩展超类，或额外指定的类型
   *
   * 例如：在 jpa 当中，默认继承 [io.github.truenine.composeserver.rds.core.entities.IEntity]，
   * 但如果需要继承别的类，则可以单独指定，例如：[io.github.truenine.composeserver.rds.core.entities.ITreeEntity]
   */
  val extendBy: KClass<*> = Unit::class,
)
