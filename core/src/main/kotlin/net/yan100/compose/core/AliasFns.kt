package net.yan100.compose.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/** ## 判断当前 Id 类型是否不为空 */
@OptIn(ExperimentalContracts::class)
fun Any?.isId(): Boolean {
  contract { returns(true) implies (this@isId is Id) }
  return this != Long.MIN_VALUE && this is Id
}

@Deprecated("框架内部调用代码，不应由用户直接调用", level = DeprecationLevel.ERROR)
inline fun getDefaultNullableId(): Id = Long.MIN_VALUE

fun String.toId(): Id? {
  return this.toLongOrNull()
}

fun Number.toId(): Id? {
  return this.toLong().takeIf { it != Long.MIN_VALUE }
}

fun Any?.toId(): Id? {
  return when (this) {
    is Id -> this
    is Number -> this.toId()
    is String -> this.toId()
    null -> null
    else -> null
  }
}
