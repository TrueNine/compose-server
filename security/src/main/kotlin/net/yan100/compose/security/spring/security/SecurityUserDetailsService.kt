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
package net.yan100.compose.security.spring.security

import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.core.models.AuthRequestInfo
import net.yan100.compose.security.UserDetailsWrapper
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

abstract class SecurityUserDetailsService : UserDetailsService {
  /**
   * 加载用户用户名
   *
   * @param username 用户名
   * @return [UserDetails]
   * @throws UsernameNotFoundException 用户名没有发现异常
   */
  @Throws(UsernameNotFoundException::class)
  override fun loadUserByUsername(username: String): UserDetails {
    log.debug("加载 loadUserByUsername account = {}", username)
    val details = UserDetailsWrapper(loadUserDetailsByAccount(username))
    log.debug("加载到 details = {}", details)
    return details
  }

  /**
   * 加载用户详细信息账户
   *
   * @param account 账户
   * @return [AuthRequestInfo]
   */
  abstract fun loadUserDetailsByAccount(account: String?): AuthRequestInfo?

  companion object {
    @JvmStatic private val log = slf4j(this::class)
  }
}
