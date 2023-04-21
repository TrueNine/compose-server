package com.truenine.component.core.http

import java.io.Serial
import java.io.Serializable

/**
 * 响应错误消息
 *
 * @author TrueNine
 * @since 2022-09-24
 */
class ErrorMessage private constructor() : Serializable {
  var msg: String? = null
    private set

  var code: Int = -1
    private set

  companion object {
    @JvmStatic
    fun failedBy(msg: String, code: Int): ErrorMessage {
      return ErrorMessage().apply {
        this.code = code
        this.msg = msg
      }
    }

    @JvmStatic
    fun failByServerError(): ErrorMessage {
      return failedByMessages(ErrMsg._500)
    }

    @JvmStatic
    fun failedByClientError(): ErrorMessage {
      return failedByMessages(ErrMsg._400)
    }

    @JvmStatic
    fun failedByUnknownError(): ErrorMessage {
      return failedByMessages(ErrMsg.UNKNOWN_ERROR)
    }

    @JvmStatic
    fun failedByMessages(messages: ErrMsg): ErrorMessage {
      return failedBy(messages.message, messages.code)
    }

    @Serial
    private const val serialVersionUID: Long = 1L
  }
}
