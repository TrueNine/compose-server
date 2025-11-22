package io.github.truenine.composeserver

import java.io.InputStream
import java.nio.charset.Charset
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

/** Empty string constant */
const val STR_EMPTY = ""

/** Underscore string constant */
const val STR_UNDERLINE = "_"

/**
 * Get resource as input stream from specified class's ClassLoader
 *
 * @param cls Specified class
 * @return Resource input stream, returns null if resource doesn't exist
 */
fun String.resourceAsStream(cls: KClass<*>): InputStream? {
  return cls.java.classLoader.getResourceAsStream(this)
}

/**
 * Get resource as input stream from generic type's ClassLoader
 *
 * @return Resource input stream, returns null if resource doesn't exist
 */
inline fun <reified C : KClass<*>> String.resourceAsStream(): InputStream? {
  return C::class.java.classLoader.getResourceAsStream(this)
}

/**
 * Check if string contains valid text
 *
 * @return true if string is not null and contains non-whitespace characters
 */
@OptIn(ExperimentalContracts::class)
fun String?.hasText(): Boolean {
  contract { returns(true) implies (this@hasText != null) }
  return IString.hasText(this)
}

/**
 * Return default value if string is null or blank
 *
 * @param default Default value
 * @return Original string or default value
 */
fun String?.orElse(default: String): String = if (hasText()) this else default

/**
 * Check if string does not contain valid text
 *
 * @return true if string is null or contains only whitespace characters
 */
@OptIn(ExperimentalContracts::class)
fun String?.nonText(): Boolean {
  contract { returns(false) implies (this@nonText != null) }
  return !this.hasText()
}

/**
 * Execute specified operation if string is not null and not blank
 *
 * @param block Operation to execute
 */
inline fun String?.ifNotNullOrBlank(crossinline block: (it: String) -> Unit) {
  if (this.hasText()) block(this)
}

/**
 * Execute transformation block if string is not null and not blank.
 *
 * @param block Transformation operation
 * @return Transformation result, or null if string is null or blank
 */
inline fun <T> String?.hasTextRun(crossinline block: String.() -> T): T? {
  return if (hasText()) block() else null
}

/**
 * Convert a multi-line string into a single-line string.
 *
 * @return Converted single-line string
 */
fun String.toOneLine(): String = IString.inLine(this)

/**
 * Convert an underscore-delimited string to camel case.
 *
 * @param firstUppercase Whether the first character should be uppercase
 * @return Converted camel-case string
 */
fun String.toPascalCase(firstUppercase: Boolean = false): String {
  return if (length == 1 || isNotBlank()) {
    split(STR_UNDERLINE)
      .joinToString(STR_EMPTY) { it.replaceFirstChar { it1 -> it1.uppercaseChar() } }
      .replaceFirstChar { if (!firstUppercase) it.lowercaseChar() else it }
  } else {
    this
  }
}

/**
 * Convert a camel-case string to an underscore-delimited string.
 *
 * @return Converted underscore-delimited string
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
 * URL-encode the string.
 *
 * @param charset Character set, default is UTF-8
 * @return URL-encoded string
 */
fun String?.toUrlEncoded(charset: Charset = Charsets.UTF_8): String = java.net.URLEncoder.encode(this ?: STR_EMPTY, charset)

/**
 * If the string starts with the specified prefix, replace the first occurrence of that prefix.
 *
 * @param meta Prefix to check and replace
 * @param replacement Replacement string
 * @return String after replacement
 */
fun String.replaceFirstIfPrefix(meta: String, replacement: String): String {
  return if (indexOf(meta) == 0) replaceFirst(meta, replacement) else meta
}
