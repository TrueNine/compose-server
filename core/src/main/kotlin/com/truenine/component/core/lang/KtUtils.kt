package com.truenine.component.core.lang

open class KtUtils

/**
 * 判断传入的所有 boolean 不得包含 false
 * @param conditions 条件
 * @param lazyMessage 消息
 */
fun requireAll(vararg conditions: Boolean, lazyMessage: (() -> String)): Boolean {
  require(!conditions.contains(false), lazyMessage)
  return true
}

/**
 * 判断传入的所有 boolean 不得包含 false
 * @param conditions 条件
 */
fun requireAll(vararg conditions: Boolean): Boolean {
  require(!conditions.contains(false))
  return true
}
