package net.yan100.compose.security.spring.security

import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.core.models.UserAuthorizationInfoModel
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
    val details = net.yan100.compose.security.SecurityUserDetails(loadUserDetailsByAccount(username))
    log.debug("加载到 details = {}", details)
    return details
  }

  /**
   * 加载用户详细信息账户
   *
   * @param account 账户
   * @return [UserAuthorizationInfoModel]
   */
  abstract fun loadUserDetailsByAccount(account: String?): UserAuthorizationInfoModel?

  companion object {
    @JvmStatic
    private val log = slf4j(this::class)
  }
}