package io.github.truenine.composeserver

import kotlin.contracts.ExperimentalContracts

/**
 * Checks if the current Id type is a valid Id, following these rules:
 * - When id is numeric, it must be greater than or equal to 0, cannot be negative
 * - When id is string, empty strings are not accepted, and only ASCII characters are supported
 */
inline fun Long.isId(): Boolean {
  return this >= 0L
}

/**
 * Checks if the current Id type is a valid Id, following these rules:
 * - When id is numeric, it must be greater than or equal to 0, cannot be negative
 * - When id is string, empty strings are not accepted, and only ASCII characters are supported
 */
@OptIn(ExperimentalContracts::class)
inline fun String.isId(): Boolean {
  return this.isNotEmpty() && this.matches(Regex("^[0-9A-Za-z]+$"))
}

@Deprecated("Internal framework code, should not be called directly by users", level = DeprecationLevel.ERROR) inline fun getDefaultNullableId(): Id = Long.MIN_VALUE

inline fun Number.toId(): Id? {
  return this.toLong().takeIf { it != Long.MIN_VALUE }
}

inline fun <T> Number.toId(receiver: (Id) -> T?): T? {
  return this.toId()?.let(receiver)
}

inline fun Number.toIdOrThrow(): Id {
  return this.toLong().takeIf { it != Long.MIN_VALUE } ?: throw IllegalArgumentException("Invalid Id: $this")
}

inline fun String.toId(): Id? {
  return this.toLongOrNull()?.takeIf { it != Long.MIN_VALUE }
}

inline fun String.toIdOrThrow(): Id {
  return this.toLongOrNull()?.takeIf { it != Long.MIN_VALUE } ?: throw IllegalArgumentException("Invalid Id: $this")
}

inline fun <T> String.toId(receiver: (Id) -> T?): T? {
  return this.toId()?.let(receiver)
}
