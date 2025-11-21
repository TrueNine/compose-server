package io.github.truenine.composeserver.security

import io.github.truenine.composeserver.IString
import io.github.truenine.composeserver.domain.AuthRequestInfo
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * UserDetailsWrapper wrapper class.
 *
 * @author TrueNine
 * @since 2022-12-10
 */
data class UserDetailsWrapper(val authUserInfo: AuthRequestInfo?) : UserDetails {
  private val auths: MutableSet<GrantedAuthority> = mutableSetOf()

  init {
    // Add role authorities
    auths += authUserInfo?.roles?.filter(IString::hasText)?.map { SimpleGrantedAuthority("ROLE_$it") } ?: emptyList()
    // Add permission authorities
    auths += authUserInfo?.permissions?.filter(IString::hasText)?.map { SimpleGrantedAuthority(it) } ?: emptyList()
    // Add department authorities
    auths += authUserInfo?.depts?.filter(IString::hasText)?.map { SimpleGrantedAuthority("DEPT_$it") } ?: emptyList()
  }

  override fun getAuthorities(): Collection<GrantedAuthority> = auths

  override fun getPassword(): String = authUserInfo?.encryptedPassword!!

  override fun getUsername(): String = authUserInfo?.account!!

  override fun isAccountNonExpired(): Boolean = authUserInfo?.nonExpired != false

  override fun isAccountNonLocked(): Boolean = authUserInfo?.nonLocked != false

  override fun isCredentialsNonExpired(): Boolean = authUserInfo?.nonExpired != false

  override fun isEnabled(): Boolean = authUserInfo?.enabled != false
}
