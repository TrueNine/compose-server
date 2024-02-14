package net.yan100.compose.core.lang

import java.nio.charset.StandardCharsets
import java.util.*

val ByteArray.utf8String: String
    get() = String(this, StandardCharsets.UTF_8)

val ByteArray.encodeBase64: ByteArray
    get() = Base64.getEncoder().encode(this)

val ByteArray.decodeBase64: ByteArray
    get() = Base64.getDecoder().decode(this)

val ByteArray.encodeBase64String: String
    get() = String(encodeBase64)

val ByteArray.decodeBase64String: String
    get() = String(decodeBase64, StandardCharsets.UTF_8)
