package net.yan100.compose.core

import net.yan100.compose.core.typing.HttpStatusTyping
import java.io.Serializable

/**
 * 响应错误消息
 *
 * @author TrueNine
 * @since 2022-09-24
 */
class ErrorBody private constructor() : Serializable {
  var msg: String? = null
    private set

  var alt: String? = null
    private set

  var code: Int? = null
    private set

  var errMap: MutableMap<String, String>? = null
    private set

  companion object {
    @JvmStatic
    @JvmOverloads
    fun failedBy(msg: String? = null, code: Int? = null, alt: String? = null, errMap: MutableMap<String, String>? = null): ErrorBody {
      return ErrorBody().apply {
        this.code = code
        this.msg = msg
        this.alt = alt
        this.errMap = errMap
      }
    }

    @JvmStatic
    fun failedByHttpStatus(messages: HttpStatusTyping): ErrorBody {
      return failedBy(msg = messages.message, code = messages.code, alt = messages.alert)
    }
  }
}