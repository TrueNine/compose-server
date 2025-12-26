package io.github.truenine.composeserver.security.spring.security

import io.github.truenine.composeserver.domain.AuthRequestInfo
import io.github.truenine.composeserver.security.UserDetailsWrapper
import io.github.truenine.composeserver.slf4j
import org.springframework.security.core.userdetails.*

abstract class SecurityUserDetailsService : UserDetailsService {
  /**
   * Load user by username.
   *
   * @param username Username
   * @return [UserDetails]
   * @throws UsernameNotFoundException When the username cannot be found
   */
  @Throws(UsernameNotFoundException::class)
  override fun loadUserByUsername(username: String): UserDetails {
    log.debug("Loading user by username, account = {}", username)
    val details = UserDetailsWrapper(loadUserDetailsByAccount(username))
    log.debug("Loaded details = {}", details)
    return details
  }

  /**
   * Load user details by account identifier.
   *
   * @param account Account identifier
   * @return [AuthRequestInfo]
   */
  abstract fun loadUserDetailsByAccount(account: String?): AuthRequestInfo?

  companion object {
    @JvmStatic private val log = slf4j(this::class)
  }
}
