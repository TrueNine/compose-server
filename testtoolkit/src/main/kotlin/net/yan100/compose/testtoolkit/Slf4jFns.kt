package net.yan100.compose.testtoolkit

import kotlin.reflect.KCallable
import org.slf4j.LoggerFactory

/** # 测试期间可使用的 日志记录器 */
inline val <reified T : Any> T.log: SysLogger
  get() = LoggerFactory.getLogger(T::class.java)

/** ## 直接打印变量的值 */
inline fun <reified T : Any> SysLogger.info(variableExp: KCallable<T>) {
  info("{}: {}", variableExp.name, variableExp.call())
}
