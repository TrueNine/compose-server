/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
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
