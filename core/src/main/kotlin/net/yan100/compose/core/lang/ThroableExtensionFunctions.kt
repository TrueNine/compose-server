package net.yan100.compose.core.lang

import net.yan100.compose.core.http.ErrMsg
import net.yan100.compose.core.http.ErrorMessage


fun Throwable.failBy(
  code: Int? = ErrMsg.UNKNOWN_ERROR.code,
  msg: String? = ErrMsg.UNKNOWN_ERROR.message,
  alert: String? = ErrMsg.UNKNOWN_ERROR.alert,
  errMap: MutableMap<String, String> = mutableMapOf()
): ErrorMessage {
  return ErrorMessage.failedBy(
    msg ?: ErrMsg.UNKNOWN_ERROR.message,
    code ?: ErrMsg.UNKNOWN_ERROR.code,
    alert ?: ErrMsg.UNKNOWN_ERROR.alert,
    errMap
  )
}
