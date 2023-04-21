package com.truenine.component.security.exceptions

import com.truenine.component.core.exceptions.KnownException

open class SecurityException(msg: String? = "程序安全异常") : KnownException(msg)

open class JwtException(msg: String? = "安全令牌异常") : SecurityException(msg)
open class JwtExpireException : JwtException("token已过期")
