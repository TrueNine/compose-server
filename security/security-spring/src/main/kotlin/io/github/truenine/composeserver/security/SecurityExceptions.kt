package io.github.truenine.composeserver.security

import io.github.truenine.composeserver.enums.HttpStatus

/**
 * Application security exception.
 *
 * Replaces the original KnownException inheritance hierarchy and directly extends RuntimeException.
 */
open class SecurityException(
  private var msg: String? = "Security exception",
  private var metaException: Throwable? = null,
  private var code: Int? = HttpStatus._403.code,
) : RuntimeException(msg, metaException) {

  open fun getMeta() = this.metaException

  open fun setMeta(ex: Throwable?) {
    this.metaException = ex
  }

  open fun setMsg(msg: String?) {
    this.msg = msg
  }

  open fun getMsg() = this.msg

  open fun getCode() = this.code

  open fun setCode(code: Int?) {
    this.code = code
  }

  override fun toString(): String {
    val s = javaClass.name
    val message = localizedMessage
    return if (message != null) "$s: $message" else "$s $msg"
  }
}

/** JWT security token exception. */
open class JwtException(msg: String? = "Security token exception", meta: Throwable? = null, code: Int? = HttpStatus._403.code) :
  SecurityException(msg, meta, code)

/** JWT token expired exception. */
open class JwtExpireException(msg: String? = "Token has expired", meta: Throwable? = null, code: Int? = HttpStatus._401.code) : JwtException(msg, meta, code)
