package io.github.truenine.composeserver.security

import io.github.truenine.composeserver.typing.HttpStatus

/**
 * 程序安全异常
 *
 * 替代原来的 KnownException 继承体系，直接继承 RuntimeException
 */
open class SecurityException(private var msg: String? = "程序安全异常", private var metaException: Throwable? = null, private var code: Int? = HttpStatus._403.code) :
  RuntimeException(msg, metaException) {

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

/** JWT 安全令牌异常 */
open class JwtException(msg: String? = "安全令牌异常", meta: Throwable? = null, code: Int? = HttpStatus._403.code) : SecurityException(msg, meta, code)

/** JWT 令牌过期异常 */
open class JwtExpireException(msg: String? = "token已过期", meta: Throwable? = null, code: Int? = HttpStatus._401.code) : JwtException(msg, meta, code)
