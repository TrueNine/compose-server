package net.yan100.compose

import kotlin.contracts.ExperimentalContracts

inline fun Long.isId(): Boolean {
  return this > 0L
}

/** ## 判断当前 Id 类型是否不为空 */
@OptIn(ExperimentalContracts::class)
inline fun String.isId(): Boolean {
  return this != "" && this.all { it.isDigit() }
}

@Deprecated("框架内部调用代码，不应由用户直接调用", level = DeprecationLevel.ERROR)
inline fun getDefaultNullableId(): Id = Long.MIN_VALUE

inline fun Number.toId(): Id? {
  return this.toLong().takeIf { it != Long.MIN_VALUE }
}

inline fun String.toId(): Id? {
  return this.toLongOrNull()?.takeIf { it != Long.MIN_VALUE }
}
