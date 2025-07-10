package io.github.truenine.composeserver.security.crypto

import java.util.*

val ByteArray.encodeBase64: ByteArray
  get() = Base64.getEncoder().encode(this)

val ByteArray.decodeBase64: ByteArray
  get() = Base64.getDecoder().decode(this)

val ByteArray.encodeBase64String: String
  get() = String(encodeBase64)

val ByteArray.decodeBase64String: String
  get() = String(decodeBase64, Charsets.UTF_8)
