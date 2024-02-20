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
package net.yan100.compose.security.exceptions

import net.yan100.compose.core.exceptions.KnownException
import net.yan100.compose.core.http.ErrMsg

open class SecurityException(
  msg: String? = "程序安全异常",
  meta: Throwable? = null,
  code: Int? = ErrMsg._403.code
) : KnownException(msg, meta, code)

open class JwtException(
  msg: String? = "安全令牌异常",
  meta: Throwable? = null,
  code: Int? = ErrMsg._403.code
) : SecurityException(msg, meta, code)

open class JwtExpireException(
  msg: String? = "token已过期",
  meta: Throwable? = null,
  code: Int? = ErrMsg._401.code
) : JwtException(msg, meta, code)
