package net.yan100.compose.core.lang

import net.yan100.compose.core.http.ErrMsg
import net.yan100.compose.core.http.ErrorMessage


fun Throwable.failBy(code: Int = ErrMsg.UNKNOWN_ERROR.code, msg: String = ErrMsg.UNKNOWN_ERROR.message): ErrorMessage {
  return ErrorMessage.failedBy(msg, code)
}
