package com.truenine.component.security.exceptions

open class SecurityException(msg: String? = "程序安全异常") :
  RuntimeException(msg)

open class JwtException(msg: String? = "安全令牌异常") : SecurityException(msg)
open class JwtExpireException : JwtException("token已过期")
