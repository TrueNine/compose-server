package io.github.truenine.composeserver.security

import io.github.truenine.composeserver.exceptions.KnownException
import io.github.truenine.composeserver.typing.HttpStatusTyping

open class SecurityException(msg: String? = "程序安全异常", meta: Throwable? = null, code: Int? = HttpStatusTyping._403.code) : KnownException(msg, meta, code)

open class JwtException(msg: String? = "安全令牌异常", meta: Throwable? = null, code: Int? = HttpStatusTyping._403.code) : SecurityException(msg, meta, code)

open class JwtExpireException(msg: String? = "token已过期", meta: Throwable? = null, code: Int? = HttpStatusTyping._401.code) : JwtException(msg, meta, code)
