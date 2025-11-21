package io.github.truenine.composeserver.security.crypto

import java.nio.charset.Charset
import java.util.*

fun uuid(): String {
  return UUID.randomUUID().toString()
}

/**
 * Base64-encode this string.
 *
 * Uses [java.util.Base64] under the hood.
 *
 * @return Base64-encoded string
 */
fun String.base64(charset: Charset = Charsets.UTF_8): String = IBase64.encode(this.toByteArray(charset))

/**
 * Decode this Base64-encoded string.
 *
 * @return Decoded string
 */
fun String.base64Decode(charset: Charset = Charsets.UTF_8): String = IBase64.decode(this, charset)

val String.base64DecodeByteArray: ByteArray
  get() = IBase64.decodeToByte(this)

/** Convert string to SHA-1 hash */
val String.sha1: String
  get() = CryptographicOperations.signatureBySha1(this)

/** Convert string to SHA-256 hash */
val String.sha256: String
  get() = CryptographicOperations.signatureBySha256(this)
