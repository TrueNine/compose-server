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
@Deprecated("过于泛用，不建议使用")
open class KnownException(
  @Deprecated("过于泛用，不建议使用") private var msg: String? = null,
  @Deprecated("过于泛用，不建议使用") private var metaException: Throwable? = null,
  @Deprecated("过于泛用，不建议使用") private var code: Int? = HttpStatusTyping.UNKNOWN.code,
) : RuntimeException(msg, metaException) {

  @Deprecated("过于泛用，不建议使用")
  open fun getMeta() = this.metaException

  @Deprecated("过于泛用，不建议使用")
  open fun setMeta(ex: Throwable?) {
    this.metaException = ex
  }

  @Deprecated("过于泛用，不建议使用")
  open fun setMsg(msg: String?) {
    this.msg = msg
  }

  @Deprecated("过于泛用，不建议使用")
  open fun getMsg() = this.msg

  @Deprecated("过于泛用，不建议使用")
  open fun getCode() = this.code

  @Deprecated("过于泛用，不建议使用")
  open fun setCode(code: Int?) {
    this.code = code
  }

  override fun toString(): String {
    val s = javaClass.name
    val message = localizedMessage
    return if (message != null) "$s: $message" else "$s $msg"
  }
}

@Deprecated("过于泛用，不建议使用")
fun requireKnown(expression: Boolean) {
  if (expression) return else requireKnown(false) { "expression not satisfied" }
}

@Deprecated("过于泛用，不建议使用")
fun requireKnown(expression: Boolean, lazyMsg: () -> Any?) {
  if (expression) return
  else requireKnown(false, KnownException(lazyMsg().toString()), lazyMsg)
}

@Deprecated("过于泛用，不建议使用")
fun <E : KnownException> requireKnown(
  expression: Boolean,
  ex: E,
  lazyMsg: () -> Any?,
) {
  if (expression) return else throw ex.apply { ex.setMsg(lazyMsg().toString()) }
}
