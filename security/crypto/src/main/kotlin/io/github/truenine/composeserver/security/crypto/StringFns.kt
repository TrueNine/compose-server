package io.github.truenine.composeserver.security.crypto

import java.nio.charset.Charset
import java.util.*

fun uuid(): String {
  return UUID.randomUUID().toString()
}

/**
 * ## base64 加密
 * - 调用 [java.util.Base64]
 *
 * @return 加密后的 base64
 */
fun String.base64(charset: Charset = Charsets.UTF_8): String = IBase64.encode(this.toByteArray(charset))

/**
 * ## 对 base64 字符串进行解密
 *
 * @return [String]
 */
fun String.base64Decode(charset: Charset = Charsets.UTF_8): String = IBase64.decode(this, charset)

val String.base64DecodeByteArray: ByteArray
  get() = IBase64.decodeToByte(this)

/** 将字符串转换为 sha1 */
val String.sha1: String
  get() = Encryptors.signatureBySha1(this)

/** 将字符串转换为 sha256 */
val String.sha256: String
  get() = Encryptors.signatureBySha256(this)
