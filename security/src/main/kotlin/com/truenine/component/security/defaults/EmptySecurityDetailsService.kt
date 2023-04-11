package com.truenine.component.security.defaults

import com.truenine.component.core.lang.EmptyDefaultModel
import com.truenine.component.core.lang.LogKt
import com.truenine.component.core.models.UserAuthorizationInfoModel
import com.truenine.component.security.spring.security.SecurityUserDetailsService


class EmptySecurityDetailsService : EmptyDefaultModel, SecurityUserDetailsService() {
  private val log = LogKt.getLog(this::class)

  init {
    log.trace("正在使用默认安全服务，生产环请自行重写")
  }

  override fun loadUserDetailsByAccount(account: String?): UserAuthorizationInfoModel? {
    log.trace("account {} 正在获取空体", account)
    return null
  }
}
