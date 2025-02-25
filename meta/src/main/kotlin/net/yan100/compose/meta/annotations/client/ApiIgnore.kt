package net.yan100.compose.meta.annotations.client

/** 忽略掉该元素，不应被生成 */
@MustBeDocumented
@Repeatable
@Target(
  AnnotationTarget.FUNCTION,
  AnnotationTarget.TYPE,
  AnnotationTarget.CLASS,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiIgnore
