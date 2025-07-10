package io.github.truenine.composeserver

import kotlin.contracts.ExperimentalContracts

/**
 * 判断当前 Id 类型是否为一个有效的 Id，它遵循以下规则
 * - id 为数值时必须大于等于 0不得为负数
 * - id 为字符串时不接受空字符串，以及不支持除了 ascii 字符 以外的任何字符
 */
inline fun Long.isId(): Boolean {
  return this >= 0L
}

/**
 * 判断当前 Id 类型是否为一个有效的 Id，它遵循以下规则
 * - id 为数值时必须大于等于 0不得为负数
 * - id 为字符串时不接受空字符串，以及不支持除了 ascii 字符 以外的任何字符
 */
@OptIn(ExperimentalContracts::class)
inline fun String.isId(): Boolean {
  return this.isNotEmpty() && this.matches(Regex("^[0-9A-Za-z]+$"))
}

@Deprecated("框架内部调用代码，不应由用户直接调用", level = DeprecationLevel.ERROR) inline fun getDefaultNullableId(): Id = Long.MIN_VALUE

inline fun Number.toId(): Id? {
  return this.toLong().takeIf { it != Long.MIN_VALUE }
}

inline fun String.toId(): Id? {
  return this.toLongOrNull()?.takeIf { it != Long.MIN_VALUE }
}
