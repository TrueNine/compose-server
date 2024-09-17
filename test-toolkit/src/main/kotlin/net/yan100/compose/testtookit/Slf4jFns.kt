package net.yan100.compose.testtookit

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KCallable

/**
 * # 测试期间可使用的 日志记录器
 */
inline val <reified T : Any> T.log: Logger get() = LoggerFactory.getLogger(T::class.java)

/**
 * ## 直接打印变量的值
 */
inline fun <reified T : Any> Logger.info(variableExp: KCallable<T>) {
  info("{}: {}", variableExp.name, variableExp.call())
}
