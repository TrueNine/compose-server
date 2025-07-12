package io.github.truenine.composeserver.testtoolkit

import org.slf4j.LoggerFactory
import kotlin.reflect.KCallable


typealias SystemTestLogger = org.slf4j.Logger


/** # 测试期间可使用的 日志记录器 */
inline val <reified T : Any> T.log: SystemTestLogger
  get() = LoggerFactory.getLogger(T::class.java)

/** ## 直接打印变量的值 */
inline fun <reified T : Any> SystemTestLogger.info(variableExp: KCallable<T>) {
  info("{}: {}", variableExp.name, variableExp.call())
}
