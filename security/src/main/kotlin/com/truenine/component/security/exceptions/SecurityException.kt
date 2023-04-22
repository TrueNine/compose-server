package com.truenine.component.security.exceptions

import com.truenine.component.core.exceptions.KnownException
import com.truenine.component.core.http.ErrMsg

open class SecurityException(msg: String? = "程序安全异常", meta: Throwable? = null, code: Int? = ErrMsg._403.code) : KnownException(msg, meta, code)

open class JwtException(msg: String? = "安全令牌异常", meta: Throwable? = null, code: Int? = ErrMsg._403.code) : SecurityException(msg, meta, code)
open class JwtExpireException(msg: String? = "token已过期", meta: Throwable? = null, code: Int? = ErrMsg._401.code) : JwtException(msg, meta, code)
