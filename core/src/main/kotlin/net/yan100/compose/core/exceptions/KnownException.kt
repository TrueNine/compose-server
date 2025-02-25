package net.yan100.compose.core.exceptions

import net.yan100.compose.core.typing.HttpStatusTyping

/**
 * 已知的异常类型
 *
 * @param msg 异常信息
 * @param metaException 错误来源
 * @author TrueNine
 * @since 2023-02-19
 */
open class KnownException(
  private var msg: String? = null,
  private var metaException: Throwable? = null,
  private var code: Int? = HttpStatusTyping.UNKNOWN.code,
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

fun requireKnown(expression: Boolean) {
  if (expression) return else requireKnown(false) { "expression not satisfied" }
}

fun requireKnown(expression: Boolean, lazyMsg: () -> Any?) {
  if (expression) return
  else requireKnown(false, KnownException(lazyMsg().toString()), lazyMsg)
}

fun <E : KnownException> requireKnown(
  expression: Boolean,
  ex: E,
  lazyMsg: () -> Any?,
) {
  if (expression) return else throw ex.apply { ex.setMsg(lazyMsg().toString()) }
}
