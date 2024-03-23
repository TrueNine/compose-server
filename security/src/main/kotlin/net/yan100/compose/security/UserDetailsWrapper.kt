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

import net.yan100.compose.core.extensionfunctions.hasText
import net.yan100.compose.core.log.slf4j
import net.yan100.compose.core.models.AuthRequestInfo
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
  init {
    log.trace("构建 = {}", authUserInfo)
  }

  companion object {
    @JvmStatic private val log = slf4j(UserDetailsWrapper::class)
  }

  override fun getAuthorities(): Collection<GrantedAuthority> {
    return mutableListOf<GrantedAuthority>().also { auths ->
      // 添加角色信息
      authUserInfo
        ?.roles
        ?.filter { text -> text.hasText() }
        ?.forEach { r ->
          auths += SimpleGrantedAuthority("ROLE_$r")
          // 添加权限信息
          authUserInfo.permissions.filter { text -> text.hasText() }.forEach { p -> auths += SimpleGrantedAuthority(p) }
          // 添加 部门信息 到鉴权列表
          authUserInfo.depts.filter { text -> text.hasText() }.map { mr -> "DEPT_$mr" }.forEach { mr -> auths += SimpleGrantedAuthority(mr) }
        }
    }
  }

  override fun getPassword(): String {
    return authUserInfo?.encryptedPassword!!
  }

  override fun getUsername(): String {
    return authUserInfo?.account!!
  }

  override fun isAccountNonExpired(): Boolean {
    return authUserInfo?.nonExpired ?: true
  }

  override fun isAccountNonLocked(): Boolean {
    return authUserInfo?.nonLocked ?: true
  }

  override fun isCredentialsNonExpired(): Boolean {
    return authUserInfo?.nonExpired ?: true
  }

  override fun isEnabled(): Boolean {
    return authUserInfo?.enabled ?: true
  }
}
