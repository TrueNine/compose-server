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

import com.fasterxml.jackson.annotation.JsonIgnore
import net.yan100.compose.core.map.RequestInfoStructMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * security校验所需的用户身份
 *
 * @author TrueNine
 * @since 2022-12-10
 */
class AuthRequestInfo : RequestInfo() {
  var encryptedPassword: String? = null

  var nonLocked = false

  var nonExpired = false

  var enabled = false
  var roles: List<String> = CopyOnWriteArrayList()
  var permissions: List<String> = CopyOnWriteArrayList()
  var depts: List<String> = CopyOnWriteArrayList()

  @get:JsonIgnore
  val cleaned: AuthRequestInfo
    get() = RequestInfoStructMap.INSTANCE.clearAuthedInfo(this)
}
