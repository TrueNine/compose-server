package net.yan100.compose

import net.yan100.compose.typing.HttpStatusTyping

fun Throwable.failBy(
  code: Int? = HttpStatusTyping.UNKNOWN.code,
  msg: String? = HttpStatusTyping.UNKNOWN.message,
  alt: String? = HttpStatusTyping.UNKNOWN.alert,
  errMap: MutableMap<String, String>? = null,
): ErrorBody {
  return ErrorBody.failedBy(msg, code, alt, errMap)
}
