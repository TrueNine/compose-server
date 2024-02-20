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
package net.yan100.compose.security.defaults

import net.yan100.compose.core.lang.EmptyDefault
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.core.models.AuthRequestInfo
import net.yan100.compose.security.spring.security.SecurityUserDetailsService

class EmptySecurityDetailsService : EmptyDefault, SecurityUserDetailsService() {
  private val log = slf4j(this::class)

  init {
    log.warn("正在使用默认安全服务，生产环请自行重写")
  }

  override fun loadUserDetailsByAccount(account: String?): AuthRequestInfo? {
    log.warn("account {} 正在获取空体", account)
    return null
  }
}
