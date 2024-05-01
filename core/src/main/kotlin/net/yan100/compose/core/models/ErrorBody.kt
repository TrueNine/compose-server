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
import net.yan100.compose.core.typing.http.HttpErrorStatus

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

  var errMap: Map<String, String>? = null
    private set

  companion object {
    @JvmStatic
    @JvmOverloads
    fun failedBy(
      msg: String? = null,
      code: Int? = null,
      alt: String? = null,
      errMap: Map<String, String>? = null,
    ): ErrorBody {
      return ErrorBody().apply {
        this.code = code
        this.msg = msg
        this.alt = alt
        this.errMap = errMap
      }
    }

    @JvmStatic
    fun failedByErrMsg(messages: HttpErrorStatus): ErrorBody {
      return failedBy(msg = messages.message, code = messages.code, alt = messages.alt)
    }

    @Serial @JvmStatic private val serialVersionUID: Long = 1L
  }
}
