package com.truenine.component.security.spring.security

import com.truenine.component.core.lang.LogKt
import com.truenine.component.security.spring.security.wrappers.SecurityUserDetails
import com.truenine.component.security.spring.security.wrappers.SecurityUserInfo
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
    log.debug(
      "加载 loadUserByUsername account = {}",
      username
    )
    return SecurityUserDetails(
      loadUserDetailsByAccount(username)
    )
  }

  /**
   * 加载用户详细信息账户
   *
   * @param account 账户
   * @return [SecurityUserInfo]
   */
  abstract fun loadUserDetailsByAccount(account: String?): SecurityUserInfo?

  companion object {
    @JvmStatic
    private val log = LogKt.getLog(SecurityUserDetailsService::class)
  }
}
