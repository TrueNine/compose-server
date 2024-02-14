package net.yan100.compose.core.http

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
    var alert: String? = null
        private set

    var code: Int = -1
        private set
    var errMap: MutableMap<String, String> = mutableMapOf()
        private set


    companion object {
        const val DEFAULT_ERROR_MESSAGE = "发生未知异常，服务器返回错误"

        @JvmStatic
        fun failedBy(
            msg: String = ErrMsg.UNKNOWN_ERROR.message,
            code: Int = ErrMsg.UNKNOWN_ERROR.code,
            alert: String = ErrMsg.UNKNOWN_ERROR.alert,
            errMap: MutableMap<String, String> = mutableMapOf()
        ): ErrorMessage {
            return ErrorMessage().apply {
                this.code = code
                this.msg = msg
                this.alert = alert
                this.errMap = errMap
            }
        }

        @JvmStatic
        val SERVER_ERROR: ErrorMessage = failedByErrMsg(ErrMsg._500)

        @JvmStatic
        val BAE_REQUEST: ErrorMessage = failedByErrMsg(ErrMsg._400)

        @JvmStatic
        val UNKNOWN_ERROR: ErrorMessage = failedByErrMsg(ErrMsg.UNKNOWN_ERROR)

        @JvmStatic
        fun failedByErrMsg(messages: ErrMsg): ErrorMessage {
            return failedBy(msg = messages.message, code = messages.code, alert = messages.alert)
        }

        @Serial
        private const val serialVersionUID: Long = 1L
    }
}
