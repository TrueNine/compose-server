package net.yan100.compose.rds.core.annotations

/**
 * 当没有订单号时，对属性进行监听设置一个新的字符串订单号
 * @see net.yan100.compose.rds.listener.BizCodeInsertListener
 * @author TrueNine
 * @since 2023-05-17
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(
    AnnotationRetention.RUNTIME
)
@MustBeDocumented
annotation class BizCode
