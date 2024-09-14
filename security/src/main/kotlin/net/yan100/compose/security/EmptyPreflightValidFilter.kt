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
package net.yan100.compose.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.yan100.compose.core.slf4j
import net.yan100.compose.core.util.IEmptyDefault
import net.yan100.compose.security.spring.security.SecurityPreflightValidFilter

class EmptyPreflightValidFilter : IEmptyDefault, SecurityPreflightValidFilter() {

  private val log = slf4j(this::class)

  init {
    log.warn("正在使用默认的jwt过滤器")
  }

  override fun getUserAuthorizationInfo(token: String?, reFlashToken: String?, request: HttpServletRequest, response: HttpServletResponse): AuthRequestInfo {
    log.warn("生成了一个空的 {}", ::AuthRequestInfo.name)
    return AuthRequestInfo()
  }
}
