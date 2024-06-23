/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core.extensionfunctions

import java.io.InputStream
import java.nio.charset.Charset
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass
import net.yan100.compose.core.util.Str

fun uuid(): String {
  return UUID.randomUUID().toString()
}

const val STR_EMPTY = ""
const val STR_SLASH = "/"
const val STR_UNDERLINE = "_"
const val STR_DOT = "."
const val STR_SPACE = " "

fun String.resourceAsStream(cls: KClass<*>): InputStream? {
  return cls.java.classLoader.getResourceAsStream(this)
}

/**
 * ## 是否包含有效字符串
 *
 * @return [Boolean]
 */
@OptIn(ExperimentalContracts::class)
fun String?.hasText(): Boolean {
  contract { returns(true) implies (this@hasText != null) }
  return Str.hasText(this)
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

inline fun String?.hasTextAlso(crossinline block: (it: String) -> Unit) {
  if (this.hasText()) block(this)
}

inline fun <T> String?.hasTextRun(crossinline block: String.() -> T): T? {
  return if (hasText()) this.block() else null
}

/**
 * ## 防空字符串
 * - 如果该字符串为 null 则转换为 ""
 * - 否则返回本身
 */
val String?.withEmpty: String
  get() = this ?: STR_EMPTY

/** ## 将该字符串转换为单行字符串 */
val String.inline: String
  get() = Str.inLine(this)

/** ## 将 foo_bar 类型的字符串转换为 fooBar */
val String.snakeCaseToCamelCase: String
  get() =
    this.split(STR_UNDERLINE)
      .joinToString(STR_EMPTY) {
        if (it.isNotEmpty()) it.replaceFirstChar { r -> if (r.isLowerCase()) r.titlecase(Locale.getDefault()) else r.toString() } else STR_EMPTY
      }
      .replaceFirstChar { it.lowercase(Locale.getDefault()) }

val String.snakeCaseToPascalCase: String
  get() = if (hasText()) split(STR_UNDERLINE).joinToString(STR_EMPTY) { it.replaceFirstChar { it1 -> it1.uppercaseChar() } } else this

val String.camelCaseToSnakeCase: String
  get() =
    fold(StringBuilder()) { acc, c ->
        if (c.isUpperCase()) {
          if (acc.isNotEmpty()) acc.append(STR_UNDERLINE)
          acc.append(c.lowercaseChar())
        } else acc.append(c)
        acc
      }
      .toString()

val String.pascalCaseToSnakeCase: String
  get() = camelCaseToSnakeCase.replaceFirst(STR_UNDERLINE, STR_EMPTY)

/**
 * ## 将字符串进行 url 编码
 * 首先调用 [withEmpty] 进行防空，然后再进行编码
 *
 * @param charset 字符集
 * @return 编码完成的字符串，使用 [java.net.URLEncoder]
 */
fun String?.urlEncoded(charset: Charset = Charsets.UTF_8): String = java.net.URLEncoder.encode(this.withEmpty, charset)

/**
 * ## base64 加密
 * - 调用 [java.util.Base64]
 *
 * @return 加密后的 base64
 */
fun String.base64(charset: Charset = Charsets.UTF_8): String = net.yan100.compose.core.encrypt.Base64Helper.encode(this.toByteArray(charset))

/**
 * ## 对 base64 字符串进行解密
 *
 * @return [String]
 */
fun String.base64Decode(charset: Charset = Charsets.UTF_8): String = net.yan100.compose.core.encrypt.Base64Helper.decode(this, charset)

fun String.base64DecodeToByteArray(): ByteArray = net.yan100.compose.core.encrypt.Base64Helper.decodeToByte(this)

fun String.replaceFirstX(meta: String, replacement: String): String {
  return if (indexOf(meta) == 0) replaceFirst(meta, replacement) else meta
}

/** ## 将所有空串视为 null */
fun String?.emptyWithNull(): String? {
  return if (this.hasText()) this else null
}
