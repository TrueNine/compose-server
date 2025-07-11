package io.github.truenine.composeserver

/**
 * 判断传入的所有 boolean 不得包含 false
 *
 * @param conditions 条件
 * @param lazyMessage 消息
 */
inline fun requireAll(vararg conditions: Boolean, crossinline lazyMessage: (() -> String)): Boolean {
  require(!conditions.contains(false), lazyMessage)
  return true
}

/**
 * 判断传入的所有 boolean 不得包含 false
 *
 * @param conditions 条件
 */
fun requireAll(vararg conditions: Boolean): Boolean {
  require(!conditions.contains(false))
  return true
}

fun <T> checkAllNotNull(vararg values: T?) {
  checkAllNotNull(values) { "index $it has null value" }
}

inline fun <T> checkAllNotNull(vararg values: T?, crossinline lazyMessage: (idx: Int) -> Any) {
  values.forEachIndexed { idx, it -> checkNotNull(it) { lazyMessage(idx) } }
}
