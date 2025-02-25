package net.yan100.compose.meta.annotations.client

/**
 * 开启所有 API 均需要的注解
 *
 * 可注解于任意位置
 */
@MustBeDocumented
@Repeatable
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiGeneratingAll
