package com.daojiatech.center.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.lang.slf4j
import com.truenine.component.core.models.UserAuthorizationInfoModel
import com.truenine.component.rds.service.UserService
import com.truenine.component.rds.service.aggregator.RbacAggregator
import com.truenine.component.security.spring.security.SecurityUserDetailsService

class SecurityServiceConfig(
  private val rbac: RbacAggregator,
  private val userService: UserService
) : SecurityUserDetailsService() {
  val log = slf4j(this::class)
  override fun loadUserDetailsByAccount(account: String?): UserAuthorizationInfoModel? {
    log.trace("加载用户信息 = {}", account)
    return account?.let {
      userService.findUserByAccount(account)?.let { user ->
        UserAuthorizationInfoModel().apply {
          this.account = user.account
          userId = user.id.toString()
          enabled = !user.band
          encryptedPassword = user.pwdEnc
          nonExpired = enabled
          nonLocked = enabled
          roles = rbac.findAllRoleNameByUserAccount(account).toList()
          permissions = rbac.findAllPermissionsNameByUserAccount(account).toList()
          log.trace("用户获取到的登录信息 = {}", account)
        }
      }
    }
  }
}
