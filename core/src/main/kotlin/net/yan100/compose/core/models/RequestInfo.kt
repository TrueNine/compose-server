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
package net.yan100.compose.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.annotation.Nullable

/**
 * 基础用户传递信息
 *
 * @author T_teng
 * @since 2023-04-06
 */
open class RequestInfo {
  @Nullable var userId: String? = null

  @Nullable var account: String? = null

  @Nullable @JsonIgnore var deviceId: String? = null

  @Nullable var loginIpAddr: String? = null

  @Nullable var currentIpAddr: String? = null

  override fun toString(): String {
    return mapOf(
        "userId" to userId,
        "account" to account,
        "currentIpAddr" to currentIpAddr,
        "loginIpAddr" to loginIpAddr,
        "deviceId" to deviceId
      )
      .toString()
  }
}
