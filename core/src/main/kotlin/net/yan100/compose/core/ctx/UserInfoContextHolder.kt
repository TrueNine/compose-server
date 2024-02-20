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
package net.yan100.compose.core.ctx

import org.springframework.core.NamedInheritableThreadLocal

object UserInfoContextHolder {
  @JvmStatic
  private val CURRENT_USER: ThreadLocal<net.yan100.compose.core.models.RequestInfo> =
    NamedInheritableThreadLocal("UserInfoContextHolder::current_user")

  @JvmStatic
  fun set(info: net.yan100.compose.core.models.RequestInfo) {
    CURRENT_USER.set(info)
  }

  @JvmStatic
  fun get(): net.yan100.compose.core.models.RequestInfo? {
    return CURRENT_USER.get()
  }

  @JvmStatic
  fun clean() {
    CURRENT_USER.remove()
  }
}
