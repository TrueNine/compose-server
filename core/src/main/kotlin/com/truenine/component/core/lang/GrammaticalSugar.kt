package com.truenine.component.core.lang

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

open class GrammaticalSugar

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

/**
 * ## 是否包含有效字符串
 * @return [Boolean]
 */
fun String?.hasText(): Boolean = Str.hasText(this)

/**
 * ## 该字符串没有值
 * @return [Boolean] 该字符串没有值，对 [hasText] 的反向调用
 */
fun String?.nonText(): Boolean = !this.hasText()

/**
 * ## 防空字符串
 * - 如果该字符串为 null 则转换为 ""
 * - 否则返回本身
 */
fun String?.nonNullStr(): String = this ?: ""

/**
 * ## 将字符串进行 url 编码
 * 首先调用 [nonNullStr] 进行防空，然后再进行编码
 * @param charset 字符集
 * @return 编码完成的字符串，使用 [java.net.URLEncoder]
 */
fun String?.urlEncoded(charset: Charset = StandardCharsets.UTF_8): String = java.net.URLEncoder.encode(this.nonNullStr(), charset)
