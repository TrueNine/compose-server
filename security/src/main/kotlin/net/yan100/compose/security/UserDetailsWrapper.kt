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
package net.yan100.compose.security

import net.yan100.compose.core.models.AuthRequestInfo
import net.yan100.compose.core.util.Str
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * UserDetailsWrapper 包装类
 *
 * @author TrueNine
 * @since 2022-12-10
 */
data class UserDetailsWrapper(val authUserInfo: AuthRequestInfo?) : UserDetails {
  private val auths: MutableSet<GrantedAuthority> = mutableSetOf()

  init {
    // 添加角色信息
    auths += authUserInfo?.roles?.filter(Str::hasText)?.map { SimpleGrantedAuthority("ROLE_$it") } ?: emptyList()
    // 添加权限信息
    auths += authUserInfo?.permissions?.filter(Str::hasText)?.map { SimpleGrantedAuthority(it) } ?: emptyList()
    // 添加 部门信息
    auths += authUserInfo?.depts?.filter(Str::hasText)?.map { SimpleGrantedAuthority("DEPT_$it") } ?: emptyList()
  }

  override fun getAuthorities(): Collection<GrantedAuthority> = auths

  override fun getPassword(): String = authUserInfo?.encryptedPassword!!

  override fun getUsername(): String = authUserInfo?.account!!

  override fun isAccountNonExpired(): Boolean = authUserInfo?.nonExpired ?: true

  override fun isAccountNonLocked(): Boolean = authUserInfo?.nonLocked ?: true

  override fun isCredentialsNonExpired(): Boolean = authUserInfo?.nonExpired ?: true

  override fun isEnabled(): Boolean = authUserInfo?.enabled ?: true
}
