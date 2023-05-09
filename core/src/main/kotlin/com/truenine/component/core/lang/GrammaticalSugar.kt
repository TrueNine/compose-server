package com.truenine.component.core.lang

import com.truenine.component.core.encrypt.Base64Helper
import java.lang.reflect.Field
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.reflect.KClass

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
val String?.withEmpty: String get() = this ?: ""

/**
 * ## 将字符串进行 url 编码
 * 首先调用 [nonNullStr] 进行防空，然后再进行编码
 * @param charset 字符集
 * @return 编码完成的字符串，使用 [java.net.URLEncoder]
 */
fun String?.urlEncoded(charset: Charset = StandardCharsets.UTF_8): String = java.net.URLEncoder.encode(this.withEmpty, charset)

/**
 * ## base64 加密
 * - 调用 [java.util.Base64]
 *
 * @return 加密后的 base64
 */
fun String.base64(charset: Charset = StandardCharsets.UTF_8): String = Base64Helper.encode(this.toByteArray(charset))

/**
 * ## 对 base64 字符串进行解密
 * @return [String]
 */
fun String.base64Decode(charset: Charset = StandardCharsets.UTF_8): String = Base64Helper.decode(this, charset)

/**
 * ## 将 foo_bar 类型的字符串转换为 fooBar
 */
val String.camelLowercaseFieldName: String
  get() = this.replace("_([a-z0-9])".toRegex()) {
    it.groupValues[1].uppercase()
  }

val String.camelUppercaseFieldName: String
  get() = if (this.hasText()) {
    this.split("_").joinToString("") { it.replaceFirstChar { it1 -> it1.uppercaseChar() } }
  } else this


/**
 * ## 递归获取一个类的所有属性
 * @param endType 结束的类型
 * @return 当前类以及所有到结束标记为止的 fields
 */
fun KClass<*>.recursionFields(endType: KClass<*> = Any::class): Array<out Field> {
  val selfFields = mutableListOf<Field>()
  var superClass: Class<*>? = this.java
  val endsWith = endType.java
  while (superClass != null) {
    selfFields += superClass.declaredFields
    superClass = superClass.superclass
    if (superClass == endsWith) break
  }
  return selfFields.toTypedArray()
}
