package net.yan100.compose.security.spring.security

import net.yan100.compose.domain.AuthRequestInfo
import net.yan100.compose.security.UserDetailsWrapper
import net.yan100.compose.slf4j
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
    @JvmStatic
    private val log = slf4j(this::class)
  }
}
