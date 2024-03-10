/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core.models

import java.io.Serial
import java.io.Serializable
import net.yan100.compose.core.typing.http.ErrMsg

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
    fun failedByErrMsg(messages: ErrMsg): ErrorMessage {
      return failedBy(msg = messages.message, code = messages.code, alert = messages.alert)
    }

    @Serial @JvmStatic private val serialVersionUID: Long = 1L
  }
}
