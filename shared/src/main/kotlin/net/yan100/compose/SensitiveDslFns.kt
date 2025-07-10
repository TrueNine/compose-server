package net.yan100.compose

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <T> sensitiveAlso(data: T, scope: ISensitiveScope<T>.(data: T) -> Unit): T {
  contract { callsInPlace(scope, InvocationKind.EXACTLY_ONCE) }
  scope(object : ISensitiveScope<T> {}, data)
  return data
}

@OptIn(ExperimentalContracts::class)
inline fun <T> sensitiveLet(data: T, scope: ISensitiveScope<T>.(data: T) -> T): T {
  contract { callsInPlace(scope, InvocationKind.EXACTLY_ONCE) }
  return scope(object : ISensitiveScope<T> {}, data)
}
