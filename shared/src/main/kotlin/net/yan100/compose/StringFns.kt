package net.yan100.compose

import java.io.InputStream
import java.nio.charset.Charset
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

/** 空字符串常量 */
const val STR_EMPTY = ""

/** 下划线字符串常量 */
const val STR_UNDERLINE = "_"

/**
 * 从指定类的 ClassLoader 中获取资源作为输入流
 *
 * @param cls 指定的类
 * @return 资源的输入流，如果资源不存在则返回 null
 */
fun String.resourceAsStream(cls: KClass<*>): InputStream? {
  return cls.java.classLoader.getResourceAsStream(this)
}

/**
 * 从泛型指定类型的 ClassLoader 中获取资源作为输入流
 *
 * @return 资源的输入流，如果资源不存在则返回 null
 */
inline fun <reified C : KClass<*>> String.resourceAsStream(): InputStream? {
  return C::class.java.classLoader.getResourceAsStream(this)
}

/**
 * 判断字符串是否包含有效文本
 *
 * @return 如果字符串不为 null 且包含非空白字符则返回 true
 */
@OptIn(ExperimentalContracts::class)
fun String?.hasText(): Boolean {
  contract { returns(true) implies (this@hasText != null) }
  return IString.hasText(this)
}

/**
 * 如果字符串为 null 或空白，则返回默认值
 *
 * @param default 默认值
 * @return 原字符串或默认值
 */
fun String?.orElse(default: String): String = if (hasText()) this else default

/**
 * 判断字符串是否不包含有效文本
 *
 * @return 如果字符串为 null 或仅包含空白字符则返回 true
 */
@OptIn(ExperimentalContracts::class)
fun String?.nonText(): Boolean {
  contract { returns(false) implies (this@nonText != null) }
  return !this.hasText()
}

/**
 * 如果字符串不为 null 且不为空白，则执行指定的操作
 *
 * @param block 要执行的操作
 */
inline fun String?.ifNotNullOrBlank(crossinline block: (it: String) -> Unit) {
  if (this.hasText()) block(this)
}

/**
 * 如果字符串不为 null 且不为空白，则执行指定的转换操作
 *
 * @param block 转换操作
 * @return 转换结果，如果字符串为 null 或空白则返回 null
 */
inline fun <T> String?.hasTextRun(crossinline block: String.() -> T): T? {
  return if (hasText()) block() else null
}

/**
 * 将多行字符串转换为单行字符串
 *
 * @return 转换后的单行字符串
 */
fun String.toOneLine(): String = IString.inLine(this)

/**
 * 将下划线分隔的字符串转换为驼峰式命名
 *
 * @param firstUppercase 首字母是否大写
 * @return 转换后的驼峰式字符串
 */
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

/**
 * 将驼峰式命名转换为下划线分隔的字符串
 *
 * @return 转换后的下划线分隔字符串
 */
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
 * 将字符串进行 URL 编码
 *
 * @param charset 字符编码，默认为 UTF-8
 * @return URL 编码后的字符串
 */
fun String?.toUrlEncoded(charset: Charset = Charsets.UTF_8): String =
  java.net.URLEncoder.encode(this ?: STR_EMPTY, charset)

/**
 * 如果字符串以指定前缀开始，则替换第一次出现的前缀
 *
 * @param meta 要检查和替换的前缀
 * @param replacement 替换的新字符串
 * @return 替换后的字符串
 */
fun String.replaceFirstIfPrefix(meta: String, replacement: String): String {
  return if (indexOf(meta) == 0) replaceFirst(meta, replacement) else meta
}
