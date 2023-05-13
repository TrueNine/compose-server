package net.yan100.compose.core.exceptions

import net.yan100.compose.core.http.ErrMsg

/**
 * 已知的异常类型
 *
 * @author TrueNine
 * @param msg 异常信息
 * @param metaException 错误来源
 * @since 2023-02-19
 */
open class KnownException(
  msg: String? = null,
  metaException: Throwable? = null,
  private val code: Int? = ErrMsg.UNKNOWN_ERROR.code
) : RuntimeException(msg, metaException) {
  open fun getCode() = this.code
}
