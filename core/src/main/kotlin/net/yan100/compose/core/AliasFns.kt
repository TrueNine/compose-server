package net.yan100.compose.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/** ## 判断当前 Id 类型是否不为空 */
@OptIn(ExperimentalContracts::class)
fun Id?.isId(): Boolean {
  contract { returns(true) implies (this@isId != null) }
  return this !== null && this != Long.MIN_VALUE
}

@Deprecated("框架内部调用代码，不应由用户直接调用", level = DeprecationLevel.ERROR)
inline fun getDefaultNullableId(): Id = Long.MIN_VALUE

fun String.toId(): Id {
  return this.toLong()
}

fun Int.toId(): Id {
  return this.toLong()
}
