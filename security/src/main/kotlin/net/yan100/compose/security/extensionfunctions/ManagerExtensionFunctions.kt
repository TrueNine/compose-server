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
package net.yan100.compose.security.extensionfunctions

import net.yan100.compose.security.UserDetailsWrapper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

fun AuthenticationManager.authenticateByAccount(
  account: String,
  password: String,
  unauthorized: () -> Unit
): UserDetailsWrapper? {
  val principal =
    this.authenticate(UsernamePasswordAuthenticationToken(account, password)).principal
      as? UserDetailsWrapper
  if (null == principal) {
    unauthorized()
    return null
  } else return principal
}