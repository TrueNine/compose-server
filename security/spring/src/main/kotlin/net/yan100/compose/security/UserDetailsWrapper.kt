package net.yan100.compose.security

import net.yan100.compose.IString
import net.yan100.compose.domain.AuthRequestInfo
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * UserDetailsWrapper 包装类
 *
 * @author TrueNine
 * @since 2022-12-10
 */
data class UserDetailsWrapper(val authUserInfo: AuthRequestInfo?) :
  UserDetails {
  private val auths: MutableSet<GrantedAuthority> = mutableSetOf()

  init {
    // 添加角色信息
    auths +=
      authUserInfo?.roles?.filter(IString::hasText)?.map {
        SimpleGrantedAuthority("ROLE_$it")
      } ?: emptyList()
    // 添加权限信息
    auths +=
      authUserInfo?.permissions?.filter(IString::hasText)?.map {
        SimpleGrantedAuthority(it)
      } ?: emptyList()
    // 添加 部门信息
    auths +=
      authUserInfo?.depts?.filter(IString::hasText)?.map {
        SimpleGrantedAuthority("DEPT_$it")
      } ?: emptyList()
  }

  override fun getAuthorities(): Collection<GrantedAuthority> = auths

  override fun getPassword(): String = authUserInfo?.encryptedPassword!!

  override fun getUsername(): String = authUserInfo?.account!!

  override fun isAccountNonExpired(): Boolean =
    authUserInfo?.nonExpired != false

  override fun isAccountNonLocked(): Boolean = authUserInfo?.nonLocked != false

  override fun isCredentialsNonExpired(): Boolean =
    authUserInfo?.nonExpired != false

  override fun isEnabled(): Boolean = authUserInfo?.enabled != false
}
