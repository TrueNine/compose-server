package net.yan100.compose.meta.annotations

import java.lang.annotation.Inherited

private typealias tg = AnnotationTarget

@Inherited
@Repeatable
@MustBeDocumented
@Target(
  tg.FUNCTION,
  tg.TYPE,
  tg.CLASS,
  tg.FIELD,
  tg.PROPERTY_GETTER,
  tg.PROPERTY_SETTER,
  AnnotationTarget.PROPERTY,
)
@Retention(AnnotationRetention.BINARY)
annotation class MetaName(val value: String = "", val name: String = "")
