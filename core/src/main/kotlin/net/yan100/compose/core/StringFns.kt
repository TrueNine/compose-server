package net.yan100.compose.core

import java.io.InputStream
import java.nio.charset.Charset
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

const val STR_EMPTY = ""
const val STR_UNDERLINE = "_"

fun String.resourceAsStream(cls: KClass<*>): InputStream? {
  return cls.java.classLoader.getResourceAsStream(this)
}

inline fun <reified C : KClass<*>> String.resourceAsStream(): InputStream? {
  return C::class.java.classLoader.getResourceAsStream(this)
}

/**
 * ## 是否包含有效字符串
 *
 * @return [Boolean]
 */
@OptIn(ExperimentalContracts::class)
fun String?.hasText(): Boolean {
  contract { returns(true) implies (this@hasText != null) }
  return IString.hasText(this)
}

fun String?.orElse(default: String): String = if (hasText()) this else default

/**
 * ## 该字符串没有值
 *
 * @return [Boolean] 该字符串没有值，对 [hasText] 的反向调用
 */
@OptIn(ExperimentalContracts::class)
fun String?.nonText(): Boolean {
  contract { returns(false) implies (this@nonText != null) }
  return !this.hasText()
}

inline fun String?.ifNotNullOrBlank(crossinline block: (it: String) -> Unit) {
  if (this.hasText()) block(this)
}

inline fun <T> String?.hasTextRun(crossinline block: String.() -> T): T? {
  return if (hasText()) block() else null
}

/** ## 将该字符串转换为单行字符串 */
fun String.toOneLine(): String = IString.inLine(this)

/** ## 将 foo_bar 类型的字符串转换为 fooBar */
fun String.toPascalCase(firstUppercase: Boolean = false): String {
  return if (length == 1 || isNotBlank()) {
    split(STR_UNDERLINE)
      .joinToString(STR_EMPTY) {
        it.replaceFirstChar { it1 -> it1.uppercaseChar() }
      }
      .replaceFirstChar { if (!firstUppercase) it.lowercaseChar() else it }
  } else {
    this
  }
}

fun String.toSnakeCase(): String {
  if (length <= 1 || isBlank()) return lowercase()
  return buildString {
    var prevIsLower = false
    this@toSnakeCase.forEachIndexed { i, c ->
      if (c.isUpperCase()) {
        if (prevIsLower) append('_')
        append(c.lowercaseChar())
        prevIsLower = false
      } else {
        append(c.lowercaseChar())
        prevIsLower = true
      }
    }
  }
}

/**
 * ## 将字符串进行 url 编码
 * 首先调用 [withEmpty] 进行防空，然后再进行编码
 *
 * @param charset 字符集
 * @return 编码完成的字符串，使用 [java.net.URLEncoder]
 */
fun String?.toUrlEncoded(charset: Charset = Charsets.UTF_8): String =
  java.net.URLEncoder.encode(this ?: STR_EMPTY, charset)

fun String.replaceFirstIfPrefix(meta: String, replacement: String): String {
  return if (indexOf(meta) == 0) replaceFirst(meta, replacement) else meta
}
