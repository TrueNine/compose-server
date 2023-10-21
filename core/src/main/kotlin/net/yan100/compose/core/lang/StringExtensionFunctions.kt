package net.yan100.compose.core.lang

import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.reflect.KClass


val String.camelUppercaseFieldName: String
  get() = if (this.hasText()) {
    this.split("_").joinToString("") { it.replaceFirstChar { it1 -> it1.uppercaseChar() } }
  } else this


fun String.resourceAsStream(cls: KClass<*>): InputStream? {
  return cls.java.classLoader.getResourceAsStream(this)
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
 * ## 将 foo_bar 类型的字符串转换为 fooBar
 */
val String.camelLowercaseFieldName: String
  get() = this.replace("_([a-z0-9])".toRegex()) {
    it.groupValues[1].uppercase()
  }

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
fun String.base64(charset: Charset = StandardCharsets.UTF_8): String = net.yan100.compose.core.encrypt.Base64Helper.encode(this.toByteArray(charset))

/**
 * ## 对 base64 字符串进行解密
 * @return [String]
 */
fun String.base64Decode(charset: Charset = StandardCharsets.UTF_8): String = net.yan100.compose.core.encrypt.Base64Helper.decode(this, charset)

fun String.replaceFirstX(meta: String, replacement: String): String {
  return if (indexOf(meta) == 0) replaceFirst(meta, replacement)
  else meta
}
