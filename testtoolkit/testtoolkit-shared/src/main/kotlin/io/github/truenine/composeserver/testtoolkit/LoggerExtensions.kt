package io.github.truenine.composeserver.testtoolkit

import kotlin.reflect.KCallable
import org.slf4j.LoggerFactory

typealias SystemTestLogger = org.slf4j.Logger

/** Logger that can be used during tests. */
inline val <reified T : Any> T.log: SystemTestLogger
  get() = LoggerFactory.getLogger(T::class.java)

/** Convenience helper to log the value of a property. */
inline fun <reified T : Any> SystemTestLogger.info(variableExp: KCallable<T>) {
  info("{}: {}", variableExp.name, variableExp.call())
}
